package de.lmu.lmuconnect.social.personal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.SocialAddButtonHandler
import de.lmu.lmuconnect.social.personal.chat.SocialChatActivity
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


private const val TAG = "SocialPersonalFragment"

class SocialPersonalFragment : Fragment(), PersonalItemAdapterEventHandler, SocialAddButtonHandler {

    private lateinit var recyclerViewAdapter: PersonalItemAdapter
    private lateinit var personalChatRecylerView : RecyclerView

    private lateinit var matrixSession: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Init UI components
        super.onViewCreated(view, savedInstanceState)
        personalChatRecylerView = view.findViewById(R.id.recyview_social_personal)
        personalChatRecylerView.layoutManager = LinearLayoutManager(context)

        matrixSession = MatrixSessionHolder.getCurrentSession()!!

        val roomSummariesQuery = roomSummaryQueryParams {
            memberships = Membership.activeMemberships()
        }
        // Then you can subscribe to livedata..
        matrixSession.roomService().getRoomSummariesLive(roomSummariesQuery).observe(viewLifecycleOwner) {
            // ... And refresh your adapter with the list. It will be automatically updated when an item of the list is changed.
            updateRoomList(it)
            dataInitialize()
        }
    }

    private fun dataInitialize() {
        val roomList = ArrayList(MatrixSessionHolder.personalList)

        recyclerViewAdapter = PersonalItemAdapter(roomList, this@SocialPersonalFragment)
        personalChatRecylerView.adapter = recyclerViewAdapter
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")
        dataInitialize()
    }

    /**
     * Handle each item press in Social Group Fragment
     */
    override fun handlePersonalItemClickEvent(personal: RoomSummary) {
        MatrixSessionHolder.currentRoomID = personal.roomId
        startActivity(Intent(activity, SocialChatActivity::class.java))
    }

    /**
     * Handle add button press in Social Group Fragment
     */
    override fun handleAddButtonClickEvent() {
        startActivity(Intent(activity, AddFriendsActivity::class.java))
    }

    /**
     * Update and filter personal chat rooms
     */
    private fun updateRoomList(roomSummaryList: List<RoomSummary>?) {
        if (roomSummaryList == null) return

        MatrixSessionHolder.personalList = arrayListOf()
        val sortedRoomSummaryList = roomSummaryList.sortedByDescending {
            it.latestPreviewableEvent?.root?.originServerTs
        }.map {
            if(it.topic == "PERSONAL")
                MatrixSessionHolder.personalList.add(it)
        }
    }

}




