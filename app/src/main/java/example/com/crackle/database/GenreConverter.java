package example.com.crackle.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GenreConverter {

    @TypeConverter
    public static ArrayList<Integer> fromInt(String value) {
        Type listType =new TypeToken<ArrayList<Integer>>(){}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Integer> list) {
        return new Gson().toJson(list);
    }
}
