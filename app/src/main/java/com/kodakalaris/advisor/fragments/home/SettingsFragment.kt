package com.kodakalaris.advisor.fragments.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.BackdoorActivity
import com.kodakalaris.advisor.activities.HomeActivity
import com.kodakalaris.advisor.eventbus.AppEvents.BacktoSettingsScreenEvent
import com.kodakalaris.advisor.model.EventSettings
import com.kodakalaris.advisor.model.StoreConfigurationUpdateRequest
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.closeKeyboard
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.drawResponder
import com.kodakalaris.advisor.utils.Helper.openKeyboard
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.Helper.toMd5
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * forth fragment from the navigation drawer/side menu
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener,
    OnEditorActionListener, TextWatcher /*View.OnTouchListener*/ {

    private lateinit var layout_main: ConstraintLayout
    private lateinit var edittext_yourname: EditText
    private lateinit var img_username: ImageView
    private lateinit var img_edit: ImageView
    private lateinit var switch_vibration: Switch
    private lateinit var switch_tone: Switch
    private lateinit var switch_notification: Switch
    private lateinit var switch_kpk_help: Switch
    private lateinit var mTvAppVersion: TextView
    private lateinit var mActivity: HomeActivity
    private lateinit var alertDialog: AlertDialog
    private var count = 0
    private var startMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initListener()
        bindData()
    }

    /**
     * bind the listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        //   layout_main.setOnTouchListener(this);
        img_edit.setOnClickListener(this)
        switch_vibration.setOnCheckedChangeListener(this)
        switch_tone.setOnCheckedChangeListener(this)
        switch_notification.setOnCheckedChangeListener(this)
        switch_kpk_help.setOnCheckedChangeListener(this)
        mTvAppVersion.setOnTouchListener(CustomTouchListener())
        edittext_yourname.setOnEditorActionListener(this)
        edittext_yourname.addTextChangedListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setAppVersion() {
        try {
            val pInfo: PackageInfo = getActivity()!!.getPackageManager()
                .getPackageInfo(getActivity()!!.getPackageName(), 0)
            val version = pInfo.versionName
            mTvAppVersion.setText(
                getResources().getString(R.string.label_version).toString() + " " + version
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * bind the data with the view/UI
     */
    private fun bindData() {
        edittext_yourname.isSelected = false
        edittext_yourname.isEnabled = false
        val username =
            getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_RESPONDER, "")
        val drawable = drawResponder(mActivity)
        edittext_yourname.setText(username)
        img_username.setImageDrawable(drawable)
        setAppVersion()
    }

    /**
     * initialize the view/UI
     * @param view
     */
    private fun initView(view: View) {
        layout_main = view.findViewById(R.id.layout_main)
        edittext_yourname = view.findViewById(R.id.edittext_yourname)
        img_username = view.findViewById(R.id.img_username)
        switch_vibration = view.findViewById(R.id.switch_vibration)
        switch_tone = view.findViewById(R.id.switch_tone)
        switch_notification = view.findViewById(R.id.switch_notification)
        switch_kpk_help = view.findViewById(R.id.switch_kpk_help)
        switch_kpk_help.setVisibility(View.GONE)
        img_edit = view.findViewById(R.id.img_edit)
        mTvAppVersion = view.findViewById(R.id.tvAppVersion)
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
     * handle the eventbus when user visiblility back to settings screen
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: BacktoSettingsScreenEvent?) {
        val donotdisturb =
            getSharedPreferenceBoolean(mActivity, SharedPreferenceHelper.KEY_DONOTDISTURB, false)
        val vibration =
            getSharedPreferenceBoolean(mActivity, SharedPreferenceHelper.KEY_VIBRATION, true)
        val tone = getSharedPreferenceBoolean(mActivity, SharedPreferenceHelper.KEY_TONE, true)
        val notification =
            getSharedPreferenceBoolean(mActivity, SharedPreferenceHelper.KEY_NOTIFICATION, true)
        Log.e("ClerkApp", "Notification Status is : $notification")
        val kpk_help_status =
            getSharedPreferenceBoolean(mActivity, SharedPreferenceHelper.KEY_KPK_HELP, true)
        if (donotdisturb) {
            switch_vibration.isChecked = false
            switch_tone.isChecked = false
            switch_notification.isChecked = false
            switch_kpk_help.isChecked = kpk_help_status
            switch_vibration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_vibration_off,
                0,
                0,
                0
            )
            switch_tone.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_tone_off,
                0,
                0,
                0
            )
            switch_notification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_notification_off,
                0,
                0,
                0
            )
        } else {
            switch_vibration.isChecked = vibration
            switch_tone.isChecked = tone
            switch_notification.isChecked = notification
            switch_kpk_help.isChecked = kpk_help_status
            switch_vibration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (vibration) R.drawable.ic_vibration_on else R.drawable.ic_vibration_off,
                0,
                0,
                0
            )
            switch_tone.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (tone) R.drawable.ic_tone_on else R.drawable.ic_tone_off,
                0,
                0,
                0
            )
            switch_notification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (notification) R.drawable.ic_notification_on else R.drawable.ic_notification_off,
                0,
                0,
                0
            )
        }
    }

    override fun onClick(v: View) {
        disableDoubleTap(v)
        when (v.id) {
            R.id.img_edit -> {
                edittext_yourname.isSelected = !edittext_yourname.isSelected
                if (edittext_yourname.isSelected) {
                    img_edit.setImageDrawable(mActivity.resources.getDrawable(R.drawable.ic_check_teal))
                    edittext_yourname.isSelected = true
                    edittext_yourname.isEnabled = true
                    edittext_yourname.requestFocus()
                    edittext_yourname.setSelection(edittext_yourname.text.length)
                    openKeyboard(mActivity)
                } else {
                    if (edittext_yourname.text != null && edittext_yourname.text.toString().length > 0) {
                        img_edit.setImageDrawable(mActivity.resources.getDrawable(R.drawable.ic_edit_teal))
                        edittext_yourname.isSelected = false
                        edittext_yourname.isEnabled = false
                        setSharedPreferenceString(
                            mActivity,
                            SharedPreferenceHelper.KEY_RESPONDER,
                            edittext_yourname.text.toString()
                        )
                        val drawable = drawResponder(mActivity)
                        img_username.setImageDrawable(drawable)
                    } else {
                        showPrompt(
                            mActivity,
                            getResources().getString(R.string.lb_required_username)
                        )
                    }
                }
            }
        }
    }

    /**
     * change the shared preference value for vibration & volume on any change
     * @param buttonView
     * @param isChecked
     */
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.switch_vibration -> {
                setSharedPreferenceBoolean(
                    mActivity,
                    SharedPreferenceHelper.KEY_VIBRATION,
                    isChecked
                )
                switch_vibration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (isChecked) R.drawable.ic_vibration_on else R.drawable.ic_vibration_off,
                    0,
                    0,
                    0
                )
            }
            R.id.switch_tone -> {
                setSharedPreferenceBoolean(mActivity, SharedPreferenceHelper.KEY_TONE, isChecked)
                switch_tone.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (isChecked) R.drawable.ic_tone_on else R.drawable.ic_tone_off, 0, 0, 0
                )
            }
            R.id.switch_notification -> {
                Log.e("ClerkApp", "Notification going : $isChecked")
                setSharedPreferenceBoolean(
                    mActivity,
                    SharedPreferenceHelper.KEY_NOTIFICATION,
                    isChecked
                )
                switch_notification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (isChecked) R.drawable.ic_notification_on else R.drawable.ic_notification_off,
                    0,
                    0,
                    0
                )
            }
            R.id.switch_kpk_help -> {
                Log.e("ClerkApp", "KPK Help going : $isChecked")
                val configuredStoreId = getSharedPreferenceString(
                    mActivity,
                    SharedPreferenceHelper.KEY_CONFIGURED_STOREID,
                    ""
                )
                Log.e("ClerkApp", "Configured StoreId : $configuredStoreId")
                if (configuredStoreId!!.isEmpty()) {
                    //Store not configured then create configuration
                    Log.e("ClerkApp", "Store Not Configured Lets create")
                    createStoreConfiguration(isChecked)
                } else {
                    Log.e("ClerkApp", "Store Already Configured Lets update")
                    updateStoreConfiguration(isChecked)
                }
            }
        }
    }

    //Method to update StoreConfiguration Help
    fun updateStoreConfiguration(flag: Boolean) {
        val StoreId = getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_STOREID, null)
        val request = StoreConfigurationUpdateRequest()
        request.StoreId = StoreId
        val eventSettings = EventSettings()
        eventSettings.EventType = "HELP"
        eventSettings.Enabled = flag
        val eventSettingsList: ArrayList<EventSettings> = ArrayList<EventSettings>()
        eventSettingsList.add(eventSettings)
        request.EventSettings = eventSettingsList
    }

    //Method to update StoreConfiguration Help
    fun createStoreConfiguration(flag: Boolean) {
        val StoreId = getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_STOREID, null)
        val request = StoreConfigurationUpdateRequest()
        request.StoreId = StoreId
        val eventSettings = EventSettings()
        eventSettings.EventType = "HELP"
        eventSettings.Enabled = flag
        val eventSettingsList: ArrayList<EventSettings> = ArrayList<EventSettings>()
        eventSettingsList.add(eventSettings)
        request.EventSettings = eventSettingsList
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        /**
         * on keyboard go click, close the keyboard close & disable the edit field
         */
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            img_edit.performClick()
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
        } else {
            throw NullPointerException()
        }
    }

    override fun onDetach() {
        super.onDetach()
        //mActivity = null;
    }

    inner class CustomTouchListener : OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) { //get system current milliseconds
                val time = System.currentTimeMillis()
                //if it  is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
                if (startMillis == 0L || time - startMillis > 5000) {
                    startMillis = time
                    count = 1
                } else { //  time-startMillis< 3000
                    count++
                }
                if (count == 7) { //do whatever you need
                    showPwdDialog()
                }
                return true
            }
            return false
        }
    }

    private fun showPwdDialog() { // get prompts.xml view
        val li = LayoutInflater.from(mActivity)
        val promptsView = li.inflate(R.layout.pwd_prompts, null)
        val alertDialogBuilder = AlertDialog.Builder(mActivity)
        alertDialogBuilder.setView(promptsView)
        val userInput = promptsView.findViewById<EditText>(R.id.etPwd)
        val tvCancel = promptsView.findViewById<TextView>(R.id.tvCancel)
        val tvYes = promptsView.findViewById<TextView>(R.id.tvYes)
        // create alert dialog
        alertDialog = alertDialogBuilder.create()
        tvCancel.setOnClickListener { view: View? ->
            disableDoubleTap(view!!)
            closeKeyboard(mActivity)
            alertDialog.cancel()
        }
        tvYes.setOnClickListener { view: View? ->
            disableDoubleTap(view!!)
            val inputManager =
                mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputManager != null && alertDialog.getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(
                    alertDialog.getCurrentFocus()!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
            checkPwd(userInput.text.toString().trim { it <= ' ' })
        }
        if (alertDialog != null) { // show it
            alertDialog.show()
            alertDialog.setCancelable(false)
            alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun checkPwd(text: String) {
        if (!text.isEmpty() && toMd5(text).equals(Constants.APP.PASSWORD_MODE, ignoreCase = true)) {
            alertDialog.cancel()
            val intent = Intent(getActivity(), BackdoorActivity::class.java)
            startActivity(intent)
        } else if (text.isEmpty()) {
            showPrompt(mActivity, mActivity.resources.getString(R.string.msg_password_required))
        } else {
            showPrompt(mActivity, mActivity.resources.getString(R.string.msg_password_notmatched))
        }
    }

    override fun onResume() {
        super.onResume()
        (getActivity() as HomeActivity).setWindowTitle(getResources().getString(R.string.lb_menu_settings))
    }

    companion object {
        private var fragment: SettingsFragment? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SettingsFragment.
         */
        @Synchronized
        fun newInstance(): SettingsFragment? {
            if (fragment == null) {
                fragment = SettingsFragment()
            }
            return fragment
        }
    }
}
