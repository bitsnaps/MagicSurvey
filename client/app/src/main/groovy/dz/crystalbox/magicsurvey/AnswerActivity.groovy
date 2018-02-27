package dz.crystalbox.magicsurvey

import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import com.android.volley.Request
import com.android.volley.error.AuthFailureError
import com.android.volley.error.VolleyError
import com.android.volley.request.JsonObjectRequest
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.Extra
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnClick
import com.arasthel.swissknife.annotations.SaveInstance
import com.arasthel.swissknife.dsl.components.GArrayAdapter
import com.orm.SugarRecord
import dz.crystalbox.magicsurvey.models.Answer
import dz.crystalbox.magicsurvey.models.User
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.json.JSONObject

import static dz.crystalbox.magicsurvey.ContextMethods.alert

@CompileStatic
class AnswerActivity extends AppCompatActivity{

    GArrayAdapter adapter

    SugarRecord sugar

    List<Answer> answers

    @InjectView(R.id.listSurvey)
    ListView listView

    UtilService utilService

    @Extra
    User user

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        SwissKnife.inject(this)
        SwissKnife.loadExtras(this)

        sugar = new SugarRecord()

        utilService = new UtilService()

        answers = sugar.listAll(Answer)

        log("Nbr of Answers: ${answers.size()}")

        if (user) {
            log("User: ${user.username}")
        } else {
            log("Please login first.")
            finish()
        }

        adapter = listView.onItem(R.layout.list_items, answers){ Answer answer, View view, int position ->
            view.onClick {
                toast("Answers for <${answer.answersAsMap.size()}> Question(s)").show()
                log(answer.answers)
            }
            view.text(R.id.item_name){
                it.text = "Answer <${answer.dateAnswerFmt()}>"
            }
            view.button(R.id.btn_delete) {
                it.onClick {
                    alert(this) {
                        title = 'Confirm delete'
                        message = "Do you want really to delete this item ?"
                        cancelable = true
                        setPositiveButton('Yes', { DialogInterface dialog, int which ->
                            answerDelete(answer)
                        })
                        setNegativeButton('No', null)
                    }
                }
            }
            view.button(R.id.btn_go){
                it.onClick {
                    syncAnswer(answer)
                }
            }
        }.adapter as GArrayAdapter
    }

    def answerDelete(Answer answer){
        if (sugar.delete(answer)){
            answers.remove(answer)
            adapter.remove(answer)
            log("Answer deleted.")
        } else {
            toast("Answer cannot be deleted.").show()
        }
    }

    def syncAnswer(Answer answer){
        toast("Sync Answer<${answer.dateAnswerFmt()}>...").show()
        Map jsonAnswers = [:]
        jsonAnswers.put(answer.dateAnswerFmt(), utilService.zipString(answer.answers).toString())
        upload(jsonAnswers)
        answerDelete(answer)
    }

    @OnClick(R.id.btnSync)
    public syncAnswers(View view){
        toast("Sync ${answers.size()} Answers...").show()
        Map jsonAnswers = [:]
        answers.each { Answer a ->
            jsonAnswers.put(a.dateAnswerFmt(), utilService.zipString(a.answers).toString())
        }
        upload(jsonAnswers)
        answers.each { answerDelete(it)}
    }

    private upload(Map jsonAnswers){
        if (!user){
            toast("Please login first.").show()
        } else {
            def accessToken = user.access_token
            VolleySingleton.getInstance(applicationContext).addToRequestQueue(
                    new JsonObjectRequest(Request.Method.POST,
                            MainActivity.ANSWERS_URL, new JSONObject(jsonAnswers),
                            { JSONObject response ->
                                toast(response.toString())
                                log("Response: ${response.toString()}")
                            }, { VolleyError error ->
                        log("ERROR Sync: ${error.message}")
                        error.printStackTrace()
                    }) {
                        @Override
                        Map getHeaders() throws AuthFailureError {
                            ["content-type" : "application/json; charset=utf-8",
                             "authorization": "Bearer " + accessToken,
                            ]
                        }
                    }
            )
        }
    }


}
