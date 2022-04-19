package AnsweringAPP.funcoes

import AnsweringAPP.dados.COINS
import AnsweringAPP.dados.DIAS_USO
import AnsweringAPP.dados.DIA_ATUAL
import AnsweringAPP.dados.TABLE_NAME
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class DailyCoins {
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())}

    fun UpdateDayAndCoins(db : SQLiteDatabase, ctx : Context) {
        var database = db
        var cv = ContentValues()
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        val cursor = db.rawQuery(selectQuery,null)
        cursor.moveToFirst()
        var diaAtual = getCurrentDate()
        var result = cursor.getString(10)
        var coinsResult = cursor.getString(2).toInt()
        var daysUsing = cursor.getString(11).toInt()
        var updateCoin = coinsResult + 1
        var updateDays = daysUsing + 1
        if (result != diaAtual){
            var updateCoin = coinsResult + 2
            cv.put(DIA_ATUAL,diaAtual)
            cv.put(COINS,updateCoin)
            cv.put(DIAS_USO,updateDays)
            db.update(TABLE_NAME,cv, null, null)
        }
        cursor.close()

    }
}