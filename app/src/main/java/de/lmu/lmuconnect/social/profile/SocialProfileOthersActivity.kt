package de.lmu.lmuconnect.social.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.squareup.picasso.Picasso
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.Constants
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.api.*
import de.lmu.lmuconnect.social.data.OthersProfileInfo
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SocialProfileOthersActivity"


class SocialProfileOthersActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var majorTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var schoolTextView: TextView
    private lateinit var pictureImageView: ImageView
    private lateinit var phoneTextView: TextView
    private lateinit var discordTextView: TextView
    private lateinit var githubTextView: TextView
    private lateinit var insTextView: TextView
    private lateinit var fabAddOrRemoveFriendButton: ExtendedFloatingActionButton

    private lateinit var friendsId: String
    private lateinit var name: String

    private lateinit var sessionManager: SessionManager
    private lateinit var matrixSession: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = ""

        name = intent.getStringExtra("name").toString()
        Log.d(TAG, "Showing $name's profile")
        friendsId = ""

        sessionManager = SessionManager(this)
        matrixSession = MatrixSessionHolder.getCurrentSession()!!

        // Initialize Views from UI activity_profile.xml
        nameTextView = findViewById(R.id.profile_name)
        majorTextView = findViewById(R.id.profile_major)
        emailTextView = findViewById(R.id.tv_profile_email_content)
        schoolTextView = findViewById(R.id.profile_school)
        phoneTextView = findViewById(R.id.tv_profile_tel_content)
        pictureImageView = findViewById(R.id.profile_image)
        discordTextView = findViewById(R.id.tv_profile_discord_content)
        githubTextView = findViewById(R.id.tv_profile_github_content)
        insTextView = findViewById(R.id.tv_profile_ins_content)
        fabAddOrRemoveFriendButton = findViewById(R.id.fab_profile_add_friend)

        dataInitialize()
    }

    override fun onResume() {
        super.onResume()

        dataInitialize()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Init top app bar
        menuInflater.inflate(R.menu.profile_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // Init icons in top app bar
        val edit = menu!!.findItem(R.id.profile_edit)
        val logout = menu.findItem(R.id.profile_logout)

        edit.isVisible = false
        logout.isVisible = false

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dataInitialize() {
        // get other's profile information
        name.let {
            ApiClient.getApiService().othersProfileInfoGet(it, sessionManager.fetchAuthToken())
                .enqueue(object :
                    Callback<OthersProfileInfoGetResponse> {
                    override fun onResponse(
                        call: Call<OthersProfileInfoGetResponse>,
                        response: Response<OthersProfileInfoGetResponse>
                    ) {
                        when (response.code()) {
                            200 -> {
                                // OK
                                val responseBody = response.body()
                                println(responseBody)
                                if (responseBody == null) {
                                    Log.e(TAG, "Response body not existent!")
                                    //Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                                    return
                                }

                                val profileInfo = OthersProfileInfo(
                                    responseBody.id,
                                    responseBody.name,
                                    responseBody.email,
                                    responseBody.degree,
                                    responseBody.school,
                                    responseBody.picture,
                                    responseBody.major,
                                    responseBody.phone,
                                    responseBody.social
                                )

                                Log.d(TAG, profileInfo.toString())

                                nameTextView.text = profileInfo.name
                                emailTextView.text = profileInfo.email
                                schoolTextView.text = profileInfo.school
                                // Check if Profile Values have been edited by user yet
                                if (profileInfo.major.startsWith("Add")) {
                                    majorTextView.text = "-"
                                } else {
                                    majorTextView.text = profileInfo.major
                                }
                                if (profileInfo.degree.startsWith("Add")) {
                                    schoolTextView.text = schoolTextView.text.toString() + " | -"
                                } else {
                                    schoolTextView.text =
                                        schoolTextView.text.toString() + " | " + profileInfo.degree
                                }
                                if (profileInfo.phone.startsWith("Add")) {
                                    phoneTextView.text = getString(R.string.social_profile_no_phone)
                                } else {
                                    phoneTextView.text = profileInfo.phone
                                }
                                if (profileInfo.social.discord.startsWith("Add")) {
                                    discordTextView.text = getString(R.string.social_profile_no_discord)
                                } else {
                                    discordTextView.text = profileInfo.social.discord
                                }
                                if (profileInfo.social.github.startsWith("Add")) {
                                    githubTextView.text = getString(R.string.social_profile_no_github)
                                } else {
                                    githubTextView.text = profileInfo.social.github
                                }
                                if (profileInfo.social.instagram.startsWith("Add")) {
                                    insTextView.text = getString(R.string.social_profile_no_instagram)
                                } else {
                                    insTextView.text = profileInfo.social.instagram
                                }

                                val path = Constants.IMAGE_URL + profileInfo.email
                                Picasso.get().load(path).into(pictureImageView)
                                friendsId = profileInfo.id

                                if (MatrixSessionHolder.currentUserName == profileInfo.name)
                                    fabAddOrRemoveFriendButton.visibility = View.GONE
                            }
                            else -> println("Error")
                        }
                    }

                    override fun onFailure(call: Call<OthersProfileInfoGetResponse>, t: Throwable) {
                        println("Error")
                        Log.e(TAG, t.message!!)
                    }
                })
        }


        // Check if user is already in friendsList
        ApiClient.getApiService()
            .friendsListGet(sessionManager.fetchAuthToken())
            .enqueue(object :
                Callback<FriendsListGetResponse> {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onResponse(
                    call: Call<FriendsListGetResponse>,
                    response: Response<FriendsListGetResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            //OK
                            val responseBody = response.body()
                            if (responseBody == null) {
                                Log.e(TAG, "Response body not existent!")
                                //Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                                return
                            }

                            val friendsIdList = responseBody.friends

                            if (!friendsIdList.contains(friendsId)) {
                                fabAddOrRemoveFriendButton.icon = ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.profile_add_friend_filled, null
                                )

                                fabAddOrRemoveFriendButton.setOnClickListener {
                                    addFriendDialog()
                                }
                            } else {
                                fabAddOrRemoveFriendButton.icon = ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.profile_remove_friend_filled, null
                                )

                                fabAddOrRemoveFriendButton.setOnClickListener {
                                    removeFriendDialog()
                                }
                            }

                            Log.d(TAG, friendsIdList.toString())
                        }
                    }
                }

                override fun onFailure(call: Call<FriendsListGetResponse>, t: Throwable) {
                    Toast.makeText(this@SocialProfileOthersActivity, t.message, Toast.LENGTH_SHORT)
                        .show()
                    Log.e(TAG, t.message!!)
                }
            })

    }

    /**
     * Dialog to add friends
     */
    private fun addFriendDialog() {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.social_profile_add_friend)
        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            // ADDED!
            ApiClient.getApiService().friendAddPost(
                FriendAddRequest(emailTextView.text.toString()),
                sessionManager.fetchAuthToken()
            ).enqueue(
                object : Callback<FriendAddResponse> {
                    override fun onResponse(
                        call: Call<FriendAddResponse>,
                        response: Response<FriendAddResponse>
                    ) {
                        when (response.code()) {
                            200 -> {
                                try {
                                    lifecycleScope.launch {
                                        MatrixSessionHolder.currentRoomID =
                                            response.body()?.let {
                                                matrixSession.roomService()
                                                    .createRoom(
                                                        CreateRoomParams().apply {
                                                            topic = "PERSONAL"
                                                            name = response.body()?.name
                                                            invitedUserIds.add(it.matrixId)
                                                            setDirectMessage()
                                                            enableEncryptionIfInvitedUsersSupportIt = true
                                                        }
                                                    )
                                            }.toString()
                                        ApiClient.getApiService().joinRoomPost(JoinRoomPostRequest(emailTextView.text.toString(), MatrixSessionHolder.currentRoomID), sessionManager.fetchAuthToken()).enqueue( object: Callback<Void> {
                                            override fun onResponse(
                                                call: Call<Void>,
                                                response: Response<Void>
                                            ) {
                                                Toast.makeText(
                                                    this@SocialProfileOthersActivity,
                                                    "Friend has been added",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                                Log.e(TAG, "Failed")
                                            }

                                        }
                                        )
                                    }
                                } catch (_: Error) {
                                }
                                finish()
                            }
                            else -> Toast.makeText(
                                this@SocialProfileOthersActivity,
                                response.code().toString() + " - " + response.message()
                                    .uppercase(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<FriendAddResponse>, t: Throwable) {
                        Toast.makeText(
                            this@SocialProfileOthersActivity,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, t.message!!)
                    }

                })
            val intent = Intent(this, SocialProfileOthersActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            // User cancelled the dialog
            finish()
        }.create()
        // Create the AlertDialog object and return it
        builder.create().show()

    }

    /**
     * Dialog to remove friend
     */
    private fun removeFriendDialog() {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.social_profile_remove_friend)
        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            // remove this friend!
            Log.d(TAG, "FriendsID: $friendsId")
            ApiClient.getApiService().friendDelete(
                FriendDeleteRequest(friendsId), SessionManager(this).fetchAuthToken()
            ).enqueue(
                object : Callback<FriendDeleteRequest> {
                    override fun onResponse(
                        call: Call<FriendDeleteRequest>,
                        response: Response<FriendDeleteRequest>
                    ) {
                        when (response.code()) {
                            200 -> {
                                // leave this group!
                                lifecycleScope.launch{
                                    matrixSession.roomService().leaveRoom(MatrixSessionHolder.currentRoomID, "")
                                }
                                finish()
                            }
                            else -> Toast.makeText(
                                this@SocialProfileOthersActivity,
                                "Friend has been removed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<FriendDeleteRequest>, t: Throwable) {
                        Log.e(TAG, t.message!!)
                    }
                })
            val intent = Intent(this, SocialProfileOthersActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            // User cancelled the dialog
            finish()
        }.create()
        // Create the AlertDialog object and return it
        builder.create().show()
    }
}




