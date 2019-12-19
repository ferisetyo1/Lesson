package feri.com.lesson.modul.menuDaftarKelas


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
import com.google.firebase.database.*

import feri.com.lesson.R
import feri.com.lesson.model.KelasModel
import feri.com.lesson.modul.kelas.DataKelasActivity
import feri.com.lesson.modul.util.const
import kotlinx.android.synthetic.main.fragment_daftar_kelas.*

/**
 * A simple [Fragment] subclass.
 */
class DaftarKelasFragment : Fragment(), View.OnClickListener, TextWatcher {

    private lateinit var v: View
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var db_reff: DatabaseReference
    private var rv_option: FirebaseRecyclerOptions<KelasModel>? = null
    private var rv_adapter: FirebaseRecyclerAdapter<KelasModel, DaftarKelasViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_daftar_kelas, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebase init
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        db_reff = firebaseDatabase.getReference(const.KELAS_DB)

        rv_update( "")

        btn_tbhKelas.setOnClickListener(this)
        fab.setOnClickListener(this)
        search.addTextChangedListener(this)
    }

    private fun rv_update(s: String) {
        var query: Query = db_reff.child(firebaseAuth.currentUser?.uid.toString())
        if (!s.isNullOrEmpty()) {
            query = query
                .orderByChild("nama")
                .equalTo(s.toLowerCase())
        }

        rv_option = FirebaseRecyclerOptions.Builder<KelasModel>()
            .setQuery(query, KelasModel::class.java!!)
            .build()

        rv_adapter =
            object : FirebaseRecyclerAdapter<KelasModel, DaftarKelasViewHolder>(rv_option!!) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): DaftarKelasViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_daftar_kelas, parent, false)

                    return DaftarKelasViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: DaftarKelasViewHolder,
                    position: Int,
                    model: KelasModel
                ) {
                    Log.d("print", model.toString())
                    holder.bind(model)
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
            };
        rv_adapter?.startListening()
        rv_kelas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_kelas.adapter = rv_adapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_tbhKelas -> {
                startActivity(Intent(activity, DataKelasActivity::class.java))
            }
            R.id.fab -> {
                startActivity(Intent(activity, DataKelasActivity::class.java))
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        rv_update(p0.toString())
    }

    override fun onDetach() {
        super.onDetach()
        rv_adapter?.stopListening()
    }
}
