package org.scos.pipeline
import org.scos.pipeline.Terraform

class KubeConfig implements Serializable {
    def pipeline, configFile, environment

    KubeConfig(pipeline, environment) {
        this.pipeline = pipeline
        this.environment = environment
        this.configFile = "${pipeline.env.WORKSPACE}/${environment}_kubeconfig"
    }

    def withConfig(closure) {
        def outputs = Terraform.gatherOutputs(pipeline, environment)
        pipeline.sh("""echo "${outputs['eks-cluster-kubeconfig'].value}" > ${configFile}""")

        pipeline.withEnv(["KUBECONFIG=${configFile}"]) {
            closure()
        }
    }
}
