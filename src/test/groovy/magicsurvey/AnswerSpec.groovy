package magicsurvey

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Answer)
@Mock(User)
class AnswerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "Answer is valid"() {
        given:
            def a = new Answer()
            a.answers = ''
            a.dateAnswer = new Date()
            a.user = new User(username: 'admin', password: 'admin')
        expect:"a valid Answer"
            a.validate()
    }
}
