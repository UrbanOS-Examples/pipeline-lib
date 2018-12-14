import spock.lang.*
import org.scos.pipeline.Terraform

class TerraformSpec extends Specification {

    def 'Terraform has basic properties' () {
      given:
        def pipelineIn = new PipelineMock()
        def envIn = 'env'
        def tf = new Terraform(pipelineIn, envIn)

      expect:
        pipelineIn == tf.pipeline
        envIn == tf.environment
    }

}
