package de.lmu.lmuconnect.menu.add_item

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.menu.api.MenuItemsPostRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "AddNewMenuItemActivity"

class AddNewMenuItemActivity : AppCompatActivity() {
    private lateinit var nameTextInput: TextInputEditText
    private lateinit var urlTextInput: TextInputEditText
    lateinit var recyclerViewAdapter: IconItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_menu_item)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Init text edits
        nameTextInput = findViewById(R.id.editText_name)
        urlTextInput = findViewById(R.id.editText_url)

        nameTextInput.doOnTextChanged { text, _, _, _ ->
            if (text == null || text.length <= 3) nameTextInput.error = getString(R.string.menu_min_3_letters)
            else nameTextInput.error = null
        }
        urlTextInput.doOnTextChanged { text, _, _, _ ->
            if (text == null || !URLUtil.isValidUrl(text.toString())) // TODO: auto-add http(s)
                urlTextInput.error = getString(R.string.menu_valid_url)
            else urlTextInput.error = null
        }

        // Init icon recycler view
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_icon)

        val iconList = arrayListOf(
            R.drawable.menu_icon_accessibility,
            R.drawable.menu_icon_alarm,
            R.drawable.menu_icon_albums,
            R.drawable.menu_icon_analytics,
            R.drawable.menu_icon_archive,
            R.drawable.menu_icon_at,
            R.drawable.menu_icon_bag,
            R.drawable.menu_icon_bed,
            R.drawable.menu_icon_beer,
            R.drawable.menu_icon_book,
            R.drawable.menu_icon_building,
            R.drawable.menu_icon_bulb,
            R.drawable.menu_icon_bus,
            R.drawable.menu_icon_cafe,
            R.drawable.menu_icon_calendar,
            R.drawable.menu_icon_chart,
            R.drawable.menu_icon_chat,
            R.drawable.menu_icon_class,
            R.drawable.menu_icon_cloud,
            R.drawable.menu_icon_code,
            R.drawable.menu_icon_color,
            R.drawable.menu_icon_desktop,
            R.drawable.menu_icon_download,
            R.drawable.menu_icon_earth,
            R.drawable.menu_icon_fitness,
            R.drawable.menu_icon_flask,
            R.drawable.menu_icon_food,
            R.drawable.menu_icon_football,
            R.drawable.menu_icon_git,
            R.drawable.menu_icon_grid,
            R.drawable.menu_icon_heart,
            R.drawable.menu_icon_help,
            R.drawable.menu_icon_information,
            R.drawable.menu_icon_key,
            R.drawable.menu_icon_language,
            R.drawable.menu_icon_laptop,
            R.drawable.menu_icon_layers,
            R.drawable.menu_icon_leaf,
            R.drawable.menu_icon_library,
            R.drawable.menu_icon_link,
            R.drawable.menu_icon_location,
            R.drawable.menu_icon_mail,
            R.drawable.menu_icon_map,
            R.drawable.menu_icon_med,
            R.drawable.menu_icon_music,
            R.drawable.menu_icon_newspaper,
            R.drawable.menu_icon_options,
            R.drawable.menu_icon_people,
            R.drawable.menu_icon_phone,
            R.drawable.menu_icon_pizza,
            R.drawable.menu_icon_planet,
            R.drawable.menu_icon_print,
            R.drawable.menu_icon_restaurant,
            R.drawable.menu_icon_ribbon,
            R.drawable.menu_icon_school,
            R.drawable.menu_icon_search,
            R.drawable.menu_icon_shapes,
            R.drawable.menu_icon_sign,
            R.drawable.menu_icon_soccer,
            R.drawable.menu_icon_social,
            R.drawable.menu_icon_subway,
            R.drawable.menu_icon_tray,
            R.drawable.menu_icon_video,
            R.drawable.menu_icon_wifi,
            R.drawable.menu_icon_woman
        )

        recyclerViewAdapter = IconItemAdapter(iconList)
        val layoutManager = GridLayoutManager(this, 5)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_addnewitem_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_finish -> {
                if (nameTextInput.error != null || urlTextInput.error != null) {
                    Toast.makeText(this, getString(R.string.menu_resolve_errors), Toast.LENGTH_SHORT).show()
                    return false
                }

                val itemName = nameTextInput.text.toString()
                val itemURL = urlTextInput.text.toString()
                val itemImg = "icon:" + resources.getResourceName(recyclerViewAdapter.currentlySelectedItem).replace("de.lmu.lmuconnect:drawable/menu_icon_", "")
                Log.d(TAG, itemImg)

                // Send API request
                ApiClient.getApiService().menuItemsPost(MenuItemsPostRequest(
                    itemName, itemURL, itemImg, null
                ), SessionManager(this).fetchAuthToken()).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        when (response.code()) {
                            201 -> {
                                Toast.makeText(this@AddNewMenuItemActivity, getString(R.string.menu_item_added), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else -> Toast.makeText(this@AddNewMenuItemActivity, response.code().toString() + " - " + response.message().uppercase(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@AddNewMenuItemActivity, t.message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, t.message!!)
                    }
                })
            }
        }

        return super.onOptionsItemSelected(item)
    }
}