package org.scos.pipeline

class Terraform implements Serializable {
    def pipeline, environment
    def backendsMap = [dev: "alm", staging: "alm", prod: "alm"]

    Terraform(pipeline, environment) {
        this.pipeline = pipeline
        this.environment = environment
    }

    def outputsAsJson() {
        def bucket_name = "scos-${backendsMap.get(environment, 'sandbox')}-terraform-state"
        def result = pipeline.sh(
            returnStdout: true,
            script: "aws s3 cp s3://${bucket_name}/env:/${environment}/operating-system -"
            ).trim()
        pipeline.readJSON(text: result).modules[0].outputs
    }

    void init() {
        pipeline.sh 'rm -rf .terraform'
        pipeline.sh "terraform init --backend-config=../backends/${backendsMap.get(environment, 'alm-sandbox')}.conf"

        if(workspaces.contains(environment)) {
            pipeline.sh "terraform workspace select ${environment}"
        } else {
            pipeline.sh "terraform workspace new ${environment}"
        }
    }

    void plan(Map extra_variables = [:]) {
        def varFile = pipeline.fileExists("variables/${environment}.tfvars") ?
            "variables/${environment}.tfvars" :
            "variables/sandbox.tfvars"

        String planCommand = "terraform plan "

        extra_variables.each { key, value ->
            planCommand += "--var=${key}='${value}' "
        }

        planCommand += "--var-file=${varFile} "
        planCommand += "--out=${environment}.plan"

        def planOutput = pipeline.sh(returnStdout: true, script: planCommand)

        pipeline.echo(planOutput)
        pipeline.writeFile(file: "plan-${environment}.txt", text: planOutput)
    }

    void apply() {
        pipeline.sh("terraform apply ${environment}.plan")
    }

    private List getWorkspaces() {
        def workspaceStr = pipeline.sh(script: "terraform workspace list", returnStdout: true)
        workspaceStr.split('\n').collect { it.substring(2) }
    }
}
