node('master') {
    ansiColor('xterm') {

        stage('Checkout') {
            deleteDir()
            env.GIT_COMMIT_HASH = checkout(scm).GIT_COMMIT

            withCredentials([usernamePassword(credentialsId: 'jenkins-github-user', passwordVariable: 'GIT_PWD', usernameVariable: 'GIT_USER')]) {
                sh 'git remote add github https://$GIT_USER:$GIT_PWD@github.com/SmartColumbusOS/pipeline-lib.git'
            }
        }

        stage('Build & Test') {
            docker.image('gradle:4.9.0-jdk8-slim').inside() {
                sh 'gradle test --no-daemon --console=rich'
            }
        }
    }
}