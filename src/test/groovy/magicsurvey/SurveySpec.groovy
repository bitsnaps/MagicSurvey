package magicsurvey

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Survey)
class SurveySpec extends Specification{
    def setup() {
    }

    def cleanup() {
    }

    void "create a Survey"() {
        given:
            Survey s = new Survey()
            s.title = 'Survey1'
            s.introMessage = 'Welcome'
            s.endMessage = 'Thank you'
            s.skipIntro = false
        expect:"survey is valid"
            s.validate()
    }
}
