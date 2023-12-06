package de.lmu.lmuconnect.study.courses.add

import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.study.courses.CoursesItemAdapter
import de.lmu.lmuconnect.study.courses.CoursesItemAdapterEventHandler
import de.lmu.lmuconnect.study.courses.api.StudyCoursesGetResponse
import de.lmu.lmuconnect.study.courses.api.StudyCoursesRegistrationRequest
import de.lmu.lmuconnect.study.courses.data.Course
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "AddCourseActivity"

class AddCourseActivity : AppCompatActivity(), CoursesItemAdapterEventHandler {

    private lateinit var prefs: SharedPreferences
    // Components from UI
    private lateinit var recyclerViewAdapter: CoursesItemAdapter
    private lateinit var recyclerView: RecyclerView

    // For filtering function
    private var filterArrayList = arrayListOf<String>()
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the Intent
            filterArrayList = result.data?.getStringArrayListExtra("data") as ArrayList<String>
            for (string in filterArrayList) Log.i(TAG, "Filtering term: $string")
            applyFilterToRecyclerView(formatsArrayListToFilterConstraintSequence(filterArrayList))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        prefs = this.getSharedPreferences("de.lmu.lmuconnect", Context.MODE_PRIVATE)
        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize
        recyclerView = findViewById(R.id.recyview_study_add_course)

        // Handle RecyclerViews
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataInitialize()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.study_addcourse_top_appbar, menu)

        if (menu != null) {
            val searchItem = menu.findItem(R.id.action_search)
            val searchView = searchItem.actionView as SearchView

            // Style
            val searchEditFrame: LinearLayout =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_edit_frame) as LinearLayout
            val layoutParams: LinearLayout.LayoutParams = searchEditFrame.layoutParams as LinearLayout.LayoutParams
            layoutParams.leftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -8f, resources.displayMetrics).toInt()

            // Associate searchable configuration with the SearchView
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.apply {
                setSearchableInfo(searchManager.getSearchableInfo(componentName))
            }

            // Filter while typing
            searchView.setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Do nothing
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "Text changed: $newText")
                    if (newText != null) performSearch(newText)
                    return true
                }
            })

            searchView.setOnCloseListener {
                performSearch("")
                true
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_filter -> {
                openActivityForResult(CourseFilterActivity::class.java)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dataInitialize() {
        ApiClient.getApiService().studyCoursesAllGet(SessionManager(this).fetchAuthToken()).enqueue(
            object : Callback<StudyCoursesGetResponse> {

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
                                Toast.makeText(this@AddCourseActivity, "ERROR", Toast.LENGTH_SHORT)
                                    .show()
                                return
                            }
                            recyclerViewAdapter =
                                CoursesItemAdapter(ArrayList(responseBody.items), this@AddCourseActivity)
                            recyclerView.adapter = recyclerViewAdapter
                        }
                        else -> Toast.makeText(
                            this@AddCourseActivity,
                            response.code().toString() + " - " + response.message().uppercase(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudyCoursesGetResponse>, t: Throwable) {
                    Toast.makeText(this@AddCourseActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun openActivityForResult(activity: Class<*>) {
        val intent = Intent(this, activity)
        resultLauncher.launch(intent)
    }

    private fun formatsArrayListToFilterConstraintSequence(constraints: ArrayList<String>) : String{
        var constraintTest = ""
        for (constraint in constraints) constraintTest += "$constraint && "

        return constraintTest
    }

    private fun applyFilterToRecyclerView(constraintSequence : String) {
        recyclerViewAdapter.getSemesterFilter().filter(constraintSequence)
    }

    override fun handleCourseItemClickEvent(course: Course.Basic) {

        ApiClient.getApiService().studyCoursesInfoGet(course.id,
            SessionManager(this).fetchAuthToken()
        ).enqueue(object : Callback<Course.Info> {
            override fun onResponse(call: Call<Course.Info>, response: Response<Course.Info>) {
                when (response.code()) {
                    200 -> {
                        // OK
                        val courseInfo = response.body()
                        if (courseInfo == null) {
                            Log.e(TAG, "Response body not existent!")
                            Toast.makeText(this@AddCourseActivity, "ERROR", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                        val items = arrayListOf<CharSequence>()
                        val checkedItem = 1

                        for (event in courseInfo.events) {
                            if (event.group != null) {
                                items.add(event.group)
                            }
                        }

                        if (courseInfo.events.size > 1 && courseInfo.events[0].group != null) {
                            AlertDialog.Builder(this@AddCourseActivity)
                                .setTitle(getString(R.string.study_add_popup_message))
                                .setNeutralButton(getString(R.string.study_add_dialogue_cancel)) { dialog, which ->
                                    dialog.cancel()
                                }
                                .setPositiveButton(getString(R.string.study_add_dialogue_ok)) { dialog, which ->
                                    val checked = (dialog as AlertDialog).listView.checkedItemPosition
                                    prefs.edit().putString("group_lsf" + courseInfo.lsfId, items[checked].toString()).apply()
                                    val groupNr = prefs.getString("group_lsf" + courseInfo.lsfId, null)
                                    Log.i(TAG, "Your selected group is $groupNr")
                                    dialog.dismiss()

                                    registerForACourse(course)

                                }
                                .setSingleChoiceItems(items.toTypedArray(), checkedItem, null)

                                .show()
                        } else {
                            registerForACourse(course)
                        }

                    }
                }
            }

            override fun onFailure(call: Call<Course.Info>, t: Throwable) {
                Toast.makeText(
                    this@AddCourseActivity,
                    "ERROR: Couldn't get course info",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, t.message!!)
            }
        })
    }

    private fun registerForACourse(course: Course) {
        ApiClient.getApiService().studyCoursesRegistrationPost(
            StudyCoursesRegistrationRequest(course.id),
            SessionManager(this).fetchAuthToken()
        ).enqueue(
            object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        200 -> {
                            // Toast.makeText(this@AddCourseActivity, "Course added", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else -> Toast.makeText(this@AddCourseActivity, response.code().toString() + " - " + response.message().uppercase(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddCourseActivity, t.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message!!)
                }
            })
    }

    private fun performSearch(searchQuery: String) {
        Log.d(TAG, "Performing search...")
        recyclerViewAdapter.filter.filter(searchQuery)
    }
}