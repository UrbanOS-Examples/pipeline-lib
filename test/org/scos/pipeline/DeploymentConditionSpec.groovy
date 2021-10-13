import spock.lang.*
import org.scos.pipeline.DeploymentCondition

class DeploymentConditionSpec extends Specification {
    def condition
    def releaseTag, candidateTag, hotfix, master, featureBranch

    def setup() {
        releaseTag = new DeploymentCondition([env: [BRANCH_NAME: '1.2.3']])
        candidateTag = new DeploymentCondition([env: [BRANCH_NAME: 'RC-2018.08.16.163102']])
        hotfix = new DeploymentCondition([env: [BRANCH_NAME: 'hotfix/1.0.3']])
        master = new DeploymentCondition([env: [BRANCH_NAME: 'master']])
        featureBranch = new DeploymentCondition([env: [BRANCH_NAME: 'smrt-123']])
    }

    def 'semantic version pattern is a release'() {
        expect:
        releaseTag.isRelease
    }

    def 'non-semver pattern is not a release'() {
        expect:
        !candidateTag.isRelease
        !hotfix.isRelease
        !master.isRelease
        !featureBranch.isRelease
    }

    def 'refspecs prepended with hotfix/ are hotfixes'() {
        expect:
        hotfix.isHotfix
    }

    def 'refspecs not prepended with hotfix are not hotfixes'() {
        expect:
        !releaseTag.isHotfix
        !candidateTag.isHotfix
        !master.isHotfix
        !featureBranch.isHotfix
    }

    def 'should deploy based on branch and environment'() {
        when:
        condition = new DeploymentCondition([env: [BRANCH_NAME: refspec]])

        then:
        condition.shouldDeploy(environment) == expectation

        where:
        refspec    | environment | expectation
        'branch'   | 'smrt-123'  | true
        'main'     | 'dev'       | true
        'main'     | 'staging'   | true
        'master'   | 'dev'       | true
        'master'   | 'staging'   | true
        '2.3.21'   | 'prod'      | true
        '2.3.21'   | 'dev'       | false
        '2.3.21'   | 'staging'   | false
        'branch'   | 'dev'       | false
        'branch'   | 'staging'   | false
        'main'     | 'prod'      | false
        'master'   | 'prod'      | false
        'smrt-239' | 'prod'      | false
    }
}
