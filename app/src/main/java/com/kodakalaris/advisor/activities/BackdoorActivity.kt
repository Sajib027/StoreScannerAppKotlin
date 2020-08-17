package com.kodakalaris.advisor.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.closeKeyboard
import com.kodakalaris.advisor.utils.Helper.devBaseUrl
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.getBaseUrl
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.openKeyboard
import com.kodakalaris.advisor.utils.Helper.prodBaseUrl
import com.kodakalaris.advisor.utils.Helper.setDefaultBaseURL
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.Helper.stageBaseUrl
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.clearSharedPreference
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceString
import okhttp3.*
import java.io.IOException
import java.util.*

/*
* Backdoor activity is the hidden screen where admin can change the application configurations
* */
class BackdoorActivity : AppCompatActivity(), View.OnClickListener, OnTouchListener {

    private lateinit var mEtServerUrl: EditText
    private lateinit var mEtNewsUrl: EditText
    private lateinit var mEtHelpUrl: EditText
    private lateinit var mIvEditUrl: ImageView
    private lateinit var btn_back: ImageButton
    private lateinit var mBtnSave: Button
    private lateinit var mSelectedBaseUrl: String
    private lateinit var main_content: ScrollView
    private lateinit var txt_mail_push_info: TextView
    private lateinit var loader_update_serverurl: ProgressBar
    private lateinit var current_server_url: TextView
    private lateinit var activateLogSwitch: Switch
    private lateinit var autocompleteTaskSwitch: Switch
    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden)
        mContext = this@BackdoorActivity
        initView()
        initListener()
        initCustomSpinner()
        bindData()
    }

    private fun bindData() {
        current_server_url.text = java.lang.String.format(
            getResources().getString(R.string.current_server_url),
            getBaseUrl(this)
        )
        if (Locale.getDefault().language.equals("de", ignoreCase = true)) {
            mEtNewsUrl.setText(
                getSharedPreferenceString(
                    mContext,
                    SharedPreferenceHelper.KEY_GERMAN_NEWS_URL,
                    Constants.APP.GERMAN_NEWS_URL
                )
            )
            mEtHelpUrl.setText(
                getSharedPreferenceString(
                    mContext,
                    SharedPreferenceHelper.KEY_GERMAN_HELP_URL,
                    Constants.APP.GERMAN_HELP_URL
                )
            )
        } else {
            mEtNewsUrl.setText(
                getSharedPreferenceString(
                    mContext,
                    SharedPreferenceHelper.KEY_ENGLISH_NEWS_URL,
                    Constants.APP.ENGLISH_NEWS_URL
                )
            )
            mEtHelpUrl.setText(
                getSharedPreferenceString(
                    mContext,
                    SharedPreferenceHelper.KEY_ENGLISH_HELP_URL,
                    Constants.APP.ENGLISH_HELP_URL
                )
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v.id == R.id.main_content && event.action == MotionEvent.ACTION_UP) {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            if (inputManager != null && getCurrentFocus() != null) {
                //inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return false
    }

    override fun onClick(view: View) {
        disableDoubleTap(view)
        when (view.id) {
            R.id.txt_mail_push_info -> {
                val responder = "Responder: " + getSharedPreferenceString(
                    this@BackdoorActivity,
                    SharedPreferenceHelper.KEY_RESPONDER,
                    ""
                )
                val storeid = "Store Id: " + getSharedPreferenceString(
                    this@BackdoorActivity,
                    SharedPreferenceHelper.KEY_STOREID,
                    ""
                )
                val deviceid = "Device Id: " + getSharedPreferenceString(
                    this@BackdoorActivity,
                    SharedPreferenceHelper.KEY_DEVICEID,
                    ""
                )
                val firebasetoken = "Firebase Token: " + getSharedPreferenceString(
                    this@BackdoorActivity,
                    SharedPreferenceHelper.KEY_FCMTOKEN,
                    ""
                )
                val mailBody =
                    responder + "\n\n" + storeid + "\n\n" + deviceid + "\n\n" + firebasetoken
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Clerk App: Push Notification data")
                intent.putExtra(Intent.EXTRA_TEXT, mailBody)
                startActivity(Intent.createChooser(intent, "Clerk App: Send Email"))
            }
            R.id.ivEditUrl -> {
                mEtServerUrl.isEnabled = true
                openKeyboard(this@BackdoorActivity)
            }
            R.id.btn_Save -> {
                closeKeyboard(this@BackdoorActivity)
                if (Locale.getDefault().language.equals("de", ignoreCase = true)) {
                    setSharedPreferenceString(
                        mContext,
                        SharedPreferenceHelper.KEY_GERMAN_NEWS_URL,
                        mEtNewsUrl.text.toString()
                    )
                    setSharedPreferenceString(
                        mContext,
                        SharedPreferenceHelper.KEY_GERMAN_HELP_URL,
                        mEtHelpUrl.text.toString()
                    )
                } else {
                    setSharedPreferenceString(
                        mContext,
                        SharedPreferenceHelper.KEY_ENGLISH_NEWS_URL,
                        mEtNewsUrl.text.toString()
                    )
                    setSharedPreferenceString(
                        mContext,
                        SharedPreferenceHelper.KEY_ENGLISH_HELP_URL,
                        mEtHelpUrl.text.toString()
                    )
                }
                if (getBaseUrl(this).equals(mEtServerUrl.text.toString(), ignoreCase = true)) {
                    finish()
                } else {
                    callRegisterDeviceApi()
                }
            }
            R.id.btn_back -> {
                closeKeyboard(this@BackdoorActivity)
                finish()
            }
        }
    }

    private fun initView() {
        mEtServerUrl = findViewById(R.id.etServerUrl)
        mEtNewsUrl = findViewById(R.id.etNewsUrl)
        mEtHelpUrl = findViewById(R.id.etHelpUrl)
        mIvEditUrl = findViewById(R.id.ivEditUrl)
        mBtnSave = findViewById(R.id.btn_Save)
        btn_back = findViewById(R.id.btn_back)
        main_content = findViewById(R.id.main_content)
        txt_mail_push_info = findViewById(R.id.txt_mail_push_info)
        loader_update_serverurl = findViewById(R.id.loader_update_serverurl)
        current_server_url = findViewById(R.id.current_server_url)
        val activateState = getSharedPreferenceBoolean(
            mContext,
            SharedPreferenceHelper.KEY_ACTIVATE_LOG,
            Constants.APP.DEFAULT_LOG_FLAG
        )
        activateLogSwitch = findViewById(R.id.switch_enablelog)
        activateLogSwitch.isChecked = activateState
        val autocompleteState = getSharedPreferenceBoolean(
            mContext,
            SharedPreferenceHelper.KEY_AUTOCOMPLETE_TASK,
            Constants.APP.DEFAULT_AUTOCOMPLETE_FLAG
        )
        autocompleteTaskSwitch = findViewById(R.id.switch_autocomplete_task)
        autocompleteTaskSwitch.isChecked = autocompleteState
        mEtServerUrl.setText(getBaseUrl(this))
        mEtServerUrl.isEnabled = false
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        mBtnSave.setOnClickListener(this)
        mIvEditUrl.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        main_content.setOnTouchListener(this)
        txt_mail_push_info.setOnClickListener(this)
        activateLogSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            updateSwitchState(isChecked)
        }
        autocompleteTaskSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            setSharedPreferenceBoolean(
                mContext,
                SharedPreferenceHelper.KEY_AUTOCOMPLETE_TASK,
                isChecked
            )
        }
    }

    /*
    * Update activity log switch state
    * */
    fun updateSwitchState(isChecked: Boolean) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQ_PERMISSION
            )
        }
        setSharedPreferenceBoolean(mContext, SharedPreferenceHelper.KEY_ACTIVATE_LOG, isChecked)
    }

    private fun initCustomSpinner() {
        val spinnerCustom: Spinner = findViewById(R.id.spinnerMode)
        // Spinner Drop down elements
        val serverURLS = ArrayList<String>()
        serverURLS.add(Constants.APP.API_MODE_DEV)
        serverURLS.add(Constants.APP.API_MODE_STAGE)
        serverURLS.add(Constants.APP.API_MODE_PROD)
        val customSpinnerAdapter = CustomSpinnerAdapter(this@BackdoorActivity, serverURLS)
        spinnerCustom.adapter = customSpinnerAdapter
        spinnerCustom.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val item = parent.getItemAtPosition(position).toString()
                getSelectedUrl(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerCustom.setSelection(currentBaseUrl)
    }

    private val currentBaseUrl: Int
        get() {
            val position: Int
            val url = getBaseUrl(this)
            when (url) {
                Constants.APP.API_DEV_BASE_URL -> {
                    position = 0
                    mEtServerUrl.setText(devBaseUrl)
                }
                Constants.APP.API_STAGE_BASE_URL -> {
                    position = 1
                    mEtServerUrl.setText(stageBaseUrl)
                }
                Constants.APP.API_PROD_BASE_URL -> {
                    position = 2
                    mEtServerUrl.setText(prodBaseUrl)
                }
                else -> {
                    position = 0
                    mEtServerUrl.setText(devBaseUrl)
                }
            }
            return position
        }

    private fun getSelectedUrl(position: Int) {
        when (position) {
            0 -> {
                mSelectedBaseUrl = devBaseUrl
                setUrl(mSelectedBaseUrl)
            }
            1 -> {
                mSelectedBaseUrl = stageBaseUrl
                setUrl(mSelectedBaseUrl)
            }
            2 -> {
                mSelectedBaseUrl = prodBaseUrl
                setUrl(mSelectedBaseUrl)
            }
        }
    }

    private fun setUrl(url: String) {
        mEtServerUrl.setText(mSelectedBaseUrl)
        if (url.isEmpty()) {
            showPrompt(this@BackdoorActivity, getResources().getString(R.string.error_empty_url))
        } else {
            mEtServerUrl.setHint(getResources().getString(R.string.error_empty_url))
        }
    }

    inner class CustomSpinnerAdapter(context: Context?, private val asr: ArrayList<String>?) :
        BaseAdapter(), SpinnerAdapter {
        override fun getCount(): Int {
            return asr?.size ?: 0
        }

        override fun getItem(i: Int): Any {
            return asr!![i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val txt = TextView(getApplicationContext())
            txt.setPadding(16, 20, 16, 20)
            txt.textSize = 18f
            txt.gravity = Gravity.CENTER_VERTICAL
            txt.text = asr!![position]
            txt.setTextColor(Color.parseColor("#000000"))
            return txt
        }

        override fun getView(i: Int, view: View?, viewgroup: ViewGroup?): View {
            val txt = TextView(getApplicationContext())
            txt.setPadding(16, 20, 16, 20)
            txt.textSize = 16f
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0)
            txt.text = asr!![i]
            txt.setTextColor(Color.parseColor("#000000"))
            return txt
        }
    }

    /**
     * Register device api call hit test to check wheather the server url is verified or not
     */
    private fun callRegisterDeviceApi() {
        if (!isConnectedToInternet(this@BackdoorActivity)) {
            showPrompt(
                this@BackdoorActivity,
                getResources().getString(R.string.error_no_internet_connection)
            )
            return
        } else if (getBaseUrl(this).equals(mEtServerUrl.text.toString(), ignoreCase = true)) {
            //Helper.showPrompt(BackdoorActivity.this, getResources().getString(R.string.error_no_changes_detected));
            // return;
        }
        loader_update_serverurl.visibility = View.VISIBLE
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val client = OkHttpClient()
        val request = Request.Builder().url(mEtServerUrl.text.toString() + "api/devices/create")
            .post(RequestBody.create(JSON, "{}")).build()
        client.newCall(request).enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                when (response.code()) {
                    401 -> {
                        reloadApp()
                    }
                    else -> {
                        runOnUiThread {
                            showPrompt(
                                this@BackdoorActivity,
                                java.lang.String.format(
                                    getResources().getString(R.string.error_while_register),
                                    response.message()
                                )
                            )
                        }
                    }
                }
                runOnUiThread { loader_update_serverurl.visibility = View.GONE }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    loader_update_serverurl.visibility = View.GONE
                    showPrompt(
                        this@BackdoorActivity,
                        java.lang.String.format(
                            getResources().getString(R.string.error_while_register),
                            "Api Failure"
                        )
                    )
                }
            }
        })
    }

    private fun reloadApp() {
        val baseUrl = mEtServerUrl.text.toString()
        val firebaseToken = getSharedPreferenceString(
            this@BackdoorActivity,
            SharedPreferenceHelper.KEY_FCMTOKEN,
            ""
        )
        clearSharedPreference(this@BackdoorActivity)
        setSharedPreferenceString(
            this@BackdoorActivity,
            SharedPreferenceHelper.KEY_BASE_URL,
            baseUrl
        )
        setSharedPreferenceString(
            this@BackdoorActivity,
            SharedPreferenceHelper.KEY_FCMTOKEN,
            firebaseToken
        )
        setSharedPreferenceBoolean(
            this@BackdoorActivity,
            SharedPreferenceHelper.KEY_AUTOCOMPLETE_TASK,
            true
        )
        setDefaultBaseURL(this@BackdoorActivity, baseUrl)
        //Chnage base URl for Test,Stag or Prod
        AppController.service = null
        val i = Intent(this@BackdoorActivity, LaunchActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.size > 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    companion object {
        private const val REQ_PERMISSION = 123
    }
}
