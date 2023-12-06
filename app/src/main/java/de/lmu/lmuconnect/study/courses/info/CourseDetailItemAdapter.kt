package de.lmu.lmuconnect.study.courses.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.study.courses.data.Course

class CourseDetailItemAdapter(
    private val detailsList: ArrayList<Course.Detail>,
) : RecyclerView.Adapter<CourseDetailItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.img_course_detail)
        val titleTextView: TextView = itemView.findViewById(R.id.tv_course_detail_title)
        val contentTextView: TextView = itemView.findViewById(R.id.tv_course_detail_content)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ui_study_course_detail_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = detailsList[position]

        holder.iconImageView.setImageResource(currentItem.iconRes)
        holder.titleTextView.text = currentItem.title
        holder.contentTextView.text = currentItem.content
    }

    override fun getItemCount(): Int {
        return detailsList.size
    }
}