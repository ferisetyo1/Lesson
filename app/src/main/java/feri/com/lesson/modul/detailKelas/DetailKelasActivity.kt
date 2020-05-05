package feri.com.lesson.modul.detailKelas

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.RekamanModel
import feri.com.lesson.model.TempRekamanModel
import feri.com.lesson.model.UserModel
import feri.com.lesson.modul.detailRekaman.DetailRekamanActivity
import feri.com.lesson.modul.dialog.LoadingDialog
import feri.com.lesson.modul.kelas.DataKelasActivity
import feri.com.lesson.modul.kelas.DenahKelasFragment
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.Helpers
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.activity_detail_kelas.*
import kotlinx.android.synthetic.main.dialog_yatidak.*

class DetailKelasActivity : AppCompatActivity() {

    private var rv_option: FirebaseRecyclerOptions<TempRekamanModel>? = null
    private var rv_adapter: FirebaseRecyclerAdapter<TempRekamanModel, PengamatViewHolder>? =
        null
    private var firebaseDatabase = DBHelper.getDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kelas)

        supportActionBar?.hide()

        btn_back.setOnClickListener {
            onBackPressed()
        }

        //fab
        btn_hapus.hide()
        btn_edit.hide()
        btn_share.hide()
        btn_opsi.setOnClickListener {
            if (!btn_edit.isShown) {
                btn_hapus.show()
                btn_edit.show()
                btn_share.show()
                btn_opsi.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_close_black_24dp
                    )
                )
                btn_opsi.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.colorBlueLab)
            } else {
                btn_hapus.hide()
                btn_edit.hide()
                btn_share.hide()
                btn_opsi.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_tune_black_24dp
                    )
                )
                btn_opsi.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.colorYellowLab)
            }
        }

        hideIcon.setOnClickListener {
            if (rv_pengamat.isVisible) {
                rv_pengamat.visibility = View.GONE
                hideIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_keyboard_arrow_up_black_24dp
                    )
                )
            } else {
                rv_pengamat.visibility = View.VISIBLE
                hideIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_keyboard_arrow_down
                    )
                )
            }
        }

        val dataKelas = intent.getParcelableExtra<KelasModel>("dataKelas")
        if (dataKelas.locked) {
            txt_ket_tutup.setText("Kelas telah ditutup")
            txt_ket_tutup.setTextColor(ContextCompat.getColor(this, R.color.red))
            txt_tutup.visibility = View.GONE
        }
        txt_tutup.setOnClickListener {
            val loadDialog = LoadingDialog()
            loadDialog.show(supportFragmentManager, "loading")
            firebaseDatabase.getReference(const.KELAS_DB).child(dataKelas.idPengajar!!)
                .child(dataKelas.id!!).child("locked")
                .setValue(true).addOnCompleteListener {
                    loadDialog.dismiss()
                    if (it.isSuccessful) {
                        txt_ket_tutup.setText("( kelas telah ditutup )")
                        txt_ket_tutup.setTextColor(ContextCompat.getColor(this, R.color.red))
                        txt_tutup.visibility = View.GONE
                    }
                }
        }

        val query = firebaseDatabase
            .getReference(const.REKAMAN_CODE)
            .orderByChild("idKelas")
            .equalTo(dataKelas!!.id)
        rv_option = FirebaseRecyclerOptions.Builder<TempRekamanModel>()
            .setQuery(query, TempRekamanModel::class.java)
            .build()

        rv_adapter = setAdapter()
        rv_adapter?.startListening()
        rv_pengamat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_pengamat.adapter = rv_adapter

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentDenah,
                DenahKelasFragment(true, dataKelas.list_group, dataKelas.denahSpan)
            )
            .commit()

        btn_share.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, dataKelas.kodeKelas);
            startActivity(Intent.createChooser(shareIntent, "Bagikan ke"))
        }


        btn_hapus.setOnClickListener {
            dialog_create(dataKelas)
        }

        btn_edit.setOnClickListener {
            startActivity(
                Intent(this, DataKelasActivity::class.java).putExtra(
                    "editData",
                    dataKelas
                ).putExtra("edit", true)
            )
        }

    }

    private fun setAdapter(): FirebaseRecyclerAdapter<TempRekamanModel, PengamatViewHolder>? =
        object : FirebaseRecyclerAdapter<TempRekamanModel, PengamatViewHolder>(rv_option!!) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PengamatViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pengamat_kelas, parent, false)
                return PengamatViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: PengamatViewHolder,
                position: Int,
                model: TempRekamanModel
            ) {
                Log.d("model", model.toString())
                firebaseDatabase.getReference(const.USER_DB).child(model.idPengamat!!)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            p0.toException().printStackTrace()
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val user = p0.getValue(UserModel::class.java)
                            holder.nomor.text = "${(position + 1)}. "
                            holder.nama_user.text = user?.nama
                            firebaseDatabase.getReference(const.REKAMAN_DB)
                                .child(model.idPengamat!!).child(model.idRekaman!!)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        p0.toException().printStackTrace()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        Log.d("test", p0.value.toString())
                                        if (p0.exists()) {
                                            val rekaman = p0.getValue(RekamanModel::class.java)
                                            holder.layout.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        this@DetailKelasActivity,
                                                        DetailRekamanActivity::class.java
                                                    ).putExtra("dataRekaman", rekaman)
                                                )
                                            }
                                            holder.tanggal.text =
                                                rekaman?.tanggal!!.substring(0, 10)
                                        }
                                    }

                                })
                        }

                    })
            }

            override fun onDataChanged() {
                super.onDataChanged()
                if (rv_adapter?.itemCount == 0) {
                    lyt_sudah_diobservasi.visibility = View.GONE
                    btn_opsi.show()
                } else {
                    lyt_sudah_diobservasi.visibility = View.VISIBLE
                    btn_opsi.hide()
                }
            }
        }

    private fun dialog_create(dataKelas: KelasModel) {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_yatidak)
        dialog.message.text = "Apakah anda akan menghapus kelas ini ?"
        dialog.btn_ya.setOnClickListener {
            val dialogx = LoadingDialog()
            dialogx.show(supportFragmentManager, "Loading")
            firebaseDatabase.getReference(const.KELAS_DB).child(dataKelas.idPengajar!!)
                .child(dataKelas.id!!).removeValue().addOnCompleteListener {
                    firebaseDatabase.getReference(const.CLASS_CODE).child(dataKelas.kodeKelas!!)
                        .removeValue()
                    dialogx.dismiss()
                    dialog.dismiss()
                    finish()
                }
        }
        dialog.btn_batal.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        rv_adapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        rv_adapter?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        rv_adapter?.stopListening()
    }
}
