package AnsweringAPP.activities

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import AnsweringAPP.dados.*
import com.AnsweringAPP.databinding.ActivityMainBinding.inflate
import com.AnsweringAPP.databinding.ActivitySettingsScreenBinding
import com.AnsweringAPP.databinding.ActivitySettingsScreenBinding.inflate
import com.AnsweringAPP.databinding.BeginnerBinding.inflate
import AnsweringAPP.funcoes.settingsSQL
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

private lateinit var binding : ActivitySettingsScreenBinding
class SettingsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingsScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        val mainClass = this
        val classdb = localSqlDatabase(mainClass)
        val db = classdb.writableDatabase

        MobileAds.initialize(this) {}
        loadBanner()

        settingsSQL().recoverTimer(db, binding.timeBeginner, binding.timeInterm, binding.timeAdvanc, binding.timeBeginInterm,
            binding.timeAllquestions)

        binding.btApply.setOnClickListener {
            if (binding.timeBeginner.text.isNotEmpty() && binding.timeInterm.text.isNotEmpty() && binding.timeAdvanc.text.isNotEmpty() && binding.timeBeginInterm.text.isNotEmpty()&& binding.timeAllquestions.text.isNotEmpty()) {
                updateTime(db, binding.timeBeginner, binding.timeInterm, binding.timeAdvanc, binding.timeBeginInterm, binding.timeAllquestions)
            }else{Toast.makeText(this,"Insert a value",Toast.LENGTH_SHORT).show()}
        }


    }

    private fun updateTime(db : SQLiteDatabase, edTx1: EditText, edTx2: EditText, edTx3: EditText, edTx4: EditText, edTx5: EditText) {
        var cv = ContentValues()
        val selectQuery = "SELECT * FROM $TABLE_NAME;"
        val cursor = db.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        cv.put(TM_BASIC,edTx1.text.toString())
        cv.put(TM_INTERM, edTx2.text.toString())
        cv.put(TM_ADVANC, edTx3.text.toString())
        cv.put(TM_BEG_INTERM, edTx4.text.toString())
        cv.put(TM_ALLQUESTIONS, edTx5.text.toString())
        db.update(TABLE_NAME,cv, null, null)
        Toast.makeText(this,"Sucess",Toast.LENGTH_SHORT).show()
        cursor.close()

    }
    private fun loadBanner() {
        val adRequest = AdRequest.Builder().build()
        binding.adView2.loadAd(adRequest)
    }
}