package com.kodakalaris.advisor.activities

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import com.kodakalaris.advisor.utils.SharedPreferenceHelper

/**
 * main launcher activity of the application,
 * this activity won't contain any UI,
 * it check if the user is already logged in or first time visiting the app, and redirect according to that
 */
class LaunchActivity : BaseActivity() {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenDefaultLauncher()
    }

    /**
     * decide which activity/screen to start with
     */
    private fun screenDefaultLauncher() {
        val registrationDonePref: Boolean = SharedPreferenceHelper.getSharedPreferenceBoolean(
            this,
            SharedPreferenceHelper.KEY_REGISTRATIONDONE,
            false
        )
        finish()
        if (registrationDonePref) {
            startActivity(
                Intent(this@LaunchActivity, WelcomeScreenActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            )
        } else {
            startActivity(
                Intent(this@LaunchActivity, ChangeStoreActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            )
        }
    }
}
