package de.lmu.lmuconnect.social

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.lmu.lmuconnect.social.personal.SocialPersonalFragment
import de.lmu.lmuconnect.social.group.SocialGroupFragment


class SocialPagerAdapter(val fragment: Fragment): FragmentStateAdapter(fragment) {
    private val fragments = arrayOfNulls<Fragment>(itemCount)
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        fragments[position] = when (position) {
            0 -> SocialPersonalFragment()
            1 -> SocialGroupFragment()
            else -> throw IllegalStateException("Fragment $position is not available")
        }
        return fragments[position]!!
    }

    fun getFragmentAtPosition(position: Int): Fragment? = fragments[position]
}