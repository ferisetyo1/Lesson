package feri.com.lesson.modul.menuDaftarKelas

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import kotlinx.android.synthetic.main.item_daftar_kelas.view.*

class DaftarKelasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var namaKelas = itemView.namaKelas
    var namaPelajaran = itemView.namaPelajaran
    var namaSekolah = itemView.namaSekolah
    var btn_opsi = itemView.btn_opsi

    fun bind(get: KelasModel) {
        Log.d("print", get.toString())
        namaKelas.text = get.nama
        namaPelajaran.text = get.namaPelajaran
        namaSekolah.text = get.namaSekolah
    }

}