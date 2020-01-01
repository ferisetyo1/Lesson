package feri.com.lesson.modul.detailRekaman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import feri.com.lesson.R

class DetailRekamanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_rekaman)

        supportActionBar?.hide()
    }
}
