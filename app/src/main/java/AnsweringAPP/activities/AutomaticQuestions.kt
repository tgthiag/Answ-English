package AnsweringAPP.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.AnsweringAPP.databinding.ActivityAutomaticQuestionsBinding

private lateinit var binding : ActivityAutomaticQuestionsBinding
class AutomaticQuestions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAutomaticQuestionsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}