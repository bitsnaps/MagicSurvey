package magicsurvey

import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
class QuestionController {

    static scaffold = Question

//    def index() { }
}
