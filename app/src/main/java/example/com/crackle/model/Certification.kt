package example.com.crackle.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Certification(@field:SerializedName("certification")
                    val certification: String, @field:SerializedName("iso_3166_1")
                    val iso: String) : Parcelable
