package com.example.go4lunch.controllers

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.go4lunch.R
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        activity_login_facebook_login_button.setOnClickListener {
            startSignInActivity()
        }

        activity_login_google_login_button.setOnClickListener {
            startSignInActivity()
        }
    }

    private fun startSignInActivity() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(mutableListOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.logo)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        handleResponseAfterSignIn(requestCode, resultCode, data)
    }

    private fun handleResponseAfterSignIn(requestCode: Int, resultCode: Int, data: Intent?) {
        val response = IdpResponse.fromResultIntent(data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                when {
                    response == null -> {
                        showSnackBar(activity_login_linear_layout, getString(R.string.error_auth_cancelled))
                    }
                    response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                        showSnackBar(activity_login_linear_layout, getString(R.string.error_auth_no_internet))
                    }
                    else -> {
                        showSnackBar(activity_login_linear_layout, getString(R.string.error_auth_unknown))
                    }
                }
            }
        }
    }

    private fun showSnackBar(linearLayout: LinearLayout, message: String) {
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}