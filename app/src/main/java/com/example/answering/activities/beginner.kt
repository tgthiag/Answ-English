package com.example.answering.activities

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.answering.dados.Questions
import com.example.answering.dados.TABLE_NAME
import com.example.answering.dados.localSqlDatabase
import com.example.answering.databinding.BeginnerBinding
import com.example.answering.funcoes.rewardedAd
import com.example.answering.funcoes.textToSpeak
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

private lateinit var binding: BeginnerBinding
class beginner : AppCompatActivity() {
    private var interstitialAd: InterstitialAd? = null
    private val bannerAd : AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BeginnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //==========DATABASE-MYSQL==============
        val mainClass = this
        val classdb = localSqlDatabase(mainClass)
        val db = classdb.writableDatabase
        classdb.initializeRow()

        //====CARREGANDO MOEDAS==========

        loadCoins(this,db)

        //===========AD-MOB=========
        MobileAds.initialize(this) {}
        loadInterstitial()
        loadBanner()
        rewardedAd().loadReward(this)


        //========BT-AD-REWARD========
        binding.btReward.setOnClickListener {
            rewardedAd().showAd(this,db)
            loadCoins(this,db)
        }




        //===========CHAMANDO PERGUNTAS==============
        var questions = Questions(this)
        var selectedLevel : Array<List<String>>
        var level:String = intent.getStringExtra("level").toString()
        when{
            level == "basic" -> selectedLevel = questions.Basic
            level == "intermediate" -> selectedLevel = questions.Intermediate
            level == "advanced" -> selectedLevel = questions.Advanced
            level == "begInterm" -> selectedLevel = questions.begInterm
            level == "allquestions" -> selectedLevel = questions.allLevels
            else ->  selectedLevel = questions.Basic
        }
        //EMBARALHANDO PERGUNTAS
        selectedLevel.shuffle()

        //==========INICIANDO FUNÇÕES DA TELA DE PERGUNTAS===============
        textToSpeak.TTS(this,selectedLevel, binding.cxTexto, binding.cxHint,applicationContext, binding.playquestion,
            binding.playhint, binding.checkDicas, binding.btFront, binding.btBack, binding.btAutomatic,db)

    }
    public override fun onPause() {
        binding.adView.pause()
        binding.adView2?.pause()
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
        binding.adView.resume()
        binding.adView2?.resume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        binding.adView.destroy()
        binding.adView2?.destroy()
        super.onDestroy()
    }

    fun loadCoins(ctx: Context, db : SQLiteDatabase): Int {
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        val cursor = db.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        var result = cursor.getString(2).toInt()
        binding.txtMoedas.text = result.toString()
        cursor.close()
        return result

    }

    private fun loadBanner() {
        val adRequest = AdRequest.Builder().build()
        binding.adView2?.loadAd(adRequest)
        binding.adView.loadAd(adRequest)
//      real banner  ca-app-pub-2884509228034182/8623568236
//     teste   ca-app-pub-3940256099942544/6300978111
    }
//real: ca-app-pub-2884509228034182/7941879672
    //teste: ca-app-pub-3940256099942544/8691691433
    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/8691691433",adRequest,object :InterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                interstitialAd = null
            }

            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                interstitialAd = p0
            }
        }


        )
    }

    fun showInterstitial(view : View){
        interstitialAd?.show(this)
    }

}