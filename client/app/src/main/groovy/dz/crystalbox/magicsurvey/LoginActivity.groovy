package dz.crystalbox.magicsurvey

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.error.VolleyError
import com.android.volley.request.JsonObjectRequest
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnClick
import com.arasthel.swissknife.annotations.OnUIThread
import groovy.transform.CompileStatic
import org.json.JSONObject

/**
 * A login screen that offers login via email/password.
 */
@CompileStatic
class LoginActivity extends AppCompatActivity {

    static final String LOGIN_URL = "http://"+MainActivity.HOST_SERVER+"/api/login"

    // UI references.
    @InjectView(R.id.username)
    EditText mUserName

    @InjectView(R.id.password)
    EditText mPasswordView

    @InjectView(R.id.sign_in_button)
    Button mSignInButton

    @InjectView(R.id.login_progress)
    View mProgressView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SwissKnife.inject(this)

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin()
                    return true
                }
                return false
            }
        })

        mSignInButton.onClick {
            attemptLogin()
        }

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnUIThread
    private void attemptLogin() {

        // Reset errors.
        mUserName.setError(null)
        mPasswordView.setError(null)

        // Store values at the time of the login attempt.
        final String username = mUserName.getText().toString()
        final String password = mPasswordView.getText().toString()

        boolean cancel = false
        View focusView = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password))
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserName.setError(getString(R.string.error_field_required))
            focusView = mUserName
            cancel = true
        }

        if (cancel) {
            // There was an error don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            new Handler().postDelayed ({
                log("Connecting to: ${LOGIN_URL}...")
//                Thread.sleep(1000)
                Map jsonMap = ['username':username,'password':password]
                VolleySingleton.getInstance(applicationContext, true).addToRequestQueue(
                        new JsonObjectRequest(Request.Method.POST, LOGIN_URL, new JSONObject(jsonMap),
                                { JSONObject response ->
                                    setResult(RESULT_OK, new Intent().putExtra("login", response.toString()))
                                    finish()
                                },{ VolleyError error ->
                                    setResult(RESULT_CANCELED, new Intent().putExtra("login", "failed"))
                                    this.toast("Login failed.").show()
                                    log("Request Error: ${error.message}")
                                    showProgress(false)
                        }), false
                )
            }, 1500)
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime)

            mSignInButton.setEnabled(!show)
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE)
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE)
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE)
            mSignInButton.setEnabled(!show)
        }
    }

    @Override
    void onBackPressed() {
        moveTaskToBack(false)
    }

    boolean isPasswordValid(String password) { password.length() > 2 }


}

