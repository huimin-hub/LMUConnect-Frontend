package de.lmu.lmuconnect.study

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.lmu.lmuconnect.study.calendar.StudyCalendarFragment
import de.lmu.lmuconnect.study.courses.StudyCoursesFragment
import de.lmu.lmuconnect.study.todo.StudyTodoFragment

class StudyPagerAdapter(val fragment: Fragment): FragmentStateAdapter(fragment) {
    private val fragments = arrayOfNulls<Fragment>(itemCount)

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        fragments[position] = when (position) {
            0 -> StudyCoursesFragment()
            1 -> StudyCalendarFragment()
            2 -> StudyTodoFragment()
            else -> throw IllegalStateException("Fragment $position is not available")
        }
        return fragments[position]!!
    }

    fun getFragmentAtPosition(position: Int): Fragment? = fragments[position]
}