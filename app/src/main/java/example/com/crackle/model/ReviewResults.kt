package example.com.crackle.model

import com.google.gson.annotations.SerializedName

data class ReviewResults(@field:SerializedName("results")
                    val reviewList: List<Review>)
