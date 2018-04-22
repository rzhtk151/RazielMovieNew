package ApiTMDB;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.raziel.razielmovie.ActivityEditOrShow;
import com.raziel.razielmovie.R;
import com.raziel.razielmovie.SearchInMovieApi;

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
 */

public class Task_Get_Movie_List_Api extends AsyncTask<String, Void, String> {
    private Activity activity;
    //variable
    /**Created to check whether the removed poster should be fetched from the API or from the phone itself*/
    private boolean postNotByUser;

    //constants
    final static String GET_MOVIE_POSTER_API = "https://image.tmdb.org/t/p/w300";
    final static String GET_MOVIE_IMDB_PATH = "http://www.imdb.com/title/";


    public Task_Get_Movie_List_Api(Activity activity, boolean postNotByUser) {
        this.activity = activity;
        this.postNotByUser = postNotByUser;
    }

    @Override
    protected String doInBackground(String... strings) {
        return sendHttpRequest(strings[0]);
    }
    @Override
    protected void onPostExecute(String s) {
        final ArrayList<Movie> list = new ArrayList<>();
        /**
         *This is where the data comes from if the activity requesting the information is 'SearchMovieApi' (the activity that searches for new movies)
         * The only figure shown is the name of the movie
         *The reason we extract more data from the movie title is that if I want to change the visibility of search results in the future, I will have the required data
         * The data presented in the Schema:
         - Movie Api ID
         - Movie name
         - Release date
         - Poster path
         **Only the first page is import from the Api - only 20 result*/
        if (s != null && activity.getClass().equals(SearchInMovieApi.class)) {
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

                    list.add(new Movie(name, overview, release_date, id, poster_path));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //create the recyclerView

            RecyclerView movieListRecyclerView = (RecyclerView)activity.findViewById(R.id.recycler_view_search_activity);
            ApiMovieAdapter apiMovieAdapter = new ApiMovieAdapter(activity, list, movieListRecyclerView);
            movieListRecyclerView.setAdapter(apiMovieAdapter);

            /**
             *This is where the data comes from if the activity requesting the information is 'ActivityEditOrShow' (the activity that searches for new movies)
             * The data presented in the Schema:
             - Movie Api ID
             - Movie name
             - Release date
             - Poster path
             -Vote average - Based on site data only
             */

        } else if (s != null && activity.getClass().equals(ActivityEditOrShow.class)) {
            //Variables
            String poster_path= "",director= "", imdb_id= "", release_date= "", overview= "", name= "",  imdb= "", trailer = "";
            double rating = 0.0;
            int api_ID = 0;

            try {
                JSONObject object = new JSONObject(s);
                name = object.getString("title");
                overview = object.getString("overview");
                release_date = object.getString("release_date");
                imdb_id = object.getString("imdb_id");
                rating = object.getDouble("vote_average");
                poster_path = object.getString("poster_path");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Reference for UI object
            EditText et_description = (EditText) activity.findViewById(R.id.et_description);
            EditText et_year = (EditText) activity.findViewById(R.id.et_year);
            EditText movie_name = (EditText) activity.findViewById(R.id.movie_name);
            Button imdb_btn = (Button) activity.findViewById(R.id.imdb_btn);
            Button trailer_btn = (Button) activity.findViewById(R.id.trailer_btn);
            /**An external extension in order to produce the circle of score*/
            DonutProgress progressBarRating = (DonutProgress) activity.findViewById(R.id.progressBarRating);

            //set the movie details
            /**Task_Get_Poster -  To Get and set the poster*/
            Task_Get_Poster task_get_poster = new Task_Get_Poster(activity);
            if(postNotByUser){
                task_get_poster.execute(GET_MOVIE_POSTER_API + poster_path);
            }
            imdb_btn.setTag(GET_MOVIE_IMDB_PATH + imdb_id+ "/");
            progressBarRating.setProgress((float) rating);
            progressBarRating.setTag(rating);
            //If there is no score then 'progressBarRating' GONE
            if(rating < 0.1){
                progressBarRating.setVisibility(View.GONE);
            }
            movie_name.setText(name);
            et_year.setText(release_date);
            et_description.setText(overview);
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
