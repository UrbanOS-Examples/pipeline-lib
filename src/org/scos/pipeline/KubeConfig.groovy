package org.scos.pipeline
import org.scos.pipeline.Terraform

class KubeConfig implements Serializable {
    def pipeline, configFile, terraform

    KubeConfig(pipeline, environment) {
        this.pipeline = pipeline
        this.terraform = new Terraform(pipeline, environment)
        this.configFile = "${pipeline.env.WORKSPACE}/${environment}_kubeconfig"
    }

    def withConfig(closure) {
        def config = terraform.outputsAsJson().eks_cluster_kubeconfig.value
        pipeline.writeFile(file: configFile, text: config)
        pipeline.withEnv(["KUBECONFIG=${configFile}"]) {
            closure()
        }
    }
}