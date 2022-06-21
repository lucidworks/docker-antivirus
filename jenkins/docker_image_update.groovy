pipeline {
    agent { node { label  'fusionBuildWorker' } }
    options{
      buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '14', numToKeepStr: '28'))
      timestamps()
      disableConcurrentBuilds()
    }
    triggers {
      cron('H 7 * * *')
    }
    environment {
      // The largest duration of the build before an alert is sent to the slow
      // builds channel for investigation, set in milliseconds
      ALERT_DURATION='600000'
      SLOW_SLACK_CHANNEL='#fusion-slow-builds'

      SLACK_CHANNEL='#build-infra-alerts'

      // GitHub details
      GITHUB_BASE_URL = "${env.GIT_URL - ~/\.git$/}" // e.g. https://github.com/lucidworks/app-studio
      GITHUB_REPO_NAME = "${env.GITHUB_BASE_URL - ~/^https:\/\/github.com\//}" // e.g. org/repo

       // URL to GitHub PR page or current commit
      GITHUB_COMMIT_URL = "${env.GIT_BRANCH =~/^PR.*merge$/ ?\
          "${env.GITHUB_BASE_URL}/pull/${env.GIT_BRANCH - ~/^PR-/ - ~/-merge/}" :\
          "${env.GITHUB_BASE_URL}/commit/${env.GIT_COMMIT}"}"

      // URL to GitHub PR page or tree of branch
      GITHUB_BRANCH_URL = "${env.GIT_BRANCH =~/^PR-/ ?\
          "${env.GITHUB_BASE_URL}/pull/${env.GIT_BRANCH - ~/^PR-/ - ~/-head/ - ~/-merge/}" :\
          "${env.GITHUB_BASE_URL}/tree/${env.GIT_BRANCH}"}"

      GITHUB_BUILD_TYPE = "${env.GIT_BRANCH =~/^PR-/ ?\
          "PR" :\
          "branch"}"
      // Git details
      GIT_SHORT_HASH = "${env.GIT_COMMIT.take(8)}"

      // Slack message colours
      COLOUR_STARTED = '#2881c9'
      COLOUR_PASSED  = '#00ae42'
      COLOUR_FAILED  = '#ff0000'

    }
    stages {
        stage("Build updated docker image") {
            steps {
                script {
                    withCredentials([
                      [$class: 'UsernamePasswordMultiBinding', credentialsId: 'ARTIFACTORY_JENKINS' , usernameVariable: 'ORG_GRADLE_PROJECT_lucidArtifactoryUsername', passwordVariable: 'ORG_GRADLE_PROJECT_lucidArtifactoryPassword']
                    ]){
                          docker.withRegistry('https://fusion-dev-docker.ci-artifactory.lucidworks.com', 'ARTIFACTORY_JENKINS') {
                            sh """
                              docker build -t fusion-dev-docker.ci-artifactory.lucidworks.com/docker-antivirus .
                            """
                            def dockerAntivirus = docker.image("fusion-dev-docker.ci-artifactory.lucidworks.com/docker-antivirus:latest")
                            dockerAntivirus.push("latest")
                          }
                          sh """
                            docker rmi fusion-dev-docker.ci-artifactory.lucidworks.com/docker-antivirus:latest
                          """
                    }
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
        success {
            slackSend (message: "Today's version of the docker-antivirus docker image is now available", \
                channel: env.SLACK_CHANNEL, color: env.COLOUR_PASSED)
        }
        failure {
            slackSend (message: "Today's version of the docker-antivirus docker image failed to build", \
                channel: env.SLACK_CHANNEL, color: env.COLOUR_FAILED)
        }
    }
}
