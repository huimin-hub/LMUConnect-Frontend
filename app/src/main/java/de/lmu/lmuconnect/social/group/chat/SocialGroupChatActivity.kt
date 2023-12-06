package de.lmu.lmuconnect.social.group.chat

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.api.UserNameGetResponse
import de.lmu.lmuconnect.social.group.members.GroupMemberListActivity
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.read.ReadService
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.api.util.Optional
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class SocialGroupChatActivity: AppCompatActivity(), Timeline.Listener {

    private lateinit var chatEditText: EditText
    private lateinit var chatSendImageButton: ImageButton
    private lateinit var typingTextView: TextView
    private lateinit var noChatTextView: TextView

    private lateinit var room : Room
    private lateinit var roomsummary : RoomSummary
    private lateinit var roomID: String
    private var timeline: Timeline? = null

    private lateinit var recyclerViewAdapter: GroupMessageItemAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private var session: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_gchat)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        chatEditText = findViewById(R.id.edit_gchat_message)
        chatSendImageButton = findViewById(R.id.button_gchat_send)
        typingTextView = findViewById(R.id.text_typing)
        noChatTextView = findViewById(R.id.text_no_chat)

        chatRecyclerView = findViewById(R.id.recycler_gchat)

        session = MatrixSessionHolder.getCurrentSession()
        roomID = MatrixSessionHolder.currentRoomID

        // Observe live data
        session!!.roomService().getRoomSummaryLive(roomID).observe(this) {
            updateRoom(it)
        }
        room = session!!.getRoom(roomID)!!
        roomsummary = session!!.roomService().getRoomSummary(roomID)!!

        supportActionBar?.title = roomsummary.displayName

        // Mark all as read
        lifecycleScope.launch {
            room.readService().markAsRead(ReadService.MarkAsReadParams.READ_RECEIPT)
        }

        // Create some settings to configure timeline
        val timelineSettings = TimelineSettings(
            initialSize = 30
        )
        // Then you can retrieve a timeline from this room.
        timeline = room.timelineService().createTimeline(null, timelineSettings).also {
            // Don't forget to add listener and start the timeline so it start listening to changes
            it.addListener(this)
            it.start()
        }

        // Initiate Recyclerview
        chatRecyclerView = findViewById(R.id.recycler_gchat)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        chatEditText.setOnClickListener {
            room.typingService().userIsTyping()

            val timer = object: CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    room.typingService().userStopsTyping()
                }
            }
            timer.start()
        }
        // set click event: click send button to send message
        chatSendImageButton.setOnClickListener {
            sendMessage(chatEditText.text.toString())
        }

        // show textview if someone is typing
        room.getRoomSummaryLive().observe(this){
            if(it.get().typingUsers.isEmpty()) {
                typingTextView.visibility = View.INVISIBLE
            } else {
                it.get().typingUsers[0].displayName?.let { it1 ->
                    ApiClient.getApiService().userNameGet(it1).enqueue(object :
                        Callback<UserNameGetResponse> {
                        override fun onResponse(call: Call<UserNameGetResponse>, response: Response<UserNameGetResponse>) {
                            typingTextView.text = response.body()?.name + getString(R.string.social_chat_is_typing)
                            typingTextView.visibility = View.VISIBLE
                        }

                        override fun onFailure(call: Call<UserNameGetResponse>, t: Throwable) {
                        }
                    })
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.social_gchat_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.social_gmember_icon -> {
                // Handle group_member icon press
                val intent = Intent(this@SocialGroupChatActivity, GroupMemberListActivity::class.java)
                intent.putExtra("roomId", roomsummary.roomId)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendMessage(message: String) {
        room.sendService().sendTextMessage(message)
        chatEditText.text.clear()
    }

    private fun updateRoom(room: Optional<RoomSummary>) {
        roomsummary = room.get()
        supportActionBar?.title = roomsummary.displayName
        if (roomsummary.displayName.contains("!")) finish()
    }

    override fun onNewTimelineEvents(eventIds: List<String>) {
        // This is new event ids coming from sync
    }

    override fun onTimelineFailure(throwable: Throwable) {
        // When a failure is happening when trying to retrieve an event.
        // This is an unrecoverable error, you might want to restart the timeline
        // timeline?.restartWithEventId("")
    }

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        // Each time the timeline is updated it will be called.
        // It can happens when sync returns, paginating, and updating (local echo, decryption finished...)
        val list = snapshot.filter { it.root.type == "m.room.message"}

        recyclerViewAdapter = GroupMessageItemAdapter(list.asReversed(), this)
        chatRecyclerView.adapter = recyclerViewAdapter

        val itemCount = chatRecyclerView.adapter?.itemCount
        chatRecyclerView.smoothScrollToPosition(chatRecyclerView.adapter!!.itemCount - 1)

        // check if there are chats or not
        if(itemCount != 0){
            noChatTextView.visibility = View.INVISIBLE
        }
    }
}

