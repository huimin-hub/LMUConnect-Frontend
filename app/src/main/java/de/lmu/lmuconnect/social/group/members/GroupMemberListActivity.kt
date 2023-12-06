package de.lmu.lmuconnect.social.group.members

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.api.GroupMemberGetRequest
import de.lmu.lmuconnect.social.api.GroupMemberGetResponse
import de.lmu.lmuconnect.social.api.GroupMemberIdResponse
import de.lmu.lmuconnect.social.api.JoinRoomPostRequest
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "GroupMemberListActivity"

class GroupMemberListActivity : AppCompatActivity() {

    // Components from UI activity_profile.xml
    private lateinit var nameTextView: TextView
    private lateinit var typTextView: TextView
    private lateinit var countTextView: TextView
    private lateinit var pictureImageView: ImageView
    private lateinit var fabAddFriend: ExtendedFloatingActionButton

    private lateinit var roomsummary : RoomSummary
    private lateinit var roomID: String

    private lateinit var recyclerViewAdapter: GroupMemberListItemAdapter
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var matrixSession: Session

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_group_list)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = ""

        //roomID = intent.getStringExtra("roomId").toString()
        roomID = MatrixSessionHolder.currentRoomID

        sessionManager = SessionManager(this)
        matrixSession = MatrixSessionHolder.getCurrentSession()!!
        roomsummary = matrixSession.roomService().getRoomSummary(roomID)!!

        // Initialize Views from UI
        nameTextView = findViewById(R.id.group_list_name)
        typTextView = findViewById(R.id.group_list_type)
        countTextView = findViewById(R.id.group_list_member_count)
        pictureImageView = findViewById(R.id.group_list_image)
        fabAddFriend = findViewById(R.id.fab_group_list_add)


        // Initialize data from roomsummary
        nameTextView.text = roomsummary.displayName
        typTextView.text = roomsummary.topic
        if (roomsummary.joinedMembersCount == 1) {
            countTextView.text = roomsummary.joinedMembersCount.toString() + getString(R.string.social_group_member)
        } else {
            countTextView.text = roomsummary.joinedMembersCount.toString() + getString(R.string.social_group_member_plural)
        }
        when(roomsummary.topic) {
            "LECTURE" -> pictureImageView.setImageResource(R.drawable.study_lecture)
            "INTERNSHIP" -> pictureImageView.setImageResource(R.drawable.study_practical)
            "SEMINAR" -> pictureImageView.setImageResource(R.drawable.study_seminar)
            "TUTORIAL" -> pictureImageView.setImageResource(R.drawable.study_tutorial)
            "EXERCISE" -> pictureImageView.setImageResource(R.drawable.study_exercise)
            "OTHERS" -> pictureImageView.setImageResource(R.drawable.social_others)
            else -> pictureImageView.setImageResource(R.drawable.menu_icon_people)
        }

        // Initiate Recyclerview
        listRecyclerView = findViewById(R.id.recyview_group_member_list)
        listRecyclerView.layoutManager = LinearLayoutManager(this)

        fabAddFriend.setOnClickListener {
            addGroupMemberDialog()
        }

        dataInitialize()
    }

    override fun onResume() {
        super.onResume()

        dataInitialize()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.social_group_list_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle cancel icon press
                finish()
            }
            R.id.action_group_leave -> {
                // Handle leave_group icon press
                leaveRoomDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dataInitialize() {

        if (roomsummary.otherMemberIds.isEmpty()) {
            // if no other member is in this group, just show currentUserName
            val members = arrayListOf(MatrixSessionHolder.currentUserName)
            recyclerViewAdapter = GroupMemberListItemAdapter(members, this@GroupMemberListActivity)
            listRecyclerView.adapter = recyclerViewAdapter

        } else {
            // if other member are in this group, get names of all members
            val idList = roomsummary.otherMemberIds
            ApiClient.getApiService().groupMemberGet(GroupMemberGetRequest(idList), sessionManager.fetchAuthToken()).enqueue(object:
                Callback<GroupMemberGetResponse> {

                override fun onResponse(call: Call<GroupMemberGetResponse>, response: Response<GroupMemberGetResponse>) {
                    recyclerViewAdapter = GroupMemberListItemAdapter(
                        response.body()?.memberList as ArrayList<String>,
                        this@GroupMemberListActivity
                    )
                    listRecyclerView.adapter = recyclerViewAdapter
                }

                override fun onFailure(call: Call<GroupMemberGetResponse>, t: Throwable) {
                    println("Error")
                }
        })
        }
    }


    private fun addGroupMemberDialog() {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.social_group_list_add_enter_email)
        // Set up the input
        val input = EditText(this)
        var inputEmail: String

        // Specify the type of input TYPE_CLASS_TEXT
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        builder.setView(input)

        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            inputEmail = input.text.toString()
            ApiClient.getApiService().userMatrixIdGet(inputEmail, sessionManager.fetchAuthToken()).enqueue( object: Callback<GroupMemberIdResponse> {
                override fun onResponse(
                    call: Call<GroupMemberIdResponse>,
                    response: Response<GroupMemberIdResponse>
                ) {
                    val matrixId = response.body()?.matrixId
                    lifecycleScope.launch {
                        if (matrixId != null) {
                            matrixSession.getRoom(roomID)?.membershipService()?.invite(matrixId)
                            ApiClient.getApiService().joinRoomPost(
                                JoinRoomPostRequest(inputEmail, roomID),
                                sessionManager.fetchAuthToken()
                            ).enqueue(object : Callback<Void> {
                                override fun onResponse(
                                    call: Call<Void>,
                                    response: Response<Void>
                                ) {
                                    Toast.makeText(
                                        this@GroupMemberListActivity,
                                        "Member has been added",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                }
                            })
                        }
                    }
                }
                override fun onFailure(call: Call<GroupMemberIdResponse>, t: Throwable) {
                }
            })
            val intent = Intent(this, GroupMemberListActivity::class.java)
            startActivity(intent)

        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            // User cancelled the dialog
            finish()
        }
        // Create the AlertDialog object and return it
        builder.create().show()
    }

    private fun leaveRoomDialog() {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.social_group_leave)
        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            // leave this group!
            lifecycleScope.launch{
                matrixSession.roomService().leaveRoom(roomID, "")
            }
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

