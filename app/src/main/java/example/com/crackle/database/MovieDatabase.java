package example.com.crackle.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import example.com.crackle.model.Movie;

import static example.com.crackle.utils.Constants.LOG_TAG;

@Database(entities = {Movie.class}, version = 3, exportSchema = false)
@TypeConverters(GenreConverter.class)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "movie";

    //singleton instantiation
    private static final Object LOCK = new Object();
    private static volatile MovieDatabase movieDatabaseInstance;

    public abstract MovieDao movieDao();

    public static MovieDatabase getInstance(Context context) {

        if (movieDatabaseInstance == null) {
            synchronized (LOCK) {
                if (movieDatabaseInstance == null) {
                    movieDatabaseInstance = Room.databaseBuilder(context, MovieDatabase.class,
                            MovieDatabase.DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                    Log.d(LOG_TAG, "Initializing the database instance");
                }
            }
        }
        return movieDatabaseInstance;
    }
}
