package ApiTMDB;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

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
 * This Task made to get the credit details 'TMDB API'
 */

public class TaskGetMovieCredit extends AsyncTask<String, Void, String> {
    private Activity activity;


    public TaskGetMovieCredit(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        return sendHttpRequest(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        /**
         * The data presented in the Schema:
         - director name
         */
        //variable
        String director = "";
            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("crew");

                //This loop run On all the names and checks whether this is the director
                for (int i = 0; i < arr.length(); i++ ) {
                    JSONObject item = arr.getJSONObject(i);
                     director = item.getString("name");
                     String job = item.getString("job");
                     if(job.equals("Director"))
                         break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Reference for UI object
            EditText et_director = (EditText) activity.findViewById(R.id.et_director);
            //set the director name
            et_director.setText(director);
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