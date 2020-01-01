package feri.com.lesson.modul.kelas

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.R
import feri.com.lesson.model.GroupModel
import kotlinx.android.synthetic.main.item_model_classroom.view.*
import kotlinx.android.synthetic.main.item_model_group_v1.view.*

class DenahKelasAdapter(val context: Context?, var list: ArrayList<GroupModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var prev_object = TextView(context)
    var prev_position = -1
    var prev_dataposition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bangku1 = itemView.classroom_bangku1
        var bangku2 = itemView.classroom_bangku2
        fun bind(gm: GroupModel) {
            bangku1.text = gm.anggota.get(0).toString()
            if (gm.anggota.size == 1) {
                bangku2.visibility = View.GONE
            } else {
                bangku2.text = gm.anggota.get(1).toString()
            }

        }

    }

    class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bangku1 = itemView.groupv1_bangku1
        var bangku2 = itemView.groupv1_bangku2
        var bangku3 = itemView.groupv1_bangku3
        var bangku4 = itemView.groupv1_bangku4
        fun bind(gm: GroupModel) {
            bangku1.text = gm.anggota.get(0).toString()
            when (gm.anggota.size) {
                1 -> {
                    bangku2.visibility = View.INVISIBLE
                    bangku3.visibility = View.INVISIBLE
                    bangku4.visibility = View.INVISIBLE
                    Log.d("test1",gm.toString())
                }
                2 -> {
                    bangku2.text = gm.anggota.get(1).toString()
                    bangku3.visibility = View.INVISIBLE
                    bangku4.visibility = View.INVISIBLE
                    Log.d("test2",gm.toString())
                }
                3 -> {
                    bangku2.text = gm.anggota.get(1).toString()
                    bangku3.text = gm.anggota.get(2).toString()
                    bangku4.visibility = View.INVISIBLE
                    Log.d("test3",gm.toString())
                }
                4 -> {
                    bangku2.text = gm.anggota.get(1).toString()
                    bangku3.text = gm.anggota.get(2).toString()
                    bangku4.text = gm.anggota.get(3).toString()
                    Log.d("test4",gm.toString())
                }
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return list.get(position).tipeGroup!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return ViewHolder2(
                LayoutInflater.from(context).inflate(
                    R.layout.item_model_group_v1,
                    parent,
                    false
                )
            )
        }
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (list.get(position).tipeGroup) {

            1 -> {
                val clause = position % 3 == 0
                var vh = holder as ViewHolder2
                vh.bind(list.get(position))
                vh.bangku1.setOnClickListener {
                    onClick(vh.bangku1, position, 0)
                }
                if (vh.bangku2.visibility == View.VISIBLE) {
                    vh.bangku2.setOnClickListener {
                        onClick(vh.bangku2, position, 1)
                    }
                }
                if (vh.bangku3.visibility == View.VISIBLE) {
                    vh.bangku3.setOnClickListener {
                        onClick(vh.bangku3, position, 2)
                    }
                }
                if (vh.bangku4.visibility == View.VISIBLE) {
                    vh.bangku4.setOnClickListener {
                        onClick(vh.bangku4, position, 3)
                    }
                }
            }

            2 -> {
                var vh = holder as ViewHolder
                vh.bind(list.get(position))
                vh.bangku1.setOnClickListener {
                    onClick(vh.bangku1, position, 0)
                }
                if (vh.bangku2.visibility == View.VISIBLE) {
                    vh.bangku2.setOnClickListener {
                        onClick(vh.bangku2, position, 1)
                    }
                }
            }
        }

    }


    private fun onClick(textView: TextView, p0: Int, p1: Int) {
        if (prev_object.text.isNullOrEmpty()) {
            prev_object = textView
            prev_position = p0
            prev_dataposition = p1

        } else {
            var s = prev_object.text.toString()
            list.get(p0).anggota.set(p1, s.toInt())
            list.get(prev_position).anggota.set(
                prev_dataposition,
                textView.text.toString().toInt()
            )
            prev_object.text = textView.text
            textView.text = s
            prev_object = TextView(context)
        }
    }

}