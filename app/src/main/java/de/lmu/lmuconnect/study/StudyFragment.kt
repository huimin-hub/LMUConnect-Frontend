package de.lmu.lmuconnect.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.lmu.lmuconnect.R

private const val TAG = "StudyFragment"

class StudyFragment : Fragment() {

    // Components from UI
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var addButton: ExtendedFloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize
        tabLayout = view.findViewById(R.id.tab_study)
        viewPager = view.findViewById(R.id.viewpager)
        addButton = view.findViewById(R.id.fab_study_add)

        // Handle viewPager
        val viewPagerAdapter = StudyPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        // Link with tabs
        val tabTitles = arrayOf(
            getString(R.string.study_tab_item1),
            getString(R.string.study_tab_item2),
            getString(R.string.study_tab_item3)
        )
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Handle fab
        addButton.setOnClickListener {
            val currentFragment = viewPagerAdapter.getFragmentAtPosition(viewPager.currentItem)

            if (currentFragment is StudyAddButtonHandler) // Current fragment can handle add button clicks
                currentFragment.handleAddButtonClickEvent()
        }

        val buttonTitles = arrayOf(
            getString(R.string.study_floatingbtn_title_1),
            getString(R.string.study_floatingbtn_title_2),
            getString(R.string.study_floatingbtn_title_3)
        )
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // addButton.text = buttonTitles[position]
                if (position != 0) addButton.hide()
                else addButton.show()

            }
        })
    }
}