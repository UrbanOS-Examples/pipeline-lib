package org.scos.pipeline
import groovy.json.JsonSlurper

class ECRepository {
    static String fetchAccountId() {
        def almAccountId = '199837183662'

        try {
            def metadataURL = "http://169.254.169.254/latest/dynamic/instance-identity/document"
            def responseText = new URL(metadataURL).getText()
            new JsonSlurper().parseText(responseText).get('accountId', almAccountId)
        }
        catch(Exception _) { almAccountId }

    }
    static String hostname() {
        "${ECRepository.fetchAccountId()}.dkr.ecr.us-east-2.amazonaws.com"
    }
}