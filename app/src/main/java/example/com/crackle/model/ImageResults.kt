package example.com.crackle.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ImageResults(@field:SerializedName("backdrops")
                   val backdrops: List<Image>) : Parcelable
