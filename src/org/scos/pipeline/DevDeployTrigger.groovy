package org.scos.pipeline

class DevDeployTrigger {
    static def devDeployTrigger(pipeline, projectName, tag, organization = "smartcitiesdata") {
        pipeline.jobDsl(scriptText: """
            job("${projectName}-dev-trigger") {
                triggers {
                    urlTrigger {
                        cron("*/5 * * * *")
                        url("https://hub.docker.com/v2/repositories/${organization}/${projectName}/tags/${tag}/") {
                            inspection("change")
                        }
                    }
                }
                steps {
                    downstreamParameterized {
                        trigger("SmartColumbusOS/${projectName}-deploy/master") {
                            block {
                                buildStepFailure("FAILURE")
                                failure("FAILURE")
                                unstable("UNSTABLE")
                            }
                            parameters {
                                booleanParam("DEV_DEPLOYMENT", true)
                            }
                        }
                    }
                }
            }
            """.trim())
    }
}
