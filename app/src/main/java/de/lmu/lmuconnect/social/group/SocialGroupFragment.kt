package de.lmu.lmuconnect.social.group

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.SocialAddButtonHandler
import de.lmu.lmuconnect.social.group.chat.SocialGroupChatActivity
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

private const val TAG = "SocialGroupFragment"

class SocialGroupFragment : Fragment(), GroupItemAdapterEventHandler, SocialAddButtonHandler {

    private lateinit var recyclerViewAdapter: GroupItemAdapter
    private lateinit var groupChatRecylerView : RecyclerView
    private lateinit var matrixSession : Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initialize UI components and matrix
        super.onViewCreated(view, savedInstanceState)
        groupChatRecylerView = view.findViewById(R.id.recyview_social_groups)
        groupChatRecylerView.layoutManager = LinearLayoutManager(context)

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

        val roomList = ArrayList(MatrixSessionHolder.groupList)

        recyclerViewAdapter = GroupItemAdapter(roomList, this@SocialGroupFragment)
        groupChatRecylerView.adapter = recyclerViewAdapter
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")
        dataInitialize()
    }

    /**
     * Handle add button press in Social Group Fragment
     */
    override fun handleAddButtonClickEvent() {
        startActivity(Intent(activity, AddGroupActivity::class.java))
    }

    /**
     * Handle each item press in Social Group Fragment
     */
    override fun handleGroupItemClickEvent(group: RoomSummary) {
        MatrixSessionHolder.currentRoomID = group.roomId
        startActivity(Intent(activity, SocialGroupChatActivity::class.java))
    }

    /**
     * Update and filter chat rooms, no-Personal room as group room
     */
    private fun updateRoomList(roomSummaryList: List<RoomSummary>?) {
        if (roomSummaryList == null) return

        MatrixSessionHolder.groupList = arrayListOf()
        val sortedRoomSummaryList = roomSummaryList.sortedByDescending {
            it.latestPreviewableEvent?.root?.originServerTs
        }.map {
            if(it.topic != "PERSONAL")
                MatrixSessionHolder.groupList.add(it)
        }
    }

}


