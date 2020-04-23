package feri.com.lesson.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
class TempKelasModel(
    var idPengajar: String? = null,
    var idKelas: String? = null
) : Parcelable {
    override fun toString(): String {
        return "TempKelasModel(idPengajar=$idPengajar, idKelas=$idKelas)"
    }
}