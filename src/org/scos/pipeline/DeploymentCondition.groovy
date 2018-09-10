package org.scos.pipeline

class DeploymentCondition {
    static final ENVIRONMENTS = ['dev', 'staging', 'prod']

    def refspec

    DeploymentCondition(refspec) {
        this.refspec = refspec
    }

    boolean getIsRelease() {
        (refspec =~ /^\d+\.\d+\.\d+$/).matches()
    }

    boolean getIsHotfix() {
        (refspec =~ /^hotfix\/.*$/).matches()
    }

    boolean isSandbox(environment) {
        !(environment in ENVIRONMENTS)
    }

    boolean isNonProdMasterBranch(environment) {
        (refspec == 'master' && environment != 'prod')
    }

    boolean shouldDeploy(environment) {
        isSandbox(environment)             ||
        isNonProdMasterBranch(environment) ||
        (isRelease && environment == 'prod')
    }
}
