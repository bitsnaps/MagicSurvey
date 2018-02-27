package magicsurvey

class Answer {

    Date dateAnswer
    String answers
    User user

    static constraints = {
        dateAnswer()
        answers()
        user(blank: true, nullable: true)
    }
}
