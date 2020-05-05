package feri.com.lesson.modul.rekaman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.RekamanModel
import feri.com.lesson.model.UserModel
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_preview_data.*
import kotlinx.android.synthetic.main.activity_preview_data.tanggalObservasi

class PreviewDataRekamanActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_data)

        supportActionBar?.hide()

        var dataRekaman = intent.getParcelableExtra<RekamanModel>("dataRekaman")
        var dataKelas = intent.getParcelableExtra<KelasModel>("dataKelas")

        firebaseDatabase = DBHelper.getDatabase()
        firebaseAuth = FirebaseAuth.getInstance()

        UpdateUI(dataRekaman, dataKelas)

        btn_mulai.setOnClickListener {
            transferDataRekaman(dataKelas,dataRekaman)
        }
    }

    private fun transferDataRekaman(
        dataKelas: KelasModel?,
        dataRekaman: RekamanModel?
    ) {
        startActivity(
            Intent(this, RekamanActivity::class.java)
                .putExtra("dataRekaman", dataRekaman)
                .putExtra("dataKelas", dataKelas)
        )
    }

    private fun UpdateUI(
        dataRekaman: RekamanModel?,
        dataKelas: KelasModel?
    ) {
        judulRekaman.text = dataRekaman?.judul
        mataPelajaran.text = dataKelas?.namaPelajaran
        tanggalObservasi.text = dataRekaman?.tanggal
        lokasi.text = dataKelas?.namaSekolah

        firebaseDatabase.getReference(const.USER_DB)
            .orderByKey()
            .equalTo(dataKelas?.idPengajar)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        var user = it.getValue(UserModel::class.java)
                        pengajar.text = user?.nama
                    }
                }

            })
        firebaseDatabase.getReference(const.USER_DB)
            .orderByKey()
            .equalTo(firebaseAuth.currentUser?.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        var user = it.getValue(UserModel::class.java)
                        namaObserver.text = user?.nama
                    }
                }

            })
    }
}
