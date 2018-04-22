package ApiTMDB;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.raziel.razielmovie.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import DB.Movie;

/**
 * Created by Raziel Shushan on 21/03/2018.
 * This Task made to get the movie detail from 'TMDB API'
 * This class created to serve only the 'MainMovieClass' - BottomNavigationView
 */

public class TaskGetMovieListFromApi extends AsyncTask<String, Void, String> {

    private Activity activity;

    public TaskGetMovieListFromApi(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {

        return sendHttpRequest(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        /**
         * The only figure shown is the name of the movie
         *The reason we extract more data from the movie title is that if I want to change the visibility of search results in the future, I will have the required data
         * The data presented in the Schema:
         - Movie Api ID
         - Movie name
         - Release date
         - Poster path
         - Vote average - Based on site data only
         */
        final ArrayList<Movie> list = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("results");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.getJSONObject(i);
                    int id = item.getInt("id");
                    String name = item.getString("title");
                    String overview = item.getString("overview");
                    String release_date = item.getString("release_date");
                    String poster_path = item.getString("poster_path");
                    double voteAverage = item.getDouble("vote_average");

                    list.add(new Movie(name, overview, release_date, id, poster_path,voteAverage));
                }

                //set the data to the rew  - 'ApiMovieAdapter'
                RecyclerView movieListRecyclerView = (RecyclerView)activity.findViewById(R.id.movie_recyclerview);
                ApiMovieAdapter apiMovieAdapter = new ApiMovieAdapter(activity, list, movieListRecyclerView);
                movieListRecyclerView.setAdapter(apiMovieAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public String sendHttpRequest(String urlString) {
        BufferedReader input = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("TAG", activity.getString(R.string.https_problem) + urlString);
                return null;
            }
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            input = new BufferedReader(inputStreamReader);
            String line;
            while ((line = input.readLine()) != null) {
                response.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    inputStreamReader.close();
                    inputStream.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
        return response.toString();
    }

}
