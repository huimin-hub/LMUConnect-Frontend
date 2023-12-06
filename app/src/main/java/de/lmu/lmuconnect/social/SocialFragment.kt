package de.lmu.lmuconnect.social

//import coil.ImageLoader
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.SocialApplication
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.data.PersonalChat
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.RoomSortOrder
import org.matrix.android.sdk.api.session.room.RoomSummaryQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

const val TAG = "SocialFragment"
private const val ROOM_ID_ARGS = "ROOM_ID_ARGS"

class SocialFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var addButton: ExtendedFloatingActionButton

    private lateinit var matrix: Matrix
    private var socialSession: Session? = null
    private lateinit var roomSummaryQueryParams: RoomSummaryQueryParams
    private lateinit var personalSummariesList: ArrayList<RoomSummary>
    private lateinit var groupSummariesList: ArrayList<RoomSummary>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views in UI components
        tabLayout = view.findViewById(R.id.tab_social)
        viewPager = view.findViewById(R.id.viewpager_social)
        addButton = view.findViewById(R.id.fab_social_add)
        // Initialize matrix
        matrix = SocialApplication.getMatrix(requireContext())
        socialSession = MatrixSessionHolder.getCurrentSession()!!
        // Create query to listen to room summary list
        roomSummaryQueryParams = roomSummaryQueryParams {
            memberships = Membership.activeMemberships()
        }
        personalSummariesList = arrayListOf()
        groupSummariesList = arrayListOf()

        // Handle viewPager
        val viewPagerAdapter = SocialPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        // Link with tabs
        val tabTitles = arrayOf(
            getString(R.string.social_tab_item1),
            getString(R.string.social_tab_item2)
        )
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        //Handle fab
        addButton.setOnClickListener {
            val currentFragment = viewPagerAdapter.getFragmentAtPosition(viewPager.currentItem)

            if (currentFragment is SocialAddButtonHandler) // Current fragment can handle add button clicks
                currentFragment.handleAddButtonClickEvent()
        }

        val buttonTitles = arrayOf(
            getString(R.string.social_floatingbtn_title_1),
            getString(R.string.social_floatingbtn_title_2)
        )
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addButton.text = buttonTitles[position]
            }
        })

        assignRooms()
        // update personal list and group list
        MatrixSessionHolder.personalList = personalSummariesList
        MatrixSessionHolder.groupList = groupSummariesList

        /**session!!.roomService().getRoomSummariesLive(roomSummaryQueryParams).observe(viewLifecycleOwner) {
            MatrixSessionHolder.personalList = it
            Log.e(TAG, "Changed Room list")
        }**/
    }

    private fun convertData(data : List<RoomSummary>) : ArrayList<PersonalChat> {
        val result : ArrayList<PersonalChat> = ArrayList()

        data.forEachIndexed { i, e ->
            result[i].id = e.roomId
            result[i].lastMessage = e.latestPreviewableEvent.toString()
        }
        Log.e(TAG, result.toString())
        return result
    }

    private fun formatMessage(timelineEvent: TimelineEvent): String {
        val messageContent = timelineEvent.root.getClearContent().toModel<MessageContent>() ?: return ""
        return messageContent.body
    }

    private fun createRoom() {
        TODO("Create rooms in matrix")

        //session!!.roomService().createRoom()
    }

    /**
     * filter room into personal list and group list
     */
    private fun assignRooms() {
        val roomSummaryList = socialSession!!.roomService().getRoomSummaries(roomSummaryQueryParams, sortOrder = RoomSortOrder.ACTIVITY)
        roomSummaryList.forEach {
            if(it.topic == "PERSONAL") {
                personalSummariesList.add(it)
            } else {
                groupSummariesList.add(it)
            }
        }
    }
}
