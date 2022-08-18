package com.call.colorscreen.ledflash.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
class ViewPagerAdapter(manager: FragmentManager):FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragments: MutableList<Fragment> = mutableListOf()
    private val mFragmentTitles: MutableList<String> = mutableListOf()
    override fun getCount(): Int {
        return mFragments.size
    }
    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }
    fun addFragment(fragment: Fragment?, title: String?) {
        fragment?.let { mFragments.add(it) }
        title?.let { mFragmentTitles.add(it) }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitles[position]
    }
}