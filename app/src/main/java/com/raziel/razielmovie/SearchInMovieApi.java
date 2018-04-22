package com.raziel.razielmovie;

import android.content.res.Configuration;
import android.os.Bundle;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ApiTMDB.Task_Get_Movie_List_Api;
public class SearchInMovieApi extends AppCompatActivity implements SearchView.OnQueryTextListener {
    //Ui object
    private MenuItem searchItem;
    private SearchView searchView;
    private TextView errorMsg;
    //recyclerView object
    private LinearLayoutManager layoutManager;
    private RecyclerView movieListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_in_movie_api);

        movieListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search_activity);
        movieListRecyclerView.setHasFixedSize(true);
        setLayoutManagerApp();
        movieListRecyclerView.setLayoutManager(layoutManager);

        //reference the toolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //show and enables the Home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //reference the UI object
        errorMsg = (TextView) findViewById(R.id.connection_error_search);



        /**SearchView function*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        searchItem = menu.findItem(R.id.search_bar_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(SearchInMovieApi.this);

        return true;
    }

    /**The menu allows you to press the ITEMS menu
     * - home button*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }



    public String API_SEARCH_MOVIE(String searchText) {
        checkInternet();
        String str = "https://api.themoviedb.org/3/search/movie?api_key=8c26aca0ea115e40aa21b6afe903d543&query=" + searchText + "&language=en-US&page=1&include_adult=false";
        return str;
    }


    /**use the 'isInternetAvailable' class
     * check and Visible/Gone the error message */
    public void checkInternet() {

        if (CheckNetwork.isInternetAvailable(this)) {
            errorMsg.setVisibility(View.GONE);
        } else {
            errorMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        checkInternet();

        Task_Get_Movie_List_Api task = new Task_Get_Movie_List_Api(SearchInMovieApi.this,true);
        String mov = "";

        try {
            mov = URLEncoder.encode(query, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //get max 20 result from the 'TMDB' API
        String str = API_SEARCH_MOVIE(mov);
        task.execute(str);

        return false;
    }

    /**The function checks whether the phone is either portrait or landscape and set the LayoutManger*/
    public void setLayoutManagerApp() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);

        } else {
            layoutManager = new LinearLayoutManager(this);

        }
    }
}
