package feri.com.lesson.modul.authentication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import feri.com.lesson.MainActivity
import feri.com.lesson.R
import feri.com.lesson.modul.dialog.LoadingDialog
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.SBCustom
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        //firebase init
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = DBHelper.getDatabase()
        db_reff = firebaseDatabase.getReference(const.USER_DB)

        //listener
        btn_login.setOnClickListener(this)
        tv_reglink.setOnClickListener(this)
    }

    fun FieldisError(): Boolean {
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
        return false
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_login -> {
                proses_login()
            }
            R.id.tv_reglink -> {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
        }
    }

    private fun proses_login() {

        if (FieldisError()) {
            return
        }

        val dialog = LoadingDialog()
        dialog.show(supportFragmentManager, "Loading")

        firebaseAuth.signInWithEmailAndPassword(
            et_email.text?.trim().toString(),
            et_password.text?.trim().toString()
        ).addOnCompleteListener {
            dialog.dismiss()
            if (it.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }.addOnFailureListener {
            dialog.dismiss()
            if (it is FirebaseAuthException) {
                if (it.errorCode.equals("ERROR_USER_NOT_FOUND")) {
                    Toast.makeText(
                        this,
                        resources.getText(R.string.errorUserNotFound).toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnFailureListener
                } else if (it.errorCode.equals("ERROR_WRONG_PASSWORD")) {
                    Toast.makeText(
                        this,
                        resources.getText(R.string.errorPasswordSalah).toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnFailureListener
                } else {
                    Toast.makeText(this, it.localizedMessage?.toString(), Toast.LENGTH_LONG).show()
                    return@addOnFailureListener
                }
            } else {
                Toast.makeText(this, it.localizedMessage?.toString(), Toast.LENGTH_LONG).show()
                return@addOnFailureListener
            }
        }
    }

}
