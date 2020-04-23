package feri.com.lesson.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
class TempRekamanModel(
    var idRekaman: String? = null,
    var idKelas: String?= null,
    var idPengajar: String? = null,
    var idPengamat: String? = null
) : Parcelable {
    override fun toString(): String {
        return "TempRekamanModel(idRekaman=$idRekaman, idKelas=$idKelas, idPengajar=$idPengajar, idPengamat=$idPengamat)"
    }
}