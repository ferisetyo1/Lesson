package feri.com.lesson.modul.kelas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import feri.com.lesson.R
import feri.com.lesson.model.GroupModel
import feri.com.lesson.model.KelasModel
import kotlinx.android.synthetic.main.activity_denah_kelas.*

class DenahKelasActivity : AppCompatActivity() {

    private var denahFragment: DenahKelasFragment = DenahKelasFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_denah_kelas)

        supportActionBar?.hide()

        var dataKelas = intent.getParcelableExtra<KelasModel>("dataKelas")
        var span = 3
        var model = 0

        var adapter = ArrayAdapter<String>(
            this,
            R.layout.spinner_text,
            resources.getStringArray(R.array.modelKelasArray)
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0?.selectedItemPosition != 0) {
                    model = p0?.selectedItemPosition!!
                    denahhh.visibility = View.VISIBLE
                    when (model) {
                        1 -> replace_fragment(model, dataKelas.list_siswa.size, span)
                        else -> replace_fragment(model, dataKelas.list_siswa.size, span)
                    }
                } else {
                    model = 0
                    denahhh.visibility = View.GONE
                }
            }

        }

        et_span.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    span = p0.toString().toInt()
                    if (model != 0) {
                        when (model) {
                            1 -> replace_fragment(model, dataKelas.list_siswa.size, span)
                            else -> replace_fragment(model, dataKelas.list_siswa.size, span)
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        btn_simpan.setOnClickListener {
            if (denahFragment?.getDataGroup().size == 0) {
                Toast.makeText(this, "pilih model kelas terlebih dahulu", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            transferDataDenah(dataKelas, denahFragment?.getDataGroup())
        }
    }

    fun transferDataDenah(
        dataKelas: KelasModel?,
        dataGroup: ArrayList<GroupModel>
    ) {
        if (denahhh.isVisible) {
            var data_intent = Intent(this, PreviewDataKelasActivity::class.java)
            dataKelas?.list_group = dataGroup
            data_intent.putExtra("dataKelas", dataKelas)
            startActivity(data_intent)
        }
    }

    fun replace_fragment(model: Int, size: Int, span: Int) {
        denahFragment = DenahKelasFragment(model, size, span)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentDenah, denahFragment)
            .commit()
    }
}
