package AnsweringAPP.funcoes

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.AnsweringAPP.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class Translate() {
    var currentLanguage: String = Locale.getDefault().language
    var tradOpt = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.fromLanguageTag(currentLanguage)!!)
        .build()
    val traduzir_pergunta = Translation.getClient(tradOpt)
    var conditions = DownloadConditions.Builder()
        .build()
    fun download(ctx: Context) {
        traduzir_pergunta.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
            }
    }
    fun question(cxQ: TextView,cxQt: TextView,ctx: Context) {
        traduzir_pergunta.translate(cxQ.text.toString())
            .addOnSuccessListener {
                cxQt.text = it
            }
            .addOnFailureListener {
                Toast.makeText(ctx, "Getting your translation pack.", Toast.LENGTH_LONG)
                    .show()
            }
    }


    fun hint(cxH: TextView,cxHT:TextView,ctx: Context){
        traduzir_pergunta.translate(cxH.text.toString())
            .addOnSuccessListener {
                if (cxH.text != "****") {
                    cxHT.text = it
                }else{cxHT.text = ctx.getString(R.string.nohintsavailable)}
            }
            .addOnFailureListener{Toast.makeText(ctx,"Getting your translation pack.",Toast.LENGTH_LONG).show()}
    }

}