package AnsweringAPP.funcoes

import AnsweringAPP.dados.FIRSTACESS
import AnsweringAPP.dados.TABLE_NAME
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.util.*


class DialogShow() {

    fun AlertDialog(ctx : Context) {
        Translate(ctx).traduzir_pergunta.translate(
            "You've started with some coins!\n\nYou will earn 2 coin every day that you open the app.\n\nCoins are used for video recording and to automatize the questions.\n\n" +
                    "You can earn 3 coins by watching a video anytime.\n\nUse the automatize button to answer questions while doing your everyday things.\n\n" +
                    "Start recording videos to share on your WhatsApp, Facebook and Instagram posts, as soon as you click the camera button, it will start, tap the blue arrows for next question.\n\nEvery level has different questions, try it!"
        ).addOnSuccessListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)

                var formatado = it.replace("! ", "! \n\n").replace(". ", ". \n\n")
                builder!!.setMessage(formatado)



            builder.apply {
                setPositiveButton("Ok") { dialog, id ->
                    val selectedId = id
                }
            }
            val dialog: AlertDialog? = builder.create()

            dialog!!.show()
        }
    }
    fun DialogCustom(ctx : Context,str: String) {
        Translate(ctx).traduzir_pergunta.translate(
            str
        ).addOnSuccessListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)

            var formatado = it.replace("! ", "! \n\n").replace(". ", ". \n\n")
            builder!!.setMessage(formatado)



            builder.apply {
                setPositiveButton("Ok") { dialog, id ->
                    val selectedId = id
                }
            }
            val dialog: AlertDialog? = builder.create()

            dialog!!.show()
        }
    }
    fun DialogReview(ctx : Context,db : SQLiteDatabase,str: String) {
        Translate(ctx).traduzir_pergunta.translate(
            str
        ).addOnSuccessListener {
            val listItems = arrayOf("Yes, I love it!", "No I didn't like it.")
            val mBuilder = AlertDialog.Builder(ctx)
            mBuilder.setTitle(it)
            mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
//                Translate(ctx).toastTrad("Thank you so much! We are happy to know!")
                if (listItems[i] == "Yes, I love it!"){
                    Translate(ctx).toastTrad("Thank you so much! We are happy to know!")
                    inAppReview().call(ctx)
                    val selectQuery = "SELECT * FROM $TABLE_NAME;"
                    var cursoress = db.rawQuery(selectQuery, null)
                    cursoress.moveToFirst()
                    var usedTheApp = cursoress.getString(4).toInt()
                    var moedas = cursoress.getString(2).toInt()
                    var cv = ContentValues()
                    var updateUseReview = usedTheApp + 1
                    cv.put(FIRSTACESS,updateUseReview)
                    db.update(TABLE_NAME,cv,null,null)
                    dialogInterface.cancel()
                }else{
                    Translate(ctx).toastTrad("Sorry, we are working to give you a better experience")
                    val selectQuery = "SELECT * FROM $TABLE_NAME;"
                    var cursoress = db.rawQuery(selectQuery, null)
                    cursoress.moveToFirst()
                    var usedTheApp = cursoress.getString(4).toInt()
                    var moedas = cursoress.getString(2).toInt()
                    var cv = ContentValues()
                    var updateUseReview = usedTheApp + 1
                    cv.put(FIRSTACESS,updateUseReview)
                    db.update(TABLE_NAME,cv,null,null)
                    dialogInterface.cancel()
                }
                dialogInterface.dismiss()
            }
            // Set the neutral/cancel button click listener
            mBuilder.setNeutralButton("Close") { dialog, which ->
                // Do something when click the neutral button
                dialog.cancel()
            }

            val mDialog = mBuilder.create()
            mDialog.show()
        }
    }
    fun firstDialog(ctx : Context) {
        var message = "First time here?\n\nWait some seconds.\n\nWe are configuring your translations.\n\n\n\n\n\nTips:\nThe blue arrows bring the next question\nWe have four levels, every level has different questions.\nStart with beginner, these are fast questions, it will be a great video."
        val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)

        builder.setMessage(message)

        builder.apply {
            setPositiveButton("Ok") { dialog, id ->
                val selectedId = id
            }
        }
        val dialog: AlertDialog? = builder.create()

        dialog!!.show()

    }
}

