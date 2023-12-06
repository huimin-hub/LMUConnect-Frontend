package de.lmu.lmuconnect

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.lmu.lmuconnect.auth.LoginActivity
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import de.lmu.lmuconnect.home.HomeFragment
import de.lmu.lmuconnect.menu.edit_mode.MenuEditModeActivity
import de.lmu.lmuconnect.menu.MenuFragment
import de.lmu.lmuconnect.social.SocialFragment
import de.lmu.lmuconnect.social.profile.SocialProfileActivity
import de.lmu.lmuconnect.study.StudyFragment
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class MainActivity : AppCompatActivity() {

    // Components from UI
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var largeLogoImageView : ImageView

    // Fragment manager
    private lateinit var fragmentManager: FragmentManager

    // Session manager
    private lateinit var sessionManager: SessionManager

    // Matrix session
    private lateinit var socialSession: Session


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        largeLogoImageView = findViewById(R.id.imageView_logo_big)
        topAppBar = findViewById(R.id.topAppBar)
        fragmentManager = supportFragmentManager
        sessionManager = SessionManager(this)

        // Add notification badge to chat
        bottomNavigationView.getOrCreateBadge(R.id.social).number = 0
        bottomNavigationView.getOrCreateBadge(R.id.social).backgroundColor = resources.getColor(R.color.lmu_green, theme)
        bottomNavigationView.getOrCreateBadge(R.id.social).isVisible = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            var largeLogoVisibility: Int = ImageView.GONE
            var appBarTitle: String? = null

            when(item.itemId) {
                R.id.home -> {
                    // Respond to navigation home click
                    fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .setReorderingAllowed(true)
                        // .addToBackStack("Home")
                        .commit()
                    largeLogoVisibility = ImageView.VISIBLE
                    appBarTitle = null
                }
                R.id.study -> {
                    // Respond to navigation study click
                    if(sessionManager.authTokenIsEmpty()) {
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, UnauthorizedFragment())
                            .setReorderingAllowed(true)
                            // .addToBackStack("Home")
                            .commit()
                    } else {
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, StudyFragment())
                            .setReorderingAllowed(true)
                            // .addToBackStack("Home")
                            .commit()
                    }
                    appBarTitle = "STUDY"
                }
                R.id.social -> {
                    // Respond to navigation social click
                    if (sessionManager.authTokenIsEmpty()) {
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, UnauthorizedFragment())
                            .setReorderingAllowed(true)
                            // .addToBackStack("Home")
                            .commit()
                    } else {
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, SocialFragment())
                            .setReorderingAllowed(true)
                            // .addToBackStack("Home")
                            .commit()
                    }

                    appBarTitle = "SOCIAL"
                }
                R.id.menu -> {
                    // Respond to navigation menu click

                    if (sessionManager.authTokenIsEmpty()) {
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, UnauthorizedFragment())
                            .setReorderingAllowed(true)
                            // .addToBackStack("Home")
                            .commit()
                    } else {
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, MenuFragment())
                            .setReorderingAllowed(true)
                            // .addToBackStack("Home")
                            .commit()
                    }

                    appBarTitle = "MENU"
                }
            }

            largeLogoImageView.visibility = largeLogoVisibility
            topAppBar.title = appBarTitle

            if (largeLogoVisibility == ImageView.VISIBLE) topAppBar.logo = null
            else topAppBar.setLogo(R.drawable.logo_small)

            topAppBar.menu[0].isVisible = item.itemId == R.id.menu

            if(sessionManager.authTokenIsEmpty()) topAppBar.menu[0].isVisible = false


            true
        }

        if(sessionManager.authTokenIsEmpty()) startActivity(Intent(this, LoginActivity::class.java))

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.app_profile -> {
                    // Handle profile icon press
                    if(sessionManager.authTokenIsEmpty()) {
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        startActivity(Intent(this, SocialProfileActivity::class.java))
                    }
                    true
                }

                R.id.menu_edit -> {
                    startActivity(Intent(this, MenuEditModeActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val roomSummariesQuery = roomSummaryQueryParams {
            memberships = Membership.activeMemberships()
        }

        if(MatrixSessionHolder.getCurrentSession() != null) {
            socialSession = MatrixSessionHolder.getCurrentSession()!!
            socialSession.roomService().getRoomSummariesLive(roomSummariesQuery).observe(this) { roomSummaries ->
                var tempCounter = 0
                roomSummaries.forEach {
                    tempCounter += it.notificationCount
                }
                if (tempCounter > 0) {
                    bottomNavigationView.getOrCreateBadge(R.id.social).number = tempCounter
                    bottomNavigationView.getOrCreateBadge(R.id.social).isVisible = true
                } else {
                    bottomNavigationView.getOrCreateBadge(R.id.social).isVisible = false
                }
            }
        }
    }

}