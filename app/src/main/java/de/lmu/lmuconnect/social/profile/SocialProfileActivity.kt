package de.lmu.lmuconnect.social.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.squareup.picasso.Picasso
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.Constants.IMAGE_URL
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.social.api.ProfileInfoGetResponse
import de.lmu.lmuconnect.social.data.ProfileInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ProfileFragment"

class SocialProfileActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var majorTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var schoolTextView: TextView
    private lateinit var pictureImageView: ImageView
    private lateinit var phoneTextView: TextView
    private lateinit var discordTextView: TextView
    private lateinit var githubTextView: TextView
    private lateinit var insTextView: TextView
    private lateinit var fabAddOrRemoveFriend: ExtendedFloatingActionButton

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = ""

        sessionManager = SessionManager(this)

        // Initialize Views from activity_profile.xml
        nameTextView = findViewById(R.id.profile_name)
        majorTextView = findViewById(R.id.profile_major)
        emailTextView = findViewById(R.id.tv_profile_email_content)
        schoolTextView = findViewById(R.id.profile_school)
        phoneTextView = findViewById(R.id.tv_profile_tel_content)
        pictureImageView = findViewById(R.id.profile_image)
        discordTextView = findViewById(R.id.tv_profile_discord_content)
        githubTextView = findViewById(R.id.tv_profile_github_content)
        insTextView = findViewById(R.id.tv_profile_ins_content)
        fabAddOrRemoveFriend = findViewById(R.id.fab_profile_add_friend)

        // Hide add/remove friend floating button
        fabAddOrRemoveFriend.hide()

        dataInitialize()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Init top app bar
        menuInflater.inflate(R.menu.profile_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            // handle home icon press
            android.R.id.home -> {
                finish()
            }
            // Handle edit icon press to show layout activity_profile_edit
            R.id.profile_edit -> {
                startActivity(Intent(this@SocialProfileActivity, SocialProfileEditActivity::class.java))
            }
            // Handle logout icon press to log out
            R.id.profile_logout -> {
                sessionManager.saveAuthToken("")
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dataInitialize() {
        // get profile information
        ApiClient.getApiService().profileInfoGet(sessionManager.fetchAuthToken()).enqueue(object :
            Callback<ProfileInfoGetResponse> {
            override fun onResponse(call: Call<ProfileInfoGetResponse>, response: Response<ProfileInfoGetResponse>) {
                when (response.code()) {
                    200 -> {
                        // OK
                        val responseBody = response.body()
                        if (responseBody == null) {
                            Log.e(TAG, "Response body not existent!")
                            return
                        }

                        val profileInfo = ProfileInfo(responseBody.name, responseBody.email, responseBody.degree,
                            responseBody.school, responseBody.picture, responseBody.major, responseBody.phone,
                            responseBody.social)

                        Log.d(TAG, profileInfo.toString())

                        nameTextView.text = profileInfo.name
                        majorTextView.text = profileInfo.major
                        emailTextView.text = profileInfo.email
                        schoolTextView.text = "${profileInfo.school} | ${profileInfo.degree}"
                        phoneTextView.text = profileInfo.phone
                        discordTextView.text = profileInfo.social.discord
                        githubTextView.text = profileInfo.social.github
                        insTextView.text = profileInfo.social.instagram

                        val path = IMAGE_URL + profileInfo.email
                        Picasso.get().load(path).into(pictureImageView)

                    }
                    else -> println("Error")
                }
            }

            override fun onFailure(call: Call<ProfileInfoGetResponse>, t: Throwable) {
                println("Error")
                Log.e(TAG, t.message!!)
            }
        })
    }
}