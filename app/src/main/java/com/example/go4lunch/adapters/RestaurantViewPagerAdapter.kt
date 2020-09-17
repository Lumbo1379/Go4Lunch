package com.example.go4lunch.adapters

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.go4lunch.fragments.ListViewFragment
import com.example.go4lunch.fragments.MapViewFragment
import com.example.go4lunch.fragments.WorkmatesViewFragment

class RestaurantViewPagerAdapter(@NonNull fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MapViewFragment()
            1 -> ListViewFragment()
            2 -> WorkmatesViewFragment()
            else -> MapViewFragment()
        }
    }
}