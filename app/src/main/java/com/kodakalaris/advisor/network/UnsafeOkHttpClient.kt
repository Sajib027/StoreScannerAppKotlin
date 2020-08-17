package com.kodakalaris.advisor.network

import android.util.Log
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * UnsafeOkHttpClient offers us an OkHttp client that completely ignores any SSL certificate issues
 */
object UnsafeOkHttpClient {
    private val TAG = UnsafeOkHttpClient::class.java.simpleName

    // Create a trust manager that does not validate certificate chains
    val unsafeOkHttpClient:
    // Install the all-trusting trust manager
    // Create an ssl socket factory with our all-trusting manager
            OkHttpClient.Builder
        get() = try { // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                        Log.d(TAG, "checkClientTrusted")
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                        Log.d(TAG, "checkServerTrusted")
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        Log.d(TAG, "X509Certificate")
                        return arrayOf()
                    }
                }
            )
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(20, TimeUnit.SECONDS)
            builder.writeTimeout(20, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}