package feri.com.lesson.modul.menuDaftarKelas


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import feri.com.lesson.R

/**
 * A simple [Fragment] subclass.
 */
class DaftarKelasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daftar_kelas, container, false)
    }


}
