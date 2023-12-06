package de.lmu.lmuconnect.social.personal.chat

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
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.profile.SocialProfileOthersActivity
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


class SocialChatActivity: AppCompatActivity(), Timeline.Listener {

    private lateinit var chatEditText: EditText
    private lateinit var chatSendImageButton: ImageButton
    private lateinit var typingTextView: TextView
    private lateinit var noChatTextView: TextView

    private lateinit var room : Room
    private lateinit var roomsummary : RoomSummary
    private lateinit var roomID: String
    private lateinit var timeline: Timeline

    private lateinit var recyclerViewAdapter: MessageItemAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private var session: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Init UI layout
        setContentView(R.layout.activity_social_chat)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Init views
        chatEditText = findViewById(R.id.edit_chat_message)
        chatSendImageButton = findViewById(R.id.button_chat_send)
        typingTextView = findViewById(R.id.text_typing)
        noChatTextView = findViewById(R.id.text_no_chats)

        chatRecyclerView = findViewById(R.id.recycler_chat)

        session = MatrixSessionHolder.getCurrentSession()
        roomID = MatrixSessionHolder.currentRoomID

        // Observe live data
        session!!.roomService().getRoomSummaryLive(roomID).observe(this) {
            updateRoom(it)
        }
        room = session!!.getRoom(roomID)!!
        roomsummary = session!!.roomService().getRoomSummary(roomID)!!

        supportActionBar?.title = roomsummary.displayName
        /**
         * Mark messages as read
         */
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
        chatRecyclerView = findViewById(R.id.recycler_chat)
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
        // handle send button press
        chatSendImageButton.setOnClickListener {
            sendMessage(chatEditText.text.toString())
        }

        /**
         * show text if other user is typing
         */
        room.getRoomSummaryLive().observe(this){
            if(it.get().typingUsers.isEmpty()) {
                typingTextView.visibility = View.INVISIBLE
            } else {
                typingTextView.text = it.get().typingUsers[0].displayName + getString(R.string.social_chat_is_typing)
                typingTextView.visibility = View.VISIBLE
            }
        }

    }


    /**
     * Init top app bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.social_chat_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Set click events in top app bar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.app_profile -> {
                // Handle profile icon press
                val intent = Intent(this@SocialChatActivity,SocialProfileOthersActivity::class.java)
                intent.putExtra("name", roomsummary.displayName)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Send message
     */
    private fun sendMessage(message: String) {
        room.sendService().sendTextMessage(message)
        chatEditText.text.clear()
    }

    /**
     * update room, get user's name
     */
    private fun updateRoom(room: Optional<RoomSummary>) {
        // Get the username
        supportActionBar?.title = roomsummary.displayName
    }

    override fun onNewTimelineEvents(eventIds: List<String>) {
        // This is new event ids coming from sync
    }

    override fun onTimelineFailure(throwable: Throwable) {
        // When a failure is happening when trying to retrieve an event.
        // This is an unrecoverable error, you might want to restart the timeline
        // timeline?.restartWithEventId("")
    }

    /**
     * Update timeline
     */
    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        // Each time the timeline is updated it will be called.
        // It can happens when sync returns, paginating, and updating (local echo, decryption finished...)
        val list = snapshot.filter { it.root.type == "m.room.message"}

        recyclerViewAdapter = MessageItemAdapter(list.asReversed(), this)
        chatRecyclerView.adapter = recyclerViewAdapter

        val itemCount = chatRecyclerView.adapter?.itemCount

        chatRecyclerView.smoothScrollToPosition(chatRecyclerView.adapter!!.itemCount - 1)

        // check if there are chats or not
        if(itemCount != 0){
            noChatTextView.visibility = View.INVISIBLE
        }
    }
}