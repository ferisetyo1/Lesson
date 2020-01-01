package feri.com.lesson.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
class CatatanModel (
    var judul:String?=null,
    var tipe:Int?=-1,
    var deskripsi:String?=null,
    var extraUrl:String?=null,
    var waktu:Long?=-1
): Parcelable{
    override fun toString(): String {
        return "CatatanModel(judul=$judul, tipe=$tipe, deskripsi=$deskripsi, extraUrl=$extraUrl, waktu=$waktu)"
    }
}
