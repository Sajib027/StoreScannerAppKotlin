package com.kodakalaris.advisor.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.HomeActivity
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import java.util.*

/**
 * third fragment from the navigation drawer/side menu
 * A simple [Fragment] subclass.
 * Use the [HelpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HelpFragment : Fragment() {

    private lateinit var HelpURLString: String
    private lateinit var loader: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        HelpURLString = (if (Locale.getDefault().language.equals("de", ignoreCase = true)) {
            getSharedPreferenceString(
                getActivity()!!,
                SharedPreferenceHelper.KEY_GERMAN_HELP_URL,
                Constants.APP.GERMAN_HELP_URL
            )
        } else {
            getSharedPreferenceString(
                getActivity()!!,
                SharedPreferenceHelper.KEY_ENGLISH_HELP_URL,
                Constants.APP.ENGLISH_HELP_URL
            )
        })!!
        if (!isConnectedToInternet(getActivity()!!)) {
            showPrompt(
                getActivity()!!,
                getResources().getString(R.string.error_no_internet_connection)
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val newsView = inflater.inflate(R.layout.fragment_help, container, false)
        loader = newsView.findViewById(R.id.loader) as ProgressBar
        val webView = newsView.findViewById(R.id.webview) as WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = Browser_home()
        webView.setWebChromeClient(MyChrome())

        if (!HelpURLString.isEmpty()) {
            webView.loadUrl(HelpURLString)
        } else {
            Toast.makeText(getActivity(), "Help Url is not configured.", Toast.LENGTH_SHORT).show()
        }
        return newsView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        (getActivity() as HomeActivity).setWindowTitle(getResources().getString(R.string.lb_menu_help))
        //        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onPause() {
        super.onPause()
        //        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    internal inner class Browser_home : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            loader.visibility = View.VISIBLE
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            loader.visibility = View.GONE
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            loader.visibility = View.GONE
            Toast.makeText(getActivity(), "Oh no! " + error.description, Toast.LENGTH_SHORT).show()
        }
    }

    private inner class MyChrome internal constructor() : WebChromeClient() {
        private var mCustomView: View? = null
        private var mCustomViewCallback: CustomViewCallback? = null
        protected var mFullscreenContainer: FrameLayout? = null
        private var mOriginalSystemUiVisibility = 0
        override fun getDefaultVideoPoster(): Bitmap? {
            return if (mCustomView == null) {
                null
            } else BitmapFactory.decodeResource(
                getActivity()!!.applicationContext.getResources(), 2130837573
            )
        }

        override fun onHideCustomView() {
            (getActivity()!!.window.getDecorView() as FrameLayout).removeView(mCustomView)
            mCustomView = null
            getActivity()!!.window.getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility)
            getActivity()!!.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            mCustomViewCallback!!.onCustomViewHidden()
            mCustomViewCallback = null
        }

        @SuppressLint("SourceLockedOrientationActivity")
        override fun onShowCustomView(
            paramView: View,
            paramCustomViewCallback: CustomViewCallback
        ) {
            if (mCustomView != null) {
                onHideCustomView()
                return
            }
            getActivity()!!.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            mCustomView = paramView
            mOriginalSystemUiVisibility =
                getActivity()!!.window.getDecorView().getSystemUiVisibility()
            mCustomViewCallback = paramCustomViewCallback
            (getActivity()!!.window.getDecorView() as FrameLayout).addView(
                mCustomView,
                FrameLayout.LayoutParams(-1, -1)
            )
            getActivity()!!.window.getDecorView().setSystemUiVisibility(3846)
        }
    }

    companion object {
        private var fragment: HelpFragment? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment HelpFragment.
         */
        @Synchronized
        fun newInstance(): HelpFragment? {
            if (fragment == null) {
                fragment = HelpFragment()
            }
            return fragment
        }
    }
}
