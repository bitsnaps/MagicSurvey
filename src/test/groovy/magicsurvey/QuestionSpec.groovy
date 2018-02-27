package magicsurvey

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Question)
class QuestionSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "create a string Question"() {
        given:
            def q = new Question()
            q.questionType = 'String'
            q.questionTitle = 'What is your name'
            q.description = 'Enter your name'
            q.required = true
        expect: "question is valid"
            q.validate()
    }

    void "create a single choice Question"() {
        given:
            def q = new Question()
            q.questionType = 'Radioboxes'
            q.questionTitle = 'How do you rate our product?'
            q.required = false
            q.choices = ['Good','OK','Bad']
        expect: "question is valid"
            q.validate()
    }
}
