package org.scos.pipeline

class Checkout implements Serializable {
    def pipeline

    Checkout(pipeline) {
        this.pipeline = pipeline
    }

    def doCheckout() {
        pipeline.deleteDir()
        pipeline.env.GIT_COMMIT_HASH = pipeline.checkout(pipeline.scm).GIT_COMMIT
        def repoName = pipeline.scm.getKey().replace("git https://", "")

        pipeline.sshagent(credentials: ["GitHub"]) {
            pipeline.withCredentials([pipeline.usernamePassword(credentialsId: 'jenkins-github-user', passwordVariable: 'GIT_PWD', usernameVariable: 'GIT_USER')]) {
                pipeline.sh "git remote add github https://\$GIT_USER:\$GIT_PWD@${repoName}"
                pipeline.sh "GIT_SSH_COMMAND='ssh -o StrictHostKeyChecking=no' git submodule update --init --recursive"
            }
        }
    }
}
