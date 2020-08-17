package com.kodakalaris.advisor.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.os.Environment
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.custom.TextDrawable
import com.kodakalaris.advisor.database.DatabaseManager
import com.kodakalaris.advisor.model.BarcodeData
import com.kodakalaris.advisor.model.ErrorDeviceType
import com.kodakalaris.advisor.model.EventBean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceString
import com.kodakalaris.advisor.utils.datetimeutils.DateTimeUnits
import com.kodakalaris.advisor.utils.datetimeutils.DateTimeUtils.getDateDiff
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for the App
 */
@SuppressLint("SimpleDateFormat")
object Helper {

    private val TAG = Helper::class.java.simpleName
    private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    /**
     * retrict user to one tap only
     * @param view view to which need to retrict tap
     * @param delay the delay time in millis
     */
    /**
     * retrict user to one tap only
     * @param view view to which need to retrict tap
     */
    @JvmOverloads
    fun disableDoubleTap(view: View, delay: Int = 400) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, delay.toLong())
    }

    /**
     * forcefully open the keyboard
     * @param context
     */
    fun openKeyboard(context: Activity) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager != null && context.currentFocus != null) {
            inputManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    /**
     * forcefully close the keyboard
     * @param context
     */
    fun closeKeyboard(context: Activity) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager != null && context.currentFocus != null) {
            inputManager.hideSoftInputFromWindow(
                context.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    /**
     * common method to show prompt on snackbar
     * @param msg
     */
    fun showPrompt(activity: Activity, msg: String?) {
        val snackbar: Snackbar = Snackbar.make(
            activity.findViewById<View>(R.id.main_content),
            msg!!,
            Snackbar.LENGTH_LONG
        )
        snackbar.getView()
            .setBackgroundColor(ContextCompat.getColor(activity, R.color.snackBarColor))
        snackbar.show()
    }

    fun vibratePhone(activity: Activity) {
        val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400), -1)
    }

    fun tonePhone(activity: Activity?) {
        Log.e(TAG, "tonePhone()")
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(activity, uri)
        ringtone.play()
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    fun checkPlayServices(context: Activity): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(
                    context,
                    resultCode,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                ).show()
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.")
                showPrompt(context, "This device is not supported by Google Play Services.")
                context.finish()
            }
            return false
        }
        return true
    }

    /**
     * util method to check internet connection
     * @param context
     * @return boolean value i.e. connected or not
     */
    fun isConnectedToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } catch (nex: NullPointerException) {
            nex.printStackTrace()
        }
        return true
    }

    /**
     * Convert the timesInMillis to string format
     * @param timesInMillis
     * @return
     */
    fun convertMillisToTime(timesInMillis: Long): String {
        return SimpleDateFormat("hh:mm").format(Date(timesInMillis))
    }

    /**
     * parse the event title according to the event Type
     * @param context
     * @param event event object
     * @return event title
     */
    fun getEventTitle(context: Context, event: EventBean): String {
        val databaseManager = DatabaseManager(context)
        return when (event.Type) {
            Constants.EVENT_TYPE.EVENT_TYPE_HELP -> {
                if (event.KPEXId != null) event.KPEXId!! else event.KNumber!!
            }
            Constants.EVENT_TYPE.EVENT_TYPE_ASSEMBLE -> {
                event.AssembleData!!.Product!!
            }
            Constants.EVENT_TYPE.EVENT_TYPE_ERROR -> {
                var errorString = ""
                var kpxId = ""
                if (event.KPEXId != null) {
                    kpxId = event.KPEXId.toString() + ": "
                }
                if (event.ErrorData != null) {
                    val deviceType: ErrorDeviceType =
                        databaseManager.getErrorDeviceType(event.ErrorData!!.ErrorDeviceType!!)!!
                    if (deviceType != null) {
                        if (Locale.getDefault().language.equals("de", ignoreCase = true)) {
                            errorString = kpxId + deviceType.GermanName
                        } else {
                            errorString = kpxId + deviceType.GermanName
                        }
                    } else {
                        event.ErrorData!!.ErrorDeviceType!!
                    }
                }
                errorString
            }
            Constants.EVENT_TYPE.EVENT_TYPE_REBOOT -> {
                context.resources.getString(R.string.reboot_title)
            }
            else -> {
                context.resources.getString(R.string.default_event_title)
            }
        }
    }

    /**
     * parse the event description according to the event Type
     * @param context
     * @param event event object
     * @return event description
     */
    fun getEventDescription(context: Context, event: EventBean): String {
        return when (event.Type) {
            Constants.EVENT_TYPE.EVENT_TYPE_HELP -> {
                context.resources.getString(R.string.help_description)
            }
            Constants.EVENT_TYPE.EVENT_TYPE_ASSEMBLE -> {
                event.AssembleData!!.ProductIdentifier!!
            }
            Constants.EVENT_TYPE.EVENT_TYPE_ERROR -> {
                event.ErrorData!!.ErrorDeviceType!!
            }
            Constants.EVENT_TYPE.EVENT_TYPE_REBOOT -> {
                context.resources.getString(R.string.reboot_description)
            }
            else -> {
                context.resources.getString(R.string.default_event_description)
            }
        }
    }

    /**
     * draw the user image/responder image
     * @param context
     * @return textdrawable of the name
     */
    fun drawResponder(context: Context): TextDrawable {
        val username = getSharedPreferenceString(context, SharedPreferenceHelper.KEY_RESPONDER, "")
        return TextDrawable.builder()
            .beginConfig()
            ?.textColor(context.getColor(R.color.textDrawableColor))
            ?.useFont(Typeface.DEFAULT)
            ?.toUpperCase()
            ?.endConfig()
            ?.buildRound(getUserName2Char(username), context.resources.getColor(R.color.Slate))!!
    }

    /**
     * format the username to first letter from first name & first letter from the second name
     * @param username responder name
     * @return formatted name, default return 'A' letter
     */
    fun getUserName2Char(username: String?): String {
        val name = username!!.split(" ").toTypedArray()
        if (name.size == 0) {
            return "A"
        } else if (name.size == 1) {
            return name[0].toUpperCase(Locale.getDefault())[0].toString() + ""
        } else if (name.size == 2) {
            return name[0].toUpperCase(Locale.getDefault())[0].toString() + "" + name[1].toUpperCase(
                Locale.getDefault()
            )[0]
        }
        return "A"
    }

    /**
     * formatize the store address, first line storename, second line street address, third line zipcode + city
     * @param storeinfo storeinfo object fetched from barcode while scanning
     * @return formatize store address
     */
    fun formatizeAddress(storeinfo: BarcodeData.StoreBean): String {
        var address = ""
        address = address + if (storeinfo.name != null) storeinfo.name!!.trim()
            .toString() + "\n" else if (storeinfo.storeid != null) storeinfo.storeid.toString() + "\n" else ""
        address = address + if (storeinfo.addr1 != null) storeinfo.addr1!!.trim() else ""
        address = address + if (storeinfo.addr2 != null) storeinfo.addr2!!.trim()
            .toString() + "\n" else "\n"
        address = address + if (storeinfo.postalcode != null) storeinfo.postalcode!!.trim()
            .toString() + " " else " "
        address = address + if (storeinfo.city != null) storeinfo.city!!.trim() else ""
        return address
    }

    /**
     * formatize the epoch time to locale string
     * @param millis time in millis
     * @return formatize data time string
     */
    private fun formatEpochTimeToLocale(millis: Long): String? {
        val date = Date(millis * 1000)
        val format: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("Etc/UTC")
        var formatted = format.format(date)
        println(formatted)
        //format.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        formatted = format.format(date)
        return formatted
    }

    private fun formatEpochTimeToLocaleToDate(millis: Long): Date {
        val date = Date(millis * 1000)
        val format: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("Etc/UTC")
        var formatted = format.format(date)
        println(formatted)
        //format.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        formatted = format.format(date)
        return date
    }

    /*
    * Funtion convert Date into specified timezone
    * */
    private fun formatEpochDateToLocaleToDate(date: Date): Date {
        val format: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("Etc/UTC")
        var formatted = format.format(date)
        println(formatted)
        //format.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        formatted = format.format(date)
        return date
    }

    /**
     * format the datetime to app format i.e. if time is less the 1 hr from current datetime, then return x min ago, else in hh:mm T format
     * @param mContext
     * @param datetime datetime in millis
     * @return formatted datetime in app format
     */
    fun formatDateTimeToAppFormat(mContext: Context, datetime: Int): String {
        val diff =
            getDateDiff(Date(), formatEpochTimeToLocale(datetime.toLong()), DateTimeUnits.HOURS)
        return if (diff > 0) {
            val simpleDateFormat = SimpleDateFormat(
                mContext.resources.getString(R.string.datetimeformat),
                Locale.getDefault()
            )
            simpleDateFormat.format(formatEpochTimeToLocaleToDate(datetime.toLong()))
        } else {
            val diffInMin = getDateDiff(
                Date(),
                formatEpochTimeToLocale(datetime.toLong()),
                DateTimeUnits.MINUTES
            )
            String.format(mContext.resources.getString(R.string.min_ago), Math.abs(diffInMin))
        }
    }

    /**
     * format the date to app format i.e. in yyyyMMdd format
     * @param mContext
     * @return formatted date
     */
    fun onlyRtrnDate(mContext: Context, datetime: Int): String {
        val simpleDateFormat =
            SimpleDateFormat(mContext.resources.getString(R.string.dateformat), Locale.getDefault())
        return simpleDateFormat.format(formatEpochTimeToLocaleToDate(datetime.toLong()))
    }

    /*
    * get the current date in the yyyyMMdd format
    * */
    fun getCurrentDate(mContext: Context): String {
        val dateFormat =
            SimpleDateFormat(mContext.resources.getString(R.string.dateformat), Locale.getDefault())
        val today = Calendar.getInstance().time
        return dateFormat.format(formatEpochDateToLocaleToDate(today))
    }

    fun changeBaseURL(context: Context?, baseUrl: String?) {
        setSharedPreferenceString(context!!, SharedPreferenceHelper.KEY_BASE_URL, baseUrl)
    }

    fun setDefaultBaseURL(context: Context?, baseUrl: String?) {
        setSharedPreferenceString(context!!, SharedPreferenceHelper.KEY_BASE_URL, baseUrl)
    }

    fun getBaseUrl(context: Context): String? {
        return getSharedPreferenceString(
            context!!,
            SharedPreferenceHelper.KEY_BASE_URL,
            Constants.APP.API_BASE_URL
        )
    }

    val devBaseUrl: String
        get() = Constants.APP.API_DEV_BASE_URL

    val stageBaseUrl: String
        get() = Constants.APP.API_STAGE_BASE_URL

    val prodBaseUrl: String
        get() = Constants.APP.API_PROD_BASE_URL

    fun toMd5(s: String): String {
        try { // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                Integer.toHexString(0xFF and messageDigest[i].toInt())
            )
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getPrinterImage(event: EventBean): Int {
        var image = R.mipmap.icon_error_generic
        if (event.Type.equals(Constants.EVENT_TYPE.EVENT_TYPE_ASSEMBLE, ignoreCase = true)) {
            image = R.mipmap.icon_new_assembly
        } else if (event.Type.equals(Constants.EVENT_TYPE.EVENT_TYPE_HELP, ignoreCase = true)) {
            image = R.mipmap.help_event_image
        } else if (event.Type.equals(Constants.EVENT_TYPE.EVENT_TYPE_ERROR, ignoreCase = true)) {
            image = R.mipmap.icon_error_generic
        }
        if (event.ErrorData != null) {
            Log.e("ClerkApp", "Error Device Type - " + event.ErrorData!!.ErrorDeviceType!!)
            when (event.ErrorData!!.ErrorDeviceType!!) {
                Constants.Printer.Error_605 -> image = R.mipmap.error_605
                Constants.Printer.Error_6800 -> image = R.mipmap.error_6800
                Constants.Printer.Error_6850 -> image = R.mipmap.error_6850
                Constants.Printer.Error_6900 -> image = R.mipmap.error_6900
                Constants.Printer.Error_8810 -> image = R.mipmap.error_8810
                Constants.Printer.Error_D4000 -> image = R.mipmap.error_d4000
                Constants.Printer.Error_D4600 -> image = R.mipmap.error_d4600
                Constants.Printer.Error_7000 -> image = R.mipmap.kodak_photo_printer_7000
                Constants.Printer.Error_DL2100 -> image = R.mipmap.error_dl2100
                Constants.Printer.Error_DL2200 -> image = R.mipmap.error_dl2200
                Constants.Printer.Error_MugPrinter -> image = R.mipmap.error_mugprinter
                Constants.Printer.Error_PosterPrint -> image = R.mipmap.error_posterprinter
                Constants.Printer.Error_RapidPrintScanner -> image =
                    R.mipmap.error_rapidprintscanner
                Constants.Printer.Error_ReceiptPrinter -> image = R.mipmap.error_receiptprinter
                Constants.Printer.Printer_D4000 -> image = R.mipmap.d4000_duplex_photo_printer
                Constants.Printer.Printer_DL2200 -> image = R.mipmap.dl2200_printer
                Constants.Printer.Printer_Epson_SureColor_P6000 -> image =
                    R.mipmap.icon_error_generic
                Constants.Printer.Printer_8800 -> image = R.mipmap.icon_error_generic
                Constants.Printer.Printer_6900 -> image = R.mipmap.kodak_6900_photo_printer
                Constants.Printer.Printer_DL2100 -> image = R.mipmap.kodak_dl2100_printer
                Constants.Printer.Printer_605 -> image = R.mipmap.kodak_photo_printer_605
                Constants.Printer.Printer_6800 -> image = R.mipmap.kodak_photo_printer_6800
                Constants.Printer.Printer_6850 -> image = R.mipmap.kodak_photo_printer_6850
                Constants.Printer.Printer_7000 -> image = R.mipmap.kodak_photo_printer_7000
                Constants.Printer.Printer_8810 -> image = R.mipmap.kodak_photo_printer_8810
                Constants.Printer.Printer_D4600 -> image = R.mipmap.kodak_photo_printer_d4600
                Constants.Printer.Printer_Nippon_Primex -> image =
                    R.mipmap.nippon_rimex_usb_receipt_printer
                Constants.Printer.Scanner_RapidPrint -> image = R.mipmap.rapid_print_scanner
                Constants.Printer.Printer_SG400 -> image = R.mipmap.sawgrass_sg400
                Constants.Printer.Printer_Epson_Pro_78XX -> image = R.mipmap.epson_stylus_pro_78xx
            }
        }
        return image
    }

    fun exportDatabse() {
        try {
            val sd = Environment.getExternalStorageDirectory()
            val data = Environment.getDataDirectory()
            if (sd.canWrite()) {
                val currentDBPath = "//data//com.kodakalaris.advisor//databases//kodakmoment_db"
                val backupDBPath = "kodakmoment.db"
                val currentDB = File(data, currentDBPath)
                val backupDB = File(sd, backupDBPath)
                if (currentDB.exists()) {
                    Log.e("NOVA", "Current DB Exist")
                    val src = FileInputStream(currentDB).channel
                    val dst = FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                }
            }
        } catch (e: Exception) {
        }
    }

    //Method to write logs into files
    fun generateLogsOnStorage(sBody: String?) {
        val flag = getSharedPreferenceBoolean(
            AppController.appContext!!,
            SharedPreferenceHelper.KEY_ACTIVATE_LOG,
            false
        )
        if (flag) {
            val formatter = SimpleDateFormat("yyyy_MM_dd")
            val now = Date()
            val fileName = formatter.format(now) + ".txt" //like 2016_01_12.txt
            try {
                val root = File(Environment.getExternalStorageDirectory(), "AdvisorComm")
                if (!root.exists()) {
                    root.mkdirs()
                }
                val gpxfile = File(root, fileName)
                if (!gpxfile.exists()) {
                    gpxfile.createNewFile()
                }
                val writer = FileWriter(gpxfile, true)
                writer.append(now.toString())
                writer.append("\n")
                writer.append(sBody)
                writer.append("\n\n")
                writer.flush()
                writer.close()
                //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
