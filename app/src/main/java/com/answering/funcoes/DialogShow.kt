package com.answering.funcoes

import com.answering.dados.FIRSTACESS
import com.answering.dados.TABLE_NAME
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class DialogShow {

    private fun idString(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "string", ctx.packageName)
    private fun idLayout(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "layout", ctx.packageName)
    private fun idView(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "id", ctx.packageName)

    fun AlertDialog(ctx: Context) {
        val source = "You've started with some coins!\n\nYou will earn 2 coin every day that you open the app.\n\nCoins are used for video recording and to automatize the questions.\n\n" +
                "You can earn 3 coins by watching a video anytime.\n\nUse the automatize button to answer questions while doing your everyday things.\n\n" +
                "Start recording videos to share on your WhatsApp, Facebook and Instagram posts, as soon as you click the camera button, it will start, tap the blue arrows for next question.\n\nEvery level has different questions, try it!"

        // Translate may call callbacks off the main thread; showInfoDialog garante execução na Main.
        Translate(ctx).traduzir_pergunta.translate(source)
            .addOnSuccessListener { translated ->
                showInfoDialog(ctx, ctx.getString(idString(ctx, "dialog_coins_title")), formatParagraphs(translated))
            }
            .addOnFailureListener {
                showInfoDialog(ctx, ctx.getString(idString(ctx, "dialog_coins_title")), formatParagraphs(source))
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
        val builder = AlertDialog.Builder(ctx).setTitle(title)
            .setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
                val positive = listItems[i] == "Yes, I love it!"
                if (positive) {
                    Translate(ctx).toastTrad("Thank you so much! We are happy to know!")
                    inAppReview().call(ctx)
                } else {
                    Translate(ctx).toastTrad("Sorry, we are working to give you a better experience")
                }
                val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME;", null)
                try {
                    if (cursor.moveToFirst()) {
                        val usedTheApp = cursor.getString(4).toInt()
                        val cv = ContentValues().apply { put(FIRSTACESS, usedTheApp + 1) }
                        db.update(TABLE_NAME, cv, null, null)
                    }
                } finally {
                    cursor.close()
                }
                dialogInterface.dismiss()
            }
            .setNeutralButton(ctx.getString(idString(ctx, "dialog_ok"))) { dialog, _ -> dialog.dismiss() }

        // Ensure dialog is shown on main thread and Activity is valid
        val showRunnable = Runnable {
            try {
                if (ctx is Activity && (ctx.isFinishing || ctx.isDestroyed)) return@Runnable
                builder.show()
            } catch (e: Exception) {
                // swallow to avoid crashing app if context invalid
            }
        }
        if (Looper.myLooper() == Looper.getMainLooper()) showRunnable.run() else Handler(Looper.getMainLooper()).post(showRunnable)
    }

    fun firstDialog(ctx: Context, db: SQLiteDatabase) {
        val subtitle = ctx.getString(idString(ctx, "dialog_first_subtitle"))
        val tips = ctx.getString(idString(ctx, "dialog_first_tips"))
        val body = "$subtitle\n\n$tips"
        showInfoDialog(ctx, ctx.getString(idString(ctx, "dialog_first_title")), body)
        val cv = ContentValues().apply { put(FIRSTACESS, 1) }
        db.update(TABLE_NAME, cv, null, null)
    }

    private fun showInfoDialog(ctx: Context, title: CharSequence?, body: CharSequence) {
        // resolve layout id (fallback to resource lookup if getIdentifier fails)
        val layoutId = idLayout(ctx, "dialog_info")
        val inflateId = if (layoutId != 0) layoutId else ctx.resources.getIdentifier("dialog_info", "layout", ctx.packageName)

        val dialogView = try {
            LayoutInflater.from(ctx).inflate(inflateId, null, false)
        } catch (e: Exception) {
            // fallback: create a simple TextView if inflation fails
            val tv = TextView(ctx)
            tv.text = body
            tv
        }

        // try to set title/message only if views exist
        try {
            val titleViewId = idView(ctx, "dialogTitle")
            val msgViewId = idView(ctx, "dialogMessage")
            if (dialogView is View) {
                val titleView = if (titleViewId != 0) dialogView.findViewById<TextView>(titleViewId) else null
                val messageView = if (msgViewId != 0) dialogView.findViewById<TextView>(msgViewId) else null

                if (titleView != null) {
                    if (title.isNullOrBlank()) titleView.visibility = View.GONE else { titleView.text = title; titleView.visibility = View.VISIBLE }
                }
                if (messageView != null) {
                    messageView.text = body
                }
            }
        } catch (e: Exception) {
            // ignore view binding errors
        }

        val builder = AlertDialog.Builder(ctx).setView(dialogView).setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }

        val showRunnable = Runnable {
            try {
                if (ctx is Activity && (ctx.isFinishing || ctx.isDestroyed)) return@Runnable
                builder.show()
            } catch (e: Exception) {
                // ignore
            }
        }
        if (Looper.myLooper() == Looper.getMainLooper()) showRunnable.run() else Handler(Looper.getMainLooper()).post(showRunnable)
    }

    private fun formatParagraphs(text: String): String =
        text.replace("! ", "! \n\n").replace(". ", ". \n\n")
}
