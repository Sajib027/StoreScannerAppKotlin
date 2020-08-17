package com.kodakalaris.advisor.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.custom.navdrawer.data.MenuItem
import com.kodakalaris.advisor.custom.navdrawer.widget.AppNavigationDrawer
import com.kodakalaris.advisor.eventbus.AppEvents.BacktoSettingsScreenEvent
import com.kodakalaris.advisor.eventbus.AppEvents.OnNotificationEvent
import com.kodakalaris.advisor.fragments.ChangeStoreFragment
import com.kodakalaris.advisor.fragments.home.*
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.drawResponder
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.tonePhone
import com.kodakalaris.advisor.utils.Helper.vibratePhone
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceInt
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/*
* Home activity is the main screen where whole functionality shows
* */
class HomeActivity : BaseActivity(), AppNavigationDrawer.OnMenuItemClickListener,
    AppNavigationDrawer.DrawerListener {

    private lateinit var appNavigationDrawer: AppNavigationDrawer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        appNavigationDrawer = findViewById(R.id.navigationDrawer)
        val menuItems = createNavigationItems()
        appNavigationDrawer.setMenuItemList(menuItems as MutableList<MenuItem>)
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(
            R.id.frame_assignments_container, AssignmentsFragment.newInstance()!!,
            AssignmentsFragment::class.java.getSimpleName()
        ).commit()
        appNavigationDrawer.setAppbarTitleTV(resources.getString(R.string.lb_menu_assignments))
        setUserData()
        attachListeners()
    }

    /**
     * set the user data (username and userimage) with the views
     */
    private fun setUserData() {
        val username =
            getSharedPreferenceString(this@HomeActivity, SharedPreferenceHelper.KEY_RESPONDER, "")
        val drawable = drawResponder(this@HomeActivity)
        appNavigationDrawer.setUserName(username)
        appNavigationDrawer.setUserImage(drawable)
    }

    private val visibleFragment: Fragment?
        get() {
            val fragmentManager: FragmentManager = this@HomeActivity.supportFragmentManager
            val fragments: List<Fragment> = fragmentManager.getFragments()
            for (fragment in fragments) {
                if (fragment != null && fragment.isVisible()) return fragment
            }
            return null
        }

    /**
     * bind the listeners
     */
    private fun attachListeners() {
        appNavigationDrawer.onMenuItemClickListener = this
        appNavigationDrawer.drawerListener = this
    }

    public override fun onStart() {
        super.onStart()
        if (isConnectedToInternet(this)) {
            EventBus.getDefault().register(this)
        }
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    /**
     * creates the navigation drawer menu items
     * @return list of menu items
     */
    private fun createNavigationItems(): List<MenuItem> {
        val menuItems: MutableList<MenuItem> = ArrayList()
        menuItems.add(
            MenuItem(
                resources.getString(R.string.lb_menu_assignments),
                R.drawable.ic_menu_assignments
            )
        )
        menuItems.add(
            MenuItem(
                resources.getString(R.string.lb_menu_tickets),
                R.drawable.ic_menu_ticket
            )
        )
        menuItems.add(MenuItem(resources.getString(R.string.lb_menu_news), R.drawable.icon_info))
        menuItems.add(MenuItem(resources.getString(R.string.lb_menu_help), R.drawable.icon_help))
        menuItems.add(
            MenuItem(
                resources.getString(R.string.lb_menu_storeinfo),
                R.drawable.ic_menu_storeinfo
            )
        )
        menuItems.add(
            MenuItem(
                resources.getString(R.string.lb_menu_servicecall),
                R.drawable.ic_menu_servicecall
            )
        )
        menuItems.add(
            MenuItem(
                resources.getString(R.string.lb_menu_settings),
                R.drawable.ic_menu_settings
            )
        )
        return menuItems
    }

    fun setWindowTitle(windowTitle: String?) {
        appNavigationDrawer.setAppbarTitleTV(windowTitle)
    }

    override fun onMenuItemClicked(position: Int) {
        var fragment: Fragment? = null
        var tag = ""
        var title = ""
        when (position) {
            0 -> {
                fragment = AssignmentsFragment.newInstance()
                tag = AssignmentsFragment::class.java.getSimpleName()
                title = resources.getString(R.string.lb_menu_assignments)
            }
            1 -> {
                fragment = TicketsFragment.newInstance()
                tag = TicketsFragment::class.java.getSimpleName()
                title = resources.getString(R.string.lb_menu_tickets)
            }
            2 -> {
                fragment = NewsFragment.newInstance()
                tag = NewsFragment::class.java.getSimpleName()
                title = resources.getString(R.string.lb_menu_news)
            }
            3 -> {
                fragment = HelpFragment.newInstance()
                tag = HelpFragment::class.java.getSimpleName()
                title = resources.getString(R.string.lb_menu_help)
            }
            4 -> {
                fragment = ChangeStoreFragment.newInstance(true)
                tag = ChangeStoreFragment.TAG_CHANGESTORE_HOME
                title = resources.getString(R.string.lb_menu_storeinfo)
            }
            5 -> {
                fragment = ServiceCallFragment.newInstance()
                tag = ServiceCallFragment::class.java.getSimpleName()
                title = resources.getString(R.string.lb_menu_servicecall)
            }
            6 -> {
                fragment = SettingsFragment.newInstance()
                tag = SettingsFragment::class.java.getSimpleName()
                title = resources.getString(R.string.lb_menu_settings)
            }
        }
        appNavigationDrawer.setAppbarTitleTV(title)
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.frame_assignments_container, fragment!!, tag)
                .commit()
        }
    }

    override fun onDrawerOpened() {}

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onDrawerOpening() {
        /*if (getVisibleFragment() instanceof HelpFragment){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        System.out.println("current visible frag " + getVisibleFragment());*/
    }

    override fun onDrawerClosing() {
        /*if (getVisibleFragment() instanceof HelpFragment){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        System.out.println("current visible frag " + getVisibleFragment());*/
    }

    override fun onDrawerClosed() {
        if (appNavigationDrawer.getAppbarTitleTV()
                .equals(resources.getString(R.string.lb_menu_settings), ignoreCase = true)
        ) {
            // fire the backtosettingsscreen event to handle vibration & tone on/off
            EventBus.getDefault().post(BacktoSettingsScreenEvent())
        }
    }

    override fun onDrawerStateChanged(newState: Int) {}

    override fun onResume() {
        super.onResume()
        setSharedPreferenceInt(this@HomeActivity, SharedPreferenceHelper.KEY_MESSAGECOUNT, 0)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        AppController.isAppIsInForeground = true
    }

    override fun onPause() {
        super.onPause()
        AppController.isAppIsInForeground = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: OnNotificationEvent?) {
        AssignmentsFragment.newInstance()!!.fetchEvents()
        val vibration = getSharedPreferenceBoolean(
            this@HomeActivity,
            SharedPreferenceHelper.KEY_VIBRATION,
            true
        )
        val tone =
            getSharedPreferenceBoolean(this@HomeActivity, SharedPreferenceHelper.KEY_TONE, true)
        if (vibration && !tone) {
            Log.e(TAG, "vibration && !tone")
            vibratePhone(this@HomeActivity)
        } else if (!vibration && tone) {
            Log.e(TAG, "!vibration && tone")
            tonePhone(this@HomeActivity)
        } else if (vibration && tone) {
            Log.e(TAG, "vibration && tone")
            tonePhone(this@HomeActivity)
            vibratePhone(this@HomeActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == Constants.APP.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                val key = data!!.getStringExtra("key")
                val fragments: List<Fragment> = supportFragmentManager.fragments
                for (fragment in fragments) {
                    if (fragment is AssignmentsFragment && key.equals("1", ignoreCase = true)) {
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frame_assignments_container,
                            AssignmentsFragment.newInstance()!!
                        ).commit()
                        AssignmentsFragment.newInstance()!!.fetchEvents()
                    }
                }
            } else if (requestCode == Constants.APP.QRCODE_REQUESTCODE2) {
                val fragment: Fragment? =
                    supportFragmentManager.findFragmentByTag(ChangeStoreFragment.TAG_CHANGESTORE_HOME)
                fragment!!.onActivityResult(requestCode, resultCode, data)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (appNavigationDrawer.isDrawerOpen) {
            appNavigationDrawer.closeDrawer()
            return
        }
        val fragments: List<Fragment> = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is AssignmentsFragment) {
                showKillAppDialog()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_assignments_container, AssignmentsFragment.newInstance()!!)
                    .commit()
                appNavigationDrawer.setAppbarTitleTV(resources.getString(R.string.lb_menu_assignments))
            }
        }
    }

    private lateinit var alertDialog: AlertDialog

    @SuppressLint("InflateParams")
    private fun showKillAppDialog() {
        // get prompts.xml view
        val li = LayoutInflater.from(this@HomeActivity)
        val promptsView = li.inflate(R.layout.app_kill_confirmation, null)
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this@HomeActivity)
        alertDialogBuilder.setView(promptsView)
        val userInput = promptsView.findViewById<EditText>(R.id.etPwd)
        val tvCancel = promptsView.findViewById<TextView>(R.id.tvCancel)
        val tvYes = promptsView.findViewById<TextView>(R.id.tvYes)
        val chkNotification = promptsView.findViewById<CheckBox>(R.id.chkNotification)
        val notificationFlag = getSharedPreferenceBoolean(
            applicationContext,
            SharedPreferenceHelper.KEY_NOTIFICATION,
            true
        )
        chkNotification.isChecked = !notificationFlag
        chkNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.e("ClerkApp", "Exit Notification status : $isChecked")
            setSharedPreferenceBoolean(
                applicationContext,
                SharedPreferenceHelper.KEY_NOTIFICATION,
                !isChecked
            )
        }
        // create alert dialog
        alertDialog = alertDialogBuilder.create()
        tvCancel.setOnClickListener { view: View? ->
            disableDoubleTap(view!!)
            alertDialog.cancel()
        }
        tvYes.setOnClickListener { view: View? ->
            disableDoubleTap(view!!)
            finish()
        }
        if (alertDialog != null) {
            // show it
            alertDialog.show()
            alertDialog.setCancelable(false)
            alertDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.getWindow()!!
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    companion object {
        private val TAG = HomeActivity::class.java.simpleName
    }
}