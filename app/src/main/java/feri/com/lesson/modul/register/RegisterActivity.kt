package feri.com.lesson.modul.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import feri.com.lesson.MainActivity
import feri.com.lesson.R
import feri.com.lesson.model.UserModel
import feri.com.lesson.modul.login.LoginActivity
import feri.com.lesson.modul.util.const
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
        tv_loginlink.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_register -> {
                proses_register()
            }
            R.id.tv_loginlink -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun proses_register() {
        if (FieldisError()) {
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(
            et_email.text?.trim().toString(),
            et_password.text?.trim().toString()
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                var curUser = firebaseAuth.currentUser
                db_reff.child(curUser?.uid.toString()).setValue(
                    UserModel(
                        curUser?.uid.toString(),
                        et_nama.text?.trim().toString().toLowerCase(),
                        et_email.text?.trim().toString().toLowerCase(),
                        "",
                        ""
                    )
                ).addOnCompleteListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Snackbar.make(regLayout, it.localizedMessage.toString(), Snackbar.LENGTH_LONG)
                        .show()
                }
            } else {
                Snackbar.make(regLayout, it.exception?.localizedMessage.toString(), Snackbar.LENGTH_LONG)
                    .show()
            }
        }.addOnFailureListener {
            Snackbar.make(regLayout, it.message.toString(), Snackbar.LENGTH_LONG)
                .show()
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
