package com.kodakalaris.advisor.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.annotation.Nullable
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceBoolean

/**
 * Welcome Screen, shows the already logged in detail with store address & username/responder name
 */
class WelcomeScreenActivity : BaseActivity(), View.OnClickListener {

    private lateinit var txt_username: TextView
    private lateinit var txt_storeaddress: TextView
    private lateinit var txt_storeaddress_heading: TextView
    private lateinit var txt_here: TextView
    private lateinit var txt_storeId: TextView
    private lateinit var btn_start: Button
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        initView()
        bindData()
        initListener()
    }

    /**
     * initialize the view
     */
    private fun initView() {
        txt_username = findViewById(R.id.txt_username)
        txt_storeId = findViewById(R.id.txt_storeId)
        txt_storeaddress = findViewById(R.id.txt_storeaddress)
        txt_storeaddress_heading = findViewById(R.id.txt_storeaddress_heading)
        txt_here = findViewById(R.id.text_here)
        btn_start = findViewById(R.id.btn_start)
    }

    /**
     * bind the data to the UI
     */
    private fun bindData() {
        val usernamePref =
            getSharedPreferenceString(this, SharedPreferenceHelper.KEY_RESPONDER, null)
        val storeidPref = getSharedPreferenceString(this, SharedPreferenceHelper.KEY_STOREID, null)
        val storeaddressPref =
            getSharedPreferenceString(this, SharedPreferenceHelper.KEY_STOREADDRESS, null)
        if (usernamePref != null) {
            txt_username.text = usernamePref
        }
        if (storeaddressPref != null) {
            if (!storeidPref!!.isEmpty()) {
                txt_storeId.visibility = View.VISIBLE
                txt_storeId.text = storeidPref
            } else {
                txt_storeId.visibility = View.GONE
            }
            txt_storeaddress_heading.visibility = View.VISIBLE
            txt_storeaddress.text = storeaddressPref
        }
        if (usernamePref != null && storeaddressPref != null && storeidPref != null) {
            btn_start.isEnabled = true
        }
    }

    /**
     * attach the listeners
     */
    private fun initListener() {
        txt_here.setOnClickListener(this)
        btn_start.setOnClickListener(this)
        txt_username.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                true
            } else false
        }
    }

    override fun onClick(v: View) {
        disableDoubleTap(v)
        when (v.id) {
            R.id.text_here -> {
                startActivity(Intent(this@WelcomeScreenActivity, ChangeStoreActivity::class.java))
            }
            R.id.btn_start -> {
                //Set default notification setting to true.
                setSharedPreferenceBoolean(
                    this@WelcomeScreenActivity,
                    SharedPreferenceHelper.KEY_NOTIFICATION,
                    true
                )
                startActivity(
                    Intent(this@WelcomeScreenActivity, HomeActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
                finish()
            }
        }
    }

    companion object {
        private val TAG = WelcomeScreenActivity::class.java.simpleName
    }
}
