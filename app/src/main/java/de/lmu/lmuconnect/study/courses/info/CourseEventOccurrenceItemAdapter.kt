package de.lmu.lmuconnect.study.courses.info

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.study.calendar.data.EventOccurrence
import de.lmu.lmuconnect.study.courses.data.Course
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToInt

private const val TAG = "CourseEventOccurrenceItemAdapter"

class CourseEventOccurrenceItemAdapter(
    private val eventsList: ArrayList<EventOccurrence>, private val context: Context
) : RecyclerView.Adapter<CourseEventOccurrenceItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.img_study_item_icon)
        val titleTextView: TextView = itemView.findViewById(R.id.tv_study_item_lecture)
        val dateTextView: TextView = itemView.findViewById(R.id.tv_study_item_time)
        val locationTextView: TextView = itemView.findViewById(R.id.tv_study_item_location)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ui_study_event_occurrence_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = eventsList[position]

        // Calculate days until event
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)
        val diffInMillies = abs(currentItem.date.time - now.time.time)
        val diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS).toInt()


        if (currentItem.type != Course.Type.LECTURE)
            holder.iconImageView.setBackgroundResource(R.color.lmu_black)
        else holder.iconImageView.setBackgroundResource(R.color.lmu_green)
        holder.iconImageView.setImageResource(currentItem.type.iconResId)
        holder.titleTextView.text = currentItem.courseName
        holder.dateTextView.text = buildString {
            append(
                when (diffInDays) {
                    0 -> context.resources.getString(R.string.today)
                    1 -> context.resources.getString(R.string.tomorrow)
                    else -> SimpleDateFormat("EEEE", Locale.getDefault()).format(currentItem.date)
                }
            )
            append(", ")
            append(generateSingleTimeText(currentItem.startTime))
            append(" - ")
            append(generateSingleTimeText(currentItem.endTime))
            append(if (currentItem.isCT) " c.t." else "")
        }
        holder.locationTextView.text = currentItem.location

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CourseInfoActivity::class.java)
            intent.putExtra("courseId", currentItem.courseId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    private fun generateSingleTimeText(time: Float): String {
        return time.toInt().toString() + ":" + ((time - time.toInt()) * 60).roundToInt().toString()
            .padStart(2, '0')
    }
}