package entities;

import java.util.ArrayList;

public class Movie extends Video {

    private int duration;

    private float rating;

    public Movie(String title,
                 ArrayList<String> cast,
                 ArrayList<String> genres,
                 int releaseYear,
                 int duration) {
        super(title, releaseYear, cast, genres);

        this. duration = duration;
    }



    public int getDuration() {
        return duration;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Movie{" +"title = " + super.getTitle()
                + ", year = " + super.getReleaseYear()
                + ", genres = " + super.getGenres()
                + ", cast " + super.getCast()
                + ", duration=" + duration
                + ", rating=" + rating
                + '}';
    }
}