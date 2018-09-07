package org.scos.pipeline

class DeploymentCondition {
    static final ENVIRONMENTS = ['dev', 'staging', 'prod']

    boolean isSandbox(environment) {
        !(environment in ENVIRONMENTS)
    }

    boolean isNonProdMasterBranch(branch, environment) {
        (branch == 'master' && environment != 'prod')
    }

    static boolean isRelease(String tag) {
        (tag =~ /^\d+\.\d+\.\d+$/).matches()
    }

    static boolean isHotfix(String ref) {
        (ref =~ /^hotfix\/.*$/).matches()
    }

    boolean shouldDeploy(environment, branch) {
        isSandbox(environment) || isNonProdMasterBranch(branch, environment) || isRelease(branch)
    }
}
