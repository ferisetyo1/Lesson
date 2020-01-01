package feri.com.lesson.modul.rekaman

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.R
import feri.com.lesson.model.CatatanModel
import feri.com.lesson.util.Helpers
import kotlinx.android.synthetic.main.item_catatan.view.*

class RekamanCatatanAdapter(val context: Context, var list: ArrayList<CatatanModel>) :
    RecyclerView.Adapter<RekamanCatatanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(get: CatatanModel,position: Int) {
            itemView.tipe_kegiatan.text =
                itemView.resources.getStringArray(R.array.tipe_kegiatan).get(get.tipe!!)
            itemView.urutan.text=(position+1).toString()
            itemView.time.text=Helpers.longtoDate(get.waktu!!)
            itemView.judul_catatan.text=get.judul
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_catatan,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position),position)
    }

    fun add(catatan:CatatanModel){
        list.add(catatan)
        notifyDataSetChanged()
    }

}