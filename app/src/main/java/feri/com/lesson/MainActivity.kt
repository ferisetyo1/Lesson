package feri.com.lesson

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import feri.com.lesson.modul.authentication.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val nav_controller = findNavController(R.id.nav_host)
        nav_view.setupWithNavController(nav_controller)

        val specialSelect = intent.getIntExtra("specialSelect", -1)
        if (specialSelect != -1) {
            nav_view.selectedItemId = specialSelect
        }
    }

    fun getNav(): BottomNavigationView = nav_view
}
