package org.scos.pipeline
import org.scos.pipeline.Common

class Terraform implements Serializable {
    static def gatherOutputs(pipeline, env) {
        new Common(pipeline, env).withEnvTerraform {
            def result = pipeline.sh(returnStdout: true, script: 'terraform output -json').trim()
            pipeline.readJSON text: result
        }
    }
}
