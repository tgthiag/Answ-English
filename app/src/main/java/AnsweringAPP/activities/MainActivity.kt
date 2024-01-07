package AnsweringAPP.activities


import AnsweringAPP.dados.TABLE_NAME
import AnsweringAPP.dados.localSqlDatabase
import AnsweringAPP.funcoes.DialogShow
import AnsweringAPP.funcoes.Translate
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.AnsweringAPP.R
import com.AnsweringAPP.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds


private lateinit var binding : ActivityMainBinding
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Translate(this).download(this)

        //==========DATABASE-MYSQL==============
        val mainClass = this
        val classdb = localSqlDatabase(mainClass)
        val db = classdb.writableDatabase
        var query = "SELECT * FROM $TABLE_NAME;"
        classdb.initializeRow()

        var cursorr = db.rawQuery(query,null)
        cursorr.moveToFirst()
        var isFirstAcess = cursorr.getString(4).toInt()

        if (isFirstAcess == 0){
            try {
                DialogShow().firstDialog(this)
            }catch (e : Exception){
                print("error")}
        }


        MobileAds.initialize(this) {}
        loadBanner()




        binding.btAboutYou.setOnClickListener {
            val intent = Intent(this, beginner::class.java)
            intent.putExtra("level", "basic")
            startActivity(intent)
        }
        binding.btEveryDayThings.setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "begInterm")
            startActivity(intent)
        }
        binding.btJobInterview.setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "advanced")
            startActivity(intent)
        }
        binding.allQuestions .setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "allquestions")
            startActivity(intent)
        }
        binding.btSettings.setOnClickListener {
            startActivity(Intent(this,SettingsScreen::class.java))
        }
        binding.btShare.setOnClickListener {
            ShareCompat.IntentBuilder(this)
                .setType("text/plain")
                .setChooserTitle(R.string.shareFriends)
                .setText(getString(R.string.shareMessage)+" https://play.google.com/store/apps/details?id=" + this.getPackageName())
                .startChooser();
        }
    }
    private fun loadBanner() {
        val adRequest = AdRequest.Builder().build()
        binding.adView?.loadAd(adRequest)
    }
}