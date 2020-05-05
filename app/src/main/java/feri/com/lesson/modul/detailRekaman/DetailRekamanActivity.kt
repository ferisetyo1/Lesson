package feri.com.lesson.modul.detailRekaman

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.codec.BmpImage
import feri.com.lesson.BuildConfig
import feri.com.lesson.R
import feri.com.lesson.model.CatatanModel
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.RekamanModel
import feri.com.lesson.model.UserModel
import feri.com.lesson.modul.dialog.CatatanDialog
import feri.com.lesson.modul.dialog.LoadingDialog
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.Helpers
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_detail_rekaman.*
import kotlinx.android.synthetic.main.dialog_yatidak.*
import kotlinx.android.synthetic.main.item_catatan.*
import kotlinx.android.synthetic.main.modal_catatan.*
import kotlinx.android.synthetic.main.modal_catatan.tipe_kegiatan
import kotlinx.android.synthetic.main.modal_opsi_detail_rekaman.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL
import kotlin.coroutines.startCoroutine

class DetailRekamanActivity : AppCompatActivity() {

    private val STORAGE_CODE: Int = 1001
    val firebaseDatabase = DBHelper.getDatabase()
    private var rv_option: FirebaseRecyclerOptions<CatatanModel>? = null
    private var rv_adapter: FirebaseRecyclerAdapter<CatatanModel, DetailRekamanCatatanViewHolder>? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_rekaman)

        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //system OS >= Marshmallow(6.0), check permission is enabled or not
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                //permission was not granted, request it
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, STORAGE_CODE)
            }
        }

        val dataRekaman = intent.getParcelableExtra<RekamanModel>("dataRekaman")
        tv_tittle.text = dataRekaman.judul

        val loadingDialog = LoadingDialog()

        btn_back.setOnClickListener {
            finish()
        }

        loadingDialog.show(supportFragmentManager, "loading")
        firebaseDatabase.getReference(const.KELAS_DB)
            .child(dataRekaman.idPengajar!!)
            .child(dataRekaman.idKelas!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val kelas = p0.getValue(KelasModel::class.java)
                        namaPelajaran.text = kelas?.namaPelajaran
                        namaSekolah.text = kelas?.namaSekolah
                    } else {
                        finish()
                    }
                }

            })

        firebaseDatabase.getReference(const.USER_DB)
            .child(dataRekaman.idPengajar!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    loadingDialog.dismiss()
                    if (p0.exists()) {
                        val user = p0.getValue(UserModel::class.java)
                        namaPengajar.text = user?.nama

                        val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
                            setAudioStreamType(AudioManager.STREAM_MUSIC)
                            try {
                                setDataSource(dataRekaman.suaraUrl)
                                prepare()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        var runnntime = object : Runnable {

                            override fun run() {
                                if (mediaPlayer?.isPlaying!!) {
                                    time_suara.text =
                                        Helpers.longtoTimeFormat(mediaPlayer.currentPosition.toLong())
                                    time_suara.postDelayed(this, 1000)
                                } else {
                                    time_suara.removeCallbacks(this)
                                }
                            }
                        }

                        btn_play.setOnClickListener {
                            mediaPlayer?.start()
                            mediaPlayer?.setOnTimedTextListener { mediaPlayer, timedText ->
                                time_suara.text = timedText.text
                            }
                            time_suara.post(runnntime)
                        }

                        btn_pause.setOnClickListener {
                            mediaPlayer?.pause()
                        }

                        btn_stop.setOnClickListener {
                            mediaPlayer?.stop()
                            mediaPlayer?.prepare()
                            time_suara.text = "00:00:00"
                        }
                    } else {
                        finish()
                    }
                }

            })

        btn_opsi.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.modal_opsi_detail_rekaman)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog.dismiss.setOnClickListener {
                dialog.dismiss()
            }
            dialog.modal_namaPengajar.text = namaPengajar.text
            dialog.modal_namaPelajaran.text = namaPelajaran.text
            dialog.modal_namaSekolah.text = namaSekolah.text

            //dialog.tv_downloadpdf
            dialog.tv_hapus.setOnClickListener {
                dialog_create(dataRekaman!!)
            }

            dialog.tv_downloadpdf.setOnClickListener {
                savePdf(dataRekaman, true)
            }
            dialog.tv_share.setOnClickListener {
                var file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/pdf_obs_${dataRekaman.id}.pdf"
                )
                if (!file.exists()) {
                    savePdf(dataRekaman, true)
                }
                val uri = FileProvider.getUriForFile(this, "feri.com.lesson.fileprovider", file)
                val intent = Intent(Intent.ACTION_SEND)
                if (uri != null) {
                    intent.type = "application/pdf" // For PDF files.
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.putExtra(Intent.EXTRA_SUBJECT, file.name)
                    intent.putExtra(Intent.EXTRA_TEXT, file.name)
                    if (intent.resolveActivity(this.packageManager) != null) {
                        startActivity(Intent.createChooser(intent, "Buka dengan..."))
                    }
                }
            }

            val background = ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.colorBlueLab
                )
            )
            background.alpha = 90
            dialog.window?.setBackgroundDrawable(background)

            dialog.show()
        }
        val query = firebaseDatabase.getReference(const.REKAMAN_DB).child(dataRekaman.idPengamat!!)
            .child(dataRekaman.id!!).child("listCatatan")

        rv_option = FirebaseRecyclerOptions.Builder<CatatanModel>()
            .setQuery(query, CatatanModel::class.java)
            .build()

        rv_adapter = setAdapter()
        rv_adapter?.startListening()
        rv_catatan_detail.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_catatan_detail.adapter = rv_adapter
    }

    private fun savePdf(obj: RekamanModel?, no_view: Boolean) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        val dialog = LoadingDialog()
        dialog.show(supportFragmentManager, "Loading")
        firebaseDatabase.getReference(const.KELAS_DB).child(obj?.idPengajar!!).child(obj.idKelas!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val objKelas = p0.getValue(KelasModel::class.java)
                    Log.d("objKelas", objKelas.toString())
                    firebaseDatabase.getReference(const.USER_DB).child(obj.idPengajar!!)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                p0.toException().printStackTrace()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val objPengajar = p0.getValue(UserModel::class.java)
                                firebaseDatabase.getReference(const.USER_DB)
                                    .child(obj?.idPengamat!!)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {
                                            p0.toException().printStackTrace()
                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            val objPengamat = p0.getValue(UserModel::class.java)
                                            try {
                                                val doc = Document()
                                                var file = File(
                                                    Environment.getExternalStoragePublicDirectory(
                                                        Environment.DIRECTORY_DOWNLOADS
                                                    ),
                                                    "/pdf_obs_${obj?.id}.pdf"
                                                )

                                                if (!file.exists()) {
                                                    file.createNewFile()
                                                }

                                                Log.d("file", file.absolutePath)

                                                PdfWriter.getInstance(
                                                    doc,
                                                    FileOutputStream(file.absoluteFile)
                                                )
                                                doc.open()
                                                doc.add(Paragraph(
                                                    obj?.judul?.toUpperCase(),
                                                    Font(
                                                        Font.FontFamily.TIMES_ROMAN,
                                                        16F,
                                                        Font.BOLD
                                                    )
                                                )
                                                    .apply {
                                                        alignment = Paragraph.ALIGN_CENTER
                                                    })
                                                doc.add(Paragraph("\n"))
                                                doc.add(
                                                    Paragraph(
                                                        "A. Informasi Kelas",
                                                        Font(
                                                            Font.FontFamily.TIMES_ROMAN,
                                                            14F,
                                                            Font.BOLD
                                                        )
                                                    )
                                                )
                                                doc.add(
                                                    Paragraph(
                                                        "1. ID Kelas : ${obj?.idKelas}\n" +
                                                                "2. Nama Sekolah : ${objKelas?.namaSekolah}\n" +
                                                                "3. Nama Kelas : ${objKelas?.nama}\n" +
                                                                "4. Nama Pengajar : ${objPengajar?.nama}\n" +
                                                                "5. Pelajaran : ${objKelas?.namaPelajaran}\n"
                                                        , Font(Font.FontFamily.TIMES_ROMAN, 12F)
                                                    ).apply {
                                                        indentationLeft = 10f
                                                    }
                                                )
                                                doc.add(
                                                    Paragraph(
                                                        "B. Informasi Observasi",
                                                        Font(
                                                            Font.FontFamily.TIMES_ROMAN,
                                                            14F,
                                                            Font.BOLD
                                                        )
                                                    )
                                                )
                                                doc.add(
                                                    Paragraph(
                                                        "1. ID Observasi\t: ${obj?.id}\n" +
                                                                "2. Nama Pengamat\t: ${objPengamat?.nama}\n" +
                                                                "3. Direkam pada\t: ${obj?.tanggal}\n"
                                                        , Font(Font.FontFamily.TIMES_ROMAN, 12F)
                                                    ).apply {
                                                        indentationLeft = 10f
                                                    }
                                                )
                                                doc.add(
                                                    Paragraph(
                                                        "C. Timeline",
                                                        Font(
                                                            Font.FontFamily.TIMES_ROMAN,
                                                            14F,
                                                            Font.BOLD
                                                        )
                                                    )
                                                )
                                                var index = 1
                                                obj?.listCatatan?.forEach {
                                                    doc.add(
                                                        Paragraph(
                                                            "${index}. " +
                                                                    Helpers.longtoTimeFormat(it.waktu!!),
                                                            Font(
                                                                Font.FontFamily.TIMES_ROMAN,
                                                                12F,
                                                                Font.BOLD
                                                            )
                                                        ).apply {
                                                            indentationLeft = 10f
                                                        }
                                                    )

                                                    var strCatatan = "Judul : ${it.judul}\n"
                                                    if (!it.extraUrl.isNullOrEmpty()) {
                                                        if (it.extraUrl!!.contains(".jpg", true)) {
                                                            strCatatan += "Tipe media : Gambar\n" +
                                                                    "Tipe Kegiatan : ${resources.getStringArray(
                                                                        R.array.tipe_kegiatan
                                                                    )
                                                                        .get(it.tipe!!)
                                                                        .toString()}\n" +
                                                                    "Deskripsi : ${it.deskripsi}\n"
                                                            doc.add(Paragraph(
                                                                strCatatan
                                                                ,
                                                                Font(
                                                                    Font.FontFamily.TIMES_ROMAN,
                                                                    12F
                                                                )
                                                            )
                                                                .apply {
                                                                    indentationLeft = 20f
                                                                })
                                                            val bmpImage =
                                                                Helpers.getImage(URL(it.extraUrl))
                                                            doc.add(
                                                                Image.getInstance(bmpImage).apply {
                                                                    indentationLeft = 20f
                                                                    scaleToFit(200f, 200f)
                                                                })
                                                        } else {
                                                            strCatatan += "Tipe media : Video\n" +
                                                                    "Tipe Kegiatan : ${resources.getStringArray(
                                                                        R.array.tipe_kegiatan
                                                                    )
                                                                        .get(it.tipe!!)
                                                                        .toString()}\n" +
                                                                    "Deskripsi : ${it.deskripsi}\n"
                                                            doc.add(Paragraph(
                                                                strCatatan
                                                                ,
                                                                Font(
                                                                    Font.FontFamily.TIMES_ROMAN,
                                                                    12F
                                                                )
                                                            )
                                                                .apply {
                                                                    indentationLeft = 20f
                                                                })
                                                        }
                                                    } else {
                                                        strCatatan += "Tipe Kegiatan : ${resources.getStringArray(
                                                            R.array.tipe_kegiatan
                                                        )
                                                            .get(it.tipe!!).toString()}\n" +
                                                                "Deskripsi : ${it.deskripsi}\n"
                                                        doc.add(Paragraph(
                                                            strCatatan
                                                            ,
                                                            Font(
                                                                Font.FontFamily.TIMES_ROMAN,
                                                                12F
                                                            )
                                                        )
                                                            .apply {
                                                                indentationLeft = 20f
                                                            })
                                                    }

//                if (!index.equals(obj?.listCatatan.size)) {
//                    doc.add(Paragraph("----section----").apply {
//                        indentationLeft = 20f
//                    })
//                }
                                                    index++
                                                }
                                                doc.setMargins(2.5f, 2.5f, 2.5f, 2.5f)
                                                doc.pageSize = PageSize.A4
                                                doc.addAuthor("Feri Setyo Efendi")
                                                doc.close()
                                                val intent = Intent(Intent.ACTION_VIEW)
                                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                val uri = FileProvider.getUriForFile(
                                                    this@DetailRekamanActivity,
                                                    "feri.com.lesson.fileprovider",
                                                    file
                                                )
                                                dialog.dismiss()
                                                if (uri != null) {
                                                    intent.setDataAndType(uri, "application/pdf")
                                                    if (no_view) {
                                                        Toast.makeText(
                                                            this@DetailRekamanActivity,
                                                            "Sukses download pdf\n disimpan pada\n${file.absoluteFile}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        startActivity(
                                                            Intent.createChooser(
                                                                intent,
                                                                "Buka dengan..."
                                                            )
                                                        )
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    this@DetailRekamanActivity,
                                                    e.message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                                e.printStackTrace()
                                            }
                                        }
                                    })
                            }

                        })
                }

            })
    }

    private fun dialog_create(dataRekaman: RekamanModel) {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_yatidak)
        dialog.message.text = "Apakah anda akan menghapus rekaman ini ?"
        dialog.btn_ya.setOnClickListener {
            val dialogx = LoadingDialog()
            dialogx.show(supportFragmentManager, "Loading")
            firebaseDatabase.getReference(const.REKAMAN_DB).child(dataRekaman.idPengamat!!)
                .child(dataRekaman.id!!).removeValue().addOnCompleteListener {
                    Toast.makeText(this, "Rekaman telah dihapus", Toast.LENGTH_LONG).show()
                    firebaseDatabase.getReference(const.REKAMAN_CODE)
                        .child(dataRekaman.kodeRekaman!!).removeValue()
                    dialog.dismiss()
                    dialogx.dismiss()
                    finish()
                }
        }
        dialog.btn_batal.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission from popup was granted, call savePdf() method
                    Toast.makeText(this, "Permission grant...!", Toast.LENGTH_SHORT).show()
                } else {
                    //permission from popup was denied, show error message
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setAdapter(): FirebaseRecyclerAdapter<CatatanModel, DetailRekamanCatatanViewHolder>? =
        object :
            FirebaseRecyclerAdapter<CatatanModel, DetailRekamanCatatanViewHolder>(rv_option!!) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): DetailRekamanCatatanViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_catatan_detail, parent, false)
                return DetailRekamanCatatanViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: DetailRekamanCatatanViewHolder,
                position: Int,
                model: CatatanModel
            ) {
                Log.d("print", model.toString())
                holder.no_urut.text = (position + 1).toString()
                holder.tipe.text =
                    resources.getStringArray(R.array.tipe_kegiatan).get(model.tipe!!).toString()
                holder.waktu.text = Helpers.longtoTimeFormat(model.waktu!!)
                holder.judul.text = model.judul
                holder.lyt.setOnClickListener {
                    val dialog = CatatanDialog(model)
                    dialog.show(supportFragmentManager, "Catatan")
                }
            }

        }

    override fun onPause() {
        super.onPause()
        rv_adapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        rv_adapter?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        rv_adapter?.stopListening()
    }

}
