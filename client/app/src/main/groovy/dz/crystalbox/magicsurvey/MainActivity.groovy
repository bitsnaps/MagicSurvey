package dz.crystalbox.magicsurvey

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import com.android.volley.Request
import com.android.volley.error.AuthFailureError
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.androidadvance.androidsurvey.SurveyActivity
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnClick
import com.arasthel.swissknife.annotations.SaveInstance
import com.arasthel.swissknife.dsl.components.GArrayAdapter
import com.orm.SugarRecord
import dz.crystalbox.magicsurvey.models.Answer
import dz.crystalbox.magicsurvey.models.Survey
import dz.crystalbox.magicsurvey.models.User
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

import static dz.crystalbox.magicsurvey.ContextMethods.alert

@CompileStatic
class MainActivity extends AppCompatActivity {

    static final boolean DEBUG = true

    static final String HOST_SERVER = "192.168.1.8:8080"
    static final int LOGIN_RESULT = 1
    static final String SURVEYS_URL = "http://"+HOST_SERVER+"/survey/json"
    static final String ANSWERS_URL = "http://"+HOST_SERVER+"/answer/json"
    static final int SURVEY_REQUEST = 1337

    @InjectView(R.id.listSurvey)
    ListView listSurvey

    SugarRecord sugar

    GArrayAdapter adapter

    List<Survey> surveys

    UtilService utilService

    @SaveInstance
    User user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // SwissKnife setup
        SwissKnife.inject(this)
        SwissKnife.restoreState(this, savedInstanceState)

        // Launch LoginActivity
        if (!user) {
            startActivityForResult(intent(LoginActivity), LOGIN_RESULT)
        }

        //  Initialize SugarORM
        sugar = new SugarRecord()

        // Initialize UtilService
        utilService = new UtilService()

        long nbSurveys = sugar.count(Survey)
        /*/ Adding some surveys
        if (!nbSurveys)
        try {
            Survey s = new Survey()
            s.title = "Survey #${nbSurveys+1}"
            s.introMessage = 'Welcome'
            s.endMessage = 'Thank you'
            s.skipIntro = false
            s.userId = 1
            s.questions = '''
[{
   description:"Enter your name",
   number_of_lines:1,
   question_title:"What is your name",
   question_type:"String",
   required:true
},
{
   "description":"Enter your age",
   "question_title":"How old are you?",
   "question_type":"Number",
   "required":false
},
{
  "question_type": "Radioboxes",
  "question_title": "Select your gender",
  "description": "",
  "required": true,
  "random_choices": true,
  "choices": [ "Male", "Female" ]
},
{
   "choices":["Search Engine", "Github", "Social Network"],
   "question_title":"How did you find us?",
   "question_type":"Checkboxes",
   "random_choices": false,
   "required":false
}]'''
            if (sugar.save(s)){
                log("Survey saved.")
            } else {
                log("Survey cannot be saved.")
            }

        } catch (Exception e){
            log("An error has occurred: ${e.message}")
        }*/

        // List all surveys
        surveys = sugar.listAll(Survey)

        // Query by id
//        Survey s = sugar.findById(Survey, 1)

        // Raw Query
//        List<Survey> surveys = sugar.findWithQuery(Survey, "select * from survey where title = ?", "Survey1")

        // Query Builder
//        Select from Survey where(Condition prop 'title' eq 'Survey1').list()

        log("No of surveys: ${surveys.size()}")

        // Set a custom layout for ListView
        adapter = listSurvey.onItem(R.layout.list_items, surveys){ Survey survey, View view, int p ->
            view.onClick {
                List q = survey.getListQuestions()
                if (q)
                    toast("${survey.title}: <${q.size()} Question(s)>").show()
                else
                    log("No question ${survey.questions}")
            }
            view.text(R.id.item_name){
                it.text = "${survey.id}: ${survey.title}"
            }
            view.button(R.id.btn_delete) {
                // Only users with ROLE_ADMIN can delete
                it.visibility = user.hasRole(User.ROLE.ROLE_ADMIN)? View.VISIBLE:View.GONE
                it.onClick {
                    alert(this) {
                        title = 'Confirm delete'
                        message = "Do you want really to delete ${survey.id}?"
                        cancelable = true
                        setPositiveButton('Yes', { DialogInterface dialog, int which ->
                            surveyDelete(survey)
                        })
                        setNegativeButton('No', null)
                    }
                }
            }
            view.button(R.id.btn_go){
                it.onClick {
                    startSurvey(survey)
                }
            }
        }.adapter as GArrayAdapter
    }

    public surveyDelete(Survey survey){
        if (sugar.delete(survey)){
            adapter.remove(survey)
            toast("Survey deleted.").show()
        } else {
            toast("Survey cannot be deleted.").show()
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null)
            switch (requestCode){
                case LOGIN_RESULT:
                    user = new JsonSlurper().parseText(data.extras.getString("login")) as User
                    setTitle("Welccome ${user.username}!")
                    log("Logged in user: ${user.username}")
                    break
                case SURVEY_REQUEST:
                    saveSurvey(data)
                    break
                default: return
            }
    }

    public saveSurvey(Intent data){
        String jsonAnswers = data.extras.getString("answers")
        if (jsonAnswers)
            try {
                toast("Saving answers...").show()
                new Answer().with {
                    dateAnswer = new Date()
                    answers = jsonAnswers
                    if (sugar.save(it))
                        log("Save Answer: ${it}")
                    else
                        log("Answers cannot be saved.")
                }
            } catch (Exception ex) {
                toast("Error Answer: ${ex.message}").show()
                ex.printStackTrace()
            }
//        log("Answer count: ${sugar.count(Answer)}")
    }

    @Override
    protected void onResume() {
        super.onResume()
        log(user?"Welcome back ${user.username}!":"you need to login.")
        if (!sugar){
            sugar = new SugarRecord()
        }
    }

    @Override
    boolean onOptionsItemSelected(MenuItem item) {
        switch (item.itemId) {
            case R.id.action_answers:
                def i = intent(AnswerActivity)
                i.putExtra('user', user as Parcelable)
                startActivity(i)
                return true
            case R.id.action_quit:
                finish()
                return true
        }
        super.onOptionsItemSelected(item)
    }

    @Override
    boolean onCreateOptionsMenu(Menu menu) {
        menuInflater.inflate(R.menu.main, menu)
        true
    }

    public startSurvey(Survey survey){
        String jsonSurvey
        try {
            jsonSurvey = survey.toJson()
        } catch (Exception ex) {
            log("JSON Exception: " + ex.message)

        }
        /*
        // A simple JSON Survey example can be like this
        String jsonSurvey ='''{
survey_properties: {
intro_message: "<strong>Your feedback helps us to build a better mobile product.</strong><br><br><br> Hello, Feedback from our clients, friends and family is how we make key decisions on what the future holds for XYZ App.<br><br>By combining data and previous feedback we have introduced many new features e.g. x, y, z.<br><br>It will take less than 2 minutes to answer the feedback quiz.",
end_message: "Thank you for having the time to take our survey.",
skip_intro: false
},
questions: [
{
question_type: "Checkboxes",
question_title: "What were you hoping the XYZ mobile app would do?",
description: "(Select all that apply)",
required: false,
random_choices: false,
choices: [
"thing #1",
"thing #2"
]
},
{
question_type: "String",
question_title: "Why did you not subscribe at the end of your free trial ?",
description: "",
required: false
},
{
question_type: "StringMultiline",
question_title: "We love feedback and if there is anything else you’d like us to improve please let us know.",
description: "",
required: false,
number_of_lines: 4
}
]
}'''*/

        try {
            Intent intentSurvey = intent(SurveyActivity)
            intentSurvey.putExtra("json_survey", jsonSurvey)
            //you have to pass as an extra the json string.
            startActivityForResult(intentSurvey, SURVEY_REQUEST)
        } catch (Exception e) {
            log("Error Surevy: ${e.message}")
        }
    }

    @OnClick(R.id.btnSync)
    public synchronizeSurvey(View view){
        toast("Synchronize surveys...").show()
        def accessToken = user.access_token
        def json = [:]
        VolleySingleton.getInstance(applicationContext, true).addToRequestQueue(
                new StringRequest(Request.Method.GET, SURVEYS_URL,
                        { String response ->
                            try {
                                if (response){
                                    log(response)
                                    // You can zip/unzip responses using utilService methods
                                    List<Survey> jsonSurveys = new JsonSlurper().parseText(response) as List<Survey>
                                    jsonSurveys.each {
                                        Survey survey = it as Survey
                                        survey.setQuestionsAsJson(it['questions'] as List)
                                        if (sugar.save(survey)){
                                            log("Survey: ${survey.title} saved.")
                                            adapter.add(survey)
                                        } else
                                            toast("Survey ${survey.title} cannot be saved.")
                                    }
                                } else {
                                    toast("No response from the server.")
                                }

                            } catch (Exception e){
                                toast("Error while parsing response.")
                                log(e.message)
                                e.printStackTrace()
                            }

                }, { VolleyError error ->
                    toast("An error has occurred.").show()
                    log("Error Sync: ${error.message}")
                    error.printStackTrace()
                }){
                    @Override
                    Map getHeaders() throws AuthFailureError {
                        ["content-type" : "application/json",
                         "authorization": "Bearer "+accessToken
                        ]
                    }
                }, false)

    }




}
