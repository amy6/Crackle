package example.com.crackle.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Image(@field:SerializedName("file_path")
            val filePath: String) : Parcelable
