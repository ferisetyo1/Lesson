package feri.com.lesson.modul.menuDaftarRekaman


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

import feri.com.lesson.R
import feri.com.lesson.model.RekamanModel
import feri.com.lesson.modul.detailRekaman.DetailRekamanActivity
import feri.com.lesson.modul.rekaman.JoinKelasActivity
import feri.com.lesson.util.DBHelper
import feri.com.lesson.util.const
import kotlinx.android.synthetic.main.fragment_daftar_rekaman.*

/**
 * A simple [Fragment] subclass.
 */
class DaftarRekamanFragment : Fragment(), View.OnClickListener, TextWatcher {

    private lateinit var v: View
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff: DatabaseReference
    private var rv_option: FirebaseRecyclerOptions<RekamanModel>? = null
    private var rv_adapter: FirebaseRecyclerAdapter<RekamanModel, DaftarRekamanViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_daftar_rekaman, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebase init
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = DBHelper.getDatabase()
        db_reff = firebaseDatabase.getReference(const.REKAMAN_DB)

        search( null)

        btn_tbhRekaman.setOnClickListener(this)
        fab.setOnClickListener(this)
        search.addTextChangedListener(this)
    }

    private fun search(s: String?) {
        var query: Query = db_reff.child(firebaseAuth.currentUser?.uid.toString())
        if (!s.isNullOrEmpty()) {
            query = query
                .orderByChild("judul")
                .startAt(s.toLowerCase())
                .endAt(s.toLowerCase()+"\uf8ff")
        }

        rv_option = FirebaseRecyclerOptions.Builder<RekamanModel>()
            .setQuery(query, RekamanModel::class.java)
            .build()

        rv_adapter = setAdapter(s)

        rv_adapter?.startListening()
        rv_rekaman.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_rekaman.adapter = rv_adapter
    }

    private fun setAdapter(s:String?): FirebaseRecyclerAdapter<RekamanModel, DaftarRekamanViewHolder>? = object : FirebaseRecyclerAdapter<RekamanModel, DaftarRekamanViewHolder>(rv_option!!) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DaftarRekamanViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_daftar_rekaman, parent, false)

            return DaftarRekamanViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: DaftarRekamanViewHolder,
            position: Int,
            model: RekamanModel
        ) {
            holder.bind(model)
            holder.lyt_container.setOnClickListener {
                startActivity(Intent(activity, DetailRekamanActivity::class.java).putExtra("dataRekaman",model))
            }
        }

        override fun onDataChanged() {
            super.onDataChanged()
            if (s.isNullOrEmpty()) {
                if (rv_adapter?.itemCount != 0) {
                    empty_layout?.visibility = View.GONE
                    default_layout?.visibility = View.VISIBLE
                    fab?.show()
                } else {
                    empty_layout?.visibility = View.VISIBLE
                    default_layout?.visibility = View.GONE
                    fab?.hide()
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_tbhRekaman -> {
                startActivity(Intent(activity, JoinKelasActivity::class.java))
            }
            R.id.fab -> {
                startActivity(Intent(activity, JoinKelasActivity::class.java))
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        search(p0.toString())
    }

    override fun onDetach() {
        super.onDetach()
        rv_adapter?.stopListening()
    }

}
