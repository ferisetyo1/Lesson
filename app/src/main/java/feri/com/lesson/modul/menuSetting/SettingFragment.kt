package feri.com.lesson.modul.menuSetting


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import feri.com.lesson.MainActivity

import feri.com.lesson.R
import feri.com.lesson.modul.authentication.LoginActivity
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_akun.setOnClickListener {
            (activity as MainActivity).getNav().selectedItemId = R.id.akunFragment
        }
        tv_kelas.setOnClickListener {
            (activity as MainActivity).getNav().selectedItemId = R.id.daftarKelasFragment
        }
        tv_rekaman.setOnClickListener {
            (activity as MainActivity).getNav().selectedItemId = R.id.daftarRekamanFragment
        }
        tv_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
    }
}
