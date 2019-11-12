package example.com.crackle.model

import com.google.gson.annotations.SerializedName

class Crew(@field:SerializedName("name")
           val name: String, @field:SerializedName("job")
           val job: String)
