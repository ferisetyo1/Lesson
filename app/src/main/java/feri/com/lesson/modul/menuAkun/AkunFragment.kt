package feri.com.lesson.modul.menuAkun

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

import feri.com.lesson.R
import feri.com.lesson.modul.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_akun.*

class AkunFragment : Fragment(), View.OnClickListener {

    private var root: View? = null
    private var firebaseAuth=FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_akun, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_logout.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_logout->{
                firebaseAuth.signOut()
                startActivity(Intent(activity,LoginActivity::class.java))
                activity?.finish()
            }
        }
    }
}
