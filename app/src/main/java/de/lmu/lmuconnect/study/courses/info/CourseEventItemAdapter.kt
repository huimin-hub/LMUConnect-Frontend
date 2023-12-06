package de.lmu.lmuconnect.study.courses.info

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.study.courses.data.Event
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.math.roundToInt

private const val TAG = "CourseEventItemAdapter"

class CourseEventItemAdapter(
    private val eventsList: ArrayList<Event>, private val context: Context
) : RecyclerView.Adapter<CourseEventItemAdapter.ViewHolder>() {

    private lateinit var prefs: SharedPreferences

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.img_course_event)
        val titleTextView: TextView = itemView.findViewById(R.id.tv_course_event_title)
        val dateTextView: TextView = itemView.findViewById(R.id.tv_course_event_time)
        val locationTextView: TextView = itemView.findViewById(R.id.tv_course_event_location)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ui_study_course_event_item, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = eventsList[position]

        if (context is CourseInfoActivity) {
            prefs = context.getSharedPreferences("de.lmu.lmuconnect", Context.MODE_PRIVATE)
            val groupNr = prefs.getString("group_lsf" + context.getCourseLsfId(), null)
            if (currentItem.group != null && currentItem.group != groupNr) {
                holder.itemView.alpha = 0.3F
                Log.i(TAG, "This is not group$groupNr, this cardboard will not appear")
            }
        }

        holder.iconImageView.setImageResource(currentItem.type.iconResId)
        if (currentItem.group == null) holder.titleTextView.text = context.getString(currentItem.type.stringRes)
        else holder.titleTextView.text =
            "${context.getString(currentItem.type.stringRes)} - ${context.getString(R.string.study_event_group)} ${currentItem.group}"
        holder.dateTextView.text = generateDateText(currentItem)
        holder.locationTextView.text = currentItem.location

        holder.itemView.setOnClickListener {
            val url = currentItem.roomfinderLink
            if (url != null) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(browserIntent)

                // TODO: Pull request to roomfinder app to unlock this
                /* val roomfinderIntent = Intent()
                roomfinderIntent.setClassName("de.lmu.navigator", "de.lmu.navigator.indoor.FloorViewActivity")
                roomfinderIntent.putExtra("EXTRA_BUILDING_CODE", "bw0147")
                roomfinderIntent.putExtra("EXTRA_ROOM_CODE", "014701112_")
                context.startActivity(roomfinderIntent) */
            } else {
                Toast.makeText(context, context.getString(R.string.study_courses_no_roomfinder_link), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    private fun generateDateText(event: Event): String {
        val weekday = if (event.weekday == -1) "Everyday" else DayOfWeek.of(event.weekday + 1).getDisplayName(TextStyle.FULL, Locale.getDefault())
        val time = generateSingleTimeText(event.startTime) + " - " + generateSingleTimeText(event.endTime)
        val isCT = if (event.isCT) "c.t." else ""

        val frequency = event.frequency.toString()
        val dateFormatter = SimpleDateFormat("LLL dd", Locale.getDefault())
        val date = if (event.frequency == Event.Frequency.SINGLE) dateFormatter.format(event.startDate) else dateFormatter.format(event.startDate) + " - " + dateFormatter.format(event.endDate)

        return "$weekday, $time $isCT\n$frequency, $date"
    }

    private fun generateSingleTimeText(time: Float): String {
        return time.toInt().toString() + ":" + ((time - time.toInt()) * 60).roundToInt().toString().padStart(2, '0')
    }
}