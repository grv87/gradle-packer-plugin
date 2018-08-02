#!/usr/bin/env groovy
/*
 * PackerPluginExtension class
 * Copyright © 2016-2018  Basil Peace
 *
 * This file is part of gradle-packer-plugin.
 *
 * This plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this plugin.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.fidata.gradle.packer

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Delete
import org.gradle.api.logging.LogLevel
import org.gradle.internal.os.OperatingSystem
import org.gradle.process.ExecSpec
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import java.util.regex.Matcher
import com.samskivert.mustache.Mustache
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import org.apache.commons.io.FilenameUtils
import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator

/**
 * `packer` extension for Gradle project
 */
@CompileStatic
class PackerPluginExtension extends PackerToolExtension {
  Map customVariables = [:]
  Date initTime = new Date()
  NoArgGenerator uuidGenerator = Generators.timeBasedGenerator()

  /* Packer uses Go text/template library. It wasn't ported to Java/Groovy
     This code uses Mustache to parse templates.
     There could be errors due to slightly different syntax.
     However, that most probably won't happen in simple templates. */
  static String parseString(String string, Map<String, String> contextTemplateData) {
    Mustache.compiler().compile(string).execute(contextTemplateData)
  }
  static final Pattern HTTP_FILENAME_PATTERN = ~$/http://\{\{\s*.HTTPIP\s*\}\}(?::\{\{\s*.HTTPPort\s*\}\})?\/([^\s<]*)/$

  List<String> packerLogLevelArgs() {
    (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG ? ['-debug'] : (List<String>)[]
  }
  List<String> awsLogLevelArgs() {
    (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG ? ['--debug'] : (List<String>)[]
  }

  List findAMI(Map<String, Object> awsEnvironment, String region, List<String> owners, Map<String, Object> filters) {
    new ByteArrayOutputStream().withStream { os ->
      project.exec { ExecSpec execSpec ->
        execSpec.with {
          environment << awsEnvironment
          commandLine(
            [
              'aws'
            ] +
              awsLogLevelArgs() +
              [
                'ec2', 'describe-images',
                '--region', region
              ] +
              (owners.size() > 0 ? ['--owners'] + owners : (List<String>)[]) +
              [
                '--filters', JsonOutput.toJson(filters.collectEntries { key, values -> ['Name': key, 'Values': values] }).replace('"', OperatingSystem.current().windows ? '\\"' : '"'),
                '--output', 'json'
              ]
          )
          standardOutput = os
        }
      }
      ((List<Map<String, Object>>)new JsonSlurper().parseText(os.toString())['Images']).sort { Map<String, Object> a, b -> ((Date)b['CreationDate']) <=> ((Date)a['CreationDate']) }
    }
  }
  @SuppressWarnings(['BusyWait', 'UnnecessaryObjectReferences'])
  void processTemplate(String fileName, Task parentTask = null) {
    project.logger.info(sprintf('gradle-packer-plugin: Processing %s template', [fileName]))
    File templateFile = project.file(fileName)
    Map<String, Object> inputJSON = (Map<String, Object>)new JsonSlurper().parse(templateFile)
    Map<String, Object> templateData = (Map<String, Object>)[
      'pwd': (Object)project.file('.').canonicalPath,
      'template_dir': templateFile.parentFile.absolutePath,
      'timestamp': initTime.time.intdiv(1000),
    ]
    List<String> customVariablesCmdLine = []
    if (inputJSON.containsKey('variables')) {
      for (Map.Entry variable in (Map<String, Object>)inputJSON['variables']) {
        if (customVariables[variable.key]) {
          templateData.put "user `${ variable.key }`".toString(), customVariables[variable.key]
          customVariablesCmdLine.push '-var'
          customVariablesCmdLine.push "${ variable.key }=${ customVariables[variable.key] }".toString()
        } else {
          templateData["user `${ variable.key }`".toString()] = variable.value
        }
      }
    }
    String imageName = templateData['user `name`'] ?: FilenameUtils.getBaseName(fileName)
    Task validate = project.task([type: Exec], "validate-$imageName") { Exec task ->
      task.with {
        group = 'Validate'
        inputs.file templateFile
        commandLine(
          [
            'packer',
            'validate', '-syntax-only'
          ] +
          customVariablesCmdLine +
          [
            fileName
          ]
        )
      }
    }
    project.tasks.getByName('validate').dependsOn validate
    Map<String, Task> ts = [:]
    for (Map<String, Object> builder in (List<Map<String, Object>>)inputJSON['builders']) {
      String builderType = (String)builder['type']
      String buildName = (String)builder['name'] ?: builderType
      String fullBuildName = "$imageName-$buildName"
      Task t = project.task("build-$fullBuildName") { Task task ->
        task.with {
          group = 'Build'
            extensions.extraProperties['builderType'] = builderType
            extensions.extraProperties['buildName'] = buildName
            extensions.extraProperties['fullBuildName'] = fullBuildName
            extensions.extraProperties['cleanTask'] = project.task("clean-$fullBuildName") { Task cleanTask ->
            cleanTask.group = 'Clean'
            cleanTask.shouldRunAfter validate
          }
          extensions.extraProperties['contextTemplateData'] = new HashMap(templateData)
          extensions.extraProperties['uuid'] = uuidGenerator.generate()
          extensions.extraProperties['contextTemplateData']['build_name'] = buildName
          extensions.extraProperties['contextTemplateData']['build_type'] = builderType
          extensions.extraProperties['contextTemplateData']['uuid'] = extensions.extraProperties['uuid']
          shouldRunAfter validate
          mustRunAfter extensions.extraProperties['cleanTask']
          inputs.file templateFile
          inputs.property 'customVariablesCmdLine', customVariablesCmdLine
          extensions.extraProperties['inputProperties'] = [:]
          extensions.extraProperties['upToDateWhen'] = []
          doLast {
            project.exec { ExecSpec execSpec ->
              execSpec.commandLine(
                [
                  'packer',
                  'build',
                  "-only=$buildName".toString()
                ] +
                customVariablesCmdLine +
                packerLogLevelArgs() +
                [
                  fileName
                ]
              )
            }
          }
        }
      }

      // VirtualBox ISO & OVF builders
      // if (builderType  == 'virtualbox-iso') {
      // As of 2016-11-02 only file: URLs are supported by Gradle
      // See: https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#files%28java.lang.Object...%29
      // But, still, 1) this should work in the future 2) most probably, there is no reason to check ISO change if there is known checksum
      // if (builder.containsKey('iso_url'))
      //   t.inputs.file parseString(builder['iso_url'], t.contextTemplateData)
      // else if (builder.containsKey('iso_urls'))
      //   for (iso_url in builder['iso_urls'])
      //     t.inputs.file parseString(iso_url, t.contextTemplateData)
      // }
      if (builder['type'] == 'virtualbox-ovf') {
        t.inputs.file parseString((String)builder['source_path'], (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
      }
      if (builderType == 'virtualbox-iso' || builderType == 'virtualbox-ovf') {
        if (builder.containsKey('floppy_files')) {
          for (String floppy_file in builder['floppy_files']) {
            t.inputs.file parseString(floppy_file, (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
          }
        }
        if (builder.containsKey('http_directory') && builder.containsKey('boot_command')) {
          Matcher m = HTTP_FILENAME_PATTERN.matcher(parseString(builder['boot_command'].toString(), (Map<String, String>)t.extensions.extraProperties['contextTemplateData'] + ['.HTTPIP': '{{ .HTTPIP }}', '.HTTPPort': '{{ .HTTPPort }}']))
          if (m.find() && m.group(1) != '') {
            t.inputs.file project.file(new File(parseString((String)builder['http_directory'], (Map<String, String>)t.extensions.extraProperties['contextTemplateData']), m.group(1)))
          }
        }
        // ISO URL
        // if (builder.containsKey('guest_addition_url'))
        //   t.inputs.file parseString(builder['guest_addition_url'], t.contextTemplateData)
        if (builder.containsKey('ssh_key_path')) {
          t.inputs.file parseString((String) builder['ssh_key_path'], (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
        }

        String outputDir = parseString((String)(builder['output_directory'] ?: "output-$buildName"), (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
        t.extensions.extraProperties['VMName'] = parseString((String)(builder['vm_name'] ?: "packer-$buildName"), (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
        // Output filename when no post-processors are used - default
        t.extensions.extraProperties['outputFileName'] = new File(outputDir, "${ t.extensions.extraProperties['VMName'] }.${ (builder['format'] ?: 'ovf') }").toString()
        project.logger.info(sprintf('gradle-packer-plugin: outputFileName %s', [t.extensions.extraProperties['outputFileName']]))
        Task powerOffVM = project.task([type: Exec], "powerOff-$fullBuildName") { Exec task ->
          task.with {
            commandLine([
              'VBoxManage',
              'controlvm',
              t.extensions.extraProperties['VMName'],
              'poweroff'
            ])
            ignoreExitValue = true
          }
        }
        Task unregisterVM = project.task([type: Exec], "unregisterVM-$fullBuildName") { Exec task ->
          task.with {
            dependsOn powerOffVM
            commandLine([
              'VBoxManage',
              'unregistervm',
              t.extensions.extraProperties['VMName'],
              '--delete'
            ])
            ignoreExitValue = true
          }
        }
        Task deleteOutputDir = project.task([type: Delete], "deleteOutputDir-$fullBuildName") { Delete task ->
          task.with {
            dependsOn unregisterVM
            delete outputDir
          }
        }
        ((Task)t.extensions.extraProperties['cleanTask']).dependsOn deleteOutputDir
      }

      // Amazon EBS builder
      if (builderType == 'amazon-ebs') {
        t.extensions.extraProperties['awsEnvironment'] = new HashMap<String, String>([
          'AWS_ACCESS_KEY_ID': parseString((String)builder['access_key'], (Map<String, String>)t.extensions.extraProperties['contextTemplateData']),
          'AWS_SECRET_ACCESS_KEY': parseString((String)builder['secret_key'], (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
        ])
        Map<String, Object> filters
        List<String> owners = []
        boolean mostRecent = false
        if (builder.containsKey('source_ami')) {
          filters = ['image-id': (Object)[parseString((String)builder['source_ami'], (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])]]
        } else {
          filters = (Map<String, Object>)((Map<String, Object>)builder['source_ami_filter']['filters']).collectEntries { key, values -> [(key): List.isInstance(values) ? ((List<String>)values).collect { [parseString(it, (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])] } : [String.isInstance(values) ? parseString((String)values, (Map<String, String>)t.extensions.extraProperties['contextTemplateData']) : values]] }
          owners = ((List<String>)(builder['source_ami_filter']['owners'] ?: [])).collect { parseString(it, (Map<String, String>)t.extensions.extraProperties['contextTemplateData']) }
          mostRecent = builder['source_ami_filter']['most_recent'] ?: false
        }
        ((Map<String, Closure>)t.extensions.extraProperties['inputProperties'])['sourceAMI'] = {
          List res = findAMI(
            (Map<String, Object>)t.extensions.extraProperties['awsEnvironment'],
            parseString((String)builder['region'], (Map<String, String>)t.extensions.extraProperties['contextTemplateData']),
            owners,
            filters
          )
          if (res.size() > 1 && mostRecent) {
            res = [res[0]]
          }
          project.logger.info(sprintf('gradle-packer-plugin: sourceAMI value %s', [JsonOutput.toJson(res)]))
          JsonOutput.toJson(res)
        }

        t.extensions.extraProperties['amiName'] = parseString((String)builder['ami_name'], (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
        Map awsContextTemplateData = new HashMap((Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
        awsContextTemplateData['timestamp'] = '*'
        awsContextTemplateData['uuid'] = '*'
        String amiNameForUpToDate = parseString((String)builder['ami_name'], awsContextTemplateData)
        if (!builder.containsKey('ami_regions')) {
          builder['ami_regions'] = []
        }
        builder['ami_regions'] = [builder['region']] + builder['ami_regions']
        for (String region in builder['ami_regions']) {
          region = parseString(region, (Map<String, String>)t.extensions.extraProperties['contextTemplateData'])
          ((List<Closure<Boolean>>)t.extensions.extraProperties['upToDateWhen']).push {
            t.extensions.extraProperties['outputAMI'] = findAMI(
              (Map<String, Object>)t.extensions.extraProperties['awsEnvironment'],
              region,
              ['self'],
              ['name': (Object)[amiNameForUpToDate]]
            )[0]
            t.extensions.extraProperties['outputAMI'] != null
          }
          Task unregisterImage = project.task("unregisterImage-$fullBuildName-$region") { Task task ->
            task.with {
              onlyIf {
                extensions.extraProperties['AMI'] = (findAMI(
                  (Map<String, Object>)t.extensions.extraProperties['awsEnvironment'],
                  region,
                  ['self'],
                  ['name': (Object)[t.extensions.extraProperties['amiName']]]
                )[0] ?: [:])['ImageId']
                extensions.extraProperties['AMI'] != null
              }
              doLast {
                project.exec { ExecSpec execSpec ->
                  execSpec.environment << (Map<String, Object>)t.extensions.extraProperties['awsEnvironment']
                  execSpec.commandLine(
                    [
                      'aws'
                    ] +
                    awsLogLevelArgs() +
                    [
                      'ec2', 'deregister-image',
                      '--region', region,
                      '--image-id', (String)extensions.extraProperties['AMI']
                    ]
                  )
                }
              }
            }
          }
          Task waitForUnregisterImage = project.task("waitForUnregisterImage-$fullBuildName-$region") { Task task ->
            task.with {
              onlyIf {
                findAMI(
                  (Map<String, Object>)t.extensions.extraProperties['awsEnvironment'],
                  region,
                  ['self'],
                  ['name': (Object)[t.extensions.extraProperties['amiName']]]
                ).size() > 0
              }
              dependsOn unregisterImage
              doLast {
                while (findAMI(
                  (Map<String, Object>)t.extensions.extraProperties['awsEnvironment'],
                  region,
                  (List<String>)['self'],
                  ['name': (Object)[t.extensions.extraProperties['amiName']]]
                ).size() > 0) {
                  TimeUnit.SECONDS.sleep(10)
                }
              }
            }
          }
          ((Task)t.extensions.extraProperties['cleanTask']).dependsOn waitForUnregisterImage
        }
        if (builder.containsKey('ssh_private_key_file')) {
          t.inputs.file parseString((String)builder['ssh_private_key_file'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
        }
        if (builder.containsKey('user_data_file')) {
          t.inputs.file parseString((String)builder['user_data_file'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
        }
      }

      ts[buildName] = t
      if (parentTask) {
        parentTask.dependsOn t
      }
    }
    Map<String, Task> processedTasks

    if (inputJSON.containsKey('provisioners')) {
      for (Map<String, Object> p in (List<Map<String, Object>>)inputJSON['provisioners']) {
        if (p.containsKey('only')) {
          processedTasks = [:]
          for (String buildName in p['only']) {
            processedTasks[buildName] = ts[buildName]
          }
        } else {
          processedTasks = new HashMap(ts)
          if (p.containsKey('except')) {
            for (buildName in p['except']) {
              processedTasks.remove buildName
            }
          }
        }
        for (Task t in processedTasks.values()) {
          Map provisioner = new HashMap(p)
          if (provisioner.containsKey('override')) {
            for (Map.Entry<String, String> override in (Map<String, String>)provisioner['override'][(String)t.extensions.extraProperties['buildName']]) {
              provisioner[override.key] = override.value
            }
          }
          provisioner.remove 'override'

          // Shell provisioner
          if (provisioner['type'] == 'shell') {
            if (provisioner.containsKey('script')) {
              t.inputs.file parseString((String)provisioner['script'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
            } else {
              if (provisioner.containsKey('scripts')) {
                for (String script in provisioner['scripts']) {
                  t.inputs.file parseString(script, (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
                }
              }
            }
          }

          // Chef solo provisioner
          if (provisioner['type'] == 'chef-solo') {
            if (provisioner.containsKey('config_template')) {
              t.inputs.file parseString((String)provisioner['config_template'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
            }
            if (provisioner.containsKey('cookbook_paths')) {
              for (String cookbook_path in provisioner['cookbook_paths']) {
                t.inputs.dir parseString(cookbook_path, (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
              }
            }
            if (provisioner.containsKey('data_bags_path')) {
              t.inputs.dir parseString((String)provisioner['data_bags_path'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
            }
            if (provisioner.containsKey('encrypted_data_bag_secret_path')) {
              t.inputs.file parseString((String)provisioner['encrypted_data_bag_secret_path'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
            }
            if (provisioner.containsKey('environments_path')) {
              t.inputs.dir parseString((String)provisioner['environments_path'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
            }
            if (provisioner.containsKey('roles_path')) {
              t.inputs.dir parseString((String)provisioner['roles_path'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
            }
          }
        }
      }
    }
    if (inputJSON.containsKey('post-processors')) {
      Closure processPostProcessors = { Map<String, Object>  p ->
        if (p.containsKey('only')) {
          processedTasks = [:]
          for (String buildName in p['only']) {
            processedTasks[buildName] = ts[buildName]
          }
        } else {
          processedTasks = new HashMap(ts)
          if (p.containsKey('except')) {
            for (String buildName in p['except']) {
              processedTasks.remove buildName
            }
          }
        }
        for (Task t in processedTasks.values()) {
          Map postProcessor = new HashMap(p)

          // Vagrant post-processor
          // Update: 2015-06-16
          if (postProcessor['type'] == 'vagrant') {
            for (Map<String, String> override in (List<Map<String, String>>)postProcessor['override']) {
              postProcessor[override.key] = override.value
            }
            postProcessor.remove 'override'

            if (postProcessor.containsKey('vagrantfile_template')) {
              t.inputs.file parseString((String)postProcessor['vagrantfile_template'], (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
            }
            if (postProcessor.containsKey('include')) {
              for (String include in postProcessor['include']) {
                t.inputs.file parseString(include, (Map<String, String>)t.extensions.extraProperties['contextTeplateData'])
              }
            }
            String vagrantProvider
            switch (t.extensions.extraProperties['builderType']) {
              case 'virtualbox-iso':
              case 'virtualbox-ovf':
                vagrantProvider = 'virtualbox'
                break
              case 'amazon-ebs':
                vagrantProvider = 'aws'
                break
            }
            t.extensions.extraProperties['outputFileName'] = parseString((String)postProcessor['output'] ?: 'packer_{{.BuildName}}_{{.Provider}}.box', (Map<String, String>)t.extensions.extraProperties['contextTeplateData'] + ['.Provider': vagrantProvider, '.ArtifactId': vagrantProvider, '.BuildName': (String)t.extensions.extraProperties['buildName']])
            project.logger.info(sprintf('gradle-packer-plugin: outputFileName %s', [t.extensions.extraProperties['outputFileName']]))
            ((Task)t.extensions.extraProperties['cleanTask']).dependsOn project.task([type: Delete], "deleteOutputFile-${t.extensions.extraProperties['fullBuildName']}") { Delete task ->
              task.delete t.extensions.extraProperties['outputFileName']
            }
          }
        }
      }
      for (p in inputJSON['post-processors']) {
        if (String.isInstance(p)) { continue }
        if (List.isInstance(p)) {
          for (Map<String, Object> p2 in (List<Map<String, Object>>)p) { processPostProcessors.call(p2) }
        } else {
          processPostProcessors.call((Map<String, Object>)p)
        }
      }
    }
    Task commonT = project.task("build-$imageName") { Task task ->
      task.with {
        group = 'Build'
        extensions.extraProperties['cleanTask'] = project.task("clean-$imageName") { Task cleanTask ->
          cleanTask.group = 'Clean'
          cleanTask.shouldRunAfter validate
        }
        shouldRunAfter validate
        mustRunAfter extensions.extraProperties['cleanTask']
        inputs.property 'customVariablesCmdLine', customVariablesCmdLine
        doLast {
          project.exec { ExecSpec execSpec ->
            execSpec.commandLine(
              [
                'packer',
                'build',
              ] +
              customVariablesCmdLine +
              packerLogLevelArgs() +
              [
                fileName
              ]
            )
          }
        }
      }
    }
    for (Task t in ts.values()) {
      ((Task)commonT.extensions.extraProperties['cleanTask']).dependsOn t.extensions.extraProperties['cleanTask']
      for (i in t.inputs.files) {
        commonT.inputs.file i
      }
      for (i in (Map<String, Closure>)t.extensions.extraProperties['inputProperties']) {
        t.inputs.property i.key, i.value
        commonT.inputs.property "${t.extensions.extraProperties['buildName']}-${i.key}", i.value
      }
      for (File o in t.outputs.files) {
        commonT.outputs.file o
      }
      Closure u = {
        if (((List<Closure<Boolean>>)t.extensions.extraProperties['upToDateWhen']).size() > 0) {
          ((List<Closure<Boolean>>)t.extensions.extraProperties['upToDateWhen']).every { it.call() }
        } else {
          true
        }
      }
      t.outputs.upToDateWhen u
      commonT.outputs.upToDateWhen u
      if (t.extensions.extraProperties.has('outputFileName')) {
        project.logger.info(sprintf('gradle-packer-plugin: task %s has outputFileName %s', [t.name, t.extensions.extraProperties['outputFileName']]))
        t.outputs.file(t.extensions.extraProperties['outputFileName'])
      }
    }
  }

  void template(String fileName, Task parentTask = null, Closure taskConfiguration) {
    processTemplate fileName, parentTask
  }

  PackerPluginExtension(Project project) {
    super(project)
  }
}
