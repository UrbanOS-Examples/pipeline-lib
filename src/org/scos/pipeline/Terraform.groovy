package org.scos.pipeline

class Terraform implements Serializable {
    def pipeline, environment
    def almDeployments = ['dev', 'staging', 'prod', 'prod-prime']

    Terraform(pipeline, environment) {
        this.pipeline = pipeline
        this.environment = environment
    }

    def outputsAsJson(String project = "operating-system") {
        def bucket_name = "scos-${almDeployments.contains(environment) ? 'alm' : 'sandbox'}-terraform-state"
        def result = pipeline.sh(
            returnStdout: true,
            script: "aws s3 cp s3://${bucket_name}/env:/${environment}/${project} -"
        ).trim()
        pipeline.readJSON(text: result).modules[0].outputs
    }

    String getDefaultVarFile() {
        pipeline.fileExists("variables/${environment}.tfvars") ?
            "variables/${environment}.tfvars" :
            "variables/sandbox.tfvars"
    }

    void init() {
        pipeline.withEnv(['TF_PLUGIN_CACHE_DIR=/efs/worker/tf-cache']) {
            pipeline.sh "${pipeline.env.WORKSPACE}/scripts/tf-init --workspace \"${environment}\" ${almDeployments.contains(environment) ? '' : '--sandbox'}"
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
        def extra_args = ['--destroy']

        if(pipeline.fileExists('variables/destroy.tfvars')) {
            extra_args << '--var-file=variables/destroy.tfvars'
        }

        plan(varFile, [:], extra_args)
    }

    void apply() {
        pipeline.sh("terraform apply ${environment}.plan")
    }

    private List getWorkspaces() {
        def workspaceStr = pipeline.sh(script: "terraform workspace list", returnStdout: true)
        workspaceStr.split('\n').collect { it.substring(2) }
    }
}
