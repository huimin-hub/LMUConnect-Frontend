package de.lmu.lmuconnect.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.menu.api.MenuItemsGetResponse
import de.lmu.lmuconnect.menu.data.MenuItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MenuFragment"

class MenuFragment : Fragment() {

    // Components from UI
    private lateinit var recyclerViewAdapter: MenuItemAdapter
    private lateinit var favRecyclerView: RecyclerView
    private lateinit var allLinksRecyclerView: RecyclerView

    // data arrays
    private lateinit var favouritesArrayList: ArrayList<MenuItem>
    private lateinit var allLinksArrayList: ArrayList<MenuItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fetch the data and initialise
        favRecyclerView = view.findViewById(R.id.recyview_menu_favourites)
        allLinksRecyclerView = view.findViewById(R.id.recyview_menu_all_link)

        // Handle favRecyclerView
        favRecyclerView.layoutManager = GridLayoutManager(
            context,
            4,
            GridLayoutManager.VERTICAL,
            false)
        favRecyclerView.setHasFixedSize(true)

        // Handle allLinksRecyclerView
        allLinksRecyclerView.layoutManager = GridLayoutManager(
            context,
            4,
            GridLayoutManager.VERTICAL,
            false)
        allLinksRecyclerView.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()

        dataInitialize()
    }

    private fun dataInitialize() {
        // Example array lists
        favouritesArrayList = arrayListOf()
        allLinksArrayList = arrayListOf()

        ApiClient.getApiService().menuItemsGet(SessionManager(requireContext()).fetchAuthToken()).enqueue(object : Callback<MenuItemsGetResponse> {
            override fun onResponse(call: Call<MenuItemsGetResponse>, response: Response<MenuItemsGetResponse>) {
                when (response.code()) {
                    200 -> {
                        // OK
                        val responseBody = response.body()
                        if (responseBody == null) {
                            Log.e(TAG, "Response body not existent!")
                            Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                            return
                        }

                        for (item in responseBody.items) {
                            val menuItem = MenuItem(item.id, item.iconUrl, item.name, item.url, item.isFavourite)
                            Log.d(TAG, menuItem.toString())
                            allLinksArrayList.add(menuItem)
                            if (menuItem.isFavourite) favouritesArrayList.add(menuItem)

                            recyclerViewAdapter = MenuItemAdapter(favouritesArrayList, this@MenuFragment)
                            favRecyclerView.adapter = recyclerViewAdapter
                            recyclerViewAdapter = MenuItemAdapter(allLinksArrayList, this@MenuFragment)
                            allLinksRecyclerView.adapter = recyclerViewAdapter
                        }
                    }
                    else -> Toast.makeText(context, response.code().toString() + " - " + response.message().uppercase(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MenuItemsGetResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message!!)
            }
        })
    }
}