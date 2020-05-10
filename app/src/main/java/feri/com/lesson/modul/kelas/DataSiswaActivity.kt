package feri.com.lesson.modul.kelas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.SiswaModel
import feri.com.lesson.util.KeyGen
import kotlinx.android.synthetic.main.activity_data_siswa.*
import kotlin.collections.ArrayList

class DataSiswaActivity : AppCompatActivity() {

    private var editData: KelasModel? = null
    private var edit: Boolean = false
    private lateinit var dataKelas: KelasModel
    var listSiswa = ArrayList<SiswaModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_siswa)

        supportActionBar?.hide()

        var jumlahSiswa = intent.getIntExtra("jumlahSiswa", 0)
        dataKelas = intent.getParcelableExtra<KelasModel>("dataKelas")
        edit = intent.getBooleanExtra("edit", false)
        editData = intent.getParcelableExtra<KelasModel>("editData")
        Log.d("edit",edit.toString())
        if (edit==true) {
            if (jumlahSiswa >= editData?.list_siswa!!.size) {
                listSiswa = editData?.list_siswa!!
                generate_siswa(jumlahSiswa - listSiswa.size)
            } else {
                var count = 1
                editData!!.list_siswa.forEach {
                    if (count <= jumlahSiswa) {
                        listSiswa.add(it)
                    }
                }
            }
            var adapter = DataSiswaAdapter(this, listSiswa)
            rv_siswa.adapter = adapter
            rv_siswa.setItemViewCacheSize(listSiswa.size)
        } else {
            generate_siswa(jumlahSiswa)
            var adapter = DataSiswaAdapter(this, listSiswa)
            rv_siswa.adapter = adapter
            rv_siswa.setItemViewCacheSize(listSiswa.size)
        }


        btn_denahKelas.setOnClickListener {
            transferDataSiswa()
        }
        btn_back.setOnClickListener {
            finish()
        }
    }

    private fun generate_siswa(jumlahSiswa: Int) {
        (1..jumlahSiswa).map {
            listSiswa.add(
                SiswaModel(
                    KeyGen.create("siswa", "_", 16),
                    "",
                    0
                )
            )
        }
    }

    private fun transferDataSiswa() {
        if ((rv_siswa.adapter as DataSiswaAdapter).FieldisEmpty()) {
            Log.d("print", "is empty")
            return
        }
        var data_intent = Intent(this, DenahKelasActivity::class.java)
        dataKelas.list_siswa = listSiswa
        editData?.list_siswa = listSiswa
        data_intent.putExtra("dataKelas", dataKelas)
        data_intent.putExtra("editData", editData)
        data_intent.putExtra("edit", edit)
        startActivity(data_intent)
    }
}
