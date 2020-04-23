package feri.com.lesson.modul.rekaman

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.RekamanModel
import feri.com.lesson.util.Helpers
import feri.com.lesson.util.KeyGen
import kotlinx.android.synthetic.main.activity_data_rekaman.*
import kotlinx.android.synthetic.main.activity_data_rekaman.tanggalObservasi
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DataRekamanActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_rekaman)

        supportActionBar?.hide()

        btn_back.setOnClickListener(this)
        btn_lanjut.setOnClickListener(this)

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = formatter.format(Date())
        tanggalObservasi.setText(date)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_lanjut -> {
                transferDataRekaman()
            }
        }
    }

    private fun transferDataRekaman() {
        if (FieldisError()) {
            return
        }
        var dataKelas = intent.getParcelableExtra<KelasModel>("dataKelas")
        var rekamanModel = RekamanModel(
            KeyGen.create("rekaman", "_", 6),
            dataKelas.id,
            dataKelas.idPengajar,
            FirebaseAuth.getInstance().currentUser?.uid,
            judulRekaman.text.trim().toString(),
            tanggalObservasi.text.trim().toString(),
            "",
            ArrayList(),
            ""
        )
        startActivity(
            Intent(this, PreviewDataRekamanActivity::class.java)
                .putExtra("dataKelas", dataKelas)
                .putExtra("dataRekaman", rekamanModel)
        )
    }

    private fun FieldisError(): Boolean {
        Log.d("aaaa", Helpers.dateCheckisTrueDate(tanggalObservasi.text.toString()).toString())
        if (judulRekaman.text.isNullOrEmpty()) {
            judulRekaman.requestFocus()
            judulRekaman.setError(getString(R.string.errorFieldKosong))
            return true
        }

        if (tanggalObservasi.text.isNullOrEmpty()) {
            tanggalObservasi.requestFocus()
            tanggalObservasi.setError(getString(R.string.errorFieldKosong))
            return true
        }
        return false
    }

}
