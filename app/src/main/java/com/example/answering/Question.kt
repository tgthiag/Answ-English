package com.example.answering

import android.widget.CheckBox
import android.widget.TextView

class Question {
    fun click(pergunta: TextView, hint: Array<List<String>>,check: CheckBox){
        pergunta.text = "Pergunta:\n \n" + hint[0][0]
        if (check.isChecked){
            pergunta.append("\n \n \n")
            pergunta.append("Dica:\n \n")
            pergunta.append("********")
        }
        else{
            pergunta.append("\n \n \n")
            pergunta.append("Dica:\n \n")
            pergunta.append(hint[0][1].toString())
            pergunta.setOnClickListener{ return@setOnClickListener click(pergunta,hint,check) }
        }
    }
}
