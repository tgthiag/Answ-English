package AnsweringAPP.funcoes

import AnsweringAPP.dados.FIRSTACESS
import AnsweringAPP.dados.TABLE_NAME
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class Instructions(var ctx: Context) {
    fun firstAcess(db : SQLiteDatabase){
        //Verificando se é o primeiro acesso, se for, mostrar Dialogo Tutor
        var query = "SELECT * FROM $TABLE_NAME;"
        var cursorr = db.rawQuery(query,null)
        cursorr.moveToFirst()
        var isFirstAcess = cursorr.getString(4).toInt()

        if (isFirstAcess < 1){
            var cv = ContentValues()
            var updateAcess = isFirstAcess + 1
            cv.put(FIRSTACESS,updateAcess)
            DialogShow().AlertDialog(ctx)
            db.update(TABLE_NAME,cv,null,null)
        }
    }
    fun callInstructions(){
        DialogShow().AlertDialog(ctx)
    }

}