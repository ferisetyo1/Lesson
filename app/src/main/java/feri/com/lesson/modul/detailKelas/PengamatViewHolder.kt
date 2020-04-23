package feri.com.lesson.modul.detailKelas

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_pengamat_kelas.view.*

class PengamatViewHolder(vitem: View) : RecyclerView.ViewHolder(vitem) {
    var nomor = vitem.nomor
    var nama_user = vitem.nama_user
    var tanggal = vitem.tanggal
}