package de.lmu.lmuconnect.study.courses.info

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.study.courses.api.StudyCoursesRegistrationRequest
import de.lmu.lmuconnect.study.courses.data.Course
import de.lmu.lmuconnect.study.courses.data.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CourseInfoActivity"

class CourseInfoActivity : AppCompatActivity() {

    private lateinit var iconImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var termTextView: TextView
    private lateinit var detailsRecyclerView: RecyclerView
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var detailsArrayList: ArrayList<Course.Detail>
    private lateinit var eventsArrayList: ArrayList<Event>

    private lateinit var courseId: String
    private lateinit var courseLsfId: String
    private lateinit var selectedUebungGroup: String
    private lateinit var prefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_info)

        prefs = this.getSharedPreferences("de.lmu.lmuconnect", Context.MODE_PRIVATE)
        dataInitialize()

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        // Init header infos
        iconImageView = findViewById(R.id.img_course_icon)
        nameTextView = findViewById(R.id.tv_course_name)
        typeTextView = findViewById(R.id.tv_course_type)
        termTextView = findViewById(R.id.tv_course_term)

        // Init recycler views
        detailsRecyclerView = findViewById(R.id.recyview_course_details)
        detailsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView = findViewById(R.id.recyview_course_events)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.study_courseinfo_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.action_unregister -> {
                ApiClient.getApiService().studyCoursesRegistrationDelete(
                    StudyCoursesRegistrationRequest(courseId),
                    SessionManager(this).fetchAuthToken()
                ).enqueue(
                    object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            when (response.code()) {
                                200 -> {
                                    // Toast.makeText(this@CourseInfoActivity, "Course removed", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                else -> Toast.makeText(this@CourseInfoActivity, response.code().toString() + " - " + response.message().uppercase(), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@CourseInfoActivity, t.message, Toast.LENGTH_SHORT).show()
                            Log.e(TAG, t.message!!)
                        }
                    })
            }

            R.id.action_open_lsf -> {
                val url = "https://lsf.verwaltung.uni-muenchen.de/qisserver/rds?state=verpublish&publishid=${courseLsfId}&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung&language=en"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }

            R.id.action_share -> {
                val url = "https://lsf.verwaltung.uni-muenchen.de/qisserver/rds?state=verpublish&publishid=${courseLsfId}&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung&language=en"
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, null))
            }

            R.id.action_change_group -> {
                val checkedItem = 1
                val items = arrayListOf<CharSequence>()

                for (event in eventsArrayList) {
                    if (event.group != null)
                        if (event.group != selectedUebungGroup) items.add(event.group)
                }

                AlertDialog.Builder(this@CourseInfoActivity)
                    .setTitle(getString(R.string.study_course_info_change_group))
                    .setNeutralButton(getString(R.string.study_add_dialogue_cancel)) { dialog, which ->
                        dialog.cancel()
                    }
                    .setPositiveButton(getString(R.string.study_add_dialogue_ok)) { dialog, which ->
                        val checked = (dialog as AlertDialog).listView.checkedItemPosition
                        prefs.edit().putString("group_lsf$courseLsfId", items[checked].toString()).apply()
                        val groupNr = prefs.getString("group_lsf$courseLsfId", null).toString()
                        Log.i(TAG, "Your selected group is $groupNr")
                        dialog.dismiss()
                        recreate()
                    }
                    .setSingleChoiceItems(items.toTypedArray(), checkedItem, null)
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dataInitialize() {
        detailsArrayList = arrayListOf()

        val courseId = intent.extras?.getString("courseId")
        if (courseId.isNullOrEmpty()) throw IllegalStateException("CourseInfoActivity must always be called with courseId parameter")

        ApiClient.getApiService().studyCoursesInfoGet(courseId,
            SessionManager(this).fetchAuthToken()
        ).enqueue(object : Callback<Course.Info> {
            override fun onResponse(call: Call<Course.Info>, response: Response<Course.Info>) {
                when (response.code()) {
                    200 -> {
                        // OK
                        val courseInfo = response.body()
                        if (courseInfo == null) {
                            Log.e(TAG, "Response body not existent!")
                            Toast.makeText(this@CourseInfoActivity, "ERROR", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }

                        this@CourseInfoActivity.courseId = courseInfo.id
                        this@CourseInfoActivity.courseLsfId = courseInfo.lsfId

                        // Header info
                        iconImageView.setImageResource(courseInfo.type.iconResId)
                        nameTextView.text = courseInfo.name
                        typeTextView.text = getString(courseInfo.type.stringRes)
                        termTextView.text = courseInfo.getPrettyTerm()

                        // Fill in details
                        detailsArrayList.add(
                            Course.Detail(
                                R.drawable.study_lecturer,
                                getString(R.string.study_course_detail_lecturer),
                                courseInfo.lecturer
                            )
                        )
                        detailsArrayList.add(
                            Course.Detail(
                                R.drawable.study_language,
                                getString(R.string.study_course_detail_language),
                                courseInfo.getPrettyLanguage()
                            )
                        )
                        detailsArrayList.add(
                            Course.Detail(
                                R.drawable.study_department,
                                getString(R.string.study_course_detail_department),
                                courseInfo.organisationalUnit
                            )
                        )

                        detailsRecyclerView.adapter = CourseDetailItemAdapter(detailsArrayList)

                        // Fill in events
                        eventsArrayList = ArrayList(courseInfo.events)
                        eventsRecyclerView.adapter =
                            CourseEventItemAdapter(eventsArrayList, this@CourseInfoActivity)

                        selectedUebungGroup = prefs.getString("group_lsf$courseLsfId", null).toString()
                        Log.i(TAG, "Your selected group is $selectedUebungGroup")
                    }
                    else -> Toast.makeText(
                        this@CourseInfoActivity,
                        response.code().toString() + " - " + response.message().uppercase(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Course.Info>, t: Throwable) {
                Toast.makeText(
                    this@CourseInfoActivity,
                    "ERROR: Couldn't get course info",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, t.message!!)
            }
        })
    }

    fun getCourseLsfId() : String {
        return this@CourseInfoActivity.courseLsfId
    }
}