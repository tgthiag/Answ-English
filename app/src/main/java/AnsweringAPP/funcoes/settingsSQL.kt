package AnsweringAPP.funcoes

import android.database.sqlite.SQLiteDatabase
import android.widget.EditText
import AnsweringAPP.dados.*

class settingsSQL{

    fun recoverTimer(db : SQLiteDatabase, edTx1: EditText, edTx2: EditText, edTx3: EditText, edTx4: EditText, edTx5: EditText){
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        val cursor = db.rawQuery(selectQuery, null)
        fun getColumn(coluna: String):Int{ return cursor.getInt(cursor.getColumnIndexOrThrow(coluna)) }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                edTx1.setText(getColumn(TM_BASIC).toString())
                edTx2.setText(getColumn(TM_INTERM).toString())
                edTx3.setText(getColumn(TM_ADVANC).toString())
                edTx4.setText(getColumn(TM_BEG_INTERM).toString())
                edTx5.setText(getColumn(TM_ALLQUESTIONS).toString())
            }

        }

        cursor.close()
    }
}
