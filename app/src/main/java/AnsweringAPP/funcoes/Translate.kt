package AnsweringAPP.funcoes

import android.content.Context
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.AnsweringAPP.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class Translate(var ctx: Context) {
    var tm = ctx.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
    var countryCodeValue = tm.networkCountryIso
    fun getLanguage(country: String) : String{
        var current : String
        when{
            country.contains("br") && Locale.getDefault().language.equals("en") ->  current = Locale.forLanguageTag("pt").toString()
            country.contains("in") && Locale.getDefault().language.equals("en") ->  current = Locale.forLanguageTag("hi").toString()
            country.contains("pk") && Locale.getDefault().language.equals("en") ->  current = Locale.forLanguageTag("ur").toString()
                else -> current = Locale.getDefault().language
        }
//        Toast.makeText(ctx,current,Toast.LENGTH_LONG).show()
        return current
    }
    var lang = getLanguage(countryCodeValue)
    var tradOpt = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.fromLanguageTag(lang).toString())
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
            .addOnFailureListener {}
    }


    fun hint(cxH: TextView,cxHT:TextView,ctx: Context){
        traduzir_pergunta.translate(cxH.text.toString())
            .addOnSuccessListener {
                if (cxH.text != "****") {
                    cxHT.text = it
                }else{cxHT.text = ctx.getString(R.string.nohintsavailable)}
            }
            .addOnFailureListener{}
    }

    fun toastTrad(str: String){
        traduzir_pergunta.translate(str)
            .addOnSuccessListener {
                Toast.makeText(ctx,it,Toast.LENGTH_LONG).show()
        }
            .addOnFailureListener {
                Toast.makeText(ctx,str,Toast.LENGTH_LONG).show()
            }
    }
    fun translateButtons(btReward : Button, btAuto: Button, checkB : CheckBox, quest : TextView, example : TextView,using : TextView) {
        if (!Locale.getDefault().language.equals("pt") && !Locale.getDefault().language.equals("hi") && !Locale.getDefault().language.equals("de")){
            var bt1 :String = btReward.text.toString()
            var bt2 :String = btAuto.text.toString()
            var chk :String = checkB.text.toString()
            var tx1 : String = quest.text.toString()
            var tx2 : String = example.text.toString()
            var tx3 : String = using.text.toString()
            traduzir_pergunta.translate(bt1).addOnSuccessListener {btReward.text = it}
            traduzir_pergunta.translate(bt2).addOnSuccessListener {btAuto.text = it}
            traduzir_pergunta.translate(chk).addOnSuccessListener {checkB.text = it}
            traduzir_pergunta.translate(tx1).addOnSuccessListener {quest.text = it}
            traduzir_pergunta.translate(tx2).addOnSuccessListener {example.text = it}
            traduzir_pergunta.translate(tx3).addOnSuccessListener {using.text = it}
            }
    }
}