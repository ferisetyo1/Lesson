package feri.com.lesson.modul.rekaman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.TempKelasModel
import feri.com.lesson.model.UserModel
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_join_kelas.*
import kotlinx.android.synthetic.main.activity_join_kelas.empty_layout

class JoinKelasActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff: DatabaseReference
    private var curr_kelasModel = KelasModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_kelas)

        supportActionBar?.hide()

        firebaseDatabase = DBHelper.getDatabase()
        db_reff = firebaseDatabase.getReference(const.CLASS_CODE)

        findKelas.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0?.isNullOrEmpty()!!) {
                    if (p0?.length == 6) {
                        finding(p0.toString())
                    } else {
                        defaultlayout.visibility = View.GONE
                        empty_layout.visibility = View.VISIBLE
                        txt_error.text = "Data tidak ditemukan"
                    }
                } else {
                    empty_layout.visibility = View.GONE
                }
            }

        })

        btn_join.setOnClickListener {
            tranferDataKelas()
        }
    }

    private fun tranferDataKelas() {
        if (!curr_kelasModel.id.isNullOrEmpty()) {
            startActivity(
                Intent(this, DataRekamanActivity::class.java)
                    .putExtra("dataKelas", curr_kelasModel)
            )
        }
    }

    private fun finding(s: String) {
        db_reff.orderByKey().equalTo(s).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.exists()) {
                    defaultlayout.visibility = View.GONE
                    empty_layout.visibility = View.VISIBLE
                    txt_error.text = "Data tidak ditemukan"
                    curr_kelasModel = KelasModel()
                } else {
                    defaultlayout.visibility = View.VISIBLE
                    empty_layout.visibility = View.GONE
                    p0.children.forEach {
                        var tempKodeClassModel = it.getValue(TempKelasModel::class.java)
                        firebaseDatabase.getReference(const.KELAS_DB)
                            .child(tempKodeClassModel?.idPengajar.toString())
                            .orderByKey()
                            .equalTo(tempKodeClassModel?.idKelas.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if (tempKodeClassModel?.idPengajar!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                                        defaultlayout.visibility = View.GONE
                                        empty_layout.visibility = View.VISIBLE
                                        txt_error.text = "Tidak dapat merekam kelas anda sendiri"
                                    } else {
                                        p0.children.forEach {
                                            val kelasModel = it.getValue(KelasModel::class.java)
                                            if (kelasModel!!.locked) {
                                                defaultlayout.visibility = View.GONE
                                                empty_layout.visibility = View.VISIBLE
                                                txt_error.text = "Kelas telah ditutup"
                                            } else {
                                                firebaseDatabase.getReference(const.REKAMAN_DB)
                                                    .child(FirebaseAuth.getInstance().uid!!)
                                                    .orderByChild("idKelas").equalTo(kelasModel.id)
                                                    .addValueEventListener(object :ValueEventListener{
                                                        override fun onCancelled(p0: DatabaseError) {
                                                            p0.toException().printStackTrace()
                                                        }

                                                        override fun onDataChange(p0: DataSnapshot) {
                                                            if (!p0.exists()){
                                                                curr_kelasModel = kelasModel!!
                                                                updateUI(kelasModel)
                                                            }else{
                                                                defaultlayout.visibility = View.GONE
                                                                empty_layout.visibility = View.VISIBLE
                                                                txt_error.text = "Anda telah melakukan rekaman pada kelas ini"
                                                            }
                                                        }

                                                    })
                                            }
                                        }
                                    }
                                }

                            })
                    }
                }
            }

        })
    }


    private fun updateUI(kelasModel: KelasModel?) {
        tanggalObservasi.text = kelasModel?.namaPelajaran
        jmlSiswa.text = kelasModel?.list_siswa?.size.toString()
        denahtype.text = when (kelasModel?.list_group?.get(0)?.tipeGroup) {
            1 -> resources.getStringArray(R.array.modelKelasArray).get(2)
            else -> resources.getStringArray(R.array.modelKelasArray).get(1)
        }
        firebaseDatabase.getReference(const.USER_DB)
            .orderByKey()
            .equalTo(kelasModel?.idPengajar)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        var user = it.getValue(UserModel::class.java)
                        namaPengajar.text = user?.nama
                    }
                }

            })
    }

    fun back(view: View) {
        onBackPressed()
    }
}
