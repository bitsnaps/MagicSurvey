package dz.crystalbox.magicsurvey.models

import com.androidadvance.androidsurvey.models.Question
import com.androidadvance.androidsurvey.models.SurveyProperties
import com.google.gson.*
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import com.orm.dsl.Ignore
import com.orm.dsl.Table
import com.orm.dsl.Unique
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor

import java.lang.reflect.Type

//@Canonical because of hashCode()
@ToString(includePackage = false)
@TupleConstructor
@Table
@CompileStatic
class Survey extends SurveyProperties implements Serializable{

    @Expose(serialize = false)
    Long id

    @Expose(serialize = false)
    String questions

    @Expose(serialize = false)
    Long userId


    List<Question> getListQuestions(){
        (this.questions?
                new Gson().fromJson(this.questions, new TypeToken<List<Question>>(){}.getType()):[]) as List<Question>
    }

    String setQuestionsAsJson(List listQuestions){
        List<Question> qs = []
        listQuestions.each {
            Map m = it as Map
            Question q = new Question()
            m.each { def k, def v ->
                if (q.hasProperty(k.toString()))
                    q["${k}"] = v
            }
            qs.add(q)
        }
        this.questions = new GsonBuilder().create().toJson(qs)
    }

    String toJson(){
        new JsonSlurper().parseText(new GsonBuilder().registerTypeAdapter(Survey, {Survey s, Type t, JsonSerializationContext ctx ->
            Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
            JsonObject obj = new JsonObject()
            obj.addProperty('survey_properties', g.toJson(s))
            obj.addProperty('questions', g.toJson(listQuestions) )
            return obj
        } as JsonSerializer).create().toJson(this))
    }

}
