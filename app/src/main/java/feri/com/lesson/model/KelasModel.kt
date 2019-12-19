package feri.com.lesson.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
class KelasModel(
    var id: String? = null,
    var idPengajar: String? = null,
    var nama: String? = null,
    var namaSekolah: String? = null,
    var namaPelajaran: String? = null,
    var list_siswa: ArrayList<SiswaModel> = ArrayList(),
    var list_group: ArrayList<GroupModel> = ArrayList(),
    var kodeKelas: String? = null
) : Parcelable {



    override fun toString(): String {
        return "KelasModel(id=$id, idPengajar=$idPengajar, nama=$nama, namaSekolah=$namaSekolah, namaPelajaran=$namaPelajaran, list_siswa=$list_siswa, list_group=$list_group, kodeKelas=$kodeKelas)"
    }
}