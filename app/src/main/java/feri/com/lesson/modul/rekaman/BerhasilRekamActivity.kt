package feri.com.lesson.modul.rekaman

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import feri.com.lesson.MainActivity
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.RekamanModel
import feri.com.lesson.model.TempRekamanModel
import feri.com.lesson.modul.detailRekaman.DetailRekamanActivity
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_berhasil_rekam.*
import java.io.File

class BerhasilRekamActivity : AppCompatActivity() {

    private lateinit var dataRekaman: RekamanModel
    private lateinit var dataKelas: KelasModel
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berhasil_rekam)

        supportActionBar?.hide()

        dataKelas = intent.getParcelableExtra("dataKelas") as KelasModel
        dataRekaman = intent.getParcelableExtra("dataRekaman") as RekamanModel

        firebaseDatabase = DBHelper.getDatabase()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        tv_judul.text = "${dataRekaman.judul} berhasil disimpan"
        val fileAudio = Uri.fromFile(File(dataRekaman.suaraUrl))
        val audioReff = firebaseStorage
            .getReference(const.fileRekaman)
            .child(firebaseAuth.currentUser?.uid!!)
            .child(dataRekaman.id!!)
            .child(fileAudio.lastPathSegment!!)
        var uploaded = 1
        var totalUploaded = dataRekaman.listCatatan.size + 1
        tv_simpan.text = "Menyimpan $uploaded/$totalUploaded data..."
        seekBar.progress = 0
        audioReff
            .putFile(fileAudio)
            .addOnProgressListener {
                var progress: Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                seekBar.progress = progress.toInt()
                tv_progress.text = "${progress.toInt()}/100"
            }
            .addOnSuccessListener {
                seekBar.progress = 0
                audioReff.downloadUrl.addOnCompleteListener {
                    val url = it.result
                    dataRekaman.suaraUrl = url.toString()
                    val dbreff = firebaseDatabase
                        .getReference(const.REKAMAN_DB)
                        .child(firebaseAuth.currentUser?.uid.toString())
                        .child(dataRekaman.id!!)
                    dbreff.setValue(dataRekaman)
                        .addOnCompleteListener {
                            if (dataRekaman.listCatatan.size == 0) {
                                uploadlayout.visibility = View.GONE
                                default_layout.visibility = View.VISIBLE
                            }
                            (0..dataRekaman.listCatatan.size - 1).forEach {
                                val posisi = it
                                var catatan = dataRekaman.listCatatan.get(it)
                                uploaded++
                                tv_simpan.setText("Menyimpan $uploaded/$totalUploaded data...")
                                seekBar.progress = 0
                                if (!catatan.extraUrl.isNullOrEmpty()) {
                                    val CatatanUri = Uri.fromFile(File(catatan.extraUrl))
                                    val catatanReff = firebaseStorage
                                        .getReference(const.fileRekaman)
                                        .child(firebaseAuth.currentUser?.uid!!)
                                        .child(dataRekaman.id!!)
                                        .child(CatatanUri.lastPathSegment!!)
                                    catatanReff.putFile(CatatanUri)
                                        .addOnProgressListener {
                                            var progress: Double =
                                                (100.0 * it.bytesTransferred) / it.totalByteCount
                                            seekBar.progress = progress.toInt()
                                            tv_progress.text = "${progress.toInt()}/100"
                                            if(progress>=100){
                                                if (posisi == dataRekaman.listCatatan.size - 1) {
                                                    uploadlayout.visibility = View.GONE
                                                    default_layout.visibility = View.VISIBLE
                                                    Log.d("test","berhasil")
                                                }
                                                Log.d("posisi",posisi.toString())
                                                Log.d("size",dataRekaman.listCatatan.size.toString())
                                            }
                                        }.addOnSuccessListener {
                                            catatanReff.downloadUrl.addOnCompleteListener {
                                                val url = it.result
                                                dbreff
                                                    .child("listCatatan")
                                                    .child(posisi.toString())
                                                    .child("extraUrl")
                                                    .setValue(url.toString())
                                                if (posisi == dataRekaman.listCatatan.size - 1) {
                                                    uploadlayout.visibility = View.GONE
                                                    default_layout.visibility = View.VISIBLE
                                                }
                                            }
                                        }
                                } else {
                                    seekBar.progress = 100
                                }
                            }
                        }
                    firebaseDatabase.getReference(const.REKAMAN_CODE)
                        .child(dataRekaman.kodeRekaman!!)
                        .setValue(
                            TempRekamanModel(
                                dataRekaman.id,
                                dataKelas.id,
                                dataRekaman.idPengajar,
                                dataRekaman.idPengamat
                            )
                        )
                }
            }
        btn_cekRekaman.setOnClickListener {
            val builderdialog = AlertDialog.Builder(this)
            builderdialog.setCancelable(false)
            builderdialog.setView(R.layout.progress)
            val dialog = builderdialog.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            firebaseDatabase.getReference(const.REKAMAN_DB)
                .child(dataRekaman.idPengamat!!)
                .child(dataRekaman.id!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        p0.toException().printStackTrace()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        dialog.dismiss()
                        val data = p0.getValue(RekamanModel::class.java)
                        startActivity(
                            Intent(
                                this@BerhasilRekamActivity,
                                DetailRekamanActivity::class.java
                            ).putExtra("dataRekaman", data)
                        )
                    }

                })
        }
        btn_listRekaman.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java).putExtra(
                    "specialSelect",
                    R.id.daftarRekamanFragment
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        }
    }
}
