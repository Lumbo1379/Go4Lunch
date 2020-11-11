package com.example.go4lunch.controllers

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.go4lunch.R
import com.example.go4lunch.adapters.RestaurantViewPagerAdapter
import com.example.go4lunch.fragments.RestaurantBottomSheetFragment
import com.example.go4lunch.helpers.UserHelper
import com.example.go4lunch.models.restaurant.PlaceDetail
import com.example.go4lunch.utils.APICalls
import com.example.go4lunch.utils.APIConstants
import com.example.go4lunch.utils.Maths
import com.example.go4lunch.utils.PreferenceKeys
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_nav_header.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val SIGN_OUT_TASK = 10
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    private lateinit var mPagerAdapter: RestaurantViewPagerAdapter
    private lateinit var currentLanguage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLanguage = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString(NotificationsActivity.PREF_KEY_LANGUAGE, "en")!!

        if (currentLanguage != "en") {
            NotificationsActivity.changeLang(baseContext, currentLanguage)
        } else {
            NotificationsActivity.changeLang(baseContext, "")
        }

        setContentView(R.layout.activity_main)

        APICalls.init(this)

        initialiseViewPager()

        attachToolbar()
        attachDrawerLayout()
        attachNavigationView()
        attachTabLayout()
        configureSearchBar()

        val intent = intent
        if (intent.getBooleanExtra("newUser", true)) {
            createUserInFirestore()
        }

        updateProfileUI()
        createNotificationChannel()

        //UserHelper.createTestUserAccounts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        //Get locations bounds
        val pref = getPreferences(Context.MODE_PRIVATE)
        val lat = pref.getFloat(PreferenceKeys.PREF_KEY_LAT, 0f).toDouble()
        val lng = pref.getFloat(PreferenceKeys.PREF_KEY_LNG, 0f).toDouble()
        val bounds = Maths.getBounds(LatLng(lat, lng), 1500.0)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setLocationRestriction(RectangularBounds.newInstance(bounds.southwest, bounds.northeast))
            .setHint(getString(R.string.search))
            .build(this)

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        mPagerAdapter.mFragments[activity_main_pager_restaurant_view.currentItem]?.setSearchQuery(place.name!!)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("PLACE ERROR", status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun configureSearchBar() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, APIConstants.API_KEY)
        }
    }

    private fun initialiseViewPager() {
        mPagerAdapter = RestaurantViewPagerAdapter(this)

        activity_main_pager_restaurant_view.adapter = mPagerAdapter
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
                0 -> {
                    tab.text = getString(R.string.map_view)
                    tab.setIcon(R.drawable.baseline_map_black_18dp)
                }
                1 -> {
                    tab.text = getString(R.string.list_view)
                    tab.setIcon(R.drawable.baseline_list_black_18dp)
                }
                2 -> {
                    tab.text = getString(R.string.workmates)
                    tab.setIcon(R.drawable.baseline_supervisor_account_black_18dp)
                }
            }
        }.attach()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.activity_main_drawer_your_lunch -> {
                val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

                val gson = Gson()
                val jsonPlace = prefs.getString("LunchPlace", "")
                val jsonDetail = prefs.getString("LunchDetail", "")

                if (jsonPlace == "" || jsonDetail == "") {
                    Toast.makeText(this, "You have no selected lunch!", Toast.LENGTH_SHORT).show()
                } else {
                    val bottomSheet = RestaurantBottomSheetFragment(
                        gson.fromJson(jsonPlace, com.example.go4lunch.models.restaurant.Place::class.java),
                        gson.fromJson(jsonDetail, PlaceDetail::class.java),
                        applicationContext
                    )

                    bottomSheet.show(this.supportFragmentManager, "restaurantBottomSheet")
                }
            }
            R.id.activity_main_drawer_settings -> {
                val intent = Intent(this, NotificationsActivity::class.java)
                startActivity(intent)
            }
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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Go4Lunch"
            val description = "Notification for who you are doing out to lunch with and where"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NotificationsActivity.CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()

        val pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val lang = pref.getString(NotificationsActivity.PREF_KEY_LANGUAGE, "en")!!

        if (currentLanguage != lang) {
            currentLanguage = lang

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("newUser", false)
            startActivity(intent)
        }
    }
}