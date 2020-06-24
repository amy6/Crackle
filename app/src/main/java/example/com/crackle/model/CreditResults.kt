package example.com.crackle.model

import com.google.gson.annotations.SerializedName

data class CreditResults(@field:SerializedName("id")
                    val id: Int, @field:SerializedName("cast")
                    val castList: List<Cast>, @field:SerializedName("crew")
                    val crewList: List<Crew>)
