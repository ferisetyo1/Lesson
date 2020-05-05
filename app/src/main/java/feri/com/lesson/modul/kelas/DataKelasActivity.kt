package feri.com.lesson.modul.kelas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import feri.com.lesson.R
import feri.com.lesson.model.GroupModel
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.SiswaModel
import feri.com.lesson.util.KeyGen
import kotlinx.android.synthetic.main.activity_data_kelas.*
import kotlin.collections.ArrayList

class DataKelasActivity : AppCompatActivity(), View.OnClickListener {

    private var editData: KelasModel? = null
    private var edit: Boolean = false
    private lateinit var curr_dataKelas: KelasModel
    private lateinit var curr_dataSiswa: ArrayList<SiswaModel>
    private lateinit var curr_denahKelas: ArrayList<Int>
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_kelas)
        supportActionBar?.hide()

        //firebase init
        firebaseAuth = FirebaseAuth.getInstance()
        edit = intent.getBooleanExtra("edit", false)
        editData = intent.getParcelableExtra<KelasModel>("editData")
        if (edit) {
            et_namakelas.setText(editData!!.nama)
            et_namasekolah.setText(editData!!.namaSekolah)
            et_pelajaran.setText(editData!!.namaPelajaran)
            et_jumlahsiswa.setText(editData!!.list_siswa.size.toString())
        }

        btn_back.setOnClickListener(this)
        btn_datasiswa.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_datasiswa -> {
                transferDataKelas()
            }
        }
    }

    private fun transferDataKelas() {
        if (FieldisEmpty()) {
            return
        }
        var data_intent = Intent(this, DataSiswaActivity::class.java)
        data_intent.putExtra(
            "dataKelas", KelasModel().apply {
                id = KeyGen.create("kelas", "_", 16)
                idPengajar = firebaseAuth.uid.toString()
                nama = et_namakelas.text.toString().toLowerCase().trim()
                namaSekolah = et_namasekolah.text.toString().toLowerCase().trim()
                namaPelajaran = et_pelajaran.text.toString().toLowerCase().trim()
            }
        )
        data_intent.putExtra("edit", true)
        editData?.apply {
            nama = et_namakelas.text.toString().toLowerCase().trim()
            namaSekolah = et_namasekolah.text.toString().toLowerCase().trim()
            namaPelajaran = et_pelajaran.text.toString().toLowerCase().trim()
        }
        data_intent.putExtra("editData", editData)
        data_intent.putExtra("jumlahSiswa", et_jumlahsiswa.text.trim().toString().toInt())
        startActivity(data_intent)
    }

    private fun FieldisEmpty(): Boolean {
        if (et_namakelas.text.isNullOrEmpty()) {
            et_namakelas.setError(getString(R.string.errorFieldKosong))
            et_namakelas.requestFocus()
            return true
        }

        if (et_namasekolah.text.isNullOrEmpty()) {
            et_namasekolah.setError(getString(R.string.errorFieldKosong))
            et_namasekolah.requestFocus()
            return true
        }

        if (et_pelajaran.text.isNullOrEmpty()) {
            et_pelajaran.setError(getString(R.string.errorFieldKosong))
            et_pelajaran.requestFocus()
            return true
        }

        if (et_jumlahsiswa.text.isNullOrEmpty()) {
            et_jumlahsiswa.setError(getString(R.string.errorFieldKosong))
            et_jumlahsiswa.requestFocus()
            return true
        }

        return false
    }
}
