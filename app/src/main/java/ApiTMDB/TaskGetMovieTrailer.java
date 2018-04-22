package ApiTMDB;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

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

/**
 * Created by Raziel Shushan on 21/03/2018.
 * This Task made to get the Trailer path from 'TMDB API'
 */

public class TaskGetMovieTrailer extends AsyncTask<String, Void, String> {
    private Activity activity;

    //constants
    final static String YOUTUBE_REQUEST = "https://www.youtube.com/watch?v=";

    public TaskGetMovieTrailer(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        return sendHttpRequest(strings[0]);
    }

    /**
     * The data presented in the Schema:
     - key - Say if the movie is a teaser or a trailer
     - site - the path site
     */
    @Override
    protected void onPostExecute(String s) {
        String key = "";
        String type = "";
        try {
            JSONObject object = new JSONObject(s);
            JSONArray arr = object.getJSONArray("results");

            for (int i = 0; i < arr.length(); i++ ) {
                JSONObject item = arr.getJSONObject(i);
                key = item.getString("key");
                type = item.getString("type");
                /**I'm only showing a trailer from YOUTUBE, so I created a loop that checks where the movie comes from*/
                String site = item.getString("site");
                if(site.equals("YouTube") && type.equals("Trailer"))
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Reference for UI object
        Button trailer = (Button) activity.findViewById(R.id.trailer_btn);

        //set the trailer data
        trailer.setTag(YOUTUBE_REQUEST + key);

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
