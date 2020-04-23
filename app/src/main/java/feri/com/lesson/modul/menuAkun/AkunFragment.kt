package feri.com.lesson.modul.menuAkun

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import feri.com.lesson.R
import feri.com.lesson.model.UserModel
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.fragment_akun.*

class AkunFragment : Fragment(), View.OnClickListener {

    private var mlistenrekaman: ValueEventListener? = null
    private lateinit var dbrekamanreff: Query
    private var mlistenkelas: ValueEventListener? = null
    private lateinit var dbkelasreff: Query
    private var mlistener: ValueEventListener? = null
    private var root: View? = null
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var dbuserreff: DatabaseReference
    private var user: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_akun, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_editProfil.setOnClickListener(this)
        dbuserreff = firebaseDatabase
            .getReference(const.USER_DB)
            .child(firebaseAuth.currentUser?.uid.toString())
        dbkelasreff = firebaseDatabase
            .getReference(const.CLASS_CODE)
            .orderByChild("idPengajar")
            .equalTo(firebaseAuth.currentUser?.uid.toString())
        dbrekamanreff = firebaseDatabase
            .getReference(const.REKAMAN_CODE)
            .orderByChild("idPengamat")
            .equalTo(firebaseAuth.currentUser?.uid.toString())

        val builderdialog = AlertDialog.Builder(requireContext())
        builderdialog.setCancelable(false)
        builderdialog.setView(R.layout.progress)
        val dialog = builderdialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        mlistener = dbuserreff
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    user = p0.getValue(UserModel::class.java)!!
                    nama_user.text = user?.nama!!
                    instansi_user.text = user?.instansi!!
                    if (!user?.fotoProfil.isNullOrEmpty()) {
                        Glide.with(context!!).load(user?.fotoProfil).into(fotoProfil)
                        fotoProfil.scaleType = ImageView.ScaleType.CENTER_CROP
                    }

                    mlistenkelas = dbkelasreff
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                p0.toException().printStackTrace()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                println(p0.toString())
                                jml_dibuat.text = p0.childrenCount.toString()

                                mlistenrekaman = dbrekamanreff
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {
                                            p0.toException().printStackTrace()
                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            jml_diobservasi.text = p0.childrenCount.toString()
                                            dialog.dismiss()
                                        }

                                    })
                            }

                        })
                }

            })

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_editProfil -> {
                if (!user?.nama.isNullOrEmpty()) {
                    startActivity(
                        Intent(activity, EditProfilActivity::class.java).putExtra(
                            "dataUser",
                            user
                        )
                    )
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (mlistener != null) {
            dbuserreff.removeEventListener(mlistener!!)
        }
        if (mlistenkelas != null) {
            dbkelasreff.removeEventListener(mlistenkelas!!)
        }
        if (mlistenrekaman != null) {
            dbrekamanreff.removeEventListener(mlistenrekaman!!)
        }
    }
}
