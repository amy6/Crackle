package example.com.crackle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import example.com.crackle.model.Movie

@Database(entities = [Movie::class], version = 3, exportSchema = false)
@TypeConverters(GenreConverter::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {

        private const val DATABASE_NAME = "movie"

        //singleton instantiation
        private val LOCK = Any()
        @Volatile
        private var movieDatabaseInstance: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase? {

            if (movieDatabaseInstance == null) {
                synchronized(LOCK) {
                    if (movieDatabaseInstance == null) {
                        movieDatabaseInstance = Room.databaseBuilder(context, MovieDatabase::class.java,
                                DATABASE_NAME)
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return movieDatabaseInstance
        }
    }
}
