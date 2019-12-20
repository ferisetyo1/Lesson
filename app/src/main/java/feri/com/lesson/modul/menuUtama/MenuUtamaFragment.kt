package feri.com.lesson.modul.menuUtama


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import feri.com.lesson.R
import feri.com.lesson.modul.rekaman.JoinKelasActivity
import kotlinx.android.synthetic.main.fragment_menu_utama.*

/**
 * A simple [Fragment] subclass.
 */
class MenuUtamaFragment : Fragment(), View.OnClickListener{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_utama, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_mulairekamhome.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_mulairekamhome->{
                startActivity(Intent(activity,JoinKelasActivity::class.java))
            }
        }
    }


}
