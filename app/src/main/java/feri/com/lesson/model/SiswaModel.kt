package feri.com.lesson.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
class SiswaModel(
    var id: String? = null,
    var nama: String? = null,
    var absen: Int? = -1
) : Parcelable {
    override fun toString(): String {
        return "SiswaModel(id=$id, nama=$nama, absen=$absen)"
    }
}