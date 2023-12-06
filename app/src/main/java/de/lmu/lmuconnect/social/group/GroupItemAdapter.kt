package de.lmu.lmuconnect.social.group

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R

import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "GroupItemAdapter"

class GroupItemAdapter(private val groupList: ArrayList<RoomSummary>, private val eventHandler: GroupItemAdapterEventHandler) :
    RecyclerView.Adapter<GroupItemAdapter.ViewHolder>(), Filterable {

    var finalList : ArrayList<RoomSummary> = groupList

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        // Initialize view in UI item design
        val groupsProfile : ImageView = itemView.findViewById(R.id.social_groups_item_image)
        val groupsName : TextView = itemView.findViewById(R.id.social_groups_item_name)
        val groupsMessage : TextView = itemView.findViewById(R.id.social_groups_item_content)
        val messageTime : TextView = itemView.findViewById(R.id.social_groups_item_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Initialize UI item design
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ui_social_groups_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = finalList[position]

        // set profile image of group based on group type
        when(currentItem.topic) {
            "LECTURE" -> holder.groupsProfile.setImageResource(R.drawable.study_lecture)
            "INTERNSHIP" -> holder.groupsProfile.setImageResource(R.drawable.study_practical)
            "SEMINAR" -> holder.groupsProfile.setImageResource(R.drawable.study_seminar)
            "TUTORIAL" -> holder.groupsProfile.setImageResource(R.drawable.study_tutorial)
            "EXERCISE" -> holder.groupsProfile.setImageResource(R.drawable.study_exercise)
            "OTHERS" -> holder.groupsProfile.setImageResource(R.drawable.social_others)
            else -> holder.groupsProfile.setImageResource(R.drawable.menu_icon_people)
        }
        // set text view of items
        holder.groupsName.text = currentItem.displayName
        holder.groupsMessage.text = currentItem.latestPreviewableEvent?.let { formatMessage(it) }
        holder.messageTime.text = currentItem.latestPreviewableEvent?.let { formatDate(it) }
        if(currentItem.hasUnreadMessages) {
            holder.groupsMessage.setTypeface(null, Typeface.BOLD_ITALIC)
        }

        holder.itemView.setOnClickListener { eventHandler.handleGroupItemClickEvent(currentItem) }
    }

    override fun getItemCount(): Int {
        return finalList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val filterResults = FilterResults()

                if (constraint == null || constraint.isEmpty()) {
                    filterResults.values = groupList
                    filterResults.count = groupList.size
                } else {

                    val filteredList = arrayListOf<RoomSummary>()
                    for (chat in groupList) {
                        if (chat.name.lowercase().contains(constraint.toString().lowercase())) {
                            filteredList.add(chat)
                        }
                    }

                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                }
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                finalList = results?.values as ArrayList<RoomSummary>

                notifyDataSetChanged()
            }
        }}}

    fun setImageResource(picture: String) {

    }
    private fun formatMessage(timelineEvent: TimelineEvent): String {
        val messageContent = timelineEvent.root.getClearContent().toModel<MessageContent>() ?: return ""
        return messageContent.body
    }

private fun formatDate(timelineEvent: TimelineEvent): String {
    val root = timelineEvent.root.originServerTs
    return root?.let { DateUtils.formatSameDayTime(it, System.currentTimeMillis(), 3, 3) } as String
}
