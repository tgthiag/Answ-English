package AnsweringAPP.activities

import AnsweringAPP.dados.*
import AnsweringAPP.funcoes.Translate
import AnsweringAPP.funcoes.rewardedAd
import AnsweringAPP.funcoes.textToSpeak
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.AnsweringAPP.databinding.BeginnerBinding
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

        //Traduções eram clicando nos botões, agora são automáticas
//        binding.cxTexto.setOnClickListener {
//            Translate().question(binding.cxTexto,binding.cxTradQ,this)
//        }
//        binding.cxHint.setOnClickListener {
//            Translate().hint(binding.cxHint,binding.cxTradH,this)
//        }

        //==========DATABASE-MYSQL==============
        val mainClass = this
        val classdb = localSqlDatabase(mainClass)
        val db = classdb.writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        val cursor = db.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        fun getInt(coluna: String):Int{ return cursor.getInt(cursor.getColumnIndexOrThrow(coluna)) }

        //====CARREGANDO MOEDAS==========

        loadCoins(this,db)

        //===========AD-MOB=========
        MobileAds.initialize(this) {}
        //loadInterstitial()
        loadBanner()
        rewardedAd().loadReward(this)


        //========BT-AD-REWARD========
        binding.btReward.setOnClickListener {
            rewardedAd().showAd(this,db)
            loadCoins(this,db)

        }




        //===========CHAMANDO PERGUNTAS e DEFININDO O TIMER==============
        var question_timer : Int
        var questions = Questions(this)
        var selectedLevel : Array<List<String>>
        var level:String = intent.getStringExtra("level").toString()
        when{
            level == "basic" -> {
                selectedLevel = questions.Basic
                question_timer = getInt(TM_BASIC)
            }

            level == "intermediate" -> {
                selectedLevel = questions.Intermediate
                question_timer = getInt(TM_INTERM)
            }
            level == "advanced" -> {
                selectedLevel = questions.Advanced
                question_timer = getInt(TM_ADVANC)
            }
            level == "begInterm" -> {
                selectedLevel = questions.begInterm
                question_timer = getInt(TM_BEG_INTERM)
            }
            level == "allquestions" -> {
                selectedLevel = questions.allLevels
                question_timer = getInt(TM_ALLQUESTIONS)
            }
            else -> {
                selectedLevel = questions.Basic
                question_timer = 9
            }
        }
        //==========DEFININDO O TIMER===================
        if (question_timer <= 4){question_timer = 5}
        var timer = (question_timer * 1000).toLong()

        //EMBARALHANDO PERGUNTAS
        selectedLevel.shuffle()

        //==========INICIANDO FUNÇÕES DA TELA DE PERGUNTAS===============
        textToSpeak.TTS(this,selectedLevel, binding.cxTexto, binding.cxHint,applicationContext, binding.playquestion,
            binding.playhint, binding.checkDicas, binding.btFront, binding.btBack, binding.btAutomatic,db,timer,
            binding.cxTradQ,
            binding.cxTradH)

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
        InterstitialAd.load(this,"ca-app-pub-2884509228034182/7941879672",adRequest,object :InterstitialAdLoadCallback(){
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