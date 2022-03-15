package AnsweringAPP.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import AnsweringAPP.activities.SettingsScreen
import AnsweringAPP.activities.beginner
import AnsweringAPP.dados.localSqlDatabase
import AnsweringAPP.funcoes.Translate
import androidx.core.app.ShareCompat
import com.AnsweringAPP.R


import com.AnsweringAPP.databinding.ActivityMainBinding


private lateinit var binding : ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Translate().download(this)

        //==========DATABASE-MYSQL==============
        val mainClass = this
        val classdb = localSqlDatabase(mainClass)
        val db = classdb.writableDatabase
        classdb.initializeRow()


        binding.btAboutYou.setOnClickListener {
            val intent = Intent(this, beginner::class.java)
            intent.putExtra("level", "basic")
            startActivity(intent)
        }
        binding.btEveryDayThings.setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "intermediate")
            startActivity(intent)
        }
        binding.btJobInterview.setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "advanced")
            startActivity(intent)
        }
        binding.btBegInterm.setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "begInterm")
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
}