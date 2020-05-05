package feri.com.lesson.modul

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import feri.com.lesson.R
import feri.com.lesson.modul.authentication.LoginActivity
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        supportActionBar?.hide()
        rg.setOnCheckedChangeListener(this)
        next.setOnClickListener {
            r2.isChecked=true
        }
    }

    fun login(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
        lyt_r1.visibility = View.GONE
        lyt_r2.visibility = View.GONE
        lyt_r3.visibility = View.GONE
        lyt_r4.visibility = View.GONE
        lyt_r5.visibility = View.GONE
        back.visibility=View.VISIBLE
        next.visibility=View.VISIBLE
        txt_masuk.visibility=View.INVISIBLE
        when (p1) {
            R.id.r1 -> {
                lyt_r1.visibility = View.VISIBLE
                back.visibility=View.INVISIBLE
                backnext(null, r2)
            }
            R.id.r2 -> {
                lyt_r2.visibility = View.VISIBLE
                backnext(r1, r3)
            }
            R.id.r3 -> {
                lyt_r3.visibility = View.VISIBLE
                backnext(r2, r4)
            }
            R.id.r4 -> {
                lyt_r4.visibility = View.VISIBLE
                backnext(r3, r5)
            }
            R.id.r5 -> {
                lyt_r5.visibility = View.VISIBLE
                txt_masuk.visibility=View.VISIBLE
                next.visibility=View.INVISIBLE
                backnext(r4, null)
            }
        }
    }

    private fun backnext(rr1: RadioButton?, rr2: RadioButton?) {
        back.setOnClickListener {
            rr1?.isChecked=true
        }
        next.setOnClickListener {
            rr2?.isChecked=true
        }
    }

}
