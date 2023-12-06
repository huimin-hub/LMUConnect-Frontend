package de.lmu.lmuconnect.social.personal


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.api.FriendAddRequest
import de.lmu.lmuconnect.social.api.FriendAddResponse
import de.lmu.lmuconnect.social.api.JoinRoomPostRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "AddFriendsActivity"

class AddFriendsActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private var friendMatrixId: String = ""
    private var friendName: String = ""

    private lateinit var sessionManager: SessionManager
    private lateinit var matrixSession: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_friend)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sessionManager = SessionManager(this)
        matrixSession = MatrixSessionHolder.getCurrentSession()!!

        // Initialize Views
        emailEditText = findViewById(R.id.et_add_friend_input)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.social_addchat_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // handle home icon press
            android.R.id.home -> {
                finish()
            }
            // handle finish icon press
            R.id.action_finish -> {
                ApiClient.getApiService().friendAddPost(
                    FriendAddRequest(emailEditText.text.toString()), sessionManager.fetchAuthToken()
                ).enqueue(object :
                    Callback<FriendAddResponse> {

                    override fun onFailure(call: Call<FriendAddResponse>, t: Throwable) {
                        Toast.makeText(
                            this@AddFriendsActivity,
                            "Could not add friend",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<FriendAddResponse>,
                        response: Response<FriendAddResponse>
                    ) {
                        when (response.code()) {
                            200 -> {
                                Toast.makeText(
                                    this@AddFriendsActivity,
                                    "Successfully added friend!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                println(response.body()?.matrixId.toString())
                                println(response.body()?.name.toString())
                                friendMatrixId = response.body()?.matrixId.toString()
                                friendName = response.body()?.name.toString()
                                //finish()


                                if (friendName != "") {
                                    // Parameters for new Room
                                    val createRoomParams = CreateRoomParams()
                                    createRoomParams.topic = "PERSONAL"
                                    createRoomParams.name = friendName
                                    createRoomParams.apply {
                                        invitedUserIds.add(friendMatrixId)
                                        setDirectMessage()
                                        enableEncryptionIfInvitedUsersSupportIt = false
                                    }
                                    println(friendMatrixId)
                                    println(friendName)

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
                                            ApiClient.getApiService().joinRoomPost(JoinRoomPostRequest(emailEditText.text.toString(), MatrixSessionHolder.currentRoomID), sessionManager.fetchAuthToken()).enqueue( object: Callback<Void> {
                                                override fun onResponse(
                                                    call: Call<Void>,
                                                    response: Response<Void>
                                                ) {
                                                    Toast.makeText(
                                                        this@AddFriendsActivity,
                                                        "Friend has been added",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    finish()
                                                }

                                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                                    Log.e(TAG, "Failed")
                                                }
                                            }
                                            )
                                        }
                                    } catch (_: Error) {
                                    }
                                }
                            }
                            400 -> {
                                Toast.makeText(
                                    this@AddFriendsActivity,
                                    "Cannot find user or user was already your friend!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            else -> {

                            }
                        }
                    }
                })
            }
        }

        return super.onOptionsItemSelected(item)
    }
}