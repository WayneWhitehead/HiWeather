package com.hidesign.hiweather.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hidesign.hiweather.views.EmptyView
import com.hidesign.hiweather.views.WeatherFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EmptyView()
            1 -> WeatherFragment()
            else -> WeatherFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}