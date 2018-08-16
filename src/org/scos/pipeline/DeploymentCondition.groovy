package org.scos.pipeline

class DeploymentCondition {
    boolean isSandbox(environment, nonSandboxEnvs) { 
        !(environment in nonSandboxEnvs)
    }

    boolean isNonProdMasterBranch(branch, environment) {
        (branch == 'master' && environment != 'prod')
    }

    static boolean isRelease(String tag) {
        (tag =~ /^\d+\.\d+\.\d+$/).matches()
    }

    boolean shouldDeploy(environment, nonSandboxEnvs, branch) {
        isSandbox(environment, nonSandboxEnvs) || isNonProdMasterBranch(branch, environment) || isRelease(branch)
    }
}