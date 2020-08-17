package com.kodakalaris.advisor.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {

    private lateinit var NewsURLString: String
    private lateinit var loader: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewsURLString = (if (Locale.getDefault().language.equals("de", ignoreCase = true)) {
            getSharedPreferenceString(
                getActivity()!!,
                SharedPreferenceHelper.KEY_GERMAN_NEWS_URL,
                Constants.APP.GERMAN_NEWS_URL
            )
        } else {
            getSharedPreferenceString(
                getActivity()!!,
                SharedPreferenceHelper.KEY_ENGLISH_NEWS_URL,
                Constants.APP.ENGLISH_NEWS_URL
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
        val newsView = inflater.inflate(R.layout.fragment_news, container, false)
        loader = newsView.findViewById(R.id.loader) as ProgressBar
        val webView = newsView.findViewById(R.id.webview) as WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
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
                Toast.makeText(getActivity(), "Oh no! " + error.description, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        if (!NewsURLString.isEmpty()) {
            webView.loadUrl(NewsURLString)
        } else {
            Toast.makeText(getActivity(), "News Url is not configured.", Toast.LENGTH_SHORT).show()
        }
        return newsView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    inner class AppWebViewClients(private val progressBar: ProgressBar) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            // TODO Auto-generated method stub
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }

        init {
            progressBar.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        (getActivity() as HomeActivity).setWindowTitle(getResources().getString(R.string.lb_menu_news))
    }

    companion object {
        private var fragment: NewsFragment? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment NewsFragment.
         */
        @Synchronized
        fun newInstance(): NewsFragment? {
            if (fragment == null) {
                fragment = NewsFragment()
            }
            return fragment
        }
    }
}