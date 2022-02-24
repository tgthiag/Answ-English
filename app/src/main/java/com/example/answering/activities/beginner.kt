package com.example.answering.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.answering.R
import com.example.answering.databinding.BeginnerBinding
import com.example.answering.funcoes.Admob
import com.example.answering.funcoes.Question
import com.example.answering.funcoes.mTTS
import com.example.answering.questions.Questions
import java.util.*

private lateinit var binding: BeginnerBinding
class beginner : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BeginnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        supportActionBar?.hide()

        //Call AD FROM ADMOB
        Admob().showAd(this, binding.adView, binding.adView2)

        //Chamando as perguntas beginner
        var questions = Questions(this)
        var selectedLevel : Array<List<String>>
        var level:String = intent.getStringExtra("level").toString()
        Toast.makeText(this,"level $level selected",Toast.LENGTH_SHORT).show()
        when{
            level == "basic" -> selectedLevel = questions.Basic
            level == "intermediate" -> selectedLevel = questions.Intermediate
            level == "advanced" -> selectedLevel = questions.Advanced
            else ->  selectedLevel = questions.Basic
        }
        selectedLevel.shuffle()


        var posicao = 0
        Question().click(applicationContext,binding.cxTexto, binding.cxHint, selectedLevel, binding.checkDicas,posicao)

        binding.btFront.setOnClickListener {
            if (posicao < selectedLevel.size -1){ posicao = posicao + 1} else{ posicao = 0}
            Question().click(applicationContext,binding.cxTexto, binding.cxHint, selectedLevel, binding.checkDicas,posicao)
        }

        binding.btBack.setOnClickListener {
            if (posicao != 0){ posicao = posicao - 1} else{ posicao = 0}
            Question().click(applicationContext,binding.cxTexto,binding.cxHint, selectedLevel, binding.checkDicas,posicao)
        }

        binding.playquestion.setOnClickListener {
            mTTS = TextToSpeech(applicationContext,TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR){
                    mTTS.setLanguage(Locale.US)
                    mTTS.speak(binding.cxTexto.text,TextToSpeech.QUEUE_FLUSH,null,null)
                }
            })
        }

        binding.playhint.setOnClickListener {
            mTTS = TextToSpeech(applicationContext,TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR){
                    if (binding.cxHint.text != "****") {
                        mTTS.setLanguage(Locale.US)
                        mTTS.speak(binding.cxHint.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
                    }else{
                        mTTS.defaultVoice
                        mTTS.speak(this.getString(R.string.nohintsavailable), TextToSpeech.QUEUE_FLUSH, null, null)
                        Toast.makeText(this,this.getString(R.string.nohintsavailable),Toast.LENGTH_SHORT).show()}
                }
            })
        }

    }
}