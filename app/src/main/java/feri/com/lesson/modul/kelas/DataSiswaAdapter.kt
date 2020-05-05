package feri.com.lesson.modul.kelas

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.R
import feri.com.lesson.model.SiswaModel
import kotlinx.android.synthetic.main.item_data_siswa.view.*

class DataSiswaAdapter(var context: Context?, listSiswaArrayList: ArrayList<SiswaModel>) :
    RecyclerView.Adapter<DataSiswaAdapter.ViewHolder>() {

    init {
        listSiswa = listSiswaArrayList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama = itemView.namaSiswa
        val absen = itemView.absen
        val myCustomEditText = MyCustomEditText()
        fun bind(siswaModel: SiswaModel) {
            nama.setText(siswaModel.nama)
            absen.text = siswaModel.absen.toString()
            nama.addTextChangedListener(myCustomEditText)
        }
    }

    class MyCustomEditText : TextWatcher {
        var position: Int = -1

        fun updatePosition(i: Int) {
            position = i
        }

        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0?.length!! > 0) {
                listSiswa.get(position).nama = p0.toString()
            }
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
//        listSiswa.get(position).nama = holder.nama.text.toString()
        val siswa = listSiswa.get(position)
        listSiswa.get(position).absen = when (siswa.absen) {
            0 -> (position + 1)
            else -> siswa.absen
        }
        holder.bind(siswa)
        holder.myCustomEditText.updatePosition(position)
    }

    fun FieldisEmpty(): Boolean {
        for (siswaModel in listSiswa) {
            if (siswaModel.nama.isNullOrEmpty()) {
                Toast.makeText(context, "Harap lengkapi nama siswa", Toast.LENGTH_LONG).show()
                return true
            }
        }
        return false
    }

    companion object {
        lateinit var listSiswa: ArrayList<SiswaModel>
    }
}