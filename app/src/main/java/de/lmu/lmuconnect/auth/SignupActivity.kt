package de.lmu.lmuconnect.auth

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.auth.api.AuthSignupPostRequest
import de.lmu.lmuconnect.auth.api.AuthSignupPostResponse
import de.lmu.lmuconnect.general.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    // Top Bar Component in Login Page
    private lateinit var topAppBar: MaterialToolbar

    // Components in SignUp page
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var signupButton: MaterialButton
    private lateinit var goToLoginButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize top bar components in Login Page
        topAppBar = findViewById(R.id.topAppBar)

        // Initialize all the view's components in Login page
        nameEditText = findViewById(R.id.et_signup_name)
        emailEditText = findViewById(R.id.et_signup_email)
        passwordEditText = findViewById(R.id.et_signup_pwd)
        phoneEditText = findViewById(R.id.et_signup_phone)
        signupButton = findViewById(R.id.btn_signup)
        goToLoginButton = findViewById(R.id.btn_signup_to_login)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.app_cancel -> {
                    // Handle profile icon press
                    finish()
                    true
                }

                else -> false
            }
        }

        signupButton.setOnClickListener {
            // Check and validate fields then send Signup Request
            if (getSignupEmail().isEmpty() || getSignupName().isEmpty() || getSignupPassword().isEmpty()) {
                Toast.makeText(this@SignupActivity, "Please fill out all required fields", Toast.LENGTH_LONG).show()
            } else if(!isValidEmail(getSignupEmail())) {
                Toast.makeText(this@SignupActivity, "Invalid Email Address", Toast.LENGTH_LONG).show()
            } else {
                ApiClient.getApiService().authSignupPost(AuthSignupPostRequest(getSignupName(), getSignupEmail(), getSignupPassword()))
                    .enqueue(object: Callback<AuthSignupPostResponse>{

                        override fun onResponse(call: Call<AuthSignupPostResponse>, response: Response<AuthSignupPostResponse>) {
                            if((response.code() == 201)){
                                Toast.makeText(this@SignupActivity, "Signed up successfully. You can now log in!", Toast.LENGTH_LONG).show()
                                finish()
                            } else if((response.code() == 401)) {
                                Toast.makeText(this@SignupActivity, "You already have an account.", Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                Toast.makeText(this@SignupActivity, "Something went wrong...", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<AuthSignupPostResponse>, t: Throwable) {
                            Toast.makeText(this@SignupActivity, "Something went wrong...", Toast.LENGTH_LONG).show()
                        }

                    })
            }

        }

        goToLoginButton.setOnClickListener{
            val intent = Intent()
            intent.setClass(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //--------------------------------- UI CODE -------------------------------------//
        // Adds an underline under the gotoSignupButton
        goToLoginButton.paintFlags = goToLoginButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        //-------------------------------------------------------------------------------//
    }

    private fun getSignupName() : String {
        return nameEditText.text.toString()
    }

    private fun getSignupEmail() : String {
        return emailEditText.text.toString()
    }

    private fun getSignupPassword() : String {
        return passwordEditText.text.toString()
    }

    private fun getSignupPhone() : String {
        return phoneEditText.text.toString()
    }

    private fun isValidEmail(email : String) : Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}