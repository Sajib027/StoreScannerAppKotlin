package com.kodakalaris.advisor.activities

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kodakalaris.advisor.application.AppController

/*
* Base activity inherited in all other activities
* */
open class BaseActivity : AppCompatActivity() {

    private val TAG = BaseActivity::class.java.simpleName

    override fun onResume() {
        super.onResume()
        AppController.isAppIsInForeground = true
        Log.d(TAG, "App is in foreground")
    }

    override fun onPause() {
        super.onPause()
        AppController.isAppIsInForeground = false
        Log.d(TAG, "App is in background")
    }
}