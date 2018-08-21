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
            script: "aws s3 cp s3://${bucket_name}/env:/${environment}/operating-system - | jq '.modules[0].outputs'"
            ).trim()
        pipeline.readJSON text: result
    }
}
