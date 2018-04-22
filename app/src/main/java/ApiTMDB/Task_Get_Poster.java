package ApiTMDB;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.raziel.razielmovie.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Raziel Shushan on 21/03/2018.
 * This Task made to get and set the poster from 'TMDB API'
 */

public class Task_Get_Poster extends AsyncTask<String, Integer, Bitmap> {

    private Activity activity;
    //ui object
    private ProgressBar progressBar_scrol;
    private ImageView imageView;

    public Task_Get_Poster(Activity activity ) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        //reference the Ui object
        imageView = (ImageView)activity.findViewById(R.id.app_bar_image);
        progressBar_scrol = (ProgressBar)activity.findViewById(R.id.progressBar_poster) ;

        imageView.setImageBitmap(null);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        Log.d("doInBackground", "starting download of image");
        Bitmap image = downloadImage(strings[0]);
        return image;
    }

    protected void onPostExecute(Bitmap bitmapend) {
        //GONE the progressBar when the download finish
        progressBar_scrol.setVisibility(View.GONE);
        if(bitmapend != null) {
            imageView.setImageBitmap(bitmapend);
        }else{
            Toast.makeText(activity, activity.getString(R.string.Problem_while_download_image), Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    private Bitmap downloadImage(String urlString){
        URL url;
        try{
            url = new URL(urlString);
            HttpURLConnection htpurlc = (HttpURLConnection) url.openConnection();
            InputStream inputStream = htpurlc.getInputStream();
            int imageSize = htpurlc.getContentLength();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead, totalRead = 0;
            byte[] data = new byte[2048];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1){
                buffer.write(data, 0, nRead);
                totalRead += nRead;
                publishProgress(totalRead);
            }

            buffer.flush();
            byte [] image = buffer.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            return bitmap;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
