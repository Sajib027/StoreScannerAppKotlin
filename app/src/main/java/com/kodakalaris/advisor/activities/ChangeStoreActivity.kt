package com.kodakalaris.advisor.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.eventbus.AppEvents
import com.kodakalaris.advisor.fragments.ChangeStoreFragment
import com.kodakalaris.advisor.model.RegisterApiResponse
import com.kodakalaris.advisor.model.RequestDeviceRegisterApi
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.closeKeyboard
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceString
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
* Change Store activity is the registration screen
* */
class ChangeStoreActivity : BaseActivity(), View.OnClickListener {

    private lateinit var container_change_store: FrameLayout
    private lateinit var btn_back: ImageButton
    private lateinit var loader: ProgressBar
    private lateinit var txt_heading: TextView
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_store)
        initView()
        initListener()
        bindFragment()
    }

    /**
     * initialize the views
     */
    private fun initView() {
        container_change_store = findViewById(R.id.container_change_store)
        txt_heading = findViewById(R.id.txt_heading)
        loader = findViewById(R.id.loader)
        btn_back = findViewById(R.id.btn_back)
    }

    /**
     * attach listeners
     */
    private fun initListener() {
        btn_back.setOnClickListener(this)
    }

    /**
     * attach the ChangeStoreFragment with the activity
     */
    private fun bindFragment() {
        val registrationDonePref =
            getSharedPreferenceBoolean(this, SharedPreferenceHelper.KEY_REGISTRATIONDONE, false)
        if (!registrationDonePref) {
            txt_heading.text = resources.getString(R.string.lb_registration_heading)
        }
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager
            .beginTransaction()
            .replace(R.id.container_change_store, ChangeStoreFragment.newInstance(false)!!)
            .commit()
    }

    override fun onClick(v: View) {
        disableDoubleTap(v)
        closeKeyboard(this@ChangeStoreActivity)
        when (v.id) {
            R.id.btn_back -> {
                finish() // remove the activity from the stack on back press
            }
        }
    }

    /**
     * whenever new registration calls up, first system will check whether there is still a register or not
     * if not then directly register the device
     * otherwise first unregister the device with the store, and then register the corresponding store with the device
     * @param params request body for registerDevice API
     */
    fun callUnregisterThenRegisterDeviceApi(
        params: RequestDeviceRegisterApi,
        storeAddress: String?
    ) {
        if (!isConnectedToInternet(this@ChangeStoreActivity)) {
            showPrompt(
                this@ChangeStoreActivity,
                resources.getString(R.string.error_no_internet_connection)
            )
            loader.visibility = View.GONE
            return
        }
        if (getSharedPreferenceBoolean(
                this@ChangeStoreActivity,
                SharedPreferenceHelper.KEY_REGISTRATIONDONE,
                false
            )
            && getSharedPreferenceString(
                this@ChangeStoreActivity,
                SharedPreferenceHelper.KEY_DEVICEID,
                null
            ) != null
        ) {
            val deviceid = getSharedPreferenceString(
                this@ChangeStoreActivity,
                SharedPreferenceHelper.KEY_DEVICEID,
                null
            )
            //Add request to logger
            generateLogsOnStorage("Unregister Device Request")
            AppController.service!!.unregisterDevice(deviceid)
                .enqueue(object : Callback<RegisterApiResponse> {
                    override fun onResponse(
                        call: Call<RegisterApiResponse>,
                        response: Response<RegisterApiResponse>
                    ) {
                        setSharedPreferenceBoolean(
                            this@ChangeStoreActivity,
                            SharedPreferenceHelper.KEY_REGISTRATIONDONE,
                            false
                        )
                        setSharedPreferenceBoolean(
                            this@ChangeStoreActivity,
                            SharedPreferenceHelper.KEY_ALREADYLOGGEDIN,
                            false
                        )
                        setSharedPreferenceString(
                            this@ChangeStoreActivity,
                            SharedPreferenceHelper.KEY_RESPONDER,
                            null
                        )
                        setSharedPreferenceString(
                            this@ChangeStoreActivity,
                            SharedPreferenceHelper.KEY_STOREID,
                            null
                        )
                        setSharedPreferenceString(
                            this@ChangeStoreActivity,
                            SharedPreferenceHelper.KEY_DEVICEID,
                            null
                        )
                        callRegisterDeviceApi(params, storeAddress)
                    }

                    override fun onFailure(call: Call<RegisterApiResponse>, t: Throwable) {
                        loader.visibility = View.GONE
                        showPrompt(
                            this@ChangeStoreActivity,
                            String.format(
                                resources.getString(R.string.error_while_register),
                                "Api Failure"
                            )
                        )
                    }
                })
        } else {
            callRegisterDeviceApi(params, storeAddress)
        }
    }

    /**
     * Register device api
     * @param params request body for registerDevice API
     */
    fun callRegisterDeviceApi(params: RequestDeviceRegisterApi, storeAddress: String?) {
        if (!isConnectedToInternet(this@ChangeStoreActivity)) {
            showPrompt(
                this@ChangeStoreActivity,
                resources.getString(R.string.error_no_internet_connection)
            )
            loader.visibility = View.GONE
            return
        }
        loader.visibility = View.VISIBLE
        EventBus.getDefault().post(AppEvents.RegisteringStoreEvent())
        //Add request to logger
        generateLogsOnStorage("Register Device Request")
        generateLogsOnStorage(params.toString())
        AppController.service!!.registerDevice(params)
            .enqueue(object : Callback<RegisterApiResponse> {
                override fun onResponse(
                    call: Call<RegisterApiResponse>,
                    response: Response<RegisterApiResponse>
                ) {
                    when (response.code()) {
                        201 -> {
                            showPrompt(
                                this@ChangeStoreActivity,
                                resources.getString(R.string.lb_store_changed_successfully)
                            )
                            setSharedPreferenceBoolean(
                                this@ChangeStoreActivity,
                                SharedPreferenceHelper.KEY_REGISTRATIONDONE,
                                true
                            )
                            setSharedPreferenceBoolean(
                                this@ChangeStoreActivity,
                                SharedPreferenceHelper.KEY_ALREADYLOGGEDIN,
                                true
                            )
                            setSharedPreferenceString(
                                this@ChangeStoreActivity,
                                SharedPreferenceHelper.KEY_RESPONDER,
                                response.body()!!.Responder
                            )
                            setSharedPreferenceBoolean(
                                this@ChangeStoreActivity,
                                SharedPreferenceHelper.KEY_AUTOCOMPLETE_TASK,
                                true
                            )
                            setSharedPreferenceString(
                                this@ChangeStoreActivity,
                                SharedPreferenceHelper.KEY_STOREID,
                                response.body()!!.StoreId
                            )
                            setSharedPreferenceString(
                                this@ChangeStoreActivity,
                                SharedPreferenceHelper.KEY_STOREADDRESS,
                                storeAddress
                            )
                            setSharedPreferenceString(
                                this@ChangeStoreActivity,
                                SharedPreferenceHelper.KEY_DEVICEID,
                                response.body()!!.DeviceId
                            )
                            startActivity(
                                Intent(
                                    this@ChangeStoreActivity,
                                    HomeActivity::class.java
                                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                            finish()
                        }
                        else -> {
                            showPrompt(
                                this@ChangeStoreActivity,
                                String.format(
                                    resources.getString(R.string.error_while_register),
                                    response.message()
                                )
                            )
                        }
                    }
                    loader.visibility = View.GONE
                }

                override fun onFailure(call: Call<RegisterApiResponse>, t: Throwable) {
                    loader.visibility = View.GONE
                    showPrompt(
                        this@ChangeStoreActivity,
                        String.format(
                            resources.getString(R.string.error_while_register),
                            "Api Failure"
                        )
                    )
                }
            })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.APP.QRCODE_REQUESTCODE1) {
            val fragment: ChangeStoreFragment? = ChangeStoreFragment.newInstance(false)
            fragment!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.size > 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Please allow storage permission to write logs.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private val TAG = ChangeStoreActivity::class.java.simpleName
        private const val REQ_PERMISSION = 123
    }
}
