package example.com.crackle.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import example.com.crackle.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();
}
