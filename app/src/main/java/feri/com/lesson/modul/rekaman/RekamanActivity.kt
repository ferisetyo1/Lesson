package feri.com.lesson.modul.rekaman

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import feri.com.lesson.R
import feri.com.lesson.model.CatatanModel
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.RekamanModel
import feri.com.lesson.util.Helpers
import feri.com.lesson.util.KeyGen
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_rekaman.*
import kotlinx.android.synthetic.main.dialog_selesaiobservasi.*
import java.io.File
import java.io.IOException

class RekamanActivity : AppCompatActivity(), View.OnClickListener {

    private var fileName: String = ""
    private var startRecording: Boolean? = true
    private var recorder: MediaRecorder? = null
    private lateinit var runnable: Runnable
    private lateinit var myhandler: Handler
    private lateinit var list_catatan: ArrayList<CatatanModel>
    private lateinit var dataKelas: KelasModel
    private lateinit var dataRekaman: RekamanModel

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rekaman)

        supportActionBar?.hide()

        ActivityCompat.requestPermissions(this, permissions, const.REQUEST_RECORD_AUDIO_PERMISSION)

        dataKelas = intent.getParcelableExtra("dataKelas") as KelasModel
        dataRekaman = intent.getParcelableExtra("dataRekaman") as RekamanModel

        val file = File(
            externalCacheDir,
            "audio_${System.currentTimeMillis()}.3gp"
        )
        fileName = file.absolutePath

        list_catatan = ArrayList()
        rv_catatan.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = RekamanCatatanAdapter(context, list_catatan)
        }

        myhandler = Handler()

        judulRekaman.text = dataRekaman.judul
        chronometer.setText("00:00:00")
        chronometer.setOnChronometerTickListener {
            it.text = Helpers.longtoTimeFormat(SystemClock.elapsedRealtime() - it.base)
        }
        btn_create_catatan.setOnClickListener(this)
        btn_record.setOnClickListener(this)
        btn_selesai.setOnClickListener(this)
        btn_create_catatan.isEnabled = false
        btn_selesai.isEnabled = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == const.REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) {
            Toast.makeText(this, "permission required!!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === const.REQUEST_CODE_CATATAN && resultCode === Activity.RESULT_OK) {
            val newCatatan = data?.getParcelableExtra("dataCatatan") as CatatanModel
            rv_catatan.apply {
                (adapter as RekamanCatatanAdapter)?.add(newCatatan)
            }
            Log.d("size", list_catatan.size.toString())
        }
    }

    var pausedtime: Long = 0;
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_record -> {
                onRecord()
                when (startRecording) {
                    true -> {
                        chronometer!!.base = SystemClock.elapsedRealtime() + pausedtime
                        chronometer!!.start()
                        //set btn_record berhenti
                        btn_create_catatan.isEnabled = true
                        btn_selesai.isEnabled = true
                        btn_record.background=ContextCompat.getDrawable(this,R.drawable.btn_circle_green)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            btn_record.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_pause_black_24dp
                                )
                            )
                        } else {
                            btn_record.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_stop_black_24dp
                                )
                            )
                        }
                    }
                    false -> {
                        chronometer!!.stop()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            pausedtime = chronometer.base - SystemClock.elapsedRealtime()
                            recorder?.pause()
                            btn_record.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_play_arrow_black_24dp
                                )
                            )
                            btn_record.background=ContextCompat.getDrawable(this,R.drawable.btn_circle_yellow)
                        }else{
                            myhandler.removeCallbacks(runnable)
                            stopRecording()
                            btn_record.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_fiber_manual_record_black_24dp
                                )
                            )
                            btn_record.background=ContextCompat.getDrawable(this,R.drawable.btn_circle_red)
                        }
                    }
                }
                startRecording = !startRecording!!
            }
            R.id.btn_create_catatan -> {
                transferDataWaktu()
            }
            R.id.btn_selesai -> {
                dialog_create()
            }
        }
    }

    private fun transferDataWaktu() {
        var time = SystemClock.elapsedRealtime() - chronometer!!.base
        var intent = Intent(this, CatatanActivity::class.java)
        intent.putExtra("waktu", time)
        startActivityForResult(intent, const.REQUEST_CODE_CATATAN)
    }

    private fun dialog_create() {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_selesaiobservasi)
        dialog.btn_ya.setOnClickListener {
            sendDataRekamantoServer()
            dialog.dismiss()
        }
        dialog.btn_batal.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun sendDataRekamantoServer() {
        dataRekaman.suaraUrl = fileName
        dataRekaman.kodeRekaman = KeyGen.create("", "", 6)
        dataRekaman.listCatatan = list_catatan
        startActivity(
            Intent(this, BerhasilRekamActivity::class.java)
                .putExtra("dataKelas", dataKelas)
                .putExtra("dataRekaman", dataRekaman)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }

    private fun onRecord() {
        if (pausedtime == 0L) {
            runnable = object : Runnable {
                override fun run() {
                    startRecording()
                }
            }
            myhandler.postDelayed(runnable, 1000)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder?.resume()
            }
        }
    }

    private fun startRecording() {

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("print failed record", "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder?.release()
        recorder = null
        if (runnable != null) {
            myhandler.removeCallbacks(runnable)
        }
    }
}
