package feri.com.lesson.modul.menuDaftarKelas

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.model.KelasModel
import kotlinx.android.synthetic.main.item_daftar_kelas.view.*

class DaftarKelasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var namaKelas = itemView.namaKelas
    var namaPelajaran = itemView.tanggalObservasi
    var namaSekolah = itemView.namaSekolah
    var btn_opsi = itemView.btn_opsi

    fun bind(get: KelasModel) {
        Log.d("print", get.toString())
        namaKelas.text = get.nama
        namaPelajaran.text = get.namaPelajaran
        namaSekolah.text = get.namaSekolah
    }

}