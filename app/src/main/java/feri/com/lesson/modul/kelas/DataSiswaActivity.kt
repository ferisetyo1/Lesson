package feri.com.lesson.modul.kelas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.SiswaModel
import feri.com.lesson.modul.util.KeyGen
import kotlinx.android.synthetic.main.activity_data_siswa.*
import java.util.*
import kotlin.collections.ArrayList

class DataSiswaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_siswa)

        supportActionBar?.hide()

        var jumlahSiswa = intent.getIntExtra("jumlahSiswa", 0)
        var dataKelas=intent.getParcelableExtra<KelasModel>("dataKelas")
        var listSiswa = ArrayList<SiswaModel>()
        (1..jumlahSiswa).map {
            listSiswa.add(
                SiswaModel(
                    KeyGen.create("siswa","_",16),
                    "",
                    0
                )
            )
        }
        var adapter=DataSiswaAdapter(this,listSiswa)
        rv_siswa.adapter=adapter

        btn_denahKelas.setOnClickListener {
            if (adapter.isEmpty()){
                Log.d("print","is empty")
                return@setOnClickListener
            }
//            Log.d("print",listSiswa.toString())
            var data_intent= Intent(this,DenahBangkuActivity::class.java)
            dataKelas.list_siswa=listSiswa
            data_intent.putExtra("dataKelas",dataKelas)
            startActivity(data_intent)
        }
        btn_back.setOnClickListener{
            finish()
        }
    }
}
