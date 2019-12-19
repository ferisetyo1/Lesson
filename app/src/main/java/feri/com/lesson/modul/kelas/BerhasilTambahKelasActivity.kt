package feri.com.lesson.modul.kelas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import feri.com.lesson.MainActivity
import feri.com.lesson.R
import kotlinx.android.synthetic.main.activity_berhasil_tambah_kelas.*

class BerhasilTambahKelasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berhasil_tambah_kelas)

        supportActionBar?.hide()

        var dataKode = intent.getStringExtra("kodeKelas")

        kodeKelas.text=dataKode

        btn_close.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}
