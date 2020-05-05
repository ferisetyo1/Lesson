package feri.com.lesson.modul.menuAkun

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import feri.com.lesson.R
import feri.com.lesson.model.UserModel
import feri.com.lesson.modul.dialog.LoadingDialog
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.KeyGen
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_edit_profil.*

class EditProfilActivity : AppCompatActivity() {

    var selectedImgUri: Uri? = null
    var firebaseStorage = FirebaseStorage.getInstance()
    var firebaseDatabase = DBHelper.getDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        supportActionBar?.hide()

        val user = intent.getParcelableExtra<UserModel>("dataUser")
        Log.d("dataUser", user.toString())

        nama_user.setText(user.nama)
        instansiUser.setText(user.instansi)
        email_user.setText(user.email)
        ganti_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            if (lyt_password.visibility == View.VISIBLE) {
                lyt_password.visibility = View.GONE
            } else {
                lyt_password.visibility = View.VISIBLE
            }
        }

        btn_simpan.setOnClickListener {
            if (FieldisError()) {
                return@setOnClickListener
            }
            var newDataUser = user
            newDataUser.nama = nama_user.text.toString().trim().toLowerCase()
            newDataUser.instansi = instansiUser.text.toString().trim().toLowerCase()

            val dialog = LoadingDialog()
            dialog.show(supportFragmentManager, "loading")
            if (selectedImgUri == null) {
                saveProfil(dialog, newDataUser, user)
            } else {
                var storereff = firebaseStorage
                    .getReference(const.fileRekaman)
                    .child(user?.id!!)
                    .child(
                        KeyGen.create("fotoprofil", "_", 10) + MimeTypeMap.getFileExtensionFromUrl(
                            contentResolver.getType(selectedImgUri!!)
                        )
                    )
                storereff.putFile(selectedImgUri!!).addOnSuccessListener {
                    storereff.downloadUrl.addOnCompleteListener {
                        val uri = it.result
                        newDataUser.fotoProfil = uri.toString()
                        saveProfil(dialog, newDataUser, user)

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

    private fun saveProfil(
        dialog: LoadingDialog,
        newDataUser: UserModel?,
        user: UserModel
    ) {
        firebaseDatabase.getReference(const.USER_DB).child(user?.id!!)
            .setValue(newDataUser).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (lyt_password.visibility == View.VISIBLE) {
                        var currUser = FirebaseAuth
                            .getInstance()
                            .currentUser
                        val credential =
                            EmailAuthProvider.getCredential(
                                currUser!!.email!!,
                                password_user_lama.text.toString()
                            )
                        currUser?.reauthenticate(credential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                currUser.updatePassword(konPassword.text.toString())
                                    .addOnCompleteListener {
                                        dialog.dismiss()
                                        if (it.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Berhasil mengubah profil dan password",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                it.exception?.localizedMessage,
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }
                                    }
                            } else {
                                dialog.dismiss()
                                Toast.makeText(
                                    this,
                                    it.exception?.localizedMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }


                    } else {
                        Toast.makeText(
                            this,
                            "Berhasil mengubah profil",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                } else {
                    dialog.dismiss()
                    Toast.makeText(
                        this,
                        it.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
        if (password_user_lama.text.isNullOrEmpty() && lyt_password.visibility == View.VISIBLE) {
            password_user.error = getString(R.string.errorFieldKosong)
            password_user.requestFocus()
            return true
        }
        if (password_user.text.isNullOrEmpty() && lyt_password.visibility == View.VISIBLE) {
            password_user.error = getString(R.string.errorFieldKosong)
            password_user.requestFocus()
            return true
        }
        if (konPassword.text.isNullOrEmpty() && lyt_password.visibility == View.VISIBLE) {
            konPassword.error = getString(R.string.errorFieldKosong)
            konPassword.requestFocus()
            return true
        }

        if (password_user.text.equals(konPassword.text) && lyt_password.visibility == View.VISIBLE) {
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
