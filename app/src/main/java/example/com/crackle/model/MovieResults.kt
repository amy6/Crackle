package example.com.crackle.model

import com.google.gson.annotations.SerializedName

data class MovieResults(@field:SerializedName("results")
                   val movies: List<Movie>)
