package dz.crystalbox.magicsurvey.models

import com.orm.dsl.Table
import com.orm.dsl.Unique
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString(includePackage = false)
@TupleConstructor
@Table
@CompileStatic
class Answer implements Serializable {

    static String DATE_FORMAT = "MM-dd-yyyy HH:mm:ss"

    @Unique
    Long id
    Date dateAnswer
    String answers

    Map getAnswersAsMap(){
        new JsonSlurper().parseText(this.answers) as Map
    }

    String dateAnswerFmt(String fmt = DATE_FORMAT){
        this.dateAnswer.format(fmt)
    }

}
