package entities;

import java.util.ArrayList;

public class Movie extends Video {

    private int duration;

    private double rating;

    public Movie(String title,
                 int releaseYear,
                 ArrayList<String> genres,
                 ArrayList<String> cast,
                 int duration, double rating) {
        super(title, releaseYear, genres, cast);

        this. duration = duration;
        this.rating = rating;
    }

    public int getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "duration=" + duration +
                ", rating=" + rating +
                '}';
    }
}