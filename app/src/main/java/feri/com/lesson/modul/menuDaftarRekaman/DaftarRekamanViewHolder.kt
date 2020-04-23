package feri.com.lesson.modul.menuDaftarRekaman

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.RekamanModel
import feri.com.lesson.model.UserModel
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.item_daftar_rekaman.view.*

class DaftarRekamanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var judul = itemView.judulRekaman
    var namaPengajar = itemView.namaPengajar
    var namaSekolah = itemView.namaSekolah
    var tglObservasi = itemView.tanggalObservasi
    var lyt_container = itemView.lyt_container_dr

    fun bind(get: RekamanModel) {
        Log.d("print", get.toString())
        judul.text = get.judul
        tglObservasi.text = get.tanggal

        val firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.getReference(const.USER_DB).child(get.idPengajar!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(UserModel::class.java)
                    namaPengajar.text = user?.nama
                }
            })
        firebaseDatabase.getReference(const.KELAS_DB).child(get.idPengajar!!).child(get.idKelas!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val kelas = p0.getValue(KelasModel::class.java)
                    namaSekolah.text = kelas?.namaSekolah
                }

            })

    }

}