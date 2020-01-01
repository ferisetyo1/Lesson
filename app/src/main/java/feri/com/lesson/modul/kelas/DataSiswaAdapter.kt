package feri.com.lesson.modul.kelas

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.R
import feri.com.lesson.model.SiswaModel
import kotlinx.android.synthetic.main.item_data_siswa.view.*

class DataSiswaAdapter(val context: Context?, var listSiswa: ArrayList<SiswaModel>) :
    RecyclerView.Adapter<DataSiswaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama = itemView.namaSiswa
        val absen = itemView.absen
        fun bind(siswaModel: SiswaModel) {
            nama.setText(siswaModel.nama)
            absen.text = siswaModel.absen.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_data_siswa,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listSiswa.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position==0){
            holder.nama.requestFocus()
        }
        holder.nama.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    holder.nama.setError(context?.getString(R.string.errorFieldKosong))
                }
                listSiswa.get(position).nama = p0.toString()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        listSiswa.get(position).absen = when (listSiswa.get(position).absen) {
            0 -> (position + 1)
            else -> listSiswa.get(position).absen
        }
        holder.bind(listSiswa.get(position))
    }

    fun FieldisEmpty():Boolean{
        for (siswaModel in listSiswa){
            if (siswaModel.nama.isNullOrEmpty()){
                return true
            }
        }
        return false
    }
}