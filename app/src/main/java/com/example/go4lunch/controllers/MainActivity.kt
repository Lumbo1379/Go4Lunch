package com.example.go4lunch.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.go4lunch.R
import com.example.go4lunch.adapters.RestaurantViewPagerAdapter
import com.example.go4lunch.utils.APICalls
import com.example.go4lunch.helpers.UserHelper
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_nav_header.view.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val SIGN_OUT_TASK = 10;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        APICalls.init(this)

        initialiseViewPager()

        attachToolbar()
        attachDrawerLayout()
        attachNavigationView()
        attachTabLayout()

        createUserInFirestore()
        updateProfileUI()

        //UserHelper.createTestUserAccounts()
    }

    private fun initialiseViewPager() {
        activity_main_pager_restaurant_view.adapter = RestaurantViewPagerAdapter(this)
        activity_main_pager_restaurant_view.isUserInputEnabled = false // Disable swiping
    }

    private fun attachToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun attachDrawerLayout() {
        val toggle = ActionBarDrawerToggle(this, activity_main, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        activity_main.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun attachNavigationView() {
        activity_main_nav_view.setNavigationItemSelectedListener(this)
    }

    private fun attachTabLayout() {
        TabLayoutMediator(activity_main_tab_layout, activity_main_pager_restaurant_view) { tab, position ->
            when (position) {
                0 -> tab.text = "Map View"
                1 -> tab.text = "List View"
                2 -> tab.text = "Workmates"
            }
        }.attach()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.activity_main_drawer_your_lunch -> activity_main_pager_restaurant_view.currentItem = 0
            R.id.activity_main_drawer_settings -> activity_main_pager_restaurant_view.currentItem = 1
            R.id.activity_main_drawer_logout -> signOutUserFromFirebase()
        }

        activity_main.closeDrawer(GravityCompat.START)

        return false;
    }

    private fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    private fun onFailureListener(): OnFailureListener {
        return OnFailureListener {
            Toast.makeText(applicationContext, getString(R.string.error_auth_unknown), Toast.LENGTH_LONG).show()
        }
    }

    private fun createUserInFirestore() {
        val urlPicture = getCurrentUser().photoUrl.toString()
        val displayName = getCurrentUser().displayName!!
        val uid = getCurrentUser().uid

        UserHelper.createUser(uid, displayName, urlPicture).addOnFailureListener(onFailureListener())
    }

    private fun updateProfileUI() {
        val headerLayout = activity_main_nav_view.getHeaderView(0)

        if (getCurrentUser().photoUrl != null) {
            Glide.with(this)
                .load(getCurrentUser().photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(headerLayout.activity_main_nav_header_profile_picture)
        }

        headerLayout.activity_main_nav_header_text_name.text = getCurrentUser().displayName
        headerLayout.activity_main_nav_header_text_email.text = getCurrentUser().email
    }

    private fun signOutUserFromFirebase() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK))
    }

    private fun updateUIAfterRESTRequestsCompleted(origin: Int) : OnSuccessListener<Void> {
        return OnSuccessListener {
            when (origin) {
                SIGN_OUT_TASK -> finish()
            }
        }
    }
}