import org.scos.pipeline.KubeConfig

def onKubernetes(environment, body) {
    def kube = new KubeConfig(this, environment)
    kube.retrieve()
    kube.execute(body)
}
