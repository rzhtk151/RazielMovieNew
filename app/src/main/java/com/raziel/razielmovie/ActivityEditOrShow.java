package com.raziel.razielmovie;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Locale;

import ApiTMDB.TaskGetMovieCredit;
import ApiTMDB.TaskGetMovieTrailer;
import ApiTMDB.Task_Get_Movie_List_Api;
import ApiTMDB.Task_Get_Poster;
import DB.DBConstants;
import DB.DBHandler;
import DB.Movie;

public class ActivityEditOrShow extends AppCompatActivity {

    //constants
    final static String GET_MOVIE_POSTER_API = "https://image.tmdb.org/t/p/w300";
    final static int REQUEST_IMAGE_CAPTURE = 1;
    AppBarLayout appBarLayout;
    Bitmap imageBitmap;
    File pic;
    //itemsTollBar
    MenuItem youTube_item;
    MenuItem share_item;
    MenuItem imdb_item;
    ImageStorge imageStorge = new ImageStorge(this);
    TaskGetMovieTrailer taskGetMovieTrailer = new TaskGetMovieTrailer(this);
    TaskGetMovieCredit taskGetMovieCredit = new TaskGetMovieCredit(this);
    Task_Get_Poster task_get_poster = new Task_Get_Poster(this);
    //UI Objects
    private EditText et_director, et_description, et_year, movie_name;
    private Button trailer_btn, imdb_btn, addToMyList;
    private RatingBar rating_movie;
    private CheckBox watched_btn;
    private ProgressBar progressBar_poster;
    private ImageView image_poster, plusImage;
    private DonutProgress progressBarRating;
    private FloatingActionButton fab_edit;
    //Database
    private DBHandler handler = new DBHandler(this);
    //Variables
    private Intent intent;
    private String imagePath = "";
    private boolean keyForCreateNewFromApi = false;
    private boolean editMode = true;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private boolean posterNotByUser = true;
    private ImageButton gmailBtn;
    private boolean upDateMode = true;
    private int apiId = 0;
    private boolean createByYourself = false;

    //constants Preferences
    final static String PREFERENC_FILE_LANGUAGE = "appLang";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set langange
        SharedPreferences preferencesLang = ActivityEditOrShow.this.getSharedPreferences(PREFERENC_FILE_LANGUAGE, MODE_PRIVATE);
        String lang = preferencesLang.getString("langApp", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = ActivityEditOrShow.this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_or_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tmp);
        setSupportActionBar(toolbar);
        //show and enables the Home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getAppBarLayout();
        //reference for UI object
        intent = new Intent(ActivityEditOrShow.this, MainMovieList.class);
        addToMyList = (Button) findViewById(R.id.add_to_watchlist);
        et_director = (EditText) findViewById(R.id.et_director);
        et_description = (EditText) findViewById(R.id.et_description);
        et_year = (EditText) findViewById(R.id.et_year);
        movie_name = (EditText) findViewById(R.id.movie_name);
        imdb_btn = (Button) findViewById(R.id.imdb_btn);
        trailer_btn = (Button) findViewById(R.id.trailer_btn);
        rating_movie = (RatingBar) findViewById(R.id.rating_movie);
        watched_btn = (CheckBox) findViewById(R.id.watched_btn);
        progressBar_poster = (ProgressBar) findViewById(R.id.progressBar_poster);
        plusImage = (ImageView) findViewById(R.id.plusImage);
        image_poster = (ImageView) findViewById(R.id.app_bar_image);
        progressBarRating = (DonutProgress) findViewById(R.id.progressBarRating);
        fab_edit = (FloatingActionButton) findViewById(R.id.fab_edit);
        gmailBtn = (ImageButton) findViewById(R.id.gmailBtn);
        apiId = getIntent().getIntExtra(DBConstants.COLUMN_NAME_API_ID, 0);


/**
 * This section accepts the data based on where the user is coming from
 There are 4 options:
 1 - clicking on one of the rew of MyList (in MainMovieList.class)
 2 - Clicking on one of the API rew, for example - TopRated, popular (in MainMovieList.class)
 3 - pressing the + button (in MainMovieList)
 4 - Clicking on one of the movies in the search results (in SearchInMovieApi.class)*/

        /**
         option  1 - clicking on one of the rew of MyList (in MainMovieList.class)*/
        int x = getIntent().getIntExtra(DBConstants.COLUMN_NAME_API_ID, 0);
        if (getIntent().hasExtra(DBConstants.COLUMN_NAME_id) && x > 1) {
            upDateMode = false;
            createByYourself = false;
            int id = getIntent().getIntExtra(DBConstants.COLUMN_NAME_id, 0);
            //get the movie from Db by id
            Movie m = handler.selectMovie(String.valueOf(id));
            //set the movie details
            movie_name.setText(m.getName());
            et_year.setText(m.getReleaseYear());
            et_description.setText(m.getDescription());
            et_director.setText(m.getDirector());
            addToMyList.setText(getString(R.string.update_data));
            /*The condition exists to check whether the video the user is clicking on is generated by the user or the API
            because the details in the condition does not exist, which will generate an error*/
            if (m.movieSource().equals("0")) {
                imdb_btn.setTag(m.getImdb().toString());
                trailer_btn.setTag(m.getTrailer().toString());
                progressBarRating.setProgress((float) m.getRating());
                imagePath = m.getImage();

                //set the image - from storage or with picasso function
                try {
                    if (imagePath.contains("app_")) {
                        loadImageFromStorage(imagePath);
                        progressBar_poster.setVisibility(View.GONE);
                    } else {
                        task_get_poster.execute(GET_MOVIE_POSTER_API + m.getImage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                gmailBtn.setVisibility(View.GONE);
                imdb_btn.setVisibility(View.GONE);
                trailer_btn.setVisibility(View.GONE);
                progressBarRating.setVisibility(View.GONE);
                progressBar_poster.setVisibility(View.GONE);
            }
            //The condition exists to enable the editMode
           if (getIntent().getBooleanExtra("editmode", false)) {
                editTextSetEnabledOrNot(movie_name, true);
                editTextSetEnabledOrNot(et_description, true);
                editTextSetEnabledOrNot(et_director, true);
                plusImage.setVisibility(View.VISIBLE);
                fab_edit.setImageResource(R.drawable.ic_keyboard_return_black_48dp);
                editMode = false;
            }
        }

        /**option  2 - Clicking on one of the API rew, for example - TopRated, popular (in MainMovieList.class)*/
        int idf = getIntent().getIntExtra(DBConstants.COLUMN_NAME_API_ID, 0);
        if (idf > 1 && getIntent().hasExtra("api_rew")) {
            getMovieByApiId();
        }
        /**option  3 - pressing the + button (in MainMovieList)*/
        if (getIntent().getIntExtra("imNew", -1) == 1) {
            //Gone all the not relevant data(user cant enter - indbPath, rating)
            imdb_btn.setVisibility(View.GONE);
            trailer_btn.setVisibility(View.GONE);
            progressBarRating.setVisibility(View.GONE);
            progressBar_poster.setVisibility(View.GONE);
            createByYourself = true;
            editMode = false;
            editTextSetEnabledOrNot(movie_name, true);
            editTextSetEnabledOrNot(et_description, true);
            editTextSetEnabledOrNot(et_director, true);
            plusImage.setVisibility(View.VISIBLE);
            fab_edit.setImageResource(R.drawable.ic_keyboard_return_black_48dp);
        }

        /**option  4 - Clicking on one of the movies in the search results (in SearchInMovieApi.class)*/
        if (getIntent().hasExtra("im")) {
            getMovieByApiId();
        }
        /**on click functions:
         * - et_year - open a Calender Dialog
         * - mDateSetListener = set the date in et_year
         * - fab_edit - open/close to edit the data

         * - plus_btn - work only at edit mode, Allows the user to take a picture of the movie
         * et_description (on touch) - enable scrolling the overView text*
         * - addToMyList - 2 option:
         * 1 - save the movie details into the DB
         * 2 - update data in existing movie*/
        et_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ActivityEditOrShow.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                et_year.setText(year + "/" + month + "/" + dayOfMonth);
            }
        };

        fab_edit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                if (editMode) {

                    editTextSetEnabledOrNot(movie_name, true);
                    editTextSetEnabledOrNot(et_description, true);
                    editTextSetEnabledOrNot(et_director, true);
                    plusImage.setVisibility(View.VISIBLE);
                    fab_edit.setImageResource(R.drawable.ic_keyboard_return_black_48dp);
                    editMode = false;
                } else {
                    editTextSetEnabledOrNot(movie_name, false);
                    editTextSetEnabledOrNot(et_description, false);
                    editTextSetEnabledOrNot(et_director, false);
                    plusImage.setVisibility(View.GONE);
                    fab_edit.setImageResource(R.drawable.ic_mode_edit_black_48dp);
                    fab_edit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellowLiteBack)));
                    editMode = true;

                }
            }
        });

        plusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });


        addToMyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validation the movie - only movie with API_id
                if (apiId > 1 && upDateMode) {
                    int movieApprove = handler.validationMovie(apiId);
                    if (movieApprove == 0) {
                        Toast.makeText(ActivityEditOrShow.this, getResources().getString(R.string.valid_movie_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //values  alyse has
                String director = et_director.getText().toString();
                String description = et_description.getText().toString();
                String year = et_year.getText().toString();
                String name = movie_name.getText().toString();
                int id = getIntent().getIntExtra(DBConstants.COLUMN_NAME_id, 0);
                if (imagePath.length() > 2) {

                    /*Bitmap viewBitmap = Bitmap.createBitmap(image_poster.getWidth(),image_poster.getHeight(),Bitmap.Config.ARGB_8888);//i is imageview whch u want to convert in bitmap*/

                    image_poster.buildDrawingCache();
                    Bitmap bmap = image_poster.getDrawingCache();
                    imageStorge.saveToInternalStorage(bmap, name);
                }
                //work when the movie exist - upData movie from myList
                if (getIntent().hasExtra(DBConstants.COLUMN_NAME_id) && !upDateMode) {
                    String image = "";
                    handler.updateMovie(new Movie(id, name, description, imagePath, director, year));
                }
                //create a new movie to myList from search activity or from the APi list
                if (getIntent().hasExtra("im") || keyForCreateNewFromApi) {
                    String imdb = imdb_btn.getTag().toString();
                    String trailer = trailer_btn.getTag().toString();
                    double rating = progressBarRating.getProgress();
                    handler.insertMovie(new Movie(name, description, imagePath, director, year, trailer, imdb, rating, "0", apiId));
                }
                //create a new movie by yourself
                if (getIntent().getIntExtra("imNew", -1) == 1) {
                    handler.insertMovie(new Movie(name, description, imagePath, director, year, "1"));
                }

                //how to close the activity
                if (getIntent().equals(MainMovieList.class)) {
                    intent.putExtra("set_my_list", "yes");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("set_my_list", "yes");
                    startActivity(intent);
                }
            }
        });


    }


    //creator the AppBar
    public AppBarLayout getAppBarLayout() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        return appBarLayout;
    }


    //API constants
    public String API_GET_CREDIT(String movie_id) {
        String str = "https://api.themoviedb.org/3/movie/" + movie_id + "/credits?api_key=8c26aca0ea115e40aa21b6afe903d543&language=" + getResources().getString(R.string.api_string_by_id_lang);

        return str;
    }

    public String API_GET_TRAILER(String movie_id) {
        String str = "https://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key=8c26aca0ea115e40aa21b6afe903d543";
        return str;
    }

    public String API_GET_MOVIE_DETAILS_BY_ID(String api_id) {
        String str = "https://api.themoviedb.org/3/movie/" + api_id + "?api_key=8c26aca0ea115e40aa21b6afe903d543&language=" + getResources().getString(R.string.api_string_by_id_lang);
        return str;
    }

    /**
     * The function defines EditText for editing or read-only as required by the user
     */
    public void editTextSetEnabledOrNot(EditText editText, boolean mode) {

        editText.setFocusableInTouchMode(mode);
        editText.setFocusable(mode);
    }


    /**
     * use to get movie details by the APi id movie
     * thie function calld only when the movie get from the 'searchActivity' or from API rew(popular, topRated, nowPlaying,upcoming)
     */
    public void getMovieByApiId() {
        Task_Get_Movie_List_Api task_get_movie_list_api = new Task_Get_Movie_List_Api(this, posterNotByUser);
        int api_id = getIntent().getIntExtra(DBConstants.COLUMN_NAME_API_ID, 0);
        if (imagePath.contains("app_")) {
            loadImageFromStorage(imagePath);
            progressBar_poster.setVisibility(View.GONE);
        } else {
            imagePath = getIntent().getStringExtra(DBConstants.COLUMN_NAME_IMAGE);

        }
        String str = API_GET_MOVIE_DETAILS_BY_ID(String.valueOf(api_id));
        task_get_movie_list_api.execute(str);
        taskGetMovieTrailer.execute(API_GET_TRAILER(String.valueOf(api_id)));
        taskGetMovieCredit.execute(API_GET_CREDIT(String.valueOf(api_id)));
        keyForCreateNewFromApi = true;
        createByYourself = false;
    }

    /**
     * start the camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * this function use only to get the photo after the user finish to take a photo
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image_poster.setImageBitmap(imageBitmap);
            posterNotByUser = false;
        }
        try {
            imagePath = imageStorge.saveToInternalStorage(imageBitmap, movie_name.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_edit_or_show, menu);
        youTube_item = menu.findItem(R.id.you_tube_item);
        share_item = menu.findItem(R.id.share_item);
        imdb_item = menu.findItem(R.id.imdb_item);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toll_bar);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();

                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
                    if(createByYourself){
                        youTube_item.setVisible(false);
                        share_item.setVisible(false);
                        imdb_item.setVisible(false);
                    }else{
                        isShow = true;
                        youTube_item.setVisible(true);
                        share_item.setVisible(true);
                        imdb_item.setVisible(true);
                    }


                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    youTube_item.setVisible(false);
                    share_item.setVisible(false);
                    imdb_item.setVisible(false);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    /**
     * The menu allows you to press the ITEMS menu
     *  HOME button
     *  trailer_btn - send the user to movie trailer in youtube
     * imdb_btn - sent the user to movie imdb page
     * mail button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.imdb_item:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imdb_btn.getTag().toString()));
                startActivity(browserIntent);
                break;

            case R.id.share_item:
                Bitmap posterSend = image_poster.getDrawingCache();
                String path = imageStorge.saveToInternalStorage(posterSend, "TMP_MOVIE");

                shareMovie(movie_name.getText().toString(), et_description.getText().toString(), image_poster, path);
                break;

            case R.id.you_tube_item:
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer_btn.getTag().toString()));
                startActivity(browserIntent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    /**
     * A function that loads the image from the DB
     */
    public void loadImageFromStorage(String path) {

        try {
            File f = new File(path, "poster.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView image_poster = (ImageView) findViewById(R.id.app_bar_image);
            image_poster.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * share the movie in gmail function:
     * after the user click the gmailBtn the intent send the data to his mail user
     * the user sent-
     * - movie name
     * - overView
     * poster - not work now
     */
    public void shareMovie(String movieName, String overview, ImageView poster, String imagePath4) {
        File imagePath = new File(imagePath4, "image");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.send_mail) + movieName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, overview);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pic = new File(imagePath, "poster.jpg");
        shareIntent.setType("*/*");

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pic));

        startActivityForResult(Intent.createChooser(shareIntent, getResources().getString(R.string.select_app_to_send_mail)), RESULT_OK);
    }


    public boolean updateLang(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = ActivityEditOrShow.this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return true;
    }

}





