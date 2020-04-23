package feri.com.lesson.modul.detailRekaman

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.model.CatatanModel
import kotlinx.android.synthetic.main.item_catatan_detail.view.*

class DetailRekamanCatatanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var judul = itemView.judul
    var no_urut = itemView.nomor
    var tipe = itemView.tipe
    var waktu = itemView.waktu
    var lyt = itemView.lyt_container_c
}