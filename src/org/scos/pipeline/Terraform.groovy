package org.scos.pipeline

class Terraform implements Serializable {
    def pipeline, environment, backend
    def backendsMap = [dev: "alm", staging: "alm", prod: "alm"]

    Terraform(pipeline, environment) {
        this.pipeline = pipeline
        this.environment = environment
        this.backend = "${backendsMap.get(environment, 'sandbox-alm')}.conf"
    }

    def outputsAsJson() {
        cloneCommon()
        terraformInit()

        pipeline.dir('infra/env') {
            def result = pipeline.sh(returnStdout: true, script: 'terraform output -json').trim()
            pipeline.readJSON text: result
        }
    }

    private terraformInit() {
        def initScript = [
            '#!/usr/bin/env bash',
            "terraform init -backend-config=../backends/${backend}",
            "terraform workspace select ${environment} || (terraform workspace new ${environment}; terraform workspace select ${environment})"
        ].join('\n')

        pipeline.dir('infra/env') {
            pipeline.sh(initScript)
        }
    }

    private cloneCommon() {
        pipeline.checkout(changelog: false,
                        poll: false,
                        scm: [$class: 'GitSCM',
                              branches: [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions: [[$class: 'CloneOption', noTags: true, shallow: true, depth: 1],
                                           [$class: 'RelativeTargetDirectory', relativeTargetDir: 'infra'],
                                           [$class: 'CleanBeforeCheckout'],
                                           [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: 'env'], [path: 'backends'], [path: 'modules']]]],
                              userRemoteConfigs: [[credentialsId: 'jenkins-github-user', url: 'https://github.com/SmartColumbusOS/common.git']]])
    }
}
