package magicsurvey

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
class SurveyController {

    static scaffold = Survey
    
    def springSecurityService

//    def index() { }

    def json(){
        def list = Survey.where{
          userId == authenticatedUser.id
        }.list()
        JSON.use('deep'){
            render list as JSON
        }
    }


}
