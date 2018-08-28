library(
    identifier: 'pipeline-lib@3.0.0',
    retriever: modernSCM([$class: 'GitSCMSource',
                          remote: 'https://github.com/SmartColumbusOS/pipeline-lib',
                          credentialsId: 'jenkins-github-user'])
)

node('master') {
    ansiColor('xterm') {

        scos.doCheckoutStage()

        stage('Build & Test') {
            docker.image('gradle:4.9.0-jdk8-slim').inside() {
                sh 'gradle test --no-daemon --console=rich'
            }
        }
    }
}