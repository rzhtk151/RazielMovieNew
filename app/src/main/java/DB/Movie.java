package DB;

import java.util.ArrayList;

/**
 * This class contains within it the elements of a film object
 */

public class Movie {
    private int _id;
    private String name;
    private String Description;
    private String image;
    private String director;
    private String releaseYear;
    private String trailer;
    private String imdb;
    private double rating;
    private ArrayList <String> genre;
    private String movieSource;
    private int watched;
    private int api_ID;

    public Movie( String name, String description, String image, String director,  String releaseYear,  String movieSource) {
        this.name = name;
        this.Description = description;
        this.image = image;
        this.director = director;
        this.releaseYear = releaseYear;
        this.movieSource = movieSource;
    }

    public Movie(int _id, String name, String description, String image, String director, String releaseYear, String trailer, String imdb, double rating, String movieSource, int watched, int api_ID) {
        this._id = _id;
        this.name = name;
        Description = description;
        this.image = image;
        this.director = director;
        this.releaseYear = releaseYear;
        this.trailer = trailer;
        this.imdb = imdb;
        this.rating = rating;
        this.movieSource = movieSource;
        this.watched = watched;
        this.api_ID = api_ID;
    }

    public Movie( String name, String description, String image, String director,  String releaseYear, String trailer, String imdb, double rating, String movieSource, int api_ID) {
        this.name = name;
        this.Description = description;
        this.image = image;
        this.director = director;
        this.releaseYear = releaseYear;
        this.trailer = trailer;
        this.imdb = imdb;
        this.rating = rating;
        this.movieSource = movieSource;
        this.api_ID = api_ID;
    }
    public Movie(int _id,String name, String description,String image, String director, String releaseYear) {
        this._id = _id;
        this.name = name;
        this.Description = description;
        this.image = image;
        this.director = director;
        this.releaseYear = releaseYear;
    }

    public Movie(String name, String description, String releaseYear, int api_ID, String image,double rating ) {
        this.name = name;
        Description = description;
        this.releaseYear = releaseYear;
        this.api_ID = api_ID;
        this.image = image;
        this.rating = rating;
    }


    public Movie(String name, String description, String releaseYear, int api_ID, String image) {
        this.name = name;
        Description = description;
        this.releaseYear = releaseYear;
        this.api_ID = api_ID;
        this.image = image;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public String movieSource() {
        return movieSource;
    }

    public void setMovieSource(String movieSource) {
        this.movieSource = movieSource;
    }

    public int getWatched() {
        return watched;
    }

    public void setWatched(int watched) {
        this.watched = watched;
    }

    public int getApi_ID() {
        return api_ID;
    }

    public void setApi_ID(int api_ID) {
        this.api_ID = api_ID;
    }

    @Override
    public String toString() {
        return "Movie - " + name;
    }
}
