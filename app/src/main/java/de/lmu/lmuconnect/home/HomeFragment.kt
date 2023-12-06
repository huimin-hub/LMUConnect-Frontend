package de.lmu.lmuconnect.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.home.api.NewsGetResponse
import de.lmu.lmuconnect.home.data.News
import de.lmu.lmuconnect.study.calendar.api.StudyEventsGetResponse
import de.lmu.lmuconnect.study.courses.info.CourseEventOccurrenceItemAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    // Components for view
    private lateinit var newsRecyclerView: CarouselRecyclerview
    private lateinit var newsFeedAdapter: NewsFeedAdapter
    private lateinit var eventsRecyclerView: RecyclerView

    // Data
    private lateinit var newsFeedList: ArrayList<News>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize
        newsRecyclerView = view.findViewById(R.id.carouselRecyview_home_news)
        eventsRecyclerView = view.findViewById(R.id.recyview_upcoming_events)
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        dataInitialize()
        super.onResume()
    }

    private fun dataInitialize() {
        // Init arrayList
        newsFeedList = arrayListOf()

        ApiClient.getApiService().newsFeedGet().enqueue(object :
            Callback<NewsGetResponse> {
            override fun onResponse(
                call: Call<NewsGetResponse>,
                response: Response<NewsGetResponse>
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

                        for (news in responseBody.items) {
                            val newsFeed = News(
                                news.id,
                                news.title,
                                news.abstract,
                                news.link,
                                news.date,
                                news.imageUrl
                            )
                            Log.d(TAG, newsFeed.toString())
                            newsFeedList.add(newsFeed)

                            newsFeedAdapter = NewsFeedAdapter(newsFeedList, this@HomeFragment)
                            newsRecyclerView.adapter = newsFeedAdapter
                        }
                    }
                    else -> Toast.makeText(
                        context,
                        response.code().toString() + " - " + response.message().uppercase(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<NewsGetResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message!!)
            }
        })

        // Upcoming events
        val currentDate = Calendar.getInstance()
        val startDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(currentDate.time)
        currentDate.add(Calendar.DAY_OF_MONTH, 3) // how many days should we look ahead?
        val endDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(currentDate.time)


        ApiClient.getApiService().studyEventsGet(
            startDateStr,
            endDateStr,
            SessionManager(requireContext()).fetchAuthToken()
        ).enqueue(object : Callback<StudyEventsGetResponse> {
            override fun onResponse(
                call: Call<StudyEventsGetResponse>,
                response: Response<StudyEventsGetResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        // OK
                        var events = response.body()?.events
                        if (events == null) {
                            Log.e(TAG, "Response body not existent!")
                            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                            return
                        }

                        events = events.filter {
                            // Calculate days until event
                            val now = Calendar.getInstance()
                            val time =
                                now.get(Calendar.HOUR_OF_DAY) + (now.get(Calendar.MINUTE) / 60f)
                            now.set(Calendar.HOUR_OF_DAY, 0)
                            now.set(Calendar.MINUTE, 0)
                            now.set(Calendar.SECOND, 0)
                            now.set(Calendar.MILLISECOND, 0)
                            val diffInMillies = abs(it.date.time - now.time.time)
                            val diffInDays =
                                TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS).toInt()

                            diffInDays != 0 || it.endTime > time
                        }

                        if (context != null)
                            eventsRecyclerView.adapter =
                                CourseEventOccurrenceItemAdapter(ArrayList(events), context!!)
                    }
                    else -> Toast.makeText(
                        context,
                        response.code().toString() + " - " + response.message().uppercase(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<StudyEventsGetResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message!!)
            }

        })
    }
}