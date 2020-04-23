package feri.com.lesson.modul.rekaman

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import feri.com.lesson.R
import feri.com.lesson.model.CatatanModel
import feri.com.lesson.util.Helpers
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_catatan.*
import kotlinx.android.synthetic.main.dialog_tambahfotovideo.view.*
import java.io.File
import java.io.IOException


class CatatanActivity : AppCompatActivity(), View.OnClickListener {

    private var time_milis: Long? = 0
    var currentpath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catatan)

        supportActionBar?.hide()

        ActivityCompat.requestPermissions(
            this,
            const.permissions_catatan,
            const.REQUEST_PERMISSION_CATATAN
        )

        time_milis = intent.getLongExtra("waktu", 0)
        waktu.text = Helpers.longtoTimeFormat(time_milis!!)

        val adapter = ArrayAdapter<String>(
            this,
            R.layout.spinner_text,
            resources.getStringArray(R.array.tipe_kegiatan)
        )
        spinnerKegiatan.adapter = adapter


        btn_tambah.setOnClickListener(this)
        btn_batal.setOnClickListener(this)
        btn_tambahfoto.setOnClickListener(this)
        pv_video.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_tambah -> {
                proses_tambah_catatan()
            }
            R.id.btn_batal -> {
                finish()
            }
            R.id.btn_tambahfoto -> {
                buat_dialog()
            }
            R.id.pv_video -> {
                when (pv_video.isPlaying) {
                    false -> pv_video.start()
                    true -> {
                        pv_video.stopPlayback()
                        pv_video.resume()
                    }
                }
            }
        }
    }

    private fun buat_dialog() {
        var dialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_tambahfotovideo, null)
        bottomSheet.btn_foto.setOnClickListener {
            dispatchTakePictureIntent()
            dialog.dismiss()
        }
        bottomSheet.btn_video.setOnClickListener {
            dispatchTakeVideoIntent()
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    private fun proses_tambah_catatan() {
        if (fieldisError()) {
            return
        }
        var returnintent = Intent()
        returnintent.putExtra(
            "dataCatatan", CatatanModel(
                judulCatatan.text.toString().trim(),
                spinnerKegiatan.selectedItemPosition,
                deskripsiCatatan.text.toString().trim(),
                this.currentpath,
                time_milis
            )
        )
        setResult(Activity.RESULT_OK, returnintent)
        finish()
    }

    private fun fieldisError(): Boolean {
        if (spinnerKegiatan.selectedItemPosition == 0) {
            return true
        }
        if (judulCatatan.text.isNullOrEmpty()) {
            return true
        }
        if (deskripsiCatatan.text.isNullOrEmpty()) {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionToRecordAccepted = if (requestCode == const.REQUEST_PERMISSION_CATATAN) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) {
            Toast.makeText(this, "permission required!!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createFile("gambar", ".jpg")
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val fileUri = FileProvider.getUriForFile(
                        this,
                        "feri.com.lesson.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                    startActivityForResult(takePictureIntent, const.REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!resultCode.equals(Activity.RESULT_CANCELED)) {
            extralayout.visibility = View.VISIBLE
            if (requestCode.equals(const.REQUEST_IMAGE_CAPTURE)) {
                val myBitmap = BitmapFactory.decodeFile(currentpath)
                pv_foto.setImageBitmap(myBitmap)
                pv_foto.rotation = -90f
                pv_video.visibility = View.GONE
                pv_foto.visibility = View.VISIBLE
            } else if (requestCode.equals(const.REQUEST_VIDEO_CAPTURE)) {
                pv_video.setVideoPath(currentpath)
                pv_video.start()
                pv_foto.visibility = View.GONE
                pv_video.visibility = View.VISIBLE
            }
        }
    }

    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                val videoFile: File? = try {
                    createFile("video", ".mp4")
                } catch (ex: IOException) {
                    null
                }
                videoFile?.also {
                    val fileUri = FileProvider.getUriForFile(
                        this,
                        "feri.com.lesson.fileprovider",
                        it
                    )
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
                    startActivityForResult(takeVideoIntent, const.REQUEST_VIDEO_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createFile(prefix: String, ext: String): File {
        // Create an image file name
        return File.createTempFile(
            "${prefix}_${System.currentTimeMillis()}", /* prefix */
            "${ext}", /* suffix */
            externalCacheDir /* directory */
        ).apply {
            currentpath = absolutePath
        }
    }
}
