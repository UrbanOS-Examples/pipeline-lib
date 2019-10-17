import spock.lang.*
import org.scos.pipeline.DevDeployTrigger

class DevDeployTriggerSpec extends Specification {
    def mockPipeline;

    def setup() {
        mockPipeline = new PipelineMock();
    }
    def 'Evaluates that a new DevDeployTrigger call with default organization' () {
        given:
        String standardRelease = (DevDeployTrigger.devDeployTrigger(mockPipeline, "proj", "tag"))

        expect:
        standardRelease.contains("proj-dev-trigger")
        standardRelease.contains("https://hub.docker.com/v2/repositories/smartcitiesdata/proj/tags/tag/")
    }
    def 'Evaluates that a new DevDeployTrigger call with a passed in organizaton' () {
        given:
        String newOrgRelease = (DevDeployTrigger.devDeployTrigger(mockPipeline, "proj", "tag", "neworg"))

        expect:
        newOrgRelease.contains("proj-dev-trigger")
        newOrgRelease.contains("https://hub.docker.com/v2/repositories/neworg/proj/tags/tag/")
    }

}
