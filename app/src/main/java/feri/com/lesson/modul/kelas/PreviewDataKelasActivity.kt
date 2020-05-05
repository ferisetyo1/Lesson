package feri.com.lesson.modul.kelas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import feri.com.lesson.MainActivity
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.TempKelasModel
import feri.com.lesson.model.UserModel
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.KeyGen
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_preview_data_kelas.*

class PreviewDataKelasActivity : AppCompatActivity() {

    private var edit: Boolean = false
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff_kelas: DatabaseReference
    private lateinit var db_reff_listkode: DatabaseReference
    private lateinit var db_reff_akun: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_data_kelas)

        supportActionBar?.hide()

        //firebase init
        firebaseDatabase = DBHelper.getDatabase()
        db_reff_kelas = firebaseDatabase.getReference(const.KELAS_DB)
        db_reff_listkode = firebaseDatabase.getReference(const.CLASS_CODE)
        db_reff_akun = firebaseDatabase.getReference(const.USER_DB)

        //intentData
        var dataKelas = intent.getParcelableExtra<KelasModel>("dataKelas")
        edit = intent.getBooleanExtra("edit", false)
        if (!edit) {
            dataKelas.kodeKelas = KeyGen.create("", "", 6)
        }
        simpanKelasKeServer(dataKelas)

        btn_back2.setOnClickListener { finish() }
    }

    private fun simpanKelasKeServer(dataKelas: KelasModel?) {
        db_reff_akun.child(dataKelas?.idPengajar!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var userData = p0.getValue(UserModel::class.java)
                    UpdateUI(userData, dataKelas)
                }

            })

        db_reff_listkode.child(dataKelas.kodeKelas!!)
            .setValue(TempKelasModel(dataKelas.idPengajar, dataKelas.id))

        if (edit){
            btn_buatKode.setText("Simpan Perubahan")
        }

        btn_buatKode.setOnClickListener {
            db_reff_kelas.child(dataKelas.idPengajar!!).child(dataKelas.id!!).setValue(dataKelas)
                .addOnCompleteListener {
                    if (!edit){
                        startActivity(
                            Intent(this, BerhasilTambahKelasActivity::class.java)
                                .addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                ).putExtra("kodeKelas", dataKelas.kodeKelas)
                        )
                    }else{
                        startActivity(
                            Intent(this, MainActivity::class.java).putExtra(
                                "specialSelect",
                                R.id.daftarKelasFragment
                            )
                        )
                        Toast.makeText(this,"Berhasil mengubah kelas",Toast.LENGTH_LONG).show()
                    }
                    finish()
                }

        }
    }

    private fun UpdateUI(
        userData: UserModel?,
        dataKelas: KelasModel?
    ) {
        namaPengajar.text = userData?.nama
        namaSekolah.text = dataKelas?.namaSekolah
        tanggalObservasi.text = dataKelas?.namaPelajaran
        jmlSiswa.text = dataKelas?.list_siswa?.size.toString()
    }
}
