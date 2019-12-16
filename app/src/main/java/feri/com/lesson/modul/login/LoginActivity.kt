package feri.com.lesson.modul.login

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import feri.com.lesson.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        db_reff = firebaseDatabase.getReference("akun")
        //listener
        btn_login.setOnClickListener(this)
    }

    fun FieldisError(): Boolean {
        if (et_email.text.isNullOrEmpty()) {
            et_email.setError(resources.getString(R.string.errorFieldKosong))
            return false
        }

        if (et_password.text.isNullOrEmpty()) {
            et_password.setError(resources.getString(R.string.errorFieldKosong))
            return false
        }
        return true
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_login -> {
                proses_login()
            }
        }
    }

    private fun proses_login() {
        if (!FieldisError()) {
            return
        }

        firebaseAuth.signInWithEmailAndPassword(
            et_email.text?.trim().toString(),
            et_password.text?.trim().toString()
        ).addOnCompleteListener(OnCompleteListener {

        }).addOnFailureListener(OnFailureListener {
            if ((it as FirebaseAuthException).errorCode.equals("ERROR_USER_NOT_FOUND")){
                Snackbar.make(loginlayout,R.string.errorUserNotFound,Snackbar.LENGTH_LONG)
                    .show()
            }
        })
    }
}
