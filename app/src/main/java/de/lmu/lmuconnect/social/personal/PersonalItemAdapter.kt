package de.lmu.lmuconnect.social.personal

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.Constants
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.social.api.UserMailGetMatrixResponse
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "PersonalItemAdapter"

class PersonalItemAdapter(private val personalChatList: ArrayList<RoomSummary>, private val eventHandler: PersonalItemAdapterEventHandler) :
    RecyclerView.Adapter<PersonalItemAdapter.ViewHolder>(), Filterable {

    var finalList : ArrayList<RoomSummary> = personalChatList

    // You can grab a room from the session
    // If the room is not known (not received from sync) it will return null

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        // Init views
        val personalProfile : ImageView = itemView.findViewById(R.id.social_personal_item_image)
        val personalName : TextView = itemView.findViewById(R.id.social_personal_item_name)
        val personalMessage : TextView = itemView.findViewById(R.id.social_personal_item_content)
        val messageTime : TextView = itemView.findViewById(R.id.social_personal_item_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ui_social_personal_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = finalList[position]

        holder.personalName.text = currentItem.displayName
        holder.personalMessage.text = currentItem.latestPreviewableEvent?.let { formatMessage(it) }
        holder.messageTime.text = currentItem.latestPreviewableEvent?.let { formatDate(it) }
        if(currentItem.hasUnreadMessages) {
            holder.personalMessage.setTypeface(null, Typeface.BOLD_ITALIC)
        }
        // Get the Profile Picture
        if(currentItem.otherMemberIds.isNotEmpty()) {
            currentItem.otherMemberIds.let {
                ApiClient.getApiService().userNameMatrixGet(it[0])
                    .enqueue(object : Callback<UserMailGetMatrixResponse> {
                        override fun onResponse(
                            call: Call<UserMailGetMatrixResponse>,
                            response: Response<UserMailGetMatrixResponse>
                        ) {
                            val path = Constants.IMAGE_URL + response.body()?.email
                            Picasso.get().load(path).fit().into(holder.personalProfile)
                        }

                        override fun onFailure(
                            call: Call<UserMailGetMatrixResponse>,
                            t: Throwable
                        ) {
                            Log.e(TAG, call.toString())
                            Log.e(TAG, t.toString())
                        }
                    })
            }
        }


        holder.itemView.setOnClickListener { eventHandler.handlePersonalItemClickEvent(currentItem) }
    }

    override fun getItemCount(): Int {
        return finalList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    filterResults.values = personalChatList
                    filterResults.count = personalChatList.size
                } else {
                    val filteredList = arrayListOf<RoomSummary>()
                    for (chat in personalChatList) {
                        if (chat.latestPreviewableEvent?.root?.senderId?.lowercase()
                                ?.contains(constraint.toString().lowercase()) == true
                        ) {
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
        }}
    }

/**
 * set format of message
 */
private fun formatMessage(timelineEvent: TimelineEvent): String {
        val messageContent = timelineEvent.root.getClearContent().toModel<MessageContent>() ?: return ""
        return messageContent.body
    }

/**
 * set format of date
 */
private fun formatDate(timelineEvent: TimelineEvent): String {
    val root = timelineEvent.root.originServerTs
    return root?.let { DateUtils.formatSameDayTime(it, System.currentTimeMillis(), 3, 3) } as String
}
