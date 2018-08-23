package org.scos.pipeline

class Terraform implements Serializable {
    def pipeline, environment
    def almDeployments = ['dev', 'staging', 'prod', 'prod-prime']

    Terraform(pipeline, environment) {
        this.pipeline = pipeline
        this.environment = environment
    }

    def outputsAsJson() {
        def bucket_name = "scos-${almDeployments.contains(environment) ? 'alm' : 'sandbox'}-terraform-state"
        def result = pipeline.sh(
            returnStdout: true,
            script: "aws s3 cp s3://${bucket_name}/env:/${environment}/operating-system -"
            ).trim()
        pipeline.readJSON(text: result).modules[0].outputs
    }

    String getDefaultVarFile() {
        pipeline.fileExists("variables/${environment}.tfvars") ?
            "variables/${environment}.tfvars" :
            "variables/sandbox.tfvars"
    }

    void init() {
        pipeline.sh 'rm -rf .terraform'
        pipeline.sh "terraform init --backend-config=../backends/${almDeployments.contains(environment) ? 'alm' : 'sandbox-alm'}.conf"

        List workspaces = pipeline.sh(
            returnStdout: true,
            script: "terraform workspace list"
            ).split('\n').collect {
                it.substring(2)
            }

        if(workspaces.contains(environment)) {
            pipeline.sh "terraform workspace select ${environment}"
        } else {
            pipeline.sh "terraform workspace new ${environment}"
        }
    }

    void plan(varFile, Map extra_variables = [:], List extra_args = []) {
        String planCommand = "terraform plan "
        planCommand += "--var-file=${varFile} "

        extra_variables.each { key, value ->
            planCommand += "--var=${key}='${value}' "
        }

        extra_args.each { arg ->
            planCommand += "${arg} "
        }

        planCommand += "--out=${environment}.plan"

        def planOutput = pipeline.sh(returnStdout: true, script: planCommand)

        pipeline.echo(planOutput)
        pipeline.writeFile(file: "plan-${environment}.txt", text: planOutput)
    }

    void planDestroy(varFile) {
        this.plan(varFile, [:], ['--destroy'])
    }

    void apply() {
        pipeline.sh("terraform apply ${environment}.plan")
    }
}