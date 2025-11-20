package AnsweringAPP.funcoes

import AnsweringAPP.dados.FIRSTACESS
import AnsweringAPP.dados.TABLE_NAME
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.answering.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class DialogShow() {

    fun AlertDialog(ctx : Context) {
        Translate(ctx).traduzir_pergunta.translate(
            "You've started with some coins!\n\nYou will earn 2 coin every day that you open the app.\n\nCoins are used for video recording and to automatize the questions.\n\n" +
                    "You can earn 3 coins by watching a video anytime.\n\nUse the automatize button to answer questions while doing your everyday things.\n\n" +
                    "Start recording videos to share on your WhatsApp, Facebook and Instagram posts, as soon as you click the camera button, it will start, tap the blue arrows for next question.\n\nEvery level has different questions, try it!"
        ).addOnSuccessListener {
            val formatted = formatParagraphs(it)
            showInfoDialog(
                ctx = ctx,
                title = ctx.getString(R.string.dialog_coins_title),
                body = formatted
            )
        }
    }
    fun DialogCustom(ctx : Context,str: String) {
        Translate(ctx).traduzir_pergunta.translate(
            str
        ).addOnSuccessListener {
            val formatted = formatParagraphs(it)
            showInfoDialog(
                ctx = ctx,
                title = null,
                body = formatted
            )
        }
    }
    fun DialogReview(ctx : Context,db : SQLiteDatabase,str: String) {
        Translate(ctx).traduzir_pergunta.translate(
            str
        ).addOnSuccessListener {
            val listItems = arrayOf("Yes, I love it!", "No I didn't like it.")
            val mBuilder = MaterialAlertDialogBuilder(ctx, R.style.AppDialogTheme)
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
        val subtitle = ctx.getString(R.string.dialog_first_subtitle)
        val tips = ctx.getString(R.string.dialog_first_tips)
        val body = "$subtitle\n\n$tips"
        showInfoDialog(
            ctx = ctx,
            title = ctx.getString(R.string.dialog_first_title),
            body = body
        )
    }

    private fun showInfoDialog(ctx: Context, title: CharSequence?, body: CharSequence) {
        val dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_info, null, false)
        val titleView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val messageView = dialogView.findViewById<TextView>(R.id.dialogMessage)

        if (title.isNullOrBlank()) {
            titleView.visibility = View.GONE
        } else {
            titleView.text = title
            titleView.visibility = View.VISIBLE
        }
        messageView.text = body

        val dialog = MaterialAlertDialogBuilder(ctx, R.style.AppDialogTheme)
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_ok) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(ctx, R.color.level_intermediate))
        }
        dialog.show()
    }

    private fun formatParagraphs(text: String): String {
        return text.replace("! ", "! \n\n").replace(". ", ". \n\n")
    }
}

