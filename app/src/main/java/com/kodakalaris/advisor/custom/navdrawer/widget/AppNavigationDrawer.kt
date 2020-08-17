package com.kodakalaris.advisor.custom.navdrawer.widget

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.IntDef
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.custom.TextDrawable
import com.kodakalaris.advisor.custom.navdrawer.data.MenuItem
import com.kodakalaris.advisor.utils.Helper.drawResponder
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceBoolean
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class AppNavigationDrawer : RelativeLayout, CompoundButton.OnCheckedChangeListener {
    //Context
    private var mContext: Context? = null
    private var mLayoutInflater: LayoutInflater? = null

    //Layouts
    private var menuItemList: MutableList<MenuItem>? = null
    private var appbarRL: RelativeLayout? = null
    private var rootLayout: ConstraintLayout? = null
    private var containerCV: CardView? = null
    private var appbarTitleTV: TextView? = null
    private var mTvAppVersion: TextView? = null
    private var txt_username: TextView? = null
    private var menuIV: ImageView? = null
    private var img_username: ImageView? = null
    private var menuSV: ScrollView? = null
    private var menuLL: LinearLayout? = null
    private var containerLL: LinearLayout? = null
    private var switch_donotdisturb: Switch? = null

    //Customization Variables
    private var appbarColor = R.color.colorPrimaryDark
    private var appbarTitleTextColor = R.color.white
    var menuItemSemiTransparentColor = R.color.transparent
        set(menuItemSemiTransparentColor) {
            field = menuItemSemiTransparentColor
            invalidate()
        }
    var navigationDrawerBackgroundColor = R.color.white
        set(navigationDrawerBackgroundColor) {
            rootLayout!!.setBackgroundColor(navigationDrawerBackgroundColor)
            field = navigationDrawerBackgroundColor
        }
    var primaryMenuItemTextColor = R.color.white
        set(primaryMenuItemTextColor) {
            field = primaryMenuItemTextColor
            invalidate()
        }
    var secondaryMenuItemTextColor = R.color.white
        set(secondaryMenuItemTextColor) {
            field = secondaryMenuItemTextColor
            invalidate()
        }
    private var menuIconTintColor = R.color.white

    //Todo Change Icon Size
    var menuIconSize = 24f
    var appbarTitleTextSize = 24f
        set(appbarTitleTextSize) {
            field = appbarTitleTextSize
            appbarTitleTV!!.textSize = appbarTitleTextSize
        }
    var primaryMenuItemTextSize = 16f
        set(primaryMenuItemTextSize) {
            field = primaryMenuItemTextSize
            invalidate()
        }
    var secondaryMenuItemTextSize = 14f
        set(secondaryMenuItemTextSize) {
            field = secondaryMenuItemTextSize
            invalidate()
        }

    //To check if drawer is open or not
    //Other stuff
    var isDrawerOpen = false
        private set
    private var currentPos = 0
    var centerX = 0f
    var centerY = 0f

    @IntDef(*[STATE_OPEN, STATE_CLOSED, STATE_OPENING, STATE_CLOSING])
    @Retention(RetentionPolicy.SOURCE)
    private annotation class State

    private val mLastEvent = MotionEvent.ACTION_UP

    //Listeners
    var onHamMenuClickListener: OnHamMenuClickListener? = null
    var onMenuItemClickListener: OnMenuItemClickListener? = null
    var drawerListener: DrawerListener? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.AppNavigationDrawer, 0, 0)
        setAttributes(a)
        a.recycle()
    }

    //Adding the child views inside CardView LinearLayout
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (containerLL == null) {
            super.addView(child, index, params)
        } else { //Forward these calls to the content view
            containerLL!!.addView(child, index, params)
        }
    }

    //Initialization
    private fun init(context: Context) {
        mContext = context
        mLayoutInflater = LayoutInflater.from(context)
        //Load RootView from xml
        val rootView = mLayoutInflater!!.inflate(R.layout.widget_navigation_drawer, this, true)
        rootLayout = rootView.findViewById(R.id.rootLayout)
        appbarRL = rootView.findViewById(R.id.appBarRL)
        containerCV = rootView.findViewById(R.id.containerCV)
        appbarTitleTV = rootView.findViewById(R.id.appBarTitleTV)
        mTvAppVersion = rootView.findViewById(R.id.tvAppVersion)
        txt_username = rootView.findViewById(R.id.txt_username)
        img_username = rootView.findViewById(R.id.img_username)
        menuIV = rootView.findViewById(R.id.menuIV)
        menuSV = rootView.findViewById(R.id.menuSV)
        menuLL = rootView.findViewById(R.id.menuLL)
        containerLL = rootView.findViewById(R.id.containerLL)
        switch_donotdisturb = rootView.findViewById(R.id.switch_donotdisturb)
        val donotdisturb_state =
            getSharedPreferenceBoolean(mContext!!, SharedPreferenceHelper.KEY_DONOTDISTURB, false)
        switch_donotdisturb!!.setChecked(donotdisturb_state)
        switch_donotdisturb!!.setCompoundDrawablesWithIntrinsicBounds(
            if (switch_donotdisturb!!.isChecked()) R.drawable.ic_mute_on else R.drawable.ic_mute_off,
            0,
            0,
            0
        )
        switch_donotdisturb!!.setOnCheckedChangeListener(this)
        try {
            val pInfo = mContext!!.packageManager.getPackageInfo(mContext!!.packageName, 0)
            val version = pInfo.versionName
            mTvAppVersion!!.setText(resources.getString(R.string.label_version) + " " + version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        menuItemList = ArrayList()
        menuIV!!.setOnClickListener(OnClickListener { view: View? ->
            hamMenuClicked()
            if (isDrawerOpen) {
                closeDrawer()
            } else {
                openDrawer()
            }
        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.switch_donotdisturb -> {
                setSharedPreferenceBoolean(
                    mContext!!,
                    SharedPreferenceHelper.KEY_DONOTDISTURB,
                    isChecked
                )
                switch_donotdisturb!!.setCompoundDrawablesWithIntrinsicBounds(
                    if (isChecked) R.drawable.ic_mute_on else R.drawable.ic_mute_off,
                    0,
                    0,
                    0
                )
                if (isChecked) {
                    setSharedPreferenceBoolean(
                        mContext!!,
                        SharedPreferenceHelper.KEY_VIBRATION,
                        false
                    )
                    setSharedPreferenceBoolean(mContext!!, SharedPreferenceHelper.KEY_TONE, false)
                }
            }
        }
    }

    private fun initMenu() {
        for (i in menuItemList!!.indices) {
            val titleTV =
                LayoutInflater.from(context).inflate(R.layout.menu_row_item, null) as TextView
            if (i == 0) {
                titleTV.isSelected = true
                currentPos = i
            }
            titleTV.tag = i
            titleTV.text = menuItemList!![i].title
            titleTV.setCompoundDrawablesRelativeWithIntrinsicBounds(
                menuItemList!![i].imageId,
                0,
                0,
                0
            )
            menuLL!!.addView(titleTV)
            titleTV.setOnClickListener { view: View ->
                if (currentPos != Integer.valueOf(view.tag.toString())) {
                    for (j in menuItemList!!.indices) {
                        menuLL!!.getChildAt(j).isSelected = false
                    }
                    view.isSelected = true
                    currentPos = Integer.valueOf(view.tag.toString())
                    menuItemClicked(currentPos)
                    closeDrawer()
                } else {
                    currentPos = Integer.valueOf(view.tag.toString())
                    menuItemClicked(currentPos)
                    closeDrawer()
                }
            }
        }
    }

    fun switchToMenu(position: Int) {
        try {
            menuLL!!.getChildAt(position).performClick()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    //Hamburger button Click Listener
    interface OnHamMenuClickListener {
        fun onHamMenuClicked()
    }

    //Listener for menu item click
    interface OnMenuItemClickListener {
        fun onMenuItemClicked(position: Int)
    }

    //Listener for monitoring events about drawer.
    interface DrawerListener {
        //Called when a drawer is opening.
        fun onDrawerOpening()

        //Called when a drawer is closing.
        fun onDrawerClosing()

        //Called when a drawer has settled in a completely open state.
        fun onDrawerOpened()

        //Called when a drawer has settled in a completely closed state.
        fun onDrawerClosed()

        //Called when the drawer motion state changes. The new state will
        fun onDrawerStateChanged(@State newState: Int)
    }

    private fun hamMenuClicked() {
        if (onHamMenuClickListener != null) {
            onHamMenuClickListener!!.onHamMenuClicked()
        }
    }

    private fun menuItemClicked(position: Int) {
        if (onMenuItemClickListener != null) {
            onMenuItemClickListener!!.onMenuItemClicked(position)
        }
    }

    private fun drawerOpened() {
        if (drawerListener != null) {
            drawerListener!!.onDrawerOpened()
            drawerListener!!.onDrawerStateChanged(STATE_OPEN)
        }
    }

    private fun drawerClosed() {
        println("Drawer Closing")
        if (drawerListener != null) {
            drawerListener!!.onDrawerClosed()
            drawerListener!!.onDrawerStateChanged(STATE_CLOSED)
        }
    }

    private fun drawerOpening() {
        if (drawerListener != null) {
            drawerListener!!.onDrawerOpening()
            drawerListener!!.onDrawerStateChanged(STATE_OPENING)
        }
    }

    private fun drawerClosing() {
        if (drawerListener != null) {
            drawerListener!!.onDrawerClosing()
            drawerListener!!.onDrawerStateChanged(STATE_CLOSING)
        }
    }

    //Closes drawer
    fun closeDrawer() {
        val username =
            getSharedPreferenceString(mContext!!, SharedPreferenceHelper.KEY_RESPONDER, "")
        val drawable = drawResponder(mContext!!)
        setUserName(username)
        setUserImage(drawable)
        drawerClosing()
        isDrawerOpen = false
        val stateSet = intArrayOf(android.R.attr.state_checked * if (isDrawerOpen) 1 else -1)
        menuIV!!.setImageState(stateSet, true)
        //appbarTitleTV.animate().translationX(centerX).start();
        containerCV
            ?.animate()
            ?.scaleX(1.0f)
            ?.scaleY(1.0f)
            ?.translationX(rootLayout!!.getX())
            ?.translationY(rootLayout!!.getY())
            ?.setDuration(500)
            ?.start()
        val handler = Handler()
        handler.postDelayed({
            drawerClosed()
            containerCV!!.setCardElevation(0.toFloat())
            containerCV!!.setRadius(0.toFloat())
        }, 500)
    }

    //Opens Drawer
    private fun openDrawer() {
        val username =
            getSharedPreferenceString(mContext!!, SharedPreferenceHelper.KEY_RESPONDER, "")
        val drawable = drawResponder(mContext!!)
        setUserName(username)
        setUserImage(drawable)
        drawerOpening()
        isDrawerOpen = true
        val stateSet = intArrayOf(android.R.attr.state_checked * if (isDrawerOpen) 1 else -1)
        menuIV!!.setImageState(stateSet, true)
        containerCV!!.setCardElevation(100.0.toFloat())
        containerCV!!.setRadius(60.0.toFloat())
        //appbarTitleTV.animate().translationX(centerX+menuIV.getWidth()+menuIV.getWidth()/4+appbarTitleTV.getWidth()/2-appbarRL.getWidth()/2).start();
        System.out.println("root layout x " + rootLayout!!.getWidth())
        System.out.println("root layout y " + rootLayout!!.getHeight())
        if (rootLayout!!.getWidth() > rootLayout!!.getHeight()) {
            containerCV!!
                .animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .translationX(925F) //925
                .translationY(24F)
                .setDuration(500)
                .start()
        } else {
            containerCV!!
                .animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .translationX(rootLayout!!.getX() + rootLayout!!.getWidth() / 7 + rootLayout!!.getWidth() / 2) //925
                .translationY(24F)
                .setDuration(500)
                .start()
        }
        val handler = Handler()
        handler.postDelayed({ drawerOpened() }, 250)
    }

    //set Attributes from xml
    private fun setAttributes(attrs: TypedArray) {
        setAppbarColor(
            attrs.getColor(
                R.styleable.AppNavigationDrawer_appbarColor,
                resources.getColor(appbarColor)
            )
        )
        setAppbarTitleTextColor(
            attrs.getColor(
                R.styleable.AppNavigationDrawer_appbarTitleTextColor,
                resources.getColor(appbarTitleTextColor)
            )
        )
    }

    //To change the AppBar Title
    fun setAppbarTitleTV(name: String?) {
        appbarTitleTV!!.text = name
    }

    fun getAppbarTitleTV(): String {
        return if (appbarTitleTV!!.text != null) appbarTitleTV!!.text.toString() else ""
    }

    //To change the AppBar Title
    fun setUserName(name: String?) {
        txt_username!!.text = name
    }

    //To change the AppBar Title
    fun setUserImage(img: TextDrawable?) {
        img_username!!.setImageDrawable(img)
    }

    //Adding menu to drawer
    fun addMenuItem(menuItem: MenuItem) {
        if (menuItemList != null) {
            menuItemList!!.add(menuItem)
        }
    }

    //Getting the list of Menu Items
    fun getMenuItemList(): List<MenuItem>? {
        return menuItemList
    }

    //Setting the list of Menu Items
    fun setMenuItemList(menuItemList: MutableList<MenuItem>) {
        this.menuItemList = menuItemList
        initMenu()
    }

    /*
     *
     * Customization :)
     *
     */
    fun getAppbarColor(): Int {
        return appbarColor
    }

    private fun setAppbarColor(appbarColor: Int) {
        this.appbarColor = appbarColor
        appbarRL!!.setBackgroundColor(appbarColor)
    }

    fun getAppbarTitleTextColor(): Int {
        return appbarTitleTextColor
    }

    private fun setAppbarTitleTextColor(appbarTitleTextColor: Int) {
        this.appbarTitleTextColor = appbarTitleTextColor
        appbarTitleTV!!.setTextColor(appbarTitleTextColor)
    }

    var menuiconTintColor: Int
        get() = menuIconTintColor
        set(menuIconTintColor) {
            this.menuIconTintColor = menuIconTintColor
            menuIV!!.setColorFilter(menuIconTintColor)
        }

    //to change the typeface of appbar title
    fun setAppbarTitleTypeface(titleTypeface: Typeface?) {
        appbarTitleTV!!.typeface = titleTypeface
    }

    companion object {
        private val TAG = AppNavigationDrawer::class.java.simpleName

        //Indicates that any drawer is open. No animation is in progress.
        private const val STATE_OPEN = 0

        //Indicates that any drawer is closed. No animation is in progress.
        private const val STATE_CLOSED = 1

        //Indicates that a drawer is in the process of opening.
        private const val STATE_OPENING = 2

        //Indicates that a drawer is in the process of closing.
        private const val STATE_CLOSING = 3
    }
}
