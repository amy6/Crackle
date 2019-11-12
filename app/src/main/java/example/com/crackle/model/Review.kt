package example.com.crackle.model

import com.google.gson.annotations.SerializedName

class Review(@field:SerializedName("author")
             val author: String, @field:SerializedName("content")
             val content: String)
