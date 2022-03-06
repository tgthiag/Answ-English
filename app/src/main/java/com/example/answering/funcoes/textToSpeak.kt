package com.example.answering.funcoes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.*
import com.example.answering.R
import com.example.answering.activities.beginner
import com.example.answering.dados.COINS
import com.example.answering.dados.TABLE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

object textToSpeak{

    fun TTS(ctx : Context,selectedLevel : Array<List<String>>, cxTexto : TextView, cxHint : TextView, applicationContext : Context, pq : TextView, ph : TextView,check : CheckBox, btf: ImageButton, btb: ImageButton, btAuto : Button,db : SQLiteDatabase){


        selectedLevel.shuffle()


        var posicao = 0
        Question().click(applicationContext,
            cxTexto, cxHint, selectedLevel, check,posicao)

        btf.setOnClickListener {
            if (posicao < selectedLevel.size -1){ posicao = posicao + 1} else{ posicao = 0}
            Question().click(applicationContext,
                cxTexto, cxHint, selectedLevel, check,posicao)
        }

        btb.setOnClickListener {
            if (posicao != 0){ posicao = posicao - 1} else{ posicao = 0}
            Question().click(applicationContext,
                cxTexto,
                cxHint, selectedLevel, check,posicao)
        }

        pq.setOnClickListener {
            mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR){
                    mTTS.setLanguage(Locale.US)
                    mTTS.speak(cxTexto.text, TextToSpeech.QUEUE_FLUSH,null,null)
                }
            })
        }

        ph.setOnClickListener {
            mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR){
                    if (cxHint.text != "****") {
                        mTTS.setLanguage(Locale.US)
                        mTTS.speak(cxHint.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
                    }else{
                        mTTS.defaultVoice
                        mTTS.speak(ctx.getString(R.string.nohintsavailable), TextToSpeech.QUEUE_FLUSH, null, null)
                        Toast.makeText(ctx,ctx.getString(R.string.nohintsavailable), Toast.LENGTH_SHORT).show()}
                }
            })
        }

        val main: CoroutineContext by lazy { Dispatchers.Main }
        var loopOn : Boolean = false
        btAuto.setOnClickListener {
            //=========CHECK AND UPDATE COINS
            var cv = ContentValues()
            val selectQuery = "SELECT * FROM $TABLE_NAME;"
            val cursor = db.rawQuery(selectQuery, null)
            cursor.moveToFirst()
            var result = cursor.getString(2).toInt()

            if (result != 0 || loopOn == true){
                if (loopOn == false){
                    //=====DIMINUIR MOEDA AO ASSISTIR VÍDEO========
                    var update =  result - 1
                    cursor.close()
                    cv.put(COINS, update)
                    db.update(TABLE_NAME, cv,null,null)
                    beginner().loadCoins(ctx,db)


                    //=======INICIANDO AUTOMATIZAÇÃO=========
                    loopOn = true
                    Toast.makeText(ctx,"Automatização iniciada, aguarde...",Toast.LENGTH_LONG).show()
                    Toast.makeText(ctx,"1 moeda utilizada, você tem agora $update moedas.",Toast.LENGTH_LONG).show()
                    GlobalScope.launch(main) { // launch a new coroutine in background and continue
                        while (loopOn == true) {
                            if (loopOn == false){break}
                            if (posicao < selectedLevel.size - 1) {
                                posicao = posicao + 1
                            } else {
                                posicao = 0
                            }
                            Question().click(
                                ctx,
                                cxTexto, cxHint, selectedLevel, check, posicao
                            )
                            delay(9000)
                            // non-blocking delay for 1 second (default time unit is ms)
                            continue
                        }
                    }
                }else{loopOn = false; Toast.makeText(ctx,"Automatização finalizada",Toast.LENGTH_SHORT).show()}
            }else{Toast.makeText(ctx,"Suas moedas acabaram! Assista a um vídeo para ganhar mais.",Toast.LENGTH_SHORT).show()}
        }





    }


}