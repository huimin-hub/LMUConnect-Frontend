package de.lmu.lmuconnect.study.courses

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.study.StudyAddButtonHandler
import de.lmu.lmuconnect.study.courses.add.AddCourseActivity
import de.lmu.lmuconnect.study.courses.api.StudyCoursesGetResponse
import de.lmu.lmuconnect.study.courses.data.Course
import de.lmu.lmuconnect.study.courses.info.CourseInfoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "StudyCoursesFragment"

class StudyCoursesFragment : Fragment(), CoursesItemAdapterEventHandler, StudyAddButtonHandler {

    // Components from UI
    private lateinit var recyclerViewAdapter: CoursesItemAdapter
    private lateinit var lecturesTitleTextView: TextView
    private lateinit var lecturesRecyclerView: RecyclerView
    private lateinit var practicalsTitleTextView: TextView
    private lateinit var practicalsRecyclerView: RecyclerView
    private lateinit var lecturesEmptyTextView: TextView

    // Data arrays
    private lateinit var lecturesArrayList: ArrayList<Course.Basic>
    private lateinit var practicalsArrayList: ArrayList<Course.Basic>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize
        lecturesTitleTextView = view.findViewById(R.id.tv_study_lectures_title)
        lecturesRecyclerView = view.findViewById(R.id.recyview_study_lectures)
        practicalsTitleTextView = view.findViewById(R.id.tv_study_practicals_title)
        practicalsRecyclerView = view.findViewById(R.id.recyview_study_practicals)
        lecturesEmptyTextView = view.findViewById(R.id.tv_study_courses_empty)

        // Handle RecyclerViews
        lecturesRecyclerView.layoutManager = LinearLayoutManager(context)
        practicalsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun dataInitialize() {
        lecturesArrayList = arrayListOf()
        practicalsArrayList = arrayListOf()

        ApiClient.getApiService()
            .studyCoursesPersonalGet(SessionManager(requireContext()).fetchAuthToken())
            .enqueue(object :
                Callback<StudyCoursesGetResponse> {
                override fun onResponse(
                    call: Call<StudyCoursesGetResponse>,
                    response: Response<StudyCoursesGetResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            // OK
                            val responseBody = response.body()
                            if (responseBody == null) {
                                Log.e(TAG, "Response body not existent!")
                                Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                                return
                            }

                            for (course in responseBody.items) {
                                if (course.type == Course.Type.LECTURE) lecturesArrayList.add(course)
                                else practicalsArrayList.add(course)
                            }

                            if (lecturesArrayList.isEmpty() && practicalsArrayList.isEmpty()) {
                                lecturesEmptyTextView.visibility = View.VISIBLE
                            } else {
                                lecturesEmptyTextView.visibility = View.GONE
                            }

                            if (lecturesArrayList.isEmpty()) {
                                lecturesTitleTextView.visibility = View.GONE
                                lecturesRecyclerView.visibility = View.GONE
                            } else {
                                lecturesTitleTextView.visibility = View.VISIBLE
                                lecturesRecyclerView.visibility = View.VISIBLE
                                recyclerViewAdapter =
                                    CoursesItemAdapter(lecturesArrayList, this@StudyCoursesFragment)
                                lecturesRecyclerView.adapter = recyclerViewAdapter
                            }

                            if (practicalsArrayList.isEmpty()) {
                                practicalsTitleTextView.visibility = View.GONE
                                practicalsRecyclerView.visibility = View.GONE
                            } else {
                                practicalsTitleTextView.visibility = View.VISIBLE
                                practicalsRecyclerView.visibility = View.VISIBLE
                                recyclerViewAdapter =
                                    CoursesItemAdapter(practicalsArrayList, this@StudyCoursesFragment)
                                practicalsRecyclerView.adapter = recyclerViewAdapter
                            }
                        }
                        else -> Toast.makeText(
                            context,
                            response.code().toString() + " - " + response.message().uppercase(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudyCoursesGetResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message!!)
                }
            })
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")
        dataInitialize()
    }

    override fun handleCourseItemClickEvent(course: Course.Basic) {
        val intent = Intent(activity, CourseInfoActivity::class.java)
        intent.putExtra("courseId", course.id)
        startActivity(intent)
    }

    override fun handleAddButtonClickEvent() {
        startActivity(Intent(activity, AddCourseActivity::class.java))
    }
}