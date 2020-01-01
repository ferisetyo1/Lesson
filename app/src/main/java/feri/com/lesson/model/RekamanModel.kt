package feri.com.lesson.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
class RekamanModel(
    var id: String? = null,
    var idKelas: String? = null,
    var idPengajar: String? = null,
    var idPengamat: String? = null,
    var judul: String? = null,
    var tanggal: String? = null,
    var suaraUrl: String? = null,
    var listCatatan: ArrayList<CatatanModel> = ArrayList(),
    var kodeRekaman: String? = null
) : Parcelable {
    override fun toString(): String {
        return "RekamanModel(id=$id, idKelas=$idKelas, idPengajar=$idPengajar, idPengamat=$idPengamat, judul=$judul, tanggal='$tanggal', suaraUrl=$suaraUrl, listCatatan=$listCatatan, kodeRekaman=$kodeRekaman)"
    }
}