package com.kodakalaris.advisor.application

import android.annotation.TargetApi
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.kodakalaris.advisor.database.DatabaseHelper
import com.kodakalaris.advisor.database.DatabaseManager
import com.kodakalaris.advisor.model.ErrorDeviceType
import com.kodakalaris.advisor.network.ApiService
import com.kodakalaris.advisor.network.AuthenticationInterceptor
import com.kodakalaris.advisor.network.UnsafeOkHttpClient
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.getBaseUrl
import com.kodakalaris.advisor.utils.Helper.setDefaultBaseURL
import com.kodakalaris.advisor.utils.NotificationHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceBoolean
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * AppController is the Application class of the App process
 */
class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        val defaultBaseUrl =
            getSharedPreferenceString(appContext, SharedPreferenceHelper.KEY_BASE_URL, null)
        setDefaultBaseURL(appContext, defaultBaseUrl ?: Constants.APP.API_PROD_BASE_URL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val databaseHelper = DatabaseHelper()
        //Method to add all errorDeviceType
        val isAdded =
            getSharedPreferenceBoolean(appContext, SharedPreferenceHelper.KEY_MAPPING_ADDED, false)
        if (!isAdded) {
            addErrorDeviceData()
            setSharedPreferenceBoolean(appContext, SharedPreferenceHelper.KEY_MAPPING_ADDED, true)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val mNotificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationHelper = NotificationHelper(appContext)
        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID) == null) {
            notificationHelper.createDonotdisturbChannel()
        }
        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(Constants.NOTIFICATION.ONLYVIBRATION_CHANNEL_ID) == null) {
            notificationHelper.createOnlyvibrationChannel()
        }
        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(Constants.NOTIFICATION.ONLYVOLUME_CHANNEL_ID) == null) {
            notificationHelper.createOnlyvolumeChannel()
        }
        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(Constants.NOTIFICATION.NORMAL_CHANNEL_ID) == null) {
            notificationHelper.createNormalChannel()
        }
    }

    fun addErrorDeviceData() {
        val databaseManager = DatabaseManager(appContext)
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "D4000 Duplex Photo Printer",
                "Drucker D4000",
                "Printer D4000"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "DL2200 Printer",
                "Drucker DL2200",
                "Printer DL2200"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Epson Stylus Pro 78XX",
                "Posterdrucker",
                "Poster printer"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Epson SureColor P6000",
                "Posterdrucker",
                "Poster printer"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak 6900 Photo Printer",
                "Drucker 6900",
                "Printer 6900"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak DL2100 Printer",
                "Drucker DL2100",
                "Printer DL2100"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak Photo Printer 605",
                "Drucker 605",
                "Printer 605"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak Photo Printer 6800",
                "Drucker 6800",
                "Printer 6800"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak Photo Printer 6850",
                "Drucker 6850",
                "Printer 6850"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak Photo Printer 7000",
                "Drucker 7000",
                "Printer 7000"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak Photo Printer 8800",
                "Drucker 8800",
                "Printer 8800"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak Photo Printer 8810",
                "Drucker 8810",
                "Printer 8810"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Kodak Photo Printer D4600",
                "Drucker D4600",
                "Printer D4600"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Nippon Primex USB Receipt Printer",
                "Bondrucker",
                "Receipt printer"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Rapid Print Scanner",
                "Rapid Print Scanner",
                "Rapid Print Scanner"
            )
        )
        databaseManager.insertErrorDeviceType(
            ErrorDeviceType(
                "Sawgrass SG400",
                "Tassendrucker",
                "Mug printer"
            )
        )
    }

    companion object {
        private val TAG = AppController::class.java.simpleName
        private lateinit var httpClient: OkHttpClient.Builder
        private lateinit var retrofit: Retrofit
        var isAppIsInForeground = false
        lateinit var appContext: Context
            private set
        /**
         * adding basic auth for api authentication
         */
        /**
         * Using Retrofit 2 for the network interaction
         */
        /**
         * adding okhttp logger for api's log
         */
        @get:Synchronized
        @set:Synchronized
        var service: ApiService? = null
            get() {
                if (field == null) {
                    httpClient = UnsafeOkHttpClient.unsafeOkHttpClient
                    /**
                     * adding okhttp logger for api's log
                     */
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    if (!httpClient.interceptors().contains(logging)) {
                        httpClient.addInterceptor(logging)
                    }
                    /**
                     * adding basic auth for api authentication
                     */
                    val interceptor = AuthenticationInterceptor(
                        Credentials.basic(
                            Constants.APP.APPID,
                            Constants.APP.APPSECRET
                        )
                    )
                    if (!httpClient.interceptors().contains(interceptor)) {
                        httpClient.addInterceptor(interceptor)
                    }
                    /**
                     * Using Retrofit 2 for the network interaction
                     */
                    retrofit = Retrofit.Builder()
                        .baseUrl(getBaseUrl(appContext)!!)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build()
                    field = retrofit.create<ApiService>(ApiService::class.java)
                }
                return field
            }

        @Synchronized
        fun getRetrofit(): Retrofit {
            if (retrofit == null) {
                service
            }
            return retrofit
        }
    }
}