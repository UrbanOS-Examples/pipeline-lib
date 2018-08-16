import org.scos.pipeline.*

def withEksCredentials(environment, body) {
    def kube = new KubeConfig(this, environment)
    kube.withConfig(body)
}

def releaseCandidateNumber() {
    ReleaseNumber.candidate()
}

def isRelease(tag) {
    DeploymentCondition.isRelease(tag)
}

def shouldDeploy(environment, branch) {
    def deploymentCondition = new DeploymentCondition()
    deploymentCondition.shouldDeploy(environment, branch)
}

def environments() {
    DeploymentCondition.ENVIRONMENTS
}

def withDockerRegistry(Closure func) {
    docker.withRegistry("https://${ECRepository.url()}", "ecr:us-east-2:aws_jenkins_user") {
        func()
    }
}

def pullImageFromDockerRegistry(imageName, imageTag) {
    image = docker.image("${ECRepository.url()}/${imageName}:${imageTag}")
    image.pull()
    image
}

def terraformOutput(environment) {
    def terraform = new Terraform(this, environment)
    terraform.outputsAsJson()
}

def doStageIf(boolean truthyValue, stageName, Closure closure) {
    if (truthyValue) {
        stage(stageName, closure)
    }
}

def addGitHubRemoteForTagging(repoName) {
    withCredentials([usernamePassword(credentialsId: 'jenkins-github-user', passwordVariable: 'GIT_PWD', usernameVariable: 'GIT_USER')]) {
        sh "git remote add github https://\$GIT_USER:\$GIT_PWD@github.com/${repoName}"
    }
}

def applyAndPushGitHubTag(tag) {
    sh "git tag -f ${tag}"
    sh "git push -f github ${tag}"
}