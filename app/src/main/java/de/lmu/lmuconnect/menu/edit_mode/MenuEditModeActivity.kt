package de.lmu.lmuconnect.menu.edit_mode

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.menu.data.MenuItem
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.menu.add_item.AddNewMenuItemActivity
import de.lmu.lmuconnect.menu.api.MenuItemsDeleteRequest
import de.lmu.lmuconnect.menu.api.MenuItemsGetResponse
import de.lmu.lmuconnect.menu.api.MenuItemsPatchRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MenuEditModeActivity"

class MenuEditModeActivity : AppCompatActivity() {

    // Components from UI
    private lateinit var recyclerViewAdapter: MenuItemEditAdapter
    private lateinit var favouritesRecyclerView: RecyclerView
    private lateinit var othersRecyclerView: RecyclerView
    private lateinit var addNewLinkButton: ImageButton

    // Data arrays
    private lateinit var favouritesArrayList: ArrayList<MenuItem>
    private lateinit var othersArrayList: ArrayList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_edit_mode)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialise
        favouritesRecyclerView = findViewById(R.id.recyview_menu_edit_favourites)
        othersRecyclerView = findViewById(R.id.recyview_menu_edit_others)
        addNewLinkButton = findViewById(R.id.menu_edit_add_link)

        addNewLinkButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MenuEditModeActivity,
                    AddNewMenuItemActivity::class.java
                )
            )
        }

        // Handle favouritesRecyclerView
        favouritesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Handle othersRecyclerView
        othersRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun dataInitialize() {
        favouritesArrayList = arrayListOf()
        othersArrayList = arrayListOf()

        ApiClient.getApiService().menuItemsGet(SessionManager(this@MenuEditModeActivity).fetchAuthToken()).enqueue(object :
            Callback<MenuItemsGetResponse> {
            override fun onResponse(call: Call<MenuItemsGetResponse>, response: Response<MenuItemsGetResponse>) {
                when (response.code()) {
                    200 -> {
                        // OK
                        val responseBody = response.body()
                        if (responseBody == null) {
                            Log.e(TAG, "Response body not existent!")
                            Toast.makeText(this@MenuEditModeActivity, "ERROR", Toast.LENGTH_SHORT).show()
                            return
                        }

                        for (item in responseBody.items) {
                            val menuItem = MenuItem(item.id, item.iconUrl, item.name, item.url, item.isFavourite)
                            Log.d(TAG, menuItem.toString())
                            if (menuItem.isFavourite) favouritesArrayList.add(menuItem)
                            else othersArrayList.add(menuItem)

                            recyclerViewAdapter = MenuItemEditAdapter(favouritesArrayList, this@MenuEditModeActivity)
                            favouritesRecyclerView.adapter = recyclerViewAdapter
                            recyclerViewAdapter = MenuItemEditAdapter(othersArrayList, this@MenuEditModeActivity)
                            othersRecyclerView.adapter = recyclerViewAdapter
                        }
                    }
                    else -> Toast.makeText(this@MenuEditModeActivity, response.code().toString() + " - " + response.message().uppercase(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MenuItemsGetResponse>, t: Throwable) {
                Toast.makeText(this@MenuEditModeActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message!!)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        dataInitialize()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menu_edit_finish -> {
                // Handle edit icon press
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun deleteLink(id: String) {
        ApiClient.getApiService().menuItemsDelete(
            MenuItemsDeleteRequest(id),
            SessionManager(this).fetchAuthToken()).enqueue(
            object : Callback<Void> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        200 -> {
                            dataInitialize()
                            Log.i(TAG, "The link is removed.")
                        }
                        else -> Toast.makeText(this@MenuEditModeActivity, response.code().toString() + " - " + response.message().uppercase(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@MenuEditModeActivity, t.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message!!)
                }
            })
    }

    fun setFavourite(menuItem: MenuItem, isFavourite: Boolean) {
        ApiClient.getApiService().menuItemsPatch(MenuItemsPatchRequest(
            menuItem.itemId, MenuItemsPatchRequest.DataChange(menuItem.itemName, menuItem.itemLink, menuItem.itemIconUrl, isFavourite)
        ), SessionManager(this).fetchAuthToken()).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> {
                        dataInitialize()
                    }
                    else -> Toast.makeText(this@MenuEditModeActivity, response.code().toString() + " - " + response.message().uppercase(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MenuEditModeActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message!!)
            }
        })
    }
}