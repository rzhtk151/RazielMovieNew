package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

/**
 * Created by Raziel Shushan on 19/03/2018.
 */

public class DBHandler {

    private DBHelper helper;

    public DBHandler(Context context) {
        this.helper = new DBHelper(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    //Insert new movie to DB
    public boolean insertMovie(Movie m) {
        SQLiteDatabase database = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBConstants.COLUMN_NAME_NAME, m.getName());
            values.put(DBConstants.COLUMN_NAME_DESCRIPTION, m.getDescription());
            values.put(DBConstants.COLUMN_NAME_DIRECTOR, m.getDirector());
            values.put(DBConstants.COLUMN_NAME_MOVIE_SOURCE, m.movieSource());
            values.put(DBConstants.COLUMN_NAME_WATCHED, m.getWatched());
            values.put(DBConstants.COLUMN_NAME_TRAILER, m.getTrailer());
            values.put(DBConstants.COLUMN_NAME_RELEASE_YEAR, m.getReleaseYear());
            values.put(DBConstants.COLUMN_NAME_RATING, m.getRating());
            values.put(DBConstants.COLUMN_NAME_IMDB, m.getImdb());
            values.put(DBConstants.COLUMN_NAME_IMAGE, m.getImage());
            values.put(DBConstants.COLUMN_NAME_API_ID, m.getApi_ID());
            database.insert(DBConstants.TABLE_NAME, null, values);

        } catch (SQLiteException e) {
            e.getCause();
        } finally {
            if (database.isOpen()) database.close();
        }
        return true;
    }

    //Update existing movie data in DB
    public boolean updateMovie(Movie m) {
        SQLiteDatabase database = helper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DBConstants.COLUMN_NAME_NAME, m.getName());
            values.put(DBConstants.COLUMN_NAME_DESCRIPTION, m.getDescription());
            values.put(DBConstants.COLUMN_NAME_DIRECTOR, m.getDirector());
            values.put(DBConstants.COLUMN_NAME_WATCHED, m.getWatched());
            values.put(DBConstants.COLUMN_NAME_RELEASE_YEAR, m.getReleaseYear());
            values.put(DBConstants.COLUMN_NAME_IMAGE, m.getImage());
            database.update(DBConstants.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(m.get_id())});
        } catch (SQLiteException e) {
            e.getCause();
        } finally {
            if (database.isOpen()) database.close();
        }
        return true;
    }

    //Deleting movie from DB
    public boolean deleteMovie(String _id) {
        SQLiteDatabase database = helper.getWritableDatabase();

        try {
            database.delete(DBConstants.TABLE_NAME, "_id=?", new String[]{_id});

        } catch (SQLiteException e) {
            e.getCause();
        } finally {
            if (database.isOpen()) database.close();
        }
        return true;
    }

    //Delete all movies from DB
    public boolean deleteAllMovie(ArrayList<Movie> m) {
        SQLiteDatabase database = helper.getWritableDatabase();

        try {
            for (int i = 0; i < m.size(); i++) {
                database.delete(DBConstants.TABLE_NAME, "_id=?", new String[]{String.valueOf(m.get(i).get_id())});
            }

        } catch (SQLiteException e) {
            e.getCause();
        } finally {
            if (database.isOpen()) database.close();
        }
        return true;
    }

    //Select one movie from the DB
    public Movie selectMovie(String _id) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = database.query(DBConstants.TABLE_NAME, null, "_id=?", new String[]{_id}, null, null, null);
        } catch (SQLiteException e) {
            e.getCause();
        }
        cursor.moveToFirst();
        Movie movie;

        int id = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_id));
        String name = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_NAME));
        String description = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_DESCRIPTION));
        String director = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_DIRECTOR));
        String releaseYear = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_RELEASE_YEAR));
        String movieSource = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_MOVIE_SOURCE));
        String imdb = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_IMDB));
        double rating = cursor.getDouble(cursor.getColumnIndex(DBConstants.COLUMN_NAME_RATING));
        int api_id = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_API_ID));
        String trailer = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_TRAILER));
        int watched = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_WATCHED));
        String image = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_IMAGE));

        movie = new Movie(id, name, description, image, director, releaseYear, trailer, imdb, rating, movieSource, watched, api_id);
        return movie;
    }

    //Select all movies from DB
    public ArrayList<Movie> selectAllMovie() {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = database.query(DBConstants.TABLE_NAME, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            e.getCause();
        }
        ArrayList<Movie> table = new ArrayList<>();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_id));
            String name = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_NAME));
            String description = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_DESCRIPTION));
            String director = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_DIRECTOR));
            String releaseYear = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_RELEASE_YEAR));
            String movieSource = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_MOVIE_SOURCE));
            String imdb = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_IMDB));
            double rating = cursor.getDouble(cursor.getColumnIndex(DBConstants.COLUMN_NAME_RATING));
            int api_id = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_API_ID));
            String trailer = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_TRAILER));
            int watched = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_WATCHED));
            String image = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_IMAGE));


            table.add(new Movie(id, name, description, image, director, releaseYear, trailer, imdb, rating, movieSource, watched, api_id));
        }

        cursor.close();

        return table;
    }

    //Select watched movies from DB
    public ArrayList<Movie> selectWatchedMovie() {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = database.query(DBConstants.TABLE_NAME, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            e.getCause();
        }
        ArrayList<Movie> table = new ArrayList<>();

        while (cursor.moveToNext()) {

            if (cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_WATCHED)) == 1) {
                int id = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_id));
                String name = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_NAME));
                String description = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_DESCRIPTION));
                String director = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_DIRECTOR));
                String releaseYear = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_RELEASE_YEAR));
                String movieSource = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_MOVIE_SOURCE));
                String imdb = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_IMDB));
                double rating = cursor.getDouble(cursor.getColumnIndex(DBConstants.COLUMN_NAME_RATING));
                int api_id = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_API_ID));
                String trailer = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_TRAILER));
                int watched = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_WATCHED));
                String image = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_NAME_IMAGE));
                table.add(new Movie(id, name, description, image, director, releaseYear, trailer, imdb, rating, movieSource, watched, api_id));
            }
        }

        cursor.close();

        return table;
    }


    //validation movie - 1=ok, 0=no;
    public int validationMovie(int apiId) {
        final String error = "The film exists";
        final String approved = "approved";

        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = database.query(DBConstants.TABLE_NAME, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            e.getCause();
        }

        while (cursor.moveToNext()) {

            if (cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_NAME_API_ID)) == apiId) {
                cursor.close();
                return 0;
            }
        }
        return 1;
    }
}

