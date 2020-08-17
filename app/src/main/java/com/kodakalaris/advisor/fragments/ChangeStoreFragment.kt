package com.kodakalaris.advisor.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.ChangeStoreActivity
import com.kodakalaris.advisor.activities.HomeActivity
import com.kodakalaris.advisor.activities.QrCodeScannerActivity
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.eventbus.AppEvents.RegisteringStoreEvent
import com.kodakalaris.advisor.model.BarcodeData
import com.kodakalaris.advisor.model.RegisterApiResponse
import com.kodakalaris.advisor.model.RequestDeviceRegisterApi
import com.kodakalaris.advisor.model.RequestModifyDeviceRegisterApi
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.closeKeyboard
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.formatizeAddress
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.openKeyboard
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [ChangeStoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangeStoreFragment : Fragment(), View.OnClickListener, OnEditorActionListener, TextWatcher,
    OnTouchListener {

    private var mIsHomeScreenFragment = true
    private var layout_main: ConstraintLayout? = null
    private var btn_qrcode_scanner: Button? = null
    private var btn_changestore: Button? = null
    private var edittext_yourname: EditText? = null
    private var txt_address: TextView? = null
    private var txt_yourname: TextView? = null
    private var txt_storeId: TextView? = null
    private var img_edit: ImageView? = null
    private var mActivity: AppCompatActivity? = null
    private var lastScannedBarcodeStoreinfo: BarcodeData.StoreBean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mIsHomeScreenFragment = getArguments()!!.getBoolean(ARG_IS_HOMESCREEN_FRAGMENT)
        }
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return if (!mIsHomeScreenFragment) {
            Log.e("ClerkApp", "Not home screen fragment ")
            inflater.inflate(R.layout.fragment_change_store, container, false)
        } else {
            Log.e("ClerkApp", "Is home screen fragment")
            inflater.inflate(R.layout.fragment_homescreen_storeinfo, container, false)
        }
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        bindListeners()
        bindData()
    }

    /**
     * initialize the view
     * @param view
     */
    private fun initView(view: View) {
        layout_main = view.findViewById(R.id.layout_main)
        btn_qrcode_scanner = view.findViewById(R.id.btn_qrcode_scanner)
        btn_changestore = view.findViewById(R.id.btn_changestore)
        txt_yourname = view.findViewById(R.id.txt_yourname)
        edittext_yourname = view.findViewById(R.id.edittext_yourname)
        txt_address = view.findViewById(R.id.txt_address)
        img_edit = view.findViewById(R.id.img_edit)
        txt_storeId = view.findViewById(R.id.txt_storeId)
    }

    /**
     * bind the listeners
     */
    private fun bindListeners() {
        btn_qrcode_scanner!!.setOnClickListener(this)
        if (!mIsHomeScreenFragment) {
            layout_main!!.setOnTouchListener(this)
            img_edit!!.setOnClickListener(this)
            edittext_yourname!!.setOnEditorActionListener(this)
            edittext_yourname!!.addTextChangedListener(this)
        }
        btn_changestore!!.setOnClickListener(this)
    }

    /**
     * bind the data with view/UI
     */
    private fun bindData() {
        val usernamePref =
            getSharedPreferenceString(mActivity!!, SharedPreferenceHelper.KEY_RESPONDER, null)
        val storeidPref =
            getSharedPreferenceString(mActivity!!, SharedPreferenceHelper.KEY_STOREID, "")
        val storeaddressPref =
            getSharedPreferenceString(mActivity!!, SharedPreferenceHelper.KEY_STOREADDRESS, null)
        if (!mIsHomeScreenFragment) {
            txt_yourname!!.visibility = View.VISIBLE
            edittext_yourname!!.visibility = View.VISIBLE
            img_edit!!.visibility = View.VISIBLE
            btn_changestore!!.isEnabled =
                storeidPref != null && storeaddressPref != null && !mIsHomeScreenFragment
            edittext_yourname!!.setText(usernamePref)
            edittext_yourname!!.setSelection(edittext_yourname!!.text.length)
            edittext_yourname!!.isCursorVisible = false
        } else {
            btn_changestore!!.isEnabled = true
        }
        if (!storeidPref!!.isEmpty()) {
            txt_storeId!!.text = storeidPref
            txt_storeId!!.visibility = View.VISIBLE
        } else {
            txt_storeId!!.visibility = View.GONE
        }
        txt_address!!.text = storeaddressPref
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v.id == R.id.layout_main && !mIsHomeScreenFragment && edittext_yourname!!.isSelected && btn_qrcode_scanner!!.isEnabled) {
            closeKeyboard(mActivity!!)
            img_edit!!.performClick()
        }
        return false
    }

    override fun onClick(v: View) {
        closeKeyboard(mActivity!!)
        when (v.id) {
            R.id.img_edit -> {
                disableDoubleTap(v)
                edittext_yourname!!.isSelected = !edittext_yourname!!.isSelected
                activateDeactivateUserfield(edittext_yourname!!.isSelected)
            }
            R.id.btn_qrcode_scanner -> {
                /**
                 * handling qrcode scanning button
                 */
                disableDoubleTap(v)
                if (!mIsHomeScreenFragment) {
                    mActivity!!.startActivityForResult(
                        Intent(
                            mActivity,
                            QrCodeScannerActivity::class.java
                        ), Constants.APP.QRCODE_REQUESTCODE1
                    )
                } else {
                    mActivity!!.startActivityForResult(
                        Intent(
                            mActivity,
                            QrCodeScannerActivity::class.java
                        ).putExtra(Constants.APP.LAST_SCREEN_IS_HOME, true),
                        Constants.APP.QRCODE_REQUESTCODE2
                    )
                }
            }
            R.id.btn_changestore -> {
                /**
                 * handling the OK button (change store button)
                 */
                disableDoubleTap(v, 3000)
                if (!mIsHomeScreenFragment) {
                    var firebaseToken = getSharedPreferenceString(
                        mActivity!!,
                        SharedPreferenceHelper.KEY_FCMTOKEN,
                        ""
                    )
                    var storeId: String? =
                        if (lastScannedBarcodeStoreinfo != null) lastScannedBarcodeStoreinfo!!.storeid else ""
                    val responder = edittext_yourname!!.text.toString()
                    if (firebaseToken!!.isEmpty()) {
                        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    return@OnCompleteListener
                                }
                                // Get new Instance ID token
                                firebaseToken = task.result?.token
                            })
                    }
                    if (storeId!!.trim { it <= ' ' }.length == 0) {
                        storeId = getSharedPreferenceString(
                            mActivity!!,
                            SharedPreferenceHelper.KEY_STOREID,
                            ""
                        )
                    }
                    if (storeId!!.trim { it <= ' ' }.length == 0) {
                        showPrompt(mActivity!!, getResources().getString(R.string.invalid_storeid))
                        return
                    } else if (responder.trim { it <= ' ' }.length == 0) {
                        showPrompt(
                            mActivity!!,
                            getResources().getString(R.string.invalid_responder)
                        )
                        return
                    } else if (firebaseToken != null && firebaseToken!!.trim { it <= ' ' }.length == 0) {
                        showPrompt(
                            mActivity!!,
                            java.lang.String.format(
                                getResources().getString(R.string.error_while_register),
                                "registration failed on notification hub"
                            )
                        )
                        return
                    }
                    Log.e("ClerkApp", "Request For Device Registration")
                    val params = RequestDeviceRegisterApi(
                        firebaseToken,
                        Constants.APP.CLOUD_NOTIFICATION,
                        storeId,
                        responder
                    )
                    //Unregister first
                    (mActivity as ChangeStoreActivity?)!!.callUnregisterThenRegisterDeviceApi(
                        params,
                        txt_address!!.text.toString()
                    )
                    //Then register
                    // ((ChangeStoreActivity) mActivity).callRegisterDeviceApi(params, txt_address.getText().toString());
                } else {
                    val deviceid = getSharedPreferenceString(
                        mActivity!!,
                        SharedPreferenceHelper.KEY_DEVICEID,
                        ""
                    )
                    val storeid =
                        if (lastScannedBarcodeStoreinfo != null) lastScannedBarcodeStoreinfo!!.storeid else ""
                    val responder = getSharedPreferenceString(
                        mActivity!!,
                        SharedPreferenceHelper.KEY_RESPONDER,
                        ""
                    )
                    if (deviceid!!.length == 0) {
                        showPrompt(mActivity!!, getResources().getString(R.string.invalid_deviceid))
                        return
                    }
                    if (storeid!!.length == 0) {
                        showPrompt(mActivity!!, getResources().getString(R.string.invalid_storeid))
                        return
                    }
                    if (responder!!.length == 0) {
                        showPrompt(
                            mActivity!!,
                            getResources().getString(R.string.invalid_responder)
                        )
                        return
                    }
                    Log.e("ClerkApp", "Request For Update Device Registration")
                    val requestModifyDeviceRegisterApi =
                        RequestModifyDeviceRegisterApi(storeid, responder)
                    updateDeviceRegistration(deviceid, requestModifyDeviceRegisterApi)
                    //Set the configured store id blank
                    setSharedPreferenceString(
                        mActivity!!,
                        SharedPreferenceHelper.KEY_CONFIGURED_STOREID,
                        ""
                    )
                }
            }
        }
    }

    private fun activateDeactivateUserfield(selected: Boolean) {
        if (selected) {
            img_edit!!.setImageDrawable(
                mActivity!!.getResources().getDrawable(R.drawable.ic_check_white)
            )
            edittext_yourname!!.isSelected = true
            edittext_yourname!!.isEnabled = true
            edittext_yourname!!.requestFocus()
            edittext_yourname!!.setSelection(edittext_yourname!!.text.length)
            edittext_yourname!!.isCursorVisible = true
            openKeyboard(mActivity!!)
        } else {
            btn_changestore!!.isEnabled =
                edittext_yourname!!.text.length > 0 && btn_qrcode_scanner!!.isEnabled
            img_edit!!.setImageDrawable(mActivity!!.getResources().getDrawable(R.drawable.ic_edit))
            edittext_yourname!!.isSelected = false
            edittext_yourname!!.isEnabled = false
            edittext_yourname!!.isCursorVisible = false
        }
    }

    /**
     * update device registration api
     * @param deviceid unique deviceid fetch from device registration api
     * @param requestModifyDeviceRegisterApi request body of the modify device registration api which contain responderid & storeid
     */
    private fun updateDeviceRegistration(
        deviceid: String?,
        requestModifyDeviceRegisterApi: RequestModifyDeviceRegisterApi
    ) {
        if (!isConnectedToInternet(mActivity!!)) {
            showPrompt(mActivity!!, getResources().getString(R.string.error_no_internet_connection))
            return
        }
        //Add request to logger
        generateLogsOnStorage("Modify Device Request")
        generateLogsOnStorage(requestModifyDeviceRegisterApi.toString())
        AppController.service!!.modifyDevice(deviceid, requestModifyDeviceRegisterApi)
            ?.enqueue(object : Callback<RegisterApiResponse?> {
                override fun onResponse(
                    call: Call<RegisterApiResponse?>,
                    response: Response<RegisterApiResponse?>
                ) {
                    when (response.code()) {
                        200 -> {
                            showPrompt(
                                mActivity!!,
                                getResources().getString(R.string.lb_store_changed_successfully)
                            )
                            setSharedPreferenceString(
                                mActivity!!,
                                SharedPreferenceHelper.KEY_STOREID,
                                response.body()!!.StoreId
                            )
                            setSharedPreferenceString(
                                mActivity!!,
                                SharedPreferenceHelper.KEY_STOREADDRESS,
                                txt_address!!.text.toString()
                            )
                            SharedPreferenceHelper.setSharedPreferenceBoolean(
                                mActivity!!,
                                SharedPreferenceHelper.KEY_AUTOCOMPLETE_TASK,
                                true
                            )
                            mActivity!!.recreate()
                        }
                        else -> {
                            showPrompt(
                                mActivity!!,
                                java.lang.String.format(
                                    getResources().getString(R.string.error_while_register),
                                    response.message()
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterApiResponse?>, t: Throwable) {
                    showPrompt(
                        mActivity!!,
                        java.lang.String.format(
                            getResources().getString(R.string.error_while_register),
                            "Api Failure"
                        )
                    )
                }
            })
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        /**
         * on keyboard go click, close the keyboard close & disable the edit field
         */

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            img_edit!!.performClick()
            return true
        }
        return false
    }

    override fun afterTextChanged(s: Editable) {}
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = if (context is HomeActivity) {
            context
        } else if (context is ChangeStoreActivity) {
            context
        } else {
            throw NullPointerException()
        }
    }

    override fun onDetach() {
        super.onDetach()
        //mActivity = null;
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    /**
     * tweek to disable OK button while registration/modify device api is calling
     * @param event event object
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: RegisteringStoreEvent?) {
        if (!mIsHomeScreenFragment) {
            btn_qrcode_scanner!!.isEnabled = false
            btn_changestore!!.isEnabled = false
            img_edit!!.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.APP.QRCODE_REQUESTCODE1 -> {
                if (resultCode == Constants.APP.QRCODE_RESULTCODE1) {
                    val lastScannedBarcodeData =
                        data!!.getStringExtra(Constants.APP.LAST_SCANNED_BARCODE_DATA)
                    lastScannedBarcodeStoreinfo = Gson().fromJson<BarcodeData>(
                        lastScannedBarcodeData,
                        BarcodeData::class.java
                    ).storeinfo
                    val lastScannedStoreId =
                        data.getStringExtra(Constants.APP.LAST_SCANNED_STORE_ID)
                    txt_storeId!!.text = lastScannedStoreId
                    txt_storeId!!.visibility = View.VISIBLE
                    txt_address!!.text = formatizeAddress(lastScannedBarcodeStoreinfo!!)
                    if (edittext_yourname!!.text != null && edittext_yourname!!.text.toString()
                            .isEmpty()
                    ) {
                        img_edit!!.performClick()
                    }
                } else if (resultCode == Constants.APP.QRCODE_RESULTCODE2) {
                    val lastScannedBarcodeError =
                        data!!.getStringExtra(Constants.APP.LAST_SCANNED_BARCODE_ERROR)
                    showPrompt(mActivity!!, lastScannedBarcodeError)
                }
            }
            Constants.APP.QRCODE_REQUESTCODE2 -> {
                if (resultCode == Constants.APP.QRCODE_RESULTCODE1) {
                    val lastScannedBarcodeData =
                        data!!.getStringExtra(Constants.APP.LAST_SCANNED_BARCODE_DATA)
                    lastScannedBarcodeStoreinfo = Gson().fromJson<BarcodeData>(
                        lastScannedBarcodeData,
                        BarcodeData::class.java
                    ).storeinfo
                    txt_address!!.text = formatizeAddress(lastScannedBarcodeStoreinfo!!)
                    val lastScannedStoreId =
                        data.getStringExtra(Constants.APP.LAST_SCANNED_STORE_ID)
                    txt_storeId!!.text = lastScannedStoreId
                } else if (resultCode == Constants.APP.QRCODE_RESULTCODE2) {
                    val lastScannedBarcodeError =
                        data!!.getStringExtra(Constants.APP.LAST_SCANNED_BARCODE_ERROR)
                    showPrompt(mActivity!!, lastScannedBarcodeError)
                }
            }
        }
    }

    companion object {
        var TAG_CHANGESTORE = "change store fragment tag1"
        var TAG_CHANGESTORE_HOME = "change store fragment tag2"
        private const val ARG_IS_HOMESCREEN_FRAGMENT = "is_homescreen_fragment"
        private var mHomeInstance: ChangeStoreFragment? = null
        private var mInstance: ChangeStoreFragment? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param IS_HOMESCREEN
         * @return A new instance of fragment ChangeStoreFragment.
         */
        fun newInstance(IS_HOMESCREEN: Boolean): ChangeStoreFragment? {
            if (mHomeInstance == null && IS_HOMESCREEN) {
                mHomeInstance = ChangeStoreFragment()
                val args = Bundle()
                args.putBoolean(ARG_IS_HOMESCREEN_FRAGMENT, IS_HOMESCREEN)
                mHomeInstance!!.setArguments(args)
                return mHomeInstance
            } else if (mInstance == null && !IS_HOMESCREEN) {
                mInstance = ChangeStoreFragment()
                val args = Bundle()
                args.putBoolean(ARG_IS_HOMESCREEN_FRAGMENT, IS_HOMESCREEN)
                mInstance!!.setArguments(args)
                return mInstance
            }
            return if (IS_HOMESCREEN) mHomeInstance else mInstance
        }
    }
}
