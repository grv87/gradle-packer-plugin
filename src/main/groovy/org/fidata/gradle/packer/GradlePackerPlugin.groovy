/*	gradle-packer-plugin
	Copyright © 2016-2017  Basil Peace

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
	implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package org.fidata.gradle.packer

import org.gradle.api.*
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Delete
import org.gradle.api.logging.LogLevel
import org.gradle.internal.os.OperatingSystem
import java.util.regex.*
import com.samskivert.mustache.Mustache
import groovy.json.JsonSlurper
import groovy.json.JsonOutput


class GradlePackerPlugin implements Plugin<Project> {
    def void apply(Project project) {
		project.task('validate') { group 'Validate' }
		project.extensions.create("packer", PackerPluginExtension)
		project.afterEvaluate {
			for(template in project.packer.templates)
				processTemplate project, template.fileName, template.parentTask
		}
    }
	/* Packer uses Go text/template library. It wasn't ported to Java/Groovy
	   This code uses Mustache to parse templates.
	   There could be errors due to slightly different syntax.
	   However, that most probably won't happen in simple templates. */
	static String parseString(string, variables) {
		Mustache.compiler().compile(string).execute(variables)
	}
	static Pattern httpFileNamePattern = ~$/http://\{\{\s*.HTTPIP\s*\}\}(?::\{\{\s*.HTTPPort\s*\}\})?\/([^\s<]*)/$

	def String findAMI(t, region) {
		Map res
		new ByteArrayOutputStream().withStream { os ->
			t.project.exec {
				environment << t.awsEnvironment
				commandLine 'aws', 'ec2', 'describe-images', '--region', region, '--filters', "Name=\"name\",Values=\"$t.AMIName\"".replace('"', OperatingSystem.current().windows ? '\\"' : '"'), '--output', 'json'
				standardOutput = os
			}
			res = new JsonSlurper().parseText(os.toString())
		}
		if (res['Images'].size() == 1)
			return res['Images'][0]['ImageId']
	}
	def void processTemplate(Project project, String fileName, Task parentTask = null) {
		project.logger.info(sprintf('gradle-packer-plugin: Processing %s template', [fileName]))
		File templateFile = project.file(fileName)
		Map InputJSON = new JsonSlurper().parse(templateFile)
		Map variables = [:]
		List<String> customVariablesCmdLine = []
		for (variable in InputJSON['variables'])
			if (project.packer.customVariables[variable.key]) {
				variables["user `$variable.key`"] = project.packer.customVariables[variable.key]
				customVariablesCmdLine.push '-var'
				customVariablesCmdLine.push "$variable.key=${project.packer.customVariables[variable.key]}"
			}
			else
				variables["user `$variable.key`"] = variable.value
		String imageName = variables['user `name`'] // ?: templateFile filename without extension
		Task validate = project.task([type: Exec], "validate-$imageName") {
			group 'Validate'
			if (project.gradle.startParameter.logLevel >= LogLevel.DEBUG)
				environment 'PACKER_LOG',  1
			commandLine ((['packer', 'validate', '-syntax-only'] + customVariablesCmdLine + [fileName]))
			inputs.file templateFile
		}
		project.tasks['validate'].dependsOn validate
		Map ts = [:]
		for (builder in InputJSON['builders']) {
			String builderType = builder['type']
			String buildName = builder['name'] ?: builderType
			String fullBuildName = "$imageName-$buildName"
			Task t = project.task([type: Exec], "build-$fullBuildName") {
				group 'Build'
				ext.builderType = builderType
				ext.buildName = buildName
				ext.fullBuildName = fullBuildName
				ext.cleanTask = project.task("clean-$fullBuildName") {
					group 'Clean'
					shouldRunAfter validate
				}
				shouldRunAfter validate
				mustRunAfter cleanTask
				commandLine((['packer', 'build', "-only=$buildName"] + customVariablesCmdLine + (project.gradle.startParameter.logLevel >= LogLevel.DEBUG ? ['-debug'] : []) + [fileName]))
				inputs.file templateFile
			}

			// VirtualBox ISO & OVF builders
			if (builderType	== 'virtualbox-iso') {
				// As of 2016-11-02 only file: URLs are supported by Gradle
				// See: https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#files%28java.lang.Object...%29
				// But, still, 1) this should work in the future 2) most probably, there is no reason to check ISO change if there is known checksum
				// if (builder.containsKey('iso_url'))
				// 	t.inputs.file parseString(builder['iso_url'], variables)
				// else if (builder.containsKey('iso_urls'))
				// 	for (iso_url in builder['iso_urls'])
				// 		t.inputs.file parseString(iso_url, variables)
			}
			if (builder['type'] == 'virtualbox-ovf') {
				t.inputs.file parseString(builder['source_path'], variables)
			}
			if (builderType == 'virtualbox-iso' || builderType == 'virtualbox-ovf') {
				if (builder.containsKey('floppy_files'))
					for (floppy_file in builder['floppy_files'])
						t.inputs.file parseString(floppy_file, variables)
				if (builder.containsKey('http_directory') && builder.containsKey('boot_command')) {
					Matcher m = httpFileNamePattern.matcher(parseString(builder['boot_command'].toString(), variables + ['.HTTPIP': '{{ .HTTPIP }}', '.HTTPPort': '{{ .HTTPPort }}']))
					if (m.find() && m.group(1) != '')
						t.inputs.file project.file(new File(parseString(builder['http_directory'], variables), m.group(1)))
				}
				// ISO URL
				// if (builder.containsKey('guest_addition_url'))
				// 	t.inputs.file parseString(builder['guest_addition_url'], variables)
				if (builder.containsKey('ssh_key_path'))
					t.inputs.file parseString(builder['ssh_key_path'], variables)

				String outputDir = parseString(builder['output_directory'] ?: "output-$buildName", variables)
				t.ext.VMName = parseString(builder['vm_name'] ?: "packer-$buildName", variables)
				// Output filename when no post-processors are used - default
				t.ext.outputFileName = project.file(new File(outputDir, t.VMName + '.' + (builder['format'] ?: 'ovf'))).toString()
				project.logger.info(sprintf('gradle-packer-plugin: outputFileName %s', [t.outputFileName]))
				Task powerOffVM = project.task([type: Exec], "powerOff-$fullBuildName") {
					group 'Clean'
					commandLine 'VBoxManage', 'controlvm', "$t.VMName", 'poweroff'
					ignoreExitValue true
				}
				Task unregisterVM = project.task([type: Exec], "unregisterVM-$fullBuildName") {
					group 'Clean'
					dependsOn powerOffVM
					commandLine 'VBoxManage', 'unregistervm', "$t.VMName", '--delete'
					ignoreExitValue true
				}
				Task deleteOutputDir = project.task([type: Delete], "deleteOutputDir-$fullBuildName") {
					group 'Clean'
					dependsOn unregisterVM
					delete outputDir
				}
				t.cleanTask.dependsOn deleteOutputDir
			}

			// Amazon EBS builder
			if (builderType == 'amazon-ebs') {
				t.ext.awsEnvironment = [
					'AWS_ACCESS_KEY_ID': parseString(builder['access_key'], variables),
					'AWS_SECRET_ACCESS_KEY': parseString(builder['secret_key'], variables)
				]
				Map filters
				String[] owners = []
				boolean mostRecent = false
				if (builder.containsKey('source_ami'))
					filters = ['image-id':  [parseString(builder['source_ami'], variables)]]
				else {
					filters = builder['source_ami_filter']['filters'].collectEntries { key, values -> ["$key": values instanceof List ? values.collect { [parseString(it, variables)] } : [values instanceof String ? parseString(values, variables) : values]] }
					owners = builder['source_ami_filter']['owners'] ?: []
					mostRecent = builder['source_ami_filter']['most_recent'] ?: false
				}
				t.inputs.property('sourceAMI', {
					Map res = [:]
					new ByteArrayOutputStream().withStream { os ->
						project.exec {
							environment << t.awsEnvironment
							commandLine(['aws', 'ec2', 'describe-images', '--region', parseString(builder['region'], variables)] + (owners.size() > 0 ? ['--owners', owners] : []) + ['--filters', JsonOutput.toJson(filters.collectEntries { key, values -> ['Name': key, 'Values': values] }).replace('"', OperatingSystem.current().windows ? '\\"' : '"'), '--output', 'json'])
							standardOutput = os
						}
						res = new JsonSlurper().parseText(os.toString())
						if (res['Images'].size() > 0 && mostRecent)
							res['Images'] = [ res['Images'].max { it['CreationDate'] } ]
					}
					project.logger.info(sprintf('gradle-packer-plugin: sourceAMI value %s', [JsonOutput.toJson(res)]))
					JsonOutput.toJson(res)
				})

				t.ext.AMIName = parseString(builder['ami_name'], variables)
				t.ext.outputAMI = [:]
				if (!builder.containsKey('ami_regions'))
					builder['ami_regions'] = []
				builder['ami_regions'] = [builder['region']] + builder['ami_regions']
				for (region in builder['ami_regions']) {
					region = parseString(region, variables)
					t.outputs.upToDateWhen {
						t.ext.outputAMI = findAMI(t, region)
						return t.ext.outputAMI != null
					}
					Task unregisterImage = project.task("unregisterImage-$fullBuildName-$region") {
						group 'Clean'
						onlyIf {
							ext.AMI = findAMI(t, region)
							return ext.AMI != null
						}
						doLast {
							project.exec {
								environment << t.awsEnvironment
								commandLine 'aws', 'ec2', 'deregister-image', '--region', region, '--image-id', ext.AMI
							}
						}
					}
					Task waitForUnregisterImage = project.task("waitForUnregisterImage-$fullBuildName-$region") {
						group: 'Clean'
						onlyIf {
							String AMI = findAMI(t, region)
							return AMI != null
						}
						dependsOn unregisterImage
						doLast {
							while (findAMI(t, region) != null)
								sleep(10 * 1000)
						}
					}
					t.cleanTask.dependsOn waitForUnregisterImage
				}
				if (builder.containsKey('ssh_private_key_file'))
					t.inputs.file parseString(builder['ssh_private_key_file'], variables)
				if (builder.containsKey('user_data_file'))
					t.inputs.file parseString(builder['user_data_file'], variables)
			}

			ts[buildName] = t
			if (parentTask)
				parentTask.dependsOn t
		}
		Map processedTasks

		if (InputJSON.containsKey('provisioners'))
			for (p in InputJSON['provisioners']) {
				if (p.containsKey('only')) {
					processedTasks = [:]
					for (buildName in p['only'])
						processedTasks[buildName] = ts[buildName]
				}
				else {
					processedTasks = new HashMap(ts)
					if (p.containsKey('except'))
						for (buildName in p['except'])
							processedTasks.remove buildName
				}
				for (t in processedTasks.values()) {
					Map provisioner = new HashMap(p)
					if (provisioner.containsKey('override'))
						for (override in provisioner['override'][t.buildName])
							provisioner[override.key] = override.value
					provisioner.remove 'override'

					// Shell provisioner
					if (provisioner['type'] == 'shell') {
						if (provisioner.containsKey('script'))
							t.inputs.file parseString(provisioner['script'], variables)
						else if (provisioner.containsKey('scripts'))
							for (script in provisioner['scripts'])
								t.inputs.file parseString(script, variables)
					}

					// Chef solo provisioner
					if (provisioner['type'] == 'chef-solo') {
						if (provisioner.containsKey('config_template'))
							t.inputs.file parseString(provisioner['config_template'], variables)
						if (provisioner.containsKey('cookbook_paths'))
							for (cookbook_path in provisioner['cookbook_paths'])
								t.inputs.dir parseString(cookbook_path, variables)
						if (provisioner.containsKey('data_bags_path'))
							t.inputs.dir parseString(provisioner['data_bags_path'], variables)
						if (provisioner.containsKey('encrypted_data_bag_secret_path'))
							t.inputs.file parseString(provisioner['encrypted_data_bag_secret_path'], variables)
						if (provisioner.containsKey('environments_path'))
							t.inputs.dir parseString(provisioner['environments_path'], variables)
						if (provisioner.containsKey('roles_path'))
							t.inputs.dir parseString(provisioner['roles_path'], variables)
					}

				}
			}
		if (InputJSON.containsKey('post-processors')) {
			Closure processPostProcessors = { p ->
				if (p.containsKey('only')) {
					processedTasks = [:]
					for (buildName in p['only'])
						processedTasks[buildName] = ts[buildName]
				}
				else {
					processedTasks = new HashMap(ts)
					if (p.containsKey('except'))
						for (buildName in p['except'])
							processedTasks.remove buildName
				}
				for (t in processedTasks.values()) {
					Map postProcessor = new HashMap(p)

					// Vagrant post-processor
					// Update: 2015-06-16
					if (postProcessor['type'] == 'vagrant') {
						for (override in postProcessor['override'])
							postProcessor[override.key] = override.value
						postProcessor.remove 'override'

						if (postProcessor.containsKey('vagrantfile_template'))
							t.inputs.file parseString(postProcessor['vagrantfile_template'], variables)
						if (postProcessor.containsKey('include'))
							for (include in postProcessor['include'])
								t.inputs.file parseString(include, variables)
						String vagrantProvider
						switch (t.builderType) {
							case 'virtualbox-iso':
							case 'virtualbox-ovf':
								vagrantProvider = 'virtualbox'
								break
							case 'amazon-ebs':
								vagrantProvider = 'aws'
								break
						}
						t.ext.outputFileName = parseString(postProcessor['output'] ?: 'packer_{{.BuildName}}_{{.Provider}}.box', variables + ['.Provider': vagrantProvider, '.ArtifactId': vagrantProvider, '.BuildName': t.buildName])
						project.logger.info(sprintf('gradle-packer-plugin: outputFileName %s', [t.outputFileName]))
						t.cleanTask.dependsOn project.task([type: Delete], "deleteOutputFile-$t.fullBuildName") {
							group 'Clean'
							delete t.outputFileName
						}
					}
				}
			}
			for (p in InputJSON['post-processors']) {
				if (p instanceof String) continue
				if (p instanceof List)
					for (p2 in p) processPostProcessors(p2)
				else processPostProcessors(p)
			}
		}
		for (t in ts.values()) {
			if (t.ext.has('outputFileName')) {
				project.logger.info(sprintf('gradle-packer-plugin: task %s has outputFileName %s', [t.name, t.outputFileName]))
				t.outputs.file(t.outputFileName)
			}
		}
	}
}

class PackerTemplate {
	String fileName
	Task parentTask = null
}

class PackerPluginExtension {
	Map customVariables = [:]
	List<PackerTemplate> templates = []

	def template(String fileName, Task parentTask = null) {
		templates.push(new PackerTemplate(fileName: fileName, parentTask: parentTask))
	}
}
