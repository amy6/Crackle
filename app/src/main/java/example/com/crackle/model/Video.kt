package example.com.crackle.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(@field:SerializedName("key")
            val key: String, @field:SerializedName("site")
            val site: String, @field:SerializedName("name")
            val title: String) : Parcelable
