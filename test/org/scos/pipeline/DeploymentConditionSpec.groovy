import spock.lang.*
import org.scos.pipeline.DeploymentCondition

class DeploymentConditionSpec extends Specification {
    def condition

    def setup() {
        condition = new DeploymentCondition()
    }

    def 'isRelease'() {
        expect:
        condition.isRelease('1.1.1')
    }

    def 'isNotRelease'(){
        expect:
        !condition.isRelease('sj.1.x')
    }

    def 'releaseCandidatesAreNotReleases'() {
        expect:
        !condition.isRelease('RC-2018.08.16.163102')
    }

    def 'hotfix is not a release'() {
        expect:
        !condition.isRelease('hotfix/1.0.3')
    }

    def 'hotfix branch isHotfix'() {
        expect:
        condition.isHotfix('hotfix/1.0.3')
    }

    def 'non-hotfix branch is not hotfix'() {
        expect:
        !condition.isHotfix('master')
    }

    def 'release tags are not hotfixes'() {
        expect:
        !condition.isHotfix('1.0.3')
    }

    def 'release candidate tags are not hotfixes'() {
        expect:
        !condition.isHotfix('RC-2018.08.16.163102')
    }

    def 'should deploy sandbox'() {
        expect:
        condition.shouldDeploy('smrt-328', 'some_branch')
    }

    def 'should deploy dev master branch'() {
        expect:
        condition.shouldDeploy('dev', 'master')
    }

    def 'should deploy staging master branch'() {
        expect:
        condition.shouldDeploy('staging', 'master')
    }

    def 'should deploy prod release tags'() {
        expect:
        condition.shouldDeploy('prod', '2.3.21')
    }

    def 'should not deploy dev feature branches'() {
        expect:
        !condition.shouldDeploy('dev', 'some_branch')
    }

    def 'should not deploy staging feature branches'() {
        expect:
        !condition.shouldDeploy('staging', 'some_branch')
    }

    def 'should not deploy prod master branch'() {
        expect:
        !condition.shouldDeploy('prod', 'master')
    }

    def 'should not deploy prod feature branches'() {
        expect:
        !condition.shouldDeploy('prod', 'smrt-329-some_branch')
    }
}
