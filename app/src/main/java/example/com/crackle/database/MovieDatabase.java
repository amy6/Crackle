package example.com.crackle.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import example.com.crackle.model.Movie;

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
                }
            }
        }
        return movieDatabaseInstance;
    }
}
