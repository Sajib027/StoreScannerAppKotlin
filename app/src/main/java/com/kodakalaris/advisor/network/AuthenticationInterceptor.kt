package com.kodakalaris.advisor.network

import android.util.Log
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset

/**
 * Authentication Interceptor to add basic auth on api's header
 */
class AuthenticationInterceptor(private val authToken: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder().header("Authorization", authToken)
        val request = builder.build()
        val isLogEnable = getSharedPreferenceBoolean(
            AppController.appContext!!,
            SharedPreferenceHelper.KEY_ACTIVATE_LOG,
            Constants.APP.DEFAULT_LOG_FLAG
        )
        Log.e("Kodak", "App Request : " + request.url())
        if (isLogEnable) {
            generateLogsOnStorage(request.url().toString())
        }
        val response = chain.proceed(request)
        val responseBody = response.body()
        val source = responseBody!!.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source.buffer()
        Log.d("Kodak", "App Response " + buffer.clone().readString(UTF8).toString())
        if (isLogEnable) {
            generateLogsOnStorage("App Response")
            generateLogsOnStorage(buffer.clone().readString(UTF8).toString())
        }
        return response
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }

}