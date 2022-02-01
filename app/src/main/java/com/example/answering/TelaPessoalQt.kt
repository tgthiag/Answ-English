package com.example.answering

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.answering.databinding.ActivityTelaPessoalQtBinding

private lateinit var binding: ActivityTelaPessoalQtBinding
class TelaPessoalQt : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaPessoalQtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        var qtPessoal = arrayOf<List<String>>(
        listOf<String>(getString(R.string.question1), getString(R.string.hint1)),
        listOf<String>(getString(R.string.question2), getString(R.string.hint2)),
        listOf<String>(getString(R.string.question3), getString(R.string.hint3)),
            listOf<String>(getString(R.string.question4), getString(R.string.hint4)),
            listOf<String>(getString(R.string.question5), getString(R.string.hint5)),
            listOf<String>(getString(R.string.question6), getString(R.string.hint6)),
            listOf<String>(getString(R.string.question7), getString(R.string.hint7)),
            listOf<String>(getString(R.string.question8), getString(R.string.hint8)),
            listOf<String>(getString(R.string.question9), getString(R.string.hint9)),
            listOf<String>(getString(R.string.question10), getString(R.string.hint10)),
            listOf<String>(getString(R.string.question11), getString(R.string.hint11)),
            listOf<String>(getString(R.string.question12), getString(R.string.hint12)),
            listOf<String>(getString(R.string.question13), getString(R.string.hint13)),
            listOf<String>(getString(R.string.question14), getString(R.string.hint14)),
            listOf<String>(getString(R.string.question15), getString(R.string.hint15)),
            listOf<String>(getString(R.string.question16), getString(R.string.hint16)),
            listOf<String>(getString(R.string.question17), getString(R.string.hint17)),
            listOf<String>(getString(R.string.question18), getString(R.string.hint18)),
            listOf<String>(getString(R.string.question19), getString(R.string.hint19)),
            listOf<String>(getString(R.string.question20), getString(R.string.hint20)),
            listOf<String>(getString(R.string.question21), getString(R.string.hint21)),
            listOf<String>(getString(R.string.question22), getString(R.string.hint22)),
            listOf<String>(getString(R.string.question23), getString(R.string.hint23)),
            listOf<String>(getString(R.string.question24), getString(R.string.hint24))

        )
        qtPessoal.shuffle()
        Question().click(binding.cxTexto,qtPessoal, binding.checkDicas)//binding.cxTexto.text = questoesPessoais[0][0]
    }
}