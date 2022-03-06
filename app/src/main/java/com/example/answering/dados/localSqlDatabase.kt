package com.example.answering.dados

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val TABLE_NAME = "answering"
val ID = "id"
val NAME = "name"
val COINS = "coins"
val INTERSTITIAL = "interstitial"

class localSqlDatabase(ctx : Context) : SQLiteOpenHelper(ctx, TABLE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE =
            """CREATE TABLE $TABLE_NAME($ID INT PRIMARY KEY, $NAME VARCHAR, $COINS INT, $INTERSTITIAL VARCHAR);"""
        db?.execSQL(CREATE_TABLE)
        //initializeRow()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME;"
        db?.execSQL(DROP_TABLE)
        onCreate(db)
        //initializeRow()
    }
    fun initializeRow() {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(ID, 1)
        cv.put(NAME, "tg")
        cv.put(COINS, 2)
        cv.put(INTERSTITIAL, 0)
        db.insert(TABLE_NAME, null, cv)
    }
}