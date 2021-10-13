package org.scos.pipeline

class DeploymentCondition {
    static final ENVIRONMENTS = ['dev', 'staging', 'prod']

    def pipeline

    DeploymentCondition(pipeline) {
        this.pipeline = pipeline
    }

    boolean getIsRelease() {
        (pipeline.env.BRANCH_NAME =~ /^\d+\.\d+\.\d+$/).matches()
    }

    boolean getIsHotfix() {
        (pipeline.env.BRANCH_NAME =~ /^hotfix\/.*$/).matches()
    }

    boolean getIsMaster() {
        pipeline.env.BRANCH_NAME == 'master' || pipeline.env.BRANCH_NAME == 'main'
    }

    boolean isSandbox(environment) {
        !(environment in ENVIRONMENTS)
    }

    boolean isNonProdMasterBranch(environment) {
        this.isMaster && environment != 'prod'
    }

    boolean shouldDeploy(environment) {
        isSandbox(environment)             ||
        isNonProdMasterBranch(environment) ||
        (isRelease && environment == 'prod')
    }
}
