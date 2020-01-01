package feri.com.lesson.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
class UserModel(
    var id: String? = null,
    var nama: String? = null,
    var email: String? = null,
    var instansi: String? = null,
    var fotoProfil: String? = null
) : Parcelable {
    override fun toString(): String {
        return "UserModel(id=$id, nama=$nama, email=$email, instansi=$instansi, fotoProfil=$fotoProfil)"
    }
}