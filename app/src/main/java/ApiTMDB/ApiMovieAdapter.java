package ApiTMDB;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raziel.razielmovie.ActivityEditOrShow;
import com.raziel.razielmovie.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import DB.DBConstants;
import DB.DBHandler;
import DB.Movie;

/**
 * This adapter create for the RecyclerView of outside data - in example word, all the rows movie nor created by the user and come for the Api data
 */

public class ApiMovieAdapter extends RecyclerView.Adapter<ApiMovieAdapter.MovieViewHolder> {
    private Context context;
    private ArrayList<Movie> dataset;
    private RecyclerView mRecyclerV;

    //constants

    final static String GET_MOVIE_POSTER_API = "https://image.tmdb.org/t/p/w300";

    public ApiMovieAdapter(Context context, ArrayList<Movie> dataset, RecyclerView mRecyclerV) {
        this.context = context;
        this.dataset = dataset;
        this.mRecyclerV = mRecyclerV;
    }

    @Override
    public ApiMovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_rew_cardview, parent, false);
        MovieViewHolder mvh = new MovieViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(ApiMovieAdapter.MovieViewHolder holder, int position) {
        holder.bind(dataset.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        //Ui object
        private TextView tvMovieName;
        private TextView tvMovieYear;
        private ImageView ivMoviePoster;
        private ImageView ivWatchedOrNot;
        private TextView overView;
        private CardView cardView;

        //Intent
        private Intent intent = new Intent(context, ActivityEditOrShow.class);

        public MovieViewHolder(View itemView) {
            super(itemView);
            //Reference for UI object
            tvMovieName = (TextView) itemView.findViewById(R.id.tv_name_list);
            tvMovieYear = (TextView) itemView.findViewById(R.id.tv_year_list);
            ivMoviePoster = (ImageView) itemView.findViewById(R.id.iv_poster_rew_recycler);
            ivWatchedOrNot = (ImageView) itemView.findViewById(R.id.movie_watched_rew);
            overView = (TextView) itemView.findViewById(R.id.over_view_list);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }

        /**Instead of realizing the rew in onBindViewHolder I created this Bind function*/
        public void bind(final Movie movie, final int position) {
            /**seting the data on the row*/
            tvMovieName.setText(movie.getName());
            tvMovieName.setText(movie.getName());
            //Show only the year
                String yearOnly = movie.getReleaseYear().substring(0,4);


            tvMovieYear.setText(yearOnly);
            DBHandler dbHandler = new DBHandler(context);
            overView.setText(movie.getDescription());
            try {

                if (movie.getImage().length()> 2) {
                    //seting the movie poster by picasso function
                        Picasso.with(context).load(GET_MOVIE_POSTER_API + movie.getImage()).into(ivMoviePoster);
                }else{
                    //default image
                    ivMoviePoster.setImageResource(R.drawable.trailericon);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

           ivWatchedOrNot.setVisibility(View.GONE);

            /**Click function -  Clicking on the movie will take you to Activity "editOrShow"
             The details sent (the data set to the row from Task_Get_Movie_List_From_Api)-
             - The ID of the movie = always equal to 0 because the data comes from an external API rather than from the DB
             - url of the image
             - ID of API You are required to retrieve all movie data directly from a destination
             -"who - to Recognize from were the movie in the 'editOrNotActivity'
             ** The user can not click on the overview*/

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra(DBConstants.COLUMN_NAME_API_ID, movie.getApi_ID());
                    intent.putExtra(DBConstants.COLUMN_NAME_IMAGE, movie.getImage());
                    intent.putExtra("api_rew","main");
                    ((Activity) context).startActivityForResult(intent, 3);
                }
            });
        }
    }


}
