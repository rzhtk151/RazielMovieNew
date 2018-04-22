package DB;

        import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This is the department that produces the DB file - is name 'movie.db'
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBConstants.DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * Columns in DB
         * COLUMN_NAME_id = "_id";
         * TABLE_NAME = "movie";
         * COLUMN_NAME_NAME = "name";
         * COLUMN_NAME_DESCRIPTION = "Description";
         * COLUMN_NAME_DIRECTOR = "director";
         * COLUMN_NAME_RELEASE_YEAR = "releaseYear";
         * COLUMN_NAME_TRAILER = "trailer";
         * COLUMN_NAME_IMDB = "imdb";
         * COLUMN_NAME_RATING = "rating";
         * COLUMN_NAME_IMAGE = "image";
         * COLUMN_NAME_MOVIE_SOURCE = "movieSource";
         * COLUMN_NAME_WATCHED = "watched";
         * COLUMN_NAME_API_ID = "api_ID";
         */

        try {
            String cmd = "CREATE TABLE " + DBConstants.TABLE_NAME + " (" + DBConstants.COLUMN_NAME_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DBConstants.COLUMN_NAME_NAME + " TEXT, " +
                    DBConstants.COLUMN_NAME_DESCRIPTION + " TEXT, " + DBConstants.COLUMN_NAME_DIRECTOR + " TEXT, "  + " TEXT, " + DBConstants.COLUMN_NAME_RELEASE_YEAR + " TEXT, " +
                    DBConstants.COLUMN_NAME_MOVIE_SOURCE + " TEXT, " + DBConstants.COLUMN_NAME_IMDB + " TEXT, " + DBConstants.COLUMN_NAME_RATING + " REAL, " + DBConstants.COLUMN_NAME_API_ID + " INTEGER, " + DBConstants.COLUMN_NAME_TRAILER + " TEXT, " +
                    DBConstants.COLUMN_NAME_WATCHED + " INTEGER, " + DBConstants.COLUMN_NAME_IMAGE + " TEXT );";
            db.execSQL(cmd);
        }catch (SQLiteException e){
            e.getCause();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
