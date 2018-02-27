package magicsurvey

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
class AnswerController {

    static scaffold = Answer
    
    def utilService
    def springSecurityService

    String DATE_FORMAT = "MM-dd-yyyy HH:mm:ss"
    
//    def index() { }

    def json(){
        def list = []
        request.JSON.each { def dateAnswer, def jsonAnswers ->
            Answer a = new Answer()
            a.dateAnswer = Date.parse(DATE_FORMAT, dateAnswer)
            a.user = isLoggedIn()?authenticatedUser:null
            a.answers = utilService.unzipString(jsonAnswers).toString() //use JSON.parse() to parse it if you want Map
            // you'd better save them in a list then loop and save each item, this will ensure using transaction
            if (a.save(flush: true))
                list += a.id
        }
        render (['feedback':"${list.size()} answer(s) synchronized."] as JSON)
    }

}
