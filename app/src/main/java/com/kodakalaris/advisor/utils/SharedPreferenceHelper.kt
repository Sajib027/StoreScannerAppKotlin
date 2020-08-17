package com.kodakalaris.advisor.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Shared Preference Helper class to set/get preferences
 */
object SharedPreferenceHelper {

    private const val PREF_FILE = "com.kodakalaris.advisor.SHARED_PREF"
    const val KEY_DEVICEID = "deviceid"
    const val KEY_FCMTOKEN = "fcmtoken"
    const val KEY_REGISTRATIONID = "registrationid"
    const val KEY_REGISTRATIONDONE = "registrationdone"
    const val KEY_ALREADYLOGGEDIN = "alreadyloggedin"
    const val KEY_RESPONDER = "username"
    const val KEY_AUTOCOMPLETE_TASK = "autocompletetask"
    const val KEY_STOREID = "storeid"
    const val KEY_STOREADDRESS = "storeaddress"
    const val KEY_KPK_HELP = "kpk_help"
    const val KEY_CONFIGURED_STOREID = "configured_storeid"
    const val KEY_STORE_KNUMBER = "store_knumber"
    const val KEY_ENGLISH_HELP_URL = "help_url"
    const val KEY_ENGLISH_NEWS_URL = "news_url"
    const val KEY_GERMAN_HELP_URL = "german_help_url"
    const val KEY_GERMAN_NEWS_URL = "german_news_url"
    const val KEY_ACTIVATE_LOG = "activate_log"
    const val KEY_DONOTDISTURB = "donotdisturb"
    const val KEY_VIBRATION = "vibration"
    const val KEY_TONE = "tone"
    const val KEY_NOTIFICATION = "notification"
    const val KEY_MESSAGECOUNT = "messagecount"
    const val KEY_BASE_URL = "base_url"
    const val KEY_MAPPING_ADDED = "isMappingAdded"

    /**
     * Set a string shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setSharedPreferenceString(context: Context, key: String?, value: String?) {
        val settings = context.getSharedPreferences(PREF_FILE, 0)
        val editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Set a integer shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setSharedPreferenceInt(context: Context, key: String?, value: Int) {
        val settings = context.getSharedPreferences(PREF_FILE, 0)
        val editor = settings.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * Set a Boolean shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    fun setSharedPreferenceBoolean(context: Context, key: String?, value: Boolean) {
        val settings = context.getSharedPreferences(PREF_FILE, 0)
        val editor = settings.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * Get a string shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getSharedPreferenceString(context: Context, key: String?, defValue: String?): String? {
        val settings = context.getSharedPreferences(PREF_FILE, 0)
        return settings.getString(key, defValue)
    }

    /**
     * Get a integer shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getSharedPreferenceInt(context: Context, key: String?, defValue: Int): Int {
        val settings = context.getSharedPreferences(PREF_FILE, 0)
        return settings.getInt(key, defValue)
    }

    /**
     * Get a boolean shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    fun getSharedPreferenceBoolean(context: Context, key: String?, defValue: Boolean): Boolean {
        val settings = context.getSharedPreferences(PREF_FILE, 0)
        return settings.getBoolean(key, defValue)
    }

    fun clearSharedPreference(context: Context) {
        val settings: SharedPreferences
        val editor: SharedPreferences.Editor
        settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        editor = settings.edit()
        editor.clear()
        editor.apply()
    }
}
