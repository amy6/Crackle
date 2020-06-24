package example.com.crackle.model

import com.google.gson.annotations.SerializedName

data class Cast(@field:SerializedName("name")
           val name: String, @field:SerializedName("profile_path")
           val profileUrl: String)
