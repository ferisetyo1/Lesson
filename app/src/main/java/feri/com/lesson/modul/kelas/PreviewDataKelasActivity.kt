package feri.com.lesson.modul.kelas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.TempKodeClassModel
import feri.com.lesson.model.UserModel
import feri.com.lesson.modul.util.KeyGen
import feri.com.lesson.modul.util.const
import kotlinx.android.synthetic.main.activity_preview_data_kelas.*

class PreviewDataKelasActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_data_kelas)

        supportActionBar?.hide()

        //firebase init
        firebaseDatabase = FirebaseDatabase.getInstance()
        db_reff = firebaseDatabase.getReference(const.KELAS_DB)
        var db_reff_listkode = firebaseDatabase.getReference(const.CLASS_CODE)
        var db_reff_akun = firebaseDatabase.getReference(const.USER_DB)

        //intentData
        var dataKelas = intent.getParcelableExtra<KelasModel>("dataKelas")
        dataKelas.kodeKelas = KeyGen.create("", "", 6)

        db_reff_akun.child(dataKelas.idPengajar!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var userData = p0.getValue(UserModel::class.java)
                    UpdateUI(userData, dataKelas)
                }

            })

        db_reff_listkode.child(dataKelas.kodeKelas!!).setValue(TempKodeClassModel(dataKelas.idPengajar,dataKelas.id))

        btn_buatKode.setOnClickListener {
            db_reff.child(dataKelas.idPengajar!!).child(dataKelas.id!!).setValue(dataKelas)
                .addOnCompleteListener {
                    startActivity(
                        Intent(this, BerhasilTambahKelasActivity::class.java)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            ).putExtra("kodeKelas", dataKelas.kodeKelas)
                    )
                    finish()
                }

        }

        btn_back2.setOnClickListener { finish() }
    }

    private fun UpdateUI(
        userData: UserModel?,
        dataKelas: KelasModel?
    ) {
        namaPengajar.text = userData?.nama
        namaSekolah.text = dataKelas?.namaSekolah
        namaPelajaran.text = dataKelas?.namaPelajaran
        jmlSiswa.text = dataKelas?.list_siswa?.size.toString()
    }
}
