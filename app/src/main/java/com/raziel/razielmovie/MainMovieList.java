package com.raziel.razielmovie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ApiTMDB.TaskGetMovieListFromApi;
import DB.DBHandler;
import DB.Movie;
import DB.MovieListAdapter;

public class MainMovieList extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //constants API

    final String GET_POPULAR_API = "https://api.themoviedb.org/3/movie/popular?api_key=8c26aca0ea115e40aa21b6afe903d543&append_to_response>2018-01-01&language=";
    final String GET_NOW_PLAYING_API = "https://api.themoviedb.org/3/movie/now_playing?api_key=8c26aca0ea115e40aa21b6afe903d543&append_to_response>2018-01-01&language=";
    final String GET_UPCOMING_API = "https://api.themoviedb.org/3/movie/upcoming?api_key=8c26aca0ea115e40aa21b6afe903d543&language=";
    final String GET_TOP_RATED_API = "https://api.themoviedb.org/3/movie/top_rated?api_key=8c26aca0ea115e40aa21b6afe903d543&language=";

    //constants Preferences
    final static String PREFERENC_FILE_LANGUAGE = "appLang";
    final static String PREFERENC_FILE_APP_MODE = "appMode";

    //constants App Mode
    final static int APP_MODE_MY_LIST = 0;
    final static int APP_MODE_NOW_PLAY = 1;
    final static int APP_MODE_UPCOMING = 2;
    final static int APP_MODE_TOP_RATED = 3;
    final static int APP_MODE_POPULAR = 4;

    //constants language
    final static String LANGUAGE_NAME_ENGLISE = "en";
    final static String LANGUAGE_NAME_HEBREW = "iw";
    //variables
    private static String lang;
    //UI object
    private MenuItem searchItem;
    private SearchView searchView;
    private FloatingActionButton fab_plus_movie;
    private TextView errorMsg, emptyMyListText;
    private BottomNavigationView navigationView;

    //preference
    private SharedPreferences preferencesLang, preferencesAppMode;
    private SharedPreferences.Editor editorAppMode, editor;
    //recyclerView
    private RecyclerView movieListRecyclerView;
    private ArrayList<Movie> arrayMovie;
    private MovieListAdapter movieListAdapter;
    private LinearLayoutManager layoutManager;
    //Database
    private DBHandler handler = new DBHandler(MainMovieList.this);
    //intent
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set langange
        setLang();

        setContentView(R.layout.activity_main_movie_list);


        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        movieListRecyclerView = (RecyclerView) findViewById(R.id.movie_recyclerview);

        navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.my_movie_list);

        fab_plus_movie = (FloatingActionButton) findViewById(R.id.fab_plus_movie);
        errorMsg = (TextView) findViewById(R.id.connection_error);


        setLayoutManagerApp();
        preferenceAppMode();
        movieListRecyclerView.setHasFixedSize(true);


//set the app langange


        /**Clicking on one of the items in navigationView will activate the function
         * According to the user's decision, the film data was presented
         Possible lists:
         - user movie list
         - TMDB(API) popular list
         - TMDB(API) Top rated list
         - TMDB(API) noe playing list
         - TMDB(API) upcoming list

         */
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.my_movie_list:
                        emptyMyListTextFunction(true);
                        selectMyMovieList();
                        errorMsg.setVisibility(View.GONE);
                        editPreferenceAppMode(APP_MODE_MY_LIST);
                        searchView.setVisibility(View.VISIBLE);
                        searchItem.setVisible(true);
                        break;

                    case R.id.get_popular:
                        emptyMyListTextFunction(false);
                        clearMyMovieList();
                        TaskGetMovieListFromApi taskGetMovieListFromApi = new TaskGetMovieListFromApi(MainMovieList.this);
                        taskGetMovieListFromApi.execute(GET_POPULAR_API + getResources().getString(R.string.api_string_by_id_lang));
                        searchView.setVisibility(View.GONE);
                        searchItem.setVisible(false);
                        checkInternet();
                        editPreferenceAppMode(APP_MODE_POPULAR);

                        break;

                    case R.id.now_play:
                        emptyMyListTextFunction(false);
                        clearMyMovieList();
                        TaskGetMovieListFromApi taskGetMovieListFromApi2 = new TaskGetMovieListFromApi(MainMovieList.this);
                        taskGetMovieListFromApi2.execute(GET_NOW_PLAYING_API + getResources().getString(R.string.api_string_by_id_lang));
                        searchView.setVisibility(View.GONE);
                        searchItem.setVisible(false);
                        checkInternet();
                        editPreferenceAppMode(APP_MODE_NOW_PLAY);
                        break;

                    case R.id.get_upcoming:
                        emptyMyListTextFunction(false);
                        clearMyMovieList();
                        TaskGetMovieListFromApi taskGetMovieListFromApi1 = new TaskGetMovieListFromApi(MainMovieList.this);
                        taskGetMovieListFromApi1.execute(GET_UPCOMING_API + getResources().getString(R.string.api_string_by_id_lang) + "&sort_by=release_date.asc&region=US&release_date.gte=2018-04-01&page=1");
                        searchView.setVisibility(View.GONE);
                        searchItem.setVisible(false);
                        checkInternet();
                        editPreferenceAppMode(APP_MODE_UPCOMING);
                        break;

                    case R.id.get_top_rated:
                        emptyMyListTextFunction(false);
                        clearMyMovieList();
                        TaskGetMovieListFromApi taskGetMovieListFromApi3 = new TaskGetMovieListFromApi(MainMovieList.this);
                        taskGetMovieListFromApi3.execute(GET_TOP_RATED_API + getResources().getString(R.string.api_string_by_id_lang) + "&page=1");
                        searchView.setVisibility(View.GONE);
                        searchItem.setVisible(false);
                        checkInternet();
                        editPreferenceAppMode(APP_MODE_TOP_RATED);
                        break;
                }
                return true;
            }
        });

/**
 * This fub go to  create a new movie from - "ActivityEditOrShow"
 */
        fab_plus_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMovieList.this);

                builder.setNegativeButton(getString(R.string.dialog_enter_new_movie), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(MainMovieList.this, ActivityEditOrShow.class);
                        intent.putExtra("imNew", 1);
                        startActivityForResult(intent, 2);
                    }
                });
                builder.setPositiveButton(getString(R.string.dialog_search_for_new_movie), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(MainMovieList.this, SearchInMovieApi.class);
                        startActivity(intent);
                    }
                });
                builder.setMessage(getString(R.string.dialog_new_movie_title));
                builder.show();
            }
        });

    }


    /**
     * Update language for app
     * Currently the app supports 2 languages
     * - Hebrew
     * - English
     */
    public boolean updateLang(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = MainMovieList.this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return true;
    }

    /**
     * Create the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_movie_list, menu);
        searchItem = menu.findItem(R.id.search_action);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(MainMovieList.this);
        if (getPreferenceAppMode() != 0) {
            searchView.setVisibility(View.GONE);
            searchItem.setVisible(false);
        } else {
            searchItem.setVisible(true);

        }

        return true;
    }

    /**
     * The menu allows you to press the ITEMS menu
     * tho option:
     * - delete storage
     * - change langange
     * - exit from the app
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.delete_all_action:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialogDeleteMain);

                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayMovie = handler.selectAllMovie();
                        handler.deleteAllMovie(arrayMovie);
                        selectMyMovieList();
                        navigationView.setSelectedItemId(R.id.my_movie_list);

                    }
                });

                builder.setNegativeButton(getString(R.string.no), null);
                builder.show();

                break;

            case R.id.change_lang_action:
                final AlertDialog.Builder builderLang = new AlertDialog.Builder(this);
                builderLang.setMessage(getString(R.string.dialog_change_language));
                builderLang.setNegativeButton(getString(R.string.no), null);
                builderLang.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (lang.equals(LANGUAGE_NAME_ENGLISE)) {
                            updateLang(MainMovieList.this, LANGUAGE_NAME_HEBREW);
                            editor.putString("langApp", LANGUAGE_NAME_HEBREW);
                        } else {
                            updateLang(MainMovieList.this, LANGUAGE_NAME_ENGLISE);
                            editor.putString("langApp", LANGUAGE_NAME_ENGLISE);
                        }
                        editor.commit();

                        Intent intentRefresh = new Intent(MainMovieList.this, MainMovieList.class);
                        startActivity(intentRefresh);
                    }
                });
                builderLang.show();
                break;
            case R.id.exit_action:
                Intent intentFinish = new Intent(Intent.ACTION_MAIN);
                intentFinish.addCategory(Intent.CATEGORY_HOME);
                intentFinish.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentFinish);
                break;

            case R.id.watched_movie:
                selectWatchedMovieList();

                break;
        }
        return true;
    }

    /**
     * The function brings user movie list from the DB
     */
    public void selectMyMovieList() {
        arrayMovie = handler.selectAllMovie();
        movieListAdapter = new MovieListAdapter(MainMovieList.this, arrayMovie, movieListRecyclerView);
        movieListRecyclerView.setAdapter(movieListAdapter);
    }

    /**
     * The function brings user only watched movie list from the DB
     */
    public void selectWatchedMovieList() {
        ArrayList<Movie> arrayMovieWatched = handler.selectWatchedMovie();
        movieListAdapter = new MovieListAdapter(MainMovieList.this, arrayMovieWatched, movieListRecyclerView);
        movieListRecyclerView.setAdapter(movieListAdapter);
    }

    /**
     * The function deletes the entire list of user movies from the DB
     */
    public void clearMyMovieList() {
        if (getPreferenceAppMode() == 0) {
            arrayMovie = new ArrayList<>();
            movieListAdapter = new MovieListAdapter(MainMovieList.this, arrayMovie, movieListRecyclerView);
            movieListRecyclerView.setAdapter(movieListAdapter);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //The function searches the user's list of movies only
        newText = newText.toLowerCase();
        ArrayList<Movie> movies = new ArrayList<>();
        for (Movie newMovie : arrayMovie) {
            String name = newMovie.getName().toLowerCase();
            if (name.contains(newText)) {
                movies.add(newMovie);
            }
            movieListAdapter.setFilter(movies);
            movieListAdapter.notifyDataSetChanged();
        }
        return true;
    }

    /**
     * use the 'isInternetAvailable' class
     * check and Visible/Gone the error message
     */
    public void checkInternet() {

        if (CheckNetwork.isInternetAvailable(this)) {
            errorMsg.setVisibility(View.GONE);
        } else {
            errorMsg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * The execution function of 'preferenceAppMode'
     */
    public void appMode(int x) {
        movieListRecyclerView.setLayoutManager(layoutManager);


        switch (x) {

            case APP_MODE_MY_LIST:
                emptyMyListTextFunction(true);
                selectMyMovieList();
                movieListAdapter.notifyDataSetChanged();
                errorMsg.setVisibility(View.GONE);

                break;

            case APP_MODE_POPULAR:
                emptyMyListTextFunction(false);
                clearMyMovieList();
                TaskGetMovieListFromApi taskGetMovieListFromApi = new TaskGetMovieListFromApi(MainMovieList.this);
                taskGetMovieListFromApi.execute(GET_POPULAR_API + getResources().getString(R.string.api_string_by_id_lang));
                navigationView.setSelectedItemId(R.id.get_popular);
                break;

            case APP_MODE_NOW_PLAY:
                emptyMyListTextFunction(false);
                clearMyMovieList();
                TaskGetMovieListFromApi taskGetMovieListFromApi2 = new TaskGetMovieListFromApi(MainMovieList.this);
                taskGetMovieListFromApi2.execute(GET_NOW_PLAYING_API + getResources().getString(R.string.api_string_by_id_lang));
                navigationView.setSelectedItemId(R.id.now_play);
                break;

            case APP_MODE_UPCOMING:
                emptyMyListTextFunction(false);
                clearMyMovieList();
                TaskGetMovieListFromApi taskGetMovieListFromApi1 = new TaskGetMovieListFromApi(MainMovieList.this);
                taskGetMovieListFromApi1.execute(GET_UPCOMING_API + getResources().getString(R.string.api_string_by_id_lang) + "&sort_by=release_date.asc&region=US&release_date.gte=2018-04-01&page=1");
                navigationView.setSelectedItemId(R.id.get_upcoming);
                break;

            case APP_MODE_TOP_RATED:
                emptyMyListTextFunction(false);
                clearMyMovieList();
                TaskGetMovieListFromApi taskGetMovieListFromApi3 = new TaskGetMovieListFromApi(MainMovieList.this);
                taskGetMovieListFromApi3.execute(GET_TOP_RATED_API + getResources().getString(R.string.api_string_by_id_lang) + "&page=1");
                navigationView.setSelectedItemId(R.id.get_top_rated);

                break;


        }
    }

    /**
     * The function  created to save the display state of the list in the transition between Land and port scape
     */
    public void preferenceAppMode() {
        preferencesAppMode = MainMovieList.this.getSharedPreferences(PREFERENC_FILE_APP_MODE, MODE_PRIVATE);
        editorAppMode = preferencesAppMode.edit();
        if (preferencesAppMode.getBoolean("firstTime", true)) {
            editorAppMode.putBoolean("firstTime", false);
            editorAppMode.putInt("appMode", APP_MODE_MY_LIST);
        }
        appMode(preferencesAppMode.getInt("appMode", 0));
        editorAppMode.commit();
    }

    /**
     * Editing function of 'preferenceAppMode'
     */
    public void editPreferenceAppMode(int mode) {
        editorAppMode = preferencesAppMode.edit();
        editorAppMode.putInt("appMode", mode);
        editorAppMode.commit();
    }

    /**
     * The function checks whether the phone is either portrait or landscape and set the LayoutManger
     */
    public void setLayoutManagerApp() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);

        } else {
            layoutManager = new LinearLayoutManager(this);

        }
    }

    public int getPreferenceAppMode() {
        return preferencesAppMode.getInt("appMode", 0);
    }

    public void setLang() {
        preferencesLang = MainMovieList.this.getSharedPreferences(PREFERENC_FILE_LANGUAGE, MODE_PRIVATE);
        editor = preferencesLang.edit();
        if (preferencesLang.getBoolean("firstTime", true)) {
            editor.putBoolean("firstTime", false);
            editor.putString("langApp", LANGUAGE_NAME_ENGLISE);
        }
        editor.commit();
        lang = preferencesLang.getString("langApp", LANGUAGE_NAME_ENGLISE);
        updateLang(this, lang);
    }

    public void emptyMyListTextFunction(boolean mode) {
        emptyMyListText = (TextView) findViewById(R.id.tv_empty_my_list);
        if (mode) {
            if (handler.selectAllMovie().size() == 0) {
                emptyMyListText.setVisibility(View.VISIBLE);

            } else {
                emptyMyListText.setVisibility(View.GONE);

            }
        } else {
            emptyMyListText.setVisibility(View.GONE);
        }
    }
}




