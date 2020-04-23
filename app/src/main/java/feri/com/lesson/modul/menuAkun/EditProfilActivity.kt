package feri.com.lesson.modul.menuAkun

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import feri.com.lesson.R
import feri.com.lesson.model.UserModel
import feri.com.lesson.util.KeyGen
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_edit_profil.*
import java.lang.reflect.Field

class EditProfilActivity : AppCompatActivity() {

    var selectedImgUri: Uri? = null
    var firebaseStorage = FirebaseStorage.getInstance()
    var firebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        supportActionBar?.hide()

        val user = intent.getParcelableExtra<UserModel>("dataUser")
        Log.d("dataUser", user.toString())

        nama_user.setText(user.nama)
        instansiUser.setText(user.instansi)
        email_user.setText(user.email)

        btn_simpan.setOnClickListener {
            if (FieldisError()) {
                return@setOnClickListener
            }
            var newDataUser = user
            newDataUser.nama = nama_user.text.toString().trim().toLowerCase()
            newDataUser.instansi = instansiUser.text.toString().trim().toLowerCase()

            var storereff = firebaseStorage
                .getReference(const.fileRekaman)
                .child(user?.id!!)
                .child(
                    KeyGen.create("fotoprofil", "_", 10) + MimeTypeMap.getFileExtensionFromUrl(
                        contentResolver.getType(selectedImgUri!!)
                    )
                )
            val builderdialog = AlertDialog.Builder(this)
            builderdialog.setCancelable(false)
            builderdialog.setView(R.layout.progress)
            val dialog = builderdialog.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.show()
            storereff.putFile(selectedImgUri!!).addOnSuccessListener {
                storereff.downloadUrl.addOnCompleteListener {
                    val uri = it.result
                    newDataUser.fotoProfil = uri.toString()
                    firebaseDatabase.getReference(const.USER_DB).child(user?.id!!)
                        .setValue(newDataUser).addOnCompleteListener {
                            if(it.isSuccessful){
                                if (!password_user.text.equals("loveumasyta")) {
                                    FirebaseAuth
                                        .getInstance()
                                        .currentUser
                                        ?.updatePassword(konPassword.text.toString())
                                        ?.addOnCompleteListener {
                                            dialog.dismiss()
                                            if (it.isSuccessful) {
                                                finish()
                                                Toast.makeText(
                                                    this,
                                                    "Sukses mengubah akun",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    it.exception.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            }else{
                                dialog.dismiss()
                                Toast.makeText(
                                    this,
                                    it.exception.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }
        }

        new_photo.setOnClickListener {
            startActivityForResult(
                Intent.createChooser(
                    Intent()
                        .setType("image/*")
                        .setAction(Intent.ACTION_GET_CONTENT),
                    "Pilih foto"
                ),
                const.REQUEST_OPEN_GALLERY
            )
        }

        btn_back.setOnClickListener {
            finish()
        }

    }

    private fun FieldisError(): Boolean {
        if (nama_user.text.isNullOrEmpty()) {
            nama_user.error = getString(R.string.errorFieldKosong)
            nama_user.requestFocus()
            return true
        }

        if (instansiUser.text.isNullOrEmpty()) {
            instansiUser.error = getString(R.string.errorFieldKosong)
            instansiUser.requestFocus()
            return true
        }
        if (password_user.text.isNullOrEmpty()) {
            password_user.error = getString(R.string.errorFieldKosong)
            password_user.requestFocus()
            return true
        }
        if (konPassword.text.isNullOrEmpty()) {
            konPassword.error = getString(R.string.errorFieldKosong)
            konPassword.requestFocus()
            return true
        }

        if (password_user.text.equals(konPassword.text)) {
            konPassword.error = "password tidak sama"
            konPassword.requestFocus()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == const.REQUEST_OPEN_GALLERY) {
            selectedImgUri = data?.data
            new_photo.setImageURI(selectedImgUri)
            new_photo.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}
