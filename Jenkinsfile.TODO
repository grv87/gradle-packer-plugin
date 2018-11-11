#!/usr/bin/env groovy
/*
 * Jenkinsfile for gradle-packer-plugin
 * Copyright © 2018  Basil Peace
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
import org.jfrog.hudson.pipeline.types.ArtifactoryServer
import org.jfrog.hudson.pipeline.types.GradleBuild
import org.jfrog.hudson.pipeline.types.buildInfo.BuildInfo

//noinspection GroovyUnusedAssignment
@SuppressWarnings(['UnusedVariable', 'NoDef', 'VariableTypeRequired'])
@Library('jenkins-pipeline-shared-library@v1.2.0') dummy

node {
  ansiColor {
    GradleBuild rtGradle

    stage ('Checkout') {
      List<Map<String, ? extends Serializable>> extensions = [
        [$class: 'WipeWorkspace'],
        [$class: 'CloneOption', noTags: false, shallow: false],
      ]
      if (!env.CHANGE_ID) {
        extensions.add([$class: 'LocalBranch', localBranch: env.BRANCH_NAME])
      }
      checkout([
        $class: 'GitSCM',
        branches: scm.branches,
        doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,
        extensions: extensions,
        userRemoteConfigs: scm.userRemoteConfigs,
      ])
      gitAuthor()
      configureGrape('Artifactory')
    }

    ArtifactoryServer server = Artifactory.server 'FIDATA'
    rtGradle = Artifactory.newGradleBuild()
    rtGradle.useWrapper = true
    rtGradle.usesPlugin = true

    /*
     * WORKAROUND:
     * Disabling Gradle Welcome message
     * should be done in fidata_build_toolset.
     * See https://github.com/FIDATA/infrastructure/issues/85
     * <grv87 2018-09-21>
     */
    /*
     * WORKAROUND:
     * Gradle can't provide console with colors but no other rich features.
     * So, we use plain console for now
     * https://github.com/gradle/gradle/issues/6843
     * <grv87 2018-09-21>
     */
    /*
     * WORKAROUND:
     * Build cache should be turned on in gradle.properties
     * as soon as we move sensitive properties to separate place
     * and put gradle.properties under version control
     * <grv87 2018-09-22>
     */
    String gradleSwitches = '-Dorg.gradle.internal.launcher.welcomeMessageEnabled=false --no-daemon  --parallel --configure-on-demand --console=plain --info --warning-mode all --full-stacktrace --build-cache'

    withGpgScope("${ pwd() }/.scoped-gpg", 'GPG', 'GPG_KEY_PASSWORD') { String fingerprint ->
      withEnv([
        "ORG_GRADLE_PROJECT_gpgKeyId=$fingerprint",
      ]) {
        withCredentials([
          usernamePassword(credentialsId: 'Github 2', usernameVariable: 'ORG_GRADLE_PROJECT_gitUsername', passwordVariable: 'ORG_GRADLE_PROJECT_gitPassword'),
          string(credentialsId: 'Github', variable: 'ORG_GRADLE_PROJECT_ghToken'),
          usernamePassword(credentialsId: 'Artifactory', usernameVariable: 'ORG_GRADLE_PROJECT_artifactoryUser', passwordVariable: 'ORG_GRADLE_PROJECT_artifactoryPassword'),
          string(credentialsId: 'GPG_KEY_PASSWORD', variable: 'ORG_GRADLE_PROJECT_gpgKeyPassphrase'),
          usernamePassword(credentialsId: 'Bintray', usernameVariable: 'ORG_GRADLE_PROJECT_bintrayUser', passwordVariable: 'ORG_GRADLE_PROJECT_bintrayAPIKey'),
        ]) {
          BuildInfo buildInfo = null
          try {
            stage('Generate Changelog') {
              timeout(time: 5, unit: 'MINUTES') {
                buildInfo = rtGradle.run tasks: 'generateChangelog', switches: gradleSwitches, buildInfo: buildInfo
                /*
                 * TODO:
                 * Move these filters into separate library
                 * <grv87 2018-09-22>
                 */
                buildInfo.env.filter.clear()
                buildInfo.env.filter.addExclude('*Password')
                buildInfo.env.filter.addExclude('*Passphrase')
                buildInfo.env.filter.addExclude('*SecretKey')
                buildInfo.env.filter.addExclude('*SECRET_KEY')
                buildInfo.env.filter.addExclude('*APIKey')
                buildInfo.env.filter.addExclude('*_API_KEY')
                buildInfo.env.filter.addExclude('*gradlePluginsKey')
                buildInfo.env.filter.addExclude('*gradlePluginsSecret')
                buildInfo.env.filter.addExclude('*OAuthClientSecret')
                buildInfo.env.filter.addExclude('*Token')
                buildInfo.env.collect()
              }
              dir('build/changelog') {
                exec 'pandoc --from=markdown_github --to=html --output=CHANGELOG.html CHANGELOG.md'
              }
              publishHTML(target: [
                reportName: 'CHANGELOG',
                reportDir: 'build/changelog',
                reportFiles: 'CHANGELOG.html',
                allowMissing: false,
                keepAll: true,
                alwaysLinkToLastBuild: env.BRANCH_NAME == 'develop' && !env.CHANGE_ID
              ])
            }
            stage('Assemble') {
              try {
                timeout(time: 5, unit: 'MINUTES') {
                  buildInfo = rtGradle.run tasks: 'assemble', switches: gradleSwitches, buildInfo: buildInfo
                }
              } finally {
                warnings(
                  consoleParsers: [
                    [parserName: 'Java Compiler (javac)'],
                    [parserName: 'JavaDoc Tool'],
                  ]
                )
              }
            }
            stage('Check') {
              try {
                timeout(time: 5, unit: 'MINUTES') {
                  buildInfo = rtGradle.run tasks: 'check', switches: gradleSwitches, buildInfo: buildInfo
                }
              } finally {
                warnings(
                  consoleParsers: [
                    [parserName: 'Java Compiler (javac)'],
                    [parserName: 'JavaDoc Tool'],
                  ]
                )
                publishHTML(target: [
                  reportName: 'CodeNarc',
                  reportDir: 'build/reports/html/codenarc',
                  reportFiles: [
                    'main',
                    'functionalTest',
                    'integTest',
                    'buildSrc',
                  ].collect { "${ it }.html" }.join(', '), // TODO: read from directory ?
                  allowMissing: true,
                  keepAll: true,
                  alwaysLinkToLastBuild: env.BRANCH_NAME == 'develop' && !env.CHANGE_ID
                ])
                junit(
                  testResults: 'build/reports/xml/**/*.xml',
                  allowEmptyResults: true,
                  keepLongStdio: true,
                )
                publishHTML(target: [
                  reportName: 'Test',
                  reportDir: 'build/reports/html/test',
                  reportFiles: 'index.html',
                  allowMissing: true,
                  keepAll: true,
                  alwaysLinkToLastBuild: env.BRANCH_NAME == 'develop' && !env.CHANGE_ID
                ])
                publishHTML(target: [
                  reportName: 'IntegTest',
                  reportDir: 'build/reports/html/integTest',
                  reportFiles: 'index.html',
                  allowMissing: true,
                  keepAll: true,
                  alwaysLinkToLastBuild: env.BRANCH_NAME == 'develop' && !env.CHANGE_ID
                ])
                publishHTML(target: [
                  reportName: 'FunctionalTest',
                  reportDir: 'build/reports/html/functionalTest',
                  reportFiles:
                    readFile(file: '.stutter/java8.lock', encoding: 'UTF-8') // TODO: respect other Java versions
                      .split('[\r\n]+')
                      // Copy of algorithm from StutterExtension.getLockedVersions
                      .findAll { !it.startsWith('#') }
                      .collect { "${ it.trim() }/index.html" }
                      .join(', '),
                  allowMissing: true,
                  keepAll: true,
                  alwaysLinkToLastBuild: env.BRANCH_NAME == 'develop' && !env.CHANGE_ID
                ])
              }
            }
            stage('Release') {
              try {
                timeout(time: 5, unit: 'MINUTES') {
                  milestone()
                  lock('gradle-packer-plugin@gh-pages') {
                    buildInfo = rtGradle.run tasks: 'release', switches: gradleSwitches, buildInfo: buildInfo
                  }
                }
              } finally {
                warnings(
                  consoleParsers: [
                    [parserName: 'Java Compiler (javac)'],
                    [parserName: 'JavaDoc Tool'],
                  ]
                )
              }
            }
          } finally {
            server.publishBuildInfo buildInfo
          }
        }
      }
    }
  }
}
