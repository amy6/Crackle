package example.com.crackle.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class CertificationResults(@field:SerializedName("countries")
                           val certificationList: List<Certification>) : Parcelable
