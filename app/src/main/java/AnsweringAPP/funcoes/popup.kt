package AnsweringAPP.funcoes

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isGone
import com.AnsweringAPP.R

class popup {
    private lateinit var alertDialog: AlertDialog
    fun showCustomDialog(ctx: Context, str:String) {
        val inflater: LayoutInflater = LayoutInflater.from(ctx)
        val dialogView: View = inflater.inflate(R.layout.popup, null)
        val header_txt = dialogView.findViewById<TextView>(R.id.header)
        header_txt.text = str
//        val details_txt = dialogView.findViewById<TextView>(R.id.details)
//        val custom_button: Button = dialogView.findViewById(R.id.customBtn)
//        custom_button.setOnClickListener {
//        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(ctx)
        dialogBuilder.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(arg0: DialogInterface) {

            }
        })
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create();
        alertDialog.window!!.getAttributes().windowAnimations
        alertDialog.show()
    }
}