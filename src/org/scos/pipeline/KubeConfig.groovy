package org.scos.pipeline

class KubeConfig implements Serializable {
    def script, environment, configFile

    KubeConfig(script, environment) {
        this.script = script
        this.environment = environment
        this.configFile = "${script.env.WORKSPACE}/${environment}_kubeconfig"
    }

    def retrieve() {
        cloneEnvInfra()
        terraformInit()

        script.dir('infra/env') {
            script.sh("terraform output eks-cluster-kubeconfig > ${configFile}")
        }
    }

    def execute(closure) {
        script.withEnv(["KUBECONFIG=${configFile}"]) {
            closure()
        }
    }

    private terraformInit() {
        def initScript = [
            '#!/usr/bin/env bash',
            'terraform init -backend-config=../backends/alm.conf',
            "terraform workspace select ${environment} || (terraform workspace new ${environment}; terraform workspace select ${environment})"
        ].join('\n')

        script.dir('infra/env') {
            script.sh(initScript)
        }
    }

    private cloneEnvInfra() {
        script.checkout(changelog: false,
                        poll: false,
                        scm: [$class: 'GitSCM',
                              branches: [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions: [[$class: 'CloneOption', noTags: true, shallow: true, depth: 2],
                                           [$class: 'RelativeTargetDirectory', relativeTargetDir: 'infra'],
                                           [$class: 'CleanBeforeCheckout'],
                                           [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: 'env'], [path: 'backends'], [path: 'modules']]]],
                              userRemoteConfigs: [[credentialsId: 'jenkins-github-user', url: 'https://github.com/SmartColumbusOS/common.git']]])
    }
}
