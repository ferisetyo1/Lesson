package feri.com.lesson.util

import android.Manifest

object const{
    val fileRekaman="file_rekaman"
    val CLASS_CODE="all_class_code"
    val GROUP_DB="list_group"
    val SISWA_DB="list_siswa"
    val USER_DB="user"
    val KELAS_DB="kelas"
    val REKAMAN_DB="rekaman"
    val REQUEST_RECORD_AUDIO_PERMISSION = 200
    val REQUEST_CODE_CATATAN = 201
    val REQUEST_VIDEO_CAPTURE = 202
    val REQUEST_IMAGE_CAPTURE = 203
    val REQUEST_PERMISSION_CATATAN = 204
    val permissions_catatan: Array<String> = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    )
}