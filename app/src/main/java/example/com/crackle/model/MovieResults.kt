package example.com.crackle.model

import com.google.gson.annotations.SerializedName

class MovieResults(@field:SerializedName("results")
                   val movies: List<Movie>)
