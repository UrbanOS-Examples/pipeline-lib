import org.scos.pipeline.*

def withEksCredentials(environment, body) {
    def kube = new KubeConfig(this, environment)
    kube.withConfig(body)
}

def releaseCandidateNumber() {
    ReleaseNumber.candidate()
}

def getChangeset() {
    new DeploymentCondition(this)
}

def environments() {
    DeploymentCondition.ENVIRONMENTS
}

def withDockerRegistry(Closure func) {
    docker.withRegistry("https://${ECRepository.hostname()}", "ecr:us-east-2:aws_jenkins_user") {
        func()
    }
}

def pullImageFromDockerRegistry(imageName, imageTag) {
    image = docker.image("${ECRepository.hostname()}/${imageName}:${imageTag}")
    image.pull()
    image
}

def getEcrHostname() {
    ECRepository.hostname()
}

def terraform(environment) {
    new Terraform(this, environment)
}

def terraformOutput(environment, project = "operating-system") {
    def terraform = new Terraform(this, environment)
    terraform.outputsAsJson(project)
}

def doStageIf(boolean truthyValue, stageName, Closure closure) {
    if (truthyValue) {
        stage(stageName, closure)
    }
}

def applyAndPushGitHubTag(tag) {
    sh "git tag -f ${tag}"
    sh "git push -f github ${tag}"
}

def doCheckoutStage() {
    stage('Checkout') {
        new Checkout(this).doCheckout()
    }
}

/** Cron Trigger for building daily between midnight and 7 am
 * Ex:
 * properties([
 *   pipelineTriggers([scos.dailyBuildTrigger()]),
 * ])
 */
def dailyBuildTrigger() {
    def cronSchedule = env.BRANCH_NAME == 'master' ? 'H H(0-6) * * *' : ''
    cron(cronSchedule)
}