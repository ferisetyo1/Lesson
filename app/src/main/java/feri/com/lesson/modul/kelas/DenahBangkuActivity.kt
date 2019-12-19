package feri.com.lesson.modul.kelas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import feri.com.lesson.R
import feri.com.lesson.model.GroupModel
import feri.com.lesson.model.KelasModel
import feri.com.lesson.model.SiswaModel
import kotlinx.android.synthetic.main.activity_denah_bangku.*

class DenahBangkuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_denah_bangku)

        supportActionBar?.hide()

        var dataKelas=intent.getParcelableExtra<KelasModel>("dataKelas")
        var dataGroup=ArrayList<GroupModel>()
        (1..dataKelas.list_siswa.size/2).map {
            dataGroup.add(GroupModel(
                "Group$it",
                1,
                arrayListOf((it*2)-1,(it*2))
            ))
        }

        if (dataKelas.list_siswa.size%2!==0){
            dataGroup.add(
                GroupModel(
                    "Group${((dataKelas.list_siswa.size/2)+1)}",
                    1,
                    arrayListOf(dataKelas.list_siswa.size)
                )
            )
        }

        var layoutManager=GridLayoutManager(this,3)
        rv_denah.layoutManager=layoutManager
        rv_denah.adapter=DenahBangkuAdapter(this,dataGroup)
        btn_simpan.setOnClickListener{
            var data_intent= Intent(this,PreviewDataKelasActivity::class.java)
            dataKelas.list_group=dataGroup
            data_intent.putExtra("dataKelas",dataKelas)
            startActivity(data_intent)
        }
    }
}
