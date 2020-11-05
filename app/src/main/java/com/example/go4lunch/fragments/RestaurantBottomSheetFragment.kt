package com.example.go4lunch.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.go4lunch.R
import com.example.go4lunch.adapters.WorkmatesAdapter
import com.example.go4lunch.helpers.UserHelper
import com.example.go4lunch.models.User
import com.example.go4lunch.models.restaurant.Place
import com.example.go4lunch.models.restaurant.PlaceDetail
import com.example.go4lunch.utils.APIConstants
import com.example.go4lunch.utils.APIParse
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_sheet_restaurant.*
import kotlinx.android.synthetic.main.bottom_sheet_restaurant.view.*


class RestaurantBottomSheetFragment : BottomSheetDialogFragment, WorkmatesAdapter.IListener {

    private var mPlace: Place
    private var mDetail: PlaceDetail
    private lateinit var mRestaurantId: String
    private lateinit var mRestaurantName: String
    private lateinit var mUser: User
    private lateinit var workmatesAdapter: WorkmatesAdapter

    constructor(place: Place, details: PlaceDetail) {
        mPlace = place
        mDetail = details
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = it as BottomSheetDialog
            setupFullHeight(bottomSheet)
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet) as FrameLayout?
        val behavior = BottomSheetBehavior.from(bottomSheet as FrameLayout)
        val layoutParams = bottomSheet.layoutParams

        val windowHeight: Int = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet!!.layoutParams = layoutParams

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        return displayMetrics.heightPixels
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_restaurant, container, false)

        view.bottom_sheet_restaurant_image_rectangle.setBackgroundColor(view.resources.getColor(R.color.colorBottomSheet))
        view.bottom_sheet_restaurant_name.text = mPlace.name
        view.bottom_sheet_restaurant_address.text = APIParse.parseAddress(mPlace.vicinity)
        Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + mPlace.photos[0].photo_reference + "&key=" + APIConstants.API_KEY).into(view.bottom_sheet_restaurant_image)

        val rating = mPlace.rating * 0.6 // Out of 5 to out of 3
        val width = (rating * 10).toFloat()  // 10dp for each star
        view.bottom_sheet_restaurant_image_rating.layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, view.resources.displayMetrics).toInt() // Convert px to dp

        mRestaurantId = mPlace.place_id
        mRestaurantName = mPlace.name

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UserHelper.getUser(getCurrentUser().uid).addOnSuccessListener { documentSnapshot ->
            mUser = documentSnapshot.toObject(User::class.java)!!
            initFloatingActionButton()
        }

        setOnClickListeners()
        configureRecyclerView()
    }

    private fun initFloatingActionButton() {

        if (mUser.restaurantId == mRestaurantId) {
            bottom_sheet_restaurant_floating_action_button.setImageResource(R.drawable.baseline_done_black_18dp)
            bottom_sheet_restaurant_floating_action_button.tag = 1
        } else {
            bottom_sheet_restaurant_floating_action_button.setImageResource(R.drawable.baseline_close_black_18dp)
            bottom_sheet_restaurant_floating_action_button.tag = 0
        }

        bottom_sheet_restaurant_floating_action_button.setOnClickListener {
            if (bottom_sheet_restaurant_floating_action_button.tag == 1) {
                bottom_sheet_restaurant_floating_action_button.setImageResource(R.drawable.baseline_close_black_18dp)
                bottom_sheet_restaurant_floating_action_button.tag = 0

                UserHelper.updateLunchDestination("", "", getCurrentUser().uid)
            } else {
                bottom_sheet_restaurant_floating_action_button.setImageResource(R.drawable.baseline_done_black_18dp)
                bottom_sheet_restaurant_floating_action_button.tag = 1

                UserHelper.updateLunchDestination(mRestaurantId, mRestaurantName, getCurrentUser().uid)
            }
        }
    }

    private fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    private fun configureRecyclerView() {
        //Configure Adapter & RecyclerView
        workmatesAdapter = WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getUsers(mRestaurantId)), Glide.with(this), this, true)

        bottom_sheet_restaurant_recycler_view_workmates.layoutManager = LinearLayoutManager(context)
        bottom_sheet_restaurant_recycler_view_workmates.adapter = workmatesAdapter
    }

    private fun generateOptionsForAdapter(query: Query): FirestoreRecyclerOptions<User> {
        return FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .setLifecycleOwner(this)
            .build()
    }

    private fun setOnClickListeners() {
        val tabLayout = bottom_sheet_tab_layout

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:" + mDetail.phoneNumber)
                            startActivity(intent)
                        }
                        1 -> {
                            Toast.makeText(context, "You have liked the restaurant!", Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mDetail.website))
                            startActivity(intent)
                        }
                    }
                }
            }

        })
    }

    override fun onDataChanged() {

    }
}