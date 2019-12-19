package feri.com.lesson.modul.kelas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.R
import feri.com.lesson.model.GroupModel
import kotlinx.android.synthetic.main.item_model_classroom.view.*

class DenahBangkuAdapter(val context: Context?, var list: ArrayList<GroupModel>) :
    RecyclerView.Adapter<DenahBangkuAdapter.ViewHolder>() {
    var prev_object = TextView(context)
    var prev_position=-1
    var prev_dataposition=-1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var namaGroup = itemView.namaGroup
        var bangku1 = itemView.bangku1
        var bangku2 = itemView.bangku2
        fun bind(gm: GroupModel) {
            namaGroup.text = gm.id
            bangku1.text = gm.listMurid.get(0).toString()
            if (gm.listMurid.size==1) {
                bangku2.visibility = View.GONE
            } else {
                bangku2.text = gm.listMurid.get(1).toString()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_model_classroom,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
        holder.bangku1.setOnClickListener {
            onClick(holder.bangku1,position,0)
        }
        if (holder.bangku2.visibility==View.VISIBLE){
            holder.bangku2.setOnClickListener {
                onClick(holder.bangku2,position,1)
            }
        }
    }

    private fun onClick(textView: TextView,p0:Int,p1:Int) {
        if (prev_object.text.isNullOrEmpty()) {
            prev_object = textView
            prev_position=p0
            prev_dataposition=p1

        } else {
            var s=prev_object.text.toString()
            list.get(p0).listMurid.set(p1,s.toInt())
            list.get(prev_position).listMurid.set(prev_dataposition,textView.text.toString().toInt())
            prev_object.text=textView.text
            textView.text=s
            prev_object=TextView(context)
        }
    }

}