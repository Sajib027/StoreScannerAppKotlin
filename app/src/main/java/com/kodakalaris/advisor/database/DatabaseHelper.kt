package com.kodakalaris.advisor.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kodakalaris.advisor.application.AppController

class DatabaseHelper : SQLiteOpenHelper(AppController.appContext, DB_NAME, null, DB_VERSION) {

    object DatabaseConstant {
        var MAPPING_TABLE = "printer_mapping"
        var COL_ID = "id"
        var COL_ERROR_DEVICE_TYPE = "errorDeviceType"
        var COL_GERMAN_NAME = "germanName"
        var COL_ENGLISH_NAME = "englishName"
    }

    override fun onCreate(db: SQLiteDatabase) {
        if (db != null) {
            val create_mapping_table =
                "CREATE TABLE " + DatabaseConstant.MAPPING_TABLE + " (" +
                        DatabaseConstant.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseConstant.COL_ERROR_DEVICE_TYPE + " TEXT, " +
                        DatabaseConstant.COL_GERMAN_NAME + " TEXT, " +
                        DatabaseConstant.COL_ENGLISH_NAME + " TEXT);"
            try {
                db.execSQL(create_mapping_table)
            } catch (e: Exception) {
                println("Exception e $e")
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        private const val DB_NAME = "kodakmoment_db"
        private const val DB_VERSION = 1
    }
}

