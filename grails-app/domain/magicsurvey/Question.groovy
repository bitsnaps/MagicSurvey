package magicsurvey

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Question implements Serializable {

    @SerializedName("question_type")
    @Expose  
    String questionType
    @SerializedName("question_title")
    @Expose
    String questionTitle
    @SerializedName("description")
    @Expose
    String description
    @SerializedName("required")
    @Expose
    Boolean required
    @SerializedName("random_choices")
    @Expose
    Boolean randomChoices

    static hasMany = [choices: String]
    @SerializedName("choices")
    @Expose
    List choices
    @SerializedName("min")
    @Expose
    Integer min
    @SerializedName("max")
    @Expose
    Integer max
    @SerializedName("number_of_lines")
    @Expose
    Integer numberOfLines    

    static constraints = {
      // you better use an enum
        questionType(inList:["String","StringMultiline","Number","Checkboxes","Radioboxes"])
        questionTitle()
        description(blank: true, nullable: true)
        required()
        randomChoices(blank: true, nullable: true)
        choices(blank: true, nullable: true)
        min(blank: true, nullable: true)
        max(blank: true, nullable: true)
        numberOfLines(blank: true, nullable: true)
    }
}
