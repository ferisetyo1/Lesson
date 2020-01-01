package feri.com.lesson.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
class GroupModel(
    var id: String? = null,
    var tipeGroup: Int? = -1,
    var anggota: ArrayList<Int> = ArrayList()
) : Parcelable {
    override fun toString(): String {
        return "GroupModel(id=$id, tipeGroup=$tipeGroup, listMurid=$anggota)"
    }
}