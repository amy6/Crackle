package example.com.crackle.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class VideoResults(@field:SerializedName("results")
                   val videos: List<Video>) : Parcelable
