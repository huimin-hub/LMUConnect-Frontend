package de.lmu.lmuconnect.social.personal.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MessageItemAdapter(messageList: List<TimelineEvent?>, val context: Context): RecyclerView.Adapter<MessageItemAdapter.ViewHolder>(), Filterable {

    private var finalList = messageList

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        // Initialize views
        val chatMessageMeTextView : TextView = itemView.findViewById(R.id.text_chat_message_me)
        val chatTimestampMeTextView: TextView = itemView.findViewById(R.id.text_chat_timestamp_me)
        val chatMessageOtherTextView : TextView = itemView.findViewById(R.id.text_chat_message_other)
        val chatTimestampOtherTextView: TextView = itemView.findViewById(R.id.text_chat_timestamp_other)
        // only have one data in UI design
        val chatDateTextView: TextView = itemView.findViewById(R.id.text_chat_date)
        // to set if it is visible
        val chatMessageMeView: View = itemView.findViewById(R.id.layout_chat_container_me)
        val chatMessageOtherView: View = itemView.findViewById(R.id.layout_chat_container_other)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ui_social_chat_item, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = finalList[position]

        // Leave Date out if previous item already showed Date
        if (position == 0) {
            holder.chatDateTextView.text =
                currentItem?.root?.originServerTs?.let { formatDate(it) }
        } else {
            if(currentItem?.root?.originServerTs?.let { formatDate(it) } == finalList[position-1]?.root?.originServerTs?.let { formatDate(it) }) {
                holder.chatDateTextView.visibility = View.GONE
            } else {
                holder.chatDateTextView.visibility = View.VISIBLE
                holder.chatDateTextView.text =
                    currentItem?.root?.originServerTs?.let { formatDate(it) }
            }
        }


        // check if the message is from user (me)
        // if yes, show the message UI design chatMessageMeView
        // if no, show the message UI design chatMessageOthersView
        if (currentItem?.senderInfo?.userId == MatrixSessionHolder.currentUserId) {
            holder.chatMessageMeTextView.text = currentItem.root.getDecryptedTextSummary()
            holder.chatTimestampMeTextView.text = currentItem.root.originServerTs?.let { formatTime(it) }
            holder.chatMessageOtherView.visibility = View.GONE
            holder.chatMessageMeView.visibility = View.VISIBLE
            holder.chatTimestampOtherTextView.visibility = View.GONE
            holder.chatTimestampMeTextView.visibility = View.VISIBLE

        } else {
            holder.chatMessageOtherTextView.text = currentItem?.root?.getDecryptedTextSummary()
            holder.chatTimestampOtherTextView.text = currentItem?.root?.originServerTs?.let { formatTime(it) }
            holder.chatMessageOtherView.visibility = View.VISIBLE
            holder.chatMessageMeView.visibility = View.GONE
            holder.chatTimestampOtherTextView.visibility = View.VISIBLE
            holder.chatTimestampMeTextView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return finalList.size
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }


    // change format of date
    private fun formatDate(time:Long) : String {
        val diffInMillies = abs(Calendar.getInstance().time.time - time)
        val diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS).toInt()
        val date = Date(time)
        return when (diffInDays) {
            0 -> context.resources.getString(R.string.today)
            1 -> context.resources.getString(R.string.yesterday)
            else -> {
                if (diffInDays < 7) SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
                else SimpleDateFormat(context.resources.getString(R.string.study_calendar_title_date_format), Locale.getDefault()).format(date)
            }
        }
    }


    // change format of time
    private fun formatTime(time:Long) : String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }
}