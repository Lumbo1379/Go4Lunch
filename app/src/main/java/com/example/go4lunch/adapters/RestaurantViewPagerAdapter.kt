package com.example.go4lunch.adapters

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.go4lunch.fragments.ListViewFragment
import com.example.go4lunch.fragments.MapViewFragment
import com.example.go4lunch.fragments.WorkmatesViewFragment
import com.example.go4lunch.utils.IFragment
import java.util.*
import kotlin.collections.HashMap

class RestaurantViewPagerAdapter(@NonNull fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    var mFragments: HashMap<Int, IFragment> = hashMapOf()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val f = MapViewFragment()
                mFragments[0] = f
                return f
            }
            1 -> {
                val f = ListViewFragment()
                mFragments[1] = f
                return f
            }
            2 -> {
                val f = WorkmatesViewFragment()
                mFragments[2] = f
                return f
            }
            else -> {
                val f = MapViewFragment()
                return f
            }
        }
    }
}