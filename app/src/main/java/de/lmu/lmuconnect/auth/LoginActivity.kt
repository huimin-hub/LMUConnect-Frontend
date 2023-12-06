package de.lmu.lmuconnect.auth

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.SocialApplication
import de.lmu.lmuconnect.auth.api.AuthLoginPostRequest
import de.lmu.lmuconnect.auth.api.AuthLoginPostResponse
import de.lmu.lmuconnect.general.Constants
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    // Top Bar Component in Login Page
    private lateinit var topAppBar: MaterialToolbar

    // Components in Login page
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var goToSignupButton: MaterialButton

    private lateinit var sessionManager: SessionManager
    private lateinit var matrix: Matrix

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Initialize top bar components in Login Page
        topAppBar = findViewById(R.id.topAppBar)

        // Initialize all the view's components in Login page
        emailEditText = findViewById(R.id.et_login_email)
        passwordEditText = findViewById(R.id.et_login_pwd)
        loginButton = findViewById(R.id.btn_login)
        goToSignupButton = findViewById(R.id.btn_login_to_signup)

        sessionManager = SessionManager(this)
        matrix = SocialApplication.getMatrix(this)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.app_cancel -> {
                    // Handle cancel icon press
                    finish()
                    true
                }

                else -> false
            }
        }

        loginButton.setOnClickListener {
            if (getLoginEmail().isEmpty() || getLoginPassword().isEmpty()) {
                Toast.makeText(this@LoginActivity, "E-Mail or Password required", Toast.LENGTH_LONG).show()
            } else if(!isValidEmail(getLoginEmail())) {
                Toast.makeText(this@LoginActivity, "Invalid Email Address", Toast.LENGTH_LONG).show()
            } else {
                ApiClient.getApiService().authLoginPost(AuthLoginPostRequest(getLoginEmail(), getLoginPassword()))
                    .enqueue(object : Callback<AuthLoginPostResponse>{
                        override fun onResponse(
                            call: Call<AuthLoginPostResponse>,
                            response: Response<AuthLoginPostResponse>
                        ) {
                            val loginResponse = response.body()

                            if((response.code() == 200 && loginResponse != null)){
                                sessionManager.saveAuthToken(loginResponse.authToken)
                                launchMatrixAuth(loginResponse.matrixName, loginResponse.matrixPassword, loginResponse.matrixId)
                                Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "E-Mail or Password incorrect", Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onFailure(call: Call<AuthLoginPostResponse>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, "E-Mail or Password incorrect", Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }

        goToSignupButton.setOnClickListener{
            val intent = Intent()
            intent.setClass(this, SignupActivity::class.java)
            startActivity(intent)
        }

        //--------------------------------- UI CODE -------------------------------------//
        // Adds an underline under the gotoSignupButton
        goToSignupButton.paintFlags = goToSignupButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        //-------------------------------------------------------------------------------//
    }

    private fun getLoginEmail() : String {
        return emailEditText.text.toString()
    }

    private fun getLoginPassword() : String {
        return passwordEditText.text.toString()
    }

    private fun isValidEmail(email : String) : Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun launchMatrixAuth(username: String, password: String, id: String) {
        // TO DO Matrix Config API CALL
        val homeserver = Constants.MATRIX_SERVER_URL.trim()

        // Create Homeserver Config
        val homeServerConnectionConfig = try {
            HomeServerConnectionConfig
                .Builder()
                .withHomeServerUri(Uri.parse(homeserver))
                .build()
        } catch (failure: Throwable) {
            Toast.makeText(this@LoginActivity, "Home server is not valid", Toast.LENGTH_SHORT).show()
            return
        }

        // direct authentication
        lifecycleScope.launch {
            try {
                matrix.authenticationService().directAuthentication(
                    homeServerConnectionConfig,
                    username,
                    password,
                    "LMUconnectApplication"
                )
            } catch (failure: Throwable) {
                Toast.makeText(this@LoginActivity, "Failure: $failure", Toast.LENGTH_SHORT).show()
                println(failure)
                null
            }?.let { session ->
                // open and launch sync
                Toast.makeText(
                    this@LoginActivity,
                    "Social User: ${session.myUserId}",
                    Toast.LENGTH_SHORT
                ).show()
                MatrixSessionHolder.setCurrentSession(session)
                session.open()
                session.syncService().startSync(true)
                finish()
            }
        }
        MatrixSessionHolder.currentUserId = id
    }
}