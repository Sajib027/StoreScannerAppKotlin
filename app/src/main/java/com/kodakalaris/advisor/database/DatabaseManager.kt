package com.kodakalaris.advisor.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.kodakalaris.advisor.model.ErrorDeviceType

open class DatabaseManager(private val context: Context) {
    private val databaseHelper: DatabaseHelper
    private lateinit var sqLiteDatabase: SQLiteDatabase
    fun insertErrorDeviceType(deviceType: ErrorDeviceType) {
        val values = ContentValues()
        values.put(
            DatabaseHelper.DatabaseConstant.COL_ERROR_DEVICE_TYPE,
            deviceType.ErrorDeviceType
        )
        values.put(DatabaseHelper.DatabaseConstant.COL_GERMAN_NAME, deviceType.GermanName)
        values.put(DatabaseHelper.DatabaseConstant.COL_ENGLISH_NAME, deviceType.EnglishName)
        sqLiteDatabase = databaseHelper.writableDatabase
        sqLiteDatabase.insert(DatabaseHelper.DatabaseConstant.MAPPING_TABLE, null, values)
    }

    fun getErrorDeviceType(ErrorDeviceType: String): ErrorDeviceType {
        var deviceType: ErrorDeviceType = ErrorDeviceType()
        sqLiteDatabase = databaseHelper.readableDatabase
        val cursor = sqLiteDatabase.rawQuery("select * from printer_mapping", null)
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                if (ErrorDeviceType.equals(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseConstant.COL_ERROR_DEVICE_TYPE)),
                        ignoreCase = true
                    )
                ) {
                    deviceType = ErrorDeviceType()
                    deviceType.ErrorDeviceType =
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseConstant.COL_ERROR_DEVICE_TYPE))
                    deviceType.GermanName =
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseConstant.COL_GERMAN_NAME))
                    deviceType.EnglishName =
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseConstant.COL_ENGLISH_NAME))
                    break
                }
            }
        }
        return deviceType
    }

    init {
        databaseHelper = DatabaseHelper()
    }
}
