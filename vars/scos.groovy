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
    docker.withRegistry("https://199837183662.dkr.ecr.us-east-2.amazonaws.com", "ecr:us-east-2:aws_jenkins_user") {
        func()
    }
}

def terraformOutput(environment) {
    def terraform = new Terraform(this, environment)
    terraform.outputsAsJson()
}