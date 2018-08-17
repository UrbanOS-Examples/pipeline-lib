import spock.lang.*
import org.scos.pipeline.ECRepository

class ECRepositorySpec extends Specification {
    def mockUrl = GroovyMock(URL)
    def metadataUrl = 'http://169.254.169.254/latest/dynamic/instance-identity/document'

    def setup() {
      GroovyMock(URL, global: true)

      new URL(metadataUrl) >> mockUrl
    }

    def 'hostname when given valid response'() {
        given:
        def accountId = '12345'
        mockUrl.getText() >> /{"accountId": "${accountId}"}/

        expect:
        ECRepository.hostname() == "${accountId}.dkr.ecr.us-east-2.amazonaws.com"
    }

    def 'hostname when given invalid response'() {
        given:
        def almAccountId = '199837183662'
        mockUrl.getText() >> /{"stuff": "got some weird stuff back"}/

        expect:
        ECRepository.hostname() == "${almAccountId}.dkr.ecr.us-east-2.amazonaws.com"
    }

    def 'hostname when given http error'() {
        given:
        def almAccountId = '199837183662'
        mockUrl.getText() >> { throw new java.net.UnknownHostException(metadataUrl) }

        expect:
        ECRepository.hostname() == "${almAccountId}.dkr.ecr.us-east-2.amazonaws.com"
    }

}