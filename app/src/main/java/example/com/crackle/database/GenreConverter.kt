package example.com.crackle.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class GenreConverter {

    @TypeConverter
    fun fromInt(value: String): ArrayList<Int>? {
        val listType = object : TypeToken<ArrayList<Int>>() {

        }.type
        return Gson().fromJson<ArrayList<Int>>(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<Int>): String {
        return Gson().toJson(list)
    }
}
