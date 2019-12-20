package feri.com.lesson.modul.rekaman

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.TempKodeClassModel
import feri.com.lesson.model.UserModel
import feri.com.lesson.modul.util.const
import kotlinx.android.synthetic.main.activity_join_kelas.*
import kotlinx.android.synthetic.main.activity_join_kelas.empty_layout
import kotlinx.android.synthetic.main.fragment_daftar_kelas.*
import java.util.*
import kotlin.collections.HashMap

class JoinKelasActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff: DatabaseReference
    private var testtt = KelasModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_kelas)

        supportActionBar?.hide()

        firebaseDatabase = FirebaseDatabase.getInstance()
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
                    }
                } else {
                    empty_layout.visibility = View.GONE
                }
            }

        })

        btn_join.setOnClickListener {
            if (!testtt.id.isNullOrEmpty()) {
                Log.d("testtt", testtt.toString())
            }
        }
    }

    private fun finding(s: String) {
        db_reff.orderByKey().equalTo(s).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.exists()) {
                    defaultlayout.visibility = View.GONE
                    empty_layout.visibility = View.VISIBLE
                    testtt = KelasModel()
                } else {
                    defaultlayout.visibility = View.VISIBLE
                    empty_layout.visibility = View.GONE
                }
                p0.children.forEach {
                    var tempKodeClassModel = it.getValue(TempKodeClassModel::class.java)
                    firebaseDatabase.getReference(const.KELAS_DB)
                        .child(tempKodeClassModel?.idPengajar.toString())
                        .orderByKey()
                        .equalTo(tempKodeClassModel?.idKelas.toString())
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                p0.children.forEach {
                                    val kelasModel = it.getValue(KelasModel::class.java)
                                    testtt = kelasModel!!
                                    updateUI(kelasModel)
                                }
                            }

                        })
                }

            }

        })
    }


    private fun updateUI(kelasModel: KelasModel?) {
        namaPelajaran.text = kelasModel?.namaPelajaran
        jmlSiswa.text = kelasModel?.list_siswa?.size.toString()
        denahtype.text = when (kelasModel?.list_group?.get(0)?.tipeGroup) {
            0 -> resources.getStringArray(R.array.modelKelasArray).get(1)
            1 -> resources.getStringArray(R.array.modelKelasArray).get(2)
            else -> resources.getStringArray(R.array.modelKelasArray).get(3)
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
}
