package feri.com.lesson.modul.authentication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import feri.com.lesson.MainActivity
import feri.com.lesson.R
import feri.com.lesson.model.UserModel
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        //firebase init
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        db_reff = firebaseDatabase.getReference(const.USER_DB)

        //listener
        btn_register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_register -> {
                proses_register()
            }
            R.id.btn_login -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun proses_register() {
        if (FieldisError()) {
            return
        }

        val builderdialog = AlertDialog.Builder(this)
        builderdialog.setCancelable(false)
        builderdialog.setView(R.layout.progress)
        val dialog = builderdialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        firebaseAuth.createUserWithEmailAndPassword(
            et_email.text?.trim().toString(),
            et_password.text?.trim().toString()
        ).addOnCompleteListener {
            dialog.dismiss()
            if (it.isSuccessful) {
                var curUser = firebaseAuth.currentUser
                db_reff.child(curUser?.uid.toString()).setValue(
                    UserModel(
                        curUser?.uid.toString(),
                        et_nama.text.trim().toString().trim().toLowerCase(),
                        et_email.text.toString().trim().toLowerCase(),
                        et_instansi.text.toString().trim().toLowerCase(),
                        ""
                    )
                ).addOnCompleteListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this,it.exception?.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            dialog.dismiss()
            if (it is FirebaseAuthException) print(it.errorCode)
            Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }

    fun FieldisError(): Boolean {
        if (et_nama.text.isNullOrEmpty()) {
            et_nama.setError(resources.getString(R.string.errorFieldKosong))
            et_nama.requestFocus()
            return true
        }

        if (et_email.text.isNullOrEmpty()) {
            et_email.setError(resources.getString(R.string.errorFieldKosong))
            et_email.requestFocus()
            return true
        }

        if (et_password.text.isNullOrEmpty()) {
            et_password.setError(resources.getString(R.string.errorFieldKosong))
            et_password.requestFocus()
            return true
        }

        if (et_konPassword.text.isNullOrEmpty()) {
            et_konPassword.setError(resources.getString(R.string.errorFieldKosong))
            et_konPassword.requestFocus()
            return true
        }

        if (et_password.text!!.equals(et_konPassword.text)){
            et_konPassword.setError(resources.getString(R.string.errorPaswordTidaksama))
            et_konPassword.requestFocus()
            return true
        }
        return false
    }
}
