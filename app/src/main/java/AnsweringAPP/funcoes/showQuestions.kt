package AnsweringAPP.funcoes

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.CheckBox
import android.widget.TextView
import java.util.*

lateinit var mTTS : TextToSpeech
class Question {
    fun click(ctx: Context, cxQT: TextView,cxHT: TextView, hint: Array<List<String>>, check: CheckBox, posDaPergunta: Int): String{
        cxHT.text = ""
        cxQT.text = hint[posDaPergunta][0]
        mTTS = TextToSpeech(ctx,TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                mTTS.setLanguage(Locale.US)
//                mTTS.setSpeechRate(0.7F)
//                mTTS.setPitch(0.7F)
                mTTS.speak(hint[posDaPergunta][0],TextToSpeech.QUEUE_FLUSH,null,null)
            }
        })
        if (check.isChecked){
            cxHT.text = "****"
        }
        else{

            cxHT.text = hint[posDaPergunta][1].toString()
        }
        return cxQT.text.toString();

    }

    fun readByVoice(ctx: Context, cxQT: TextView){
        mTTS = TextToSpeech(ctx,TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                mTTS.setLanguage(Locale.US)
//                mTTS.setSpeechRate(0.7F)
//                mTTS.setPitch(0.7F)
                mTTS.speak(cxQT.text,TextToSpeech.QUEUE_FLUSH,null,null)
            }
        })

    }


}
