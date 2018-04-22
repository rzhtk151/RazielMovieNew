package DB;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raziel.razielmovie.ActivityEditOrShow;
import com.raziel.razielmovie.MainMovieList;
import com.raziel.razielmovie.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * This adapter create for the RecyclerView Which contains the "my movie" list  - in example word, all the rows movie that created by the user
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private Context context;
    private ArrayList<Movie> dataset;
    private RecyclerView mRecyclerV;

    //constants
    final static String GET_MOVIE_POSTER_API = "https://image.tmdb.org/t/p/w300";

    public MovieListAdapter(Context context, ArrayList<Movie> dataset, RecyclerView mRecyclerV) {
        this.context = context;
        this.dataset = dataset;
        this.mRecyclerV = mRecyclerV;
    }

    @Override
    public MovieListAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_rew_cardview, parent, false);
        MovieViewHolder mvh = new MovieViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(dataset.get(position), position);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void setFilter(ArrayList<Movie> movies) {
        dataset = new ArrayList<>();
        dataset.addAll(movies);
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        //UI object
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
            overView.setMovementMethod(new ScrollingMovementMethod());
            cardView = (CardView) itemView.findViewById(R.id.cardview);


        }

        /**Instead of realizing the rew in onBindViewHolder I created this Bind function*/

        public void bind(final Movie movie, final int position) {

            /**If the user clicks on one of the options in the Context menu, this function will be activated
             * case 1 - delete the movie from DB:
               The movie will be deleted only after the user approves the deletion in Dialog
             * case 2 - edit the movie - will take the user to Activity "editOrShow"
             The details sent (the data set to the row from Task_Get_Movie_List_From_Api)-
             - The ID of the movie = DB id
             - url of the image
             - ID of API You are required to retrieve all movie data directly from a destination
             -"who - to Recognize from were the movie in the 'editOrNotActivity'
             -'editMode' - Tells Activity whether to start the edit mode or not (It's up side down  - true is false and false is true )
             ** The user can not click on the overview */
            final MenuItem.OnMenuItemClickListener mOnMyActionClickListener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        //delete
                        case 1:

                            final AlertDialog.Builder builderLang = new AlertDialog.Builder(context);
                            builderLang.setMessage(context.getString(R.string.dialog_delete_movie_title));
                            builderLang.setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainMovieList mainMovieList = new MainMovieList();
                                    DBHandler handler = new DBHandler(context);
                                    handler.deleteMovie(String.valueOf(movie.get_id()));
                                    mRecyclerV.removeViewAt(position);
                                    dataset.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, dataset.size());
                                }
                            });

                            builderLang.setPositiveButton(context.getString(R.string.no), null);
                            builderLang.show();
                            break;

                        //edit
                        case 2:
                            intent.putExtra(DBConstants.COLUMN_NAME_id, movie.get_id());
                            intent.putExtra(DBConstants.COLUMN_NAME_API_ID, movie.getApi_ID());
                            intent.putExtra(DBConstants.COLUMN_NAME_IMAGE, movie.getImage());
                            intent.putExtra("who", "main");
                            intent.putExtra("editmode", true);
                            ((Activity) context).startActivityForResult(intent, 3);

                            break;
                        default:
                            break;
                    }
                    return true;
                }
            };

            /**This function Create the ContextMenu eith 2 option
             * delete the movie
             * edit the movie*/

            final View.OnCreateContextMenuListener mOnCreateContextMenuListener = new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    MenuItem edit = menu.add(Menu.NONE, 1, 1, context.getResources().getString(R.string.delete));
                    MenuItem delete = menu.add(Menu.NONE, 2, 2, context.getResources().getString(R.string.edit));
                    edit.setOnMenuItemClickListener(mOnMyActionClickListener);
                    delete.setOnMenuItemClickListener(mOnMyActionClickListener);


                }
            };

            itemView.setOnCreateContextMenuListener(mOnCreateContextMenuListener);
            /**seting the data on the row*/
            emptyTextViewOrNot(tvMovieName, movie.getName());
            //Show only the year
            String yearOnly = "";
            if (movie.getReleaseYear().length() > 4) {
                yearOnly = movie.getReleaseYear().substring(0, 4);

            }
            emptyTextViewOrNot(tvMovieYear, yearOnly);
            emptyTextViewOrNot(overView, (movie.getDescription()));

            //0 == no watched  , 1 = watched
            if(movie.getWatched() == 0){
                ivWatchedOrNot.setImageResource(R.mipmap.seemovieicon);
            }else if(movie.getWatched() == 1){
                ivWatchedOrNot.setImageResource(R.mipmap.treuseemovie);
            }

            try {
                //set the image - from storage or with picasso function

                if (movie.getImage().length()> 2) {
                    if(!movie.getImage().contains("app_")){
                        Picasso.with(context).load(GET_MOVIE_POSTER_API + movie.getImage()).into(ivMoviePoster);
                    }else{
                        loadImageFromStorage(movie.getImage(),ivMoviePoster);
                    }
                //default movie
                } else {
                    ivMoviePoster.setImageResource(R.drawable.trailericon);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //clickable/on touch function

            /**Click function -  Clicking on the movie will take you to Activity "editOrShow"
             The details sent (the data set to the row from Task_Get_Movie_List_From_Api)-
             - The ID of the movie = DB id
             - url of the image
             - ID of API You are required to retrieve all movie data directly from a destination
             -"who - to Recognize from were the movie in the 'editOrNotActivity'
             -'editMode' - Tells Activity whether to start the edit mode or not (It's up side down  - true is false and false is true )
             ** The user can not click on the overview*/
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra(DBConstants.COLUMN_NAME_id, movie.get_id());
                    intent.putExtra(DBConstants.COLUMN_NAME_API_ID, movie.getApi_ID());
                    intent.putExtra(DBConstants.COLUMN_NAME_IMAGE, movie.getImage());
                    intent.putExtra("who", "main");
                    intent.putExtra("editmode", false);
                    ((Activity) context).startActivityForResult(intent, 3);
                }
            });
            /**Click function -  Long Click on the movie will open a context menu with 2 option
             * - delete the movie
             * - edit the movie
             ** The user can not click on the overview*/
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.showContextMenu();
                    return true;
                }
            });

            ivWatchedOrNot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBHandler dbHandler = new DBHandler(context);

                    if(movie.getWatched() == 1){
                        ivWatchedOrNot.setImageResource(R.mipmap.seemovieicon);
                        movie.setWatched(0);
                        dbHandler.updateMovie(movie);
                    }else{
                        ivWatchedOrNot.setImageResource(R.mipmap.treuseemovie);
                        movie.setWatched(1);
                        dbHandler.updateMovie(movie);
                    }

                }
            });


        }

/**The function checks whether there is text
 * If true it will be displayed "Missing data" in red
 * if false displayed the source text*/
        public void emptyTextViewOrNot(TextView textView, String str) {
            if (str.equals("")) {
                textView.setText(context.getResources().getString(R.string.empty_text_view));
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));

            } else {
                textView.setText(str);
            }
        }

    }

/**A function that loads the image from the DB*/
    public void loadImageFromStorage(String path, ImageView imageView){

        try {
            File f=new File(path, "poster.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}

