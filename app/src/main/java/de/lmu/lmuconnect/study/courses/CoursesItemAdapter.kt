package de.lmu.lmuconnect.study.courses

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.study.courses.data.Course

private const val TAG = "CoursesItemAdapter"

class CoursesItemAdapter(private val coursesList: ArrayList<Course.Basic>, private val eventHandler: CoursesItemAdapterEventHandler) :
    RecyclerView.Adapter<CoursesItemAdapter.ViewHolder>(), Filterable {

    var finalList : ArrayList<Course.Basic> = coursesList

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val courseIcon : ImageView = itemView.findViewById(R.id.img_study_item_icon)
        val lectureName : TextView = itemView.findViewById(R.id.tv_study_item_lecture)
        val lecturerName : TextView = itemView.findViewById(R.id.tv_study_item_lecturer)
        val semesterTextView : TextView = itemView.findViewById(R.id.tv_study_item_sem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ui_study_course_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = finalList[position]

        if (currentItem.type != Course.Type.LECTURE)
            holder.courseIcon.setBackgroundResource(R.color.lmu_black)
        else holder.courseIcon.setBackgroundResource(R.color.lmu_green)
        holder.courseIcon.setImageResource(currentItem.type.iconResId)
        holder.lectureName.text = currentItem.name
        holder.lecturerName.text = currentItem.lecturer
        holder.semesterTextView.text = currentItem.getPrettyTerm()

        holder.itemView.setOnClickListener { eventHandler.handleCourseItemClickEvent(currentItem) }
    }

    override fun getItemCount(): Int {
        return finalList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    filterResults.values = coursesList
                    filterResults.count = coursesList.size
                } else {
                    val filteredList = arrayListOf<Course.Basic>()
                    for (course in coursesList) {
                        if (course.name.lowercase().contains(constraint.toString().lowercase())) {
                            filteredList.add(course)
                        }
                    }

                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                }
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                finalList = results?.values as ArrayList<Course.Basic>

                notifyDataSetChanged()
            }

        }
    }

    fun getSemesterFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    filterResults.values = finalList
                    filterResults.count = finalList.size
                } else {
                    val filteredList = arrayListOf<Course.Basic>()
                    val constraintsArray = constraint.toString().split(" && ")
                    for (string in constraintsArray) {
                        if (string != "") {
                            Log.d(TAG, string)
                            for (course in coursesList) {
                                if (course.term.lowercase() == string.lowercase().trim()) {
                                    filteredList.add(course)
                                }
                            }
                        }
                    }

                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                }
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                finalList = results?.values as ArrayList<Course.Basic>
                notifyDataSetChanged()
            }

        }
    }
}