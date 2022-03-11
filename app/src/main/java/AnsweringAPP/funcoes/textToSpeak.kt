package AnsweringAPP.funcoes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.speech.tts.TextToSpeech
import android.widget.*
import com.AnsweringAPP.R
import AnsweringAPP.activities.beginner
import AnsweringAPP.dados.COINS
import AnsweringAPP.dados.TABLE_NAME
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

object textToSpeak{

    fun TTS(ctx : Context,selectedLevel : Array<List<String>>, cxTexto : TextView, cxHint : TextView, applicationContext : Context, pq : TextView, ph : TextView,check : CheckBox, btf: ImageButton, btb: ImageButton, btAuto : Button,db : SQLiteDatabase,question_delay:Long,tradQ : TextView, tradH : TextView){


        selectedLevel.shuffle()


        var posicao = 0
        Question().click(applicationContext,
            cxTexto, cxHint, selectedLevel, check,posicao)

        btf.setOnClickListener {
            tradQ.text = ""
            tradH.text = ""
            if (posicao < selectedLevel.size -1){ posicao = posicao + 1} else{ posicao = 0}
            Question().click(applicationContext,
                cxTexto, cxHint, selectedLevel, check,posicao)
        }

        btb.setOnClickListener {
            tradQ.text = ""
            tradH.text = ""
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
                var job: Job? = null
                if (loopOn == false){
                    //=====DIMINUIR MOEDA AO ASSISTIR VÍDEO========
                    var update =  result - 1
                    cursor.close()
                    cv.put(COINS, update)
                    db.update(TABLE_NAME, cv,null,null)
                    beginner().loadCoins(ctx,db)


                    //=======INICIANDO AUTOMATIZAÇÃO=========
                    loopOn = true
                    Toast.makeText(ctx,ctx.getString(R.string.auto_start),Toast.LENGTH_LONG).show()
                    Toast.makeText(ctx,"1 moeda utilizada, você tem agora $update moedas.",Toast.LENGTH_LONG).show()
                    var job: Job? = null
                    job = GlobalScope.launch(main) { // launch a new coroutine in background and continue
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
                            delay(question_delay)
                            // non-blocking delay for 1 second (default time unit is ms)
                            tradQ.text = ""
                            tradH.text = ""
                            continue
                        }
                    }
                }else{loopOn = false; Toast.makeText(ctx,"Automation finished.",Toast.LENGTH_SHORT).show();job?.cancel()}
            }else{Toast.makeText(ctx,ctx.getString(R.string.coins_out),Toast.LENGTH_SHORT).show()}
        }





    }


}