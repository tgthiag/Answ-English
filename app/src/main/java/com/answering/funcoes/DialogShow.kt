package com.answering.funcoes

import com.answering.dados.FIRSTACESS
import com.answering.dados.TABLE_NAME
import com.answering.funcoes.inAppReview
import com.answering.funcoes.Translate
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.answering.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogShow {

    private fun idString(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "string", ctx.packageName)
    private fun idLayout(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "layout", ctx.packageName)
    private fun idStyle(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "style", ctx.packageName)
    private fun idColor(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "color", ctx.packageName)
    private fun idView(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "id", ctx.packageName)

    fun AlertDialog(ctx: Context) {
        val source = "You've started with some coins!\n\nYou will earn 2 coin every day that you open the app.\n\nCoins are used for video recording and to automatize the questions.\n\n" +
                "You can earn 3 coins by watching a video anytime.\n\nUse the automatize button to answer questions while doing your everyday things.\n\n" +
                "Start recording videos to share on your WhatsApp, Facebook and Instagram posts, as soon as you click the camera button, it will start, tap the blue arrows for next question.\n\nEvery level has different questions, try it!"
        Translate(ctx).traduzir_pergunta.translate(source)
            .addOnSuccessListener { translated ->
                showInfoDialog(
                    ctx,
                    ctx.getString(idString(ctx, "dialog_coins_title")),
                    formatParagraphs(translated)
                )
            }
            .addOnFailureListener {
                showInfoDialog(
                    ctx,
                    ctx.getString(idString(ctx, "dialog_coins_title")),
                    formatParagraphs(source)
                )
            }
    }

    fun DialogCustom(ctx: Context, bodyOriginal: String) {
        Translate(ctx).traduzir_pergunta.translate(bodyOriginal)
            .addOnSuccessListener { translated ->
                showInfoDialog(ctx, null, formatParagraphs(translated))
            }
            .addOnFailureListener {
                showInfoDialog(ctx, null, formatParagraphs(bodyOriginal))
            }
    }

    fun DialogReview(ctx: Context, db: SQLiteDatabase, titleOriginal: String) {
        Translate(ctx).traduzir_pergunta.translate(titleOriginal)
            .addOnSuccessListener { titleTranslated ->
                buildReviewDialog(ctx, db, titleTranslated)
            }
            .addOnFailureListener {
                buildReviewDialog(ctx, db, titleOriginal)
            }
    }

    private fun buildReviewDialog(ctx: Context, db: SQLiteDatabase, title: String) {
        val listItems = arrayOf("Yes, I love it!", "No I didn't like it.")
        val builder = MaterialAlertDialogBuilder(ctx, idStyle(ctx, "AppDialogTheme")).setTitle(title)
            .setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
                val positive = listItems[i] == "Yes, I love it!"
                if (positive) {
                    Translate(ctx).toastTrad("Thank you so much! We are happy to know!")
                    inAppReview().call(ctx)
                } else {
                    Translate(ctx).toastTrad("Sorry, we are working to give you a better experience")
                }
                val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME;", null)
                cursor.moveToFirst()
                val usedTheApp = cursor.getString(4).toInt()
                val cv = ContentValues().apply { put(FIRSTACESS, usedTheApp + 1) }
                db.update(TABLE_NAME, cv, null, null)
                cursor.close()
                dialogInterface.dismiss()
            }
            .setNeutralButton(ctx.getString(idString(ctx, "dialog_ok"))) { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    fun firstDialog(ctx: Context, db: SQLiteDatabase) {
        val subtitle = ctx.getString(idString(ctx, "dialog_first_subtitle"))
        val tips = ctx.getString(idString(ctx, "dialog_first_tips"))
        val body = "$subtitle\n\n$tips"
        showInfoDialog(
            ctx,
            ctx.getString(idString(ctx, "dialog_first_title")),
            body
        )
        val cv = ContentValues().apply { put(FIRSTACESS, 1) }
        db.update(TABLE_NAME, cv, null, null)
    }

    private fun showInfoDialog(ctx: Context, title: CharSequence?, body: CharSequence) {
        val dialogView = LayoutInflater.from(ctx).inflate(idLayout(ctx, "dialog_info"), null, false)
        val titleView = dialogView.findViewById<TextView>(idView(ctx, "dialogTitle"))
        val messageView = dialogView.findViewById<TextView>(idView(ctx, "dialogMessage"))

        if (title.isNullOrBlank()) {
            titleView.visibility = View.GONE
        } else {
            titleView.text = title
            titleView.visibility = View.VISIBLE
        }
        messageView.text = body

        val dialog = AlertDialog.Builder(ctx)
            .setView(dialogView)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun formatParagraphs(text: String): String =
        text.replace("! ", "! \n\n").replace(". ", ". \n\n")
}
