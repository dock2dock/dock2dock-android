package com.dock2dock.android.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentModel(var fragment: Fragment, var title: String)

class PagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private lateinit var currentFragment: Fragment

    var fragments = listOf<FragmentModel>()

    override fun getCount(): Int {
        return fragments.count()
    }

    override fun getItem(position: Int): Fragment {
        var fragment = fragments[position].fragment
        currentFragment = fragment
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments[position].title
    }
}