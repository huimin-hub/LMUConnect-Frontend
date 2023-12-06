package de.lmu.lmuconnect.social.group.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.Constants
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.social.api.UserMailGetMatrixResponse
import de.lmu.lmuconnect.social.profile.SocialProfileOthersActivity
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class GroupMessageItemAdapter(messageList: List<TimelineEvent?>, val context: Context): RecyclerView.Adapter<GroupMessageItemAdapter.ViewHolder>(), Filterable {

    private var finalList = messageList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatMessageMeTextView : TextView = itemView.findViewById(R.id.text_chat_message_me)
        val chatTimestampMeTextView: TextView = itemView.findViewById(R.id.text_chat_timestamp_me)
        val chatImageOtherImageView: ShapeableImageView = itemView.findViewById(R.id.image_gchat_profile_other)
        val chatNameOtherTextView: TextView = itemView.findViewById(R.id.text_gchat_user_other)
        val chatMessageOtherTextView : TextView = itemView.findViewById(R.id.text_gchat_message_other)
        val chatTimestampOtherTextView: TextView = itemView.findViewById(R.id.text_gchat_timestamp_other)
        // Initialize Date
        val chatDateTextView: TextView = itemView.findViewById(R.id.text_gchat_date)

        // to set if it is visible
        val chatMessageMeView: View = itemView.findViewById(R.id.layout_gchat_container_me)
        val chatMessageOtherView: View = itemView.findViewById(R.id.layout_gchat_container_other)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // add UI design for message item of group chat
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ui_social_gchat_item, parent, false)

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
        // if yes, show the message UI design without profile image and name from right side
        // if no, show the message UI design with profile image and name from left side
        if (currentItem?.senderInfo?.userId == MatrixSessionHolder.currentUserId) {
                holder.chatMessageMeTextView.text = currentItem.root.getDecryptedTextSummary()
                holder.chatTimestampMeTextView.text =
                    currentItem.root.originServerTs?.let { formatTime(it) }
                holder.chatMessageMeView.visibility = View.VISIBLE
                holder.chatTimestampMeTextView.visibility = View.VISIBLE
                holder.chatMessageOtherView.visibility = View.GONE
                holder.chatNameOtherTextView.visibility = View.GONE
                holder.chatImageOtherImageView.visibility = View.GONE
                holder.chatTimestampOtherTextView.visibility = View.GONE
            } else {
            currentItem?.senderInfo?.userId?.let {
                ApiClient.getApiService().userNameMatrixGet(it).enqueue(object : Callback<UserMailGetMatrixResponse> {
                    override fun onResponse(call: Call<UserMailGetMatrixResponse>, response: Response<UserMailGetMatrixResponse>) {
                        val path = Constants.IMAGE_URL + response.body()?.email
                        Picasso.get().load(path).fit().into(holder.chatImageOtherImageView)
                    }

                    override fun onFailure(call: Call<UserMailGetMatrixResponse>, t: Throwable) {
                    }
                })
            }
                holder.chatNameOtherTextView.text = currentItem?.senderInfo?.displayName
                holder.chatMessageOtherTextView.text = currentItem?.root?.getDecryptedTextSummary()
                holder.chatTimestampOtherTextView.text =
                    currentItem?.root?.originServerTs?.let { formatTime(it) }
                holder.chatMessageMeView.visibility = View.GONE
                holder.chatTimestampMeTextView.visibility = View.GONE
                holder.chatMessageOtherView.visibility = View.VISIBLE
                holder.chatNameOtherTextView.visibility = View.VISIBLE
                holder.chatImageOtherImageView.visibility = View.VISIBLE
                holder.chatTimestampOtherTextView.visibility = View.VISIBLE

                holder.chatImageOtherImageView.setOnClickListener {
                    val intent = Intent(context,SocialProfileOthersActivity::class.java)
                    intent.putExtra("name", currentItem?.senderInfo?.displayName)
                    context.startActivity(intent)
                }
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