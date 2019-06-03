package org.scos.pipeline

class DevDeployTrigger {
    static def devDeployTrigger(pipeline, projectName) {
        pipeline.jobDsl(scriptText: """
            job("${projectName}-dev-trigger") {
                triggers {
                    urlTrigger {
                        cron("*/5 * * * *")
                        url("https://hub.docker.com/v2/repositories/smartcitiesdata/${projectName}/tags/latest/") {
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
