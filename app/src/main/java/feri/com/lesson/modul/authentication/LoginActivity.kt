package feri.com.lesson.modul.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import feri.com.lesson.MainActivity
import feri.com.lesson.R
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
        firebaseDatabase = FirebaseDatabase.getInstance()
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

        firebaseAuth.signInWithEmailAndPassword(
            et_email.text?.trim().toString(),
            et_password.text?.trim().toString()
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }.addOnFailureListener {
            if (it is FirebaseAuthException){
                if (it.errorCode.equals("ERROR_USER_NOT_FOUND")) {
                    SBCustom.getSnackbar(loginlayout,resources.getText(R.string.errorUserNotFound).toString()).show()
                    return@addOnFailureListener
                } else if (it.errorCode.equals("ERROR_WRONG_PASSWORD")) {
                    SBCustom.getSnackbar(loginlayout,resources.getText(R.string.errorPasswordSalah).toString()).show()
                    return@addOnFailureListener
                } else {
                    SBCustom.getSnackbar(loginlayout,it.localizedMessage.toString()).show()
                    return@addOnFailureListener
                }
            }else{
                SBCustom.getSnackbar(loginlayout,it.localizedMessage.toString()).show()
                return@addOnFailureListener
            }
        }
    }

}
