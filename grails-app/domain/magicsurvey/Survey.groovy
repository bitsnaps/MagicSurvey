package magicsurvey

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import grails.rest.Resource

//@Resource(formats = ['json'])
class Survey implements Serializable {

    @SerializedName("title")
    @Expose
    String title
    @SerializedName("intro_message")
    @Expose
    String introMessage
    @SerializedName("end_message")
    @Expose
    String endMessage
    @SerializedName("skip_intro")
    @Expose
    Boolean skipIntro
    @SerializedName("questions")
    @Expose
    static hasMany = [questions:Question]
    @Expose(serialize=false)
    Long userId

    static constraints = {
        title()
        introMessage(blank: true, nullable: true)
        endMessage(blank: true, nullable: true)
        skipIntro(blank: true, nullable: true)
        questions(blank: true, nullable: true)
        userId(blank: true, nullable: true)
    }
}
