package feri.com.lesson.modul.kelas


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager

import feri.com.lesson.R
import feri.com.lesson.model.GroupModel
import kotlinx.android.synthetic.main.fragment_denah_kelas.*

/**
 * A simple [Fragment] subclass.
 */
class DenahKelasFragment() : Fragment() {

    private var model: Int = 0
    private var size: Int = 0
    private var span: Int = 0
    private var dataGroupModel = ArrayList<GroupModel>()

    constructor(model: Int, size: Int, span: Int) : this() {
        this.model = model
        this.size = size
        this.span = span
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_denah_kelas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataGroupModel = when (model) {
            1 -> generate_group_model(size, span)
            else -> generate_classroom_model(size, span)
        }

        var layoutManager = GridLayoutManager(context, span)
        rv_denah.layoutManager = layoutManager
        rv_denah.adapter = DenahKelasAdapter(context, dataGroupModel)
    }

    fun generate_classroom_model(size: Int, span: Int): ArrayList<GroupModel> {
        var dataGroup = ArrayList<GroupModel>()
        (1..size / 2).map {
            dataGroup.add(
                GroupModel(
                    "Group$it",
                    2,
                    arrayListOf((it * 2) - 1, (it * 2))
                )
            )
        }

        if (size % 2 !== 0) {
            dataGroup.add(
                GroupModel(
                    "Group${((size / 2) + 1)}",
                    2,
                    arrayListOf(size)
                )
            )
        }

        return dataGroup
    }

    fun generate_group_model(size: Int, span: Int): ArrayList<GroupModel> {
        var dataGroup = ArrayList<GroupModel>()
        if (size % 4 == 0) {
            (1..size / 4).map {
                dataGroup.add(
                    GroupModel(
                        "Group$it",
                        1,
                        arrayListOf((it * 4) - 3, (it * 4) - 2, (it * 4) - 1, (it * 4))
                    )
                )
            }
        }
        if (size % 4 != 0 && size > 4) {
            (1..size / 4).map {
                dataGroup.add(
                    GroupModel(
                        "Group$it",
                        1,
                        arrayListOf((it * 4) - 3, (it * 4) - 2, (it * 4) - 1, (it * 4))
                    )
                )
            }
        }
        if (size % 4 == 1) {
            dataGroup.add(
                GroupModel(
                    "Group${((size / 4) + 1)}",
                    1,
                    arrayListOf(size)
                )
            )
        }
        if (size % 4 == 2) {
            dataGroup.add(
                GroupModel(
                    "Group${(size / 4) + 1}",
                    1,
                    arrayListOf(size - 1, size)
                )
            )

        }
        if (size % 4 == 3) {
            dataGroup.add(
                GroupModel(
                    "Group${(size / 4) + 1}",
                    1,
                    arrayListOf(size - 2, size - 1, size)
                )
            )
        }

        return dataGroup
    }

    fun getDataGroup(): ArrayList<GroupModel> = this.dataGroupModel

}
