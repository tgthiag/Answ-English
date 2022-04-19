package AnsweringAPP.dados

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val TABLE_NAME = "answering"
val ID = "id"
val NAME = "name"
val NAME2 = "name2"
val COINS = "coins"
val FIRSTACESS = "first"
val TM_BASIC = "tm_basic"
val TM_INTERM = "tm_interm"
val TM_ADVANC = "tm_advanc"
val TM_BEG_INTERM = "tm_beg_interm"
val TM_ALLQUESTIONS = "tm_allquestions"
val DIA_ATUAL = "current_day"
val DIAS_USO = "days_qtd"


class localSqlDatabase(ctx : Context) : SQLiteOpenHelper(ctx, TABLE_NAME,null,5) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE =
            """CREATE TABLE $TABLE_NAME($ID INT PRIMARY KEY, $NAME VARCHAR, $COINS INT,$NAME2 VARCHAR, $FIRSTACESS VARCHAR,$TM_BASIC INT,$TM_INTERM INT,$TM_ADVANC INT,$TM_BEG_INTERM INT, $TM_ALLQUESTIONS INT, $DIA_ATUAL VARCHAR, $DIAS_USO INT);"""
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
        cv.put(COINS, 5)
        cv.put(NAME2, "tg")
        cv.put(FIRSTACESS, 0)
        cv.put(TM_BASIC, 9)
        cv.put(TM_INTERM, 9)
        cv.put(TM_ADVANC, 9)
        cv.put(TM_BEG_INTERM, 9)
        cv.put(TM_ALLQUESTIONS, 9)
        cv.put(DIAS_USO, 0)
        db.insert(TABLE_NAME, null, cv)
        db.close()
    }
}