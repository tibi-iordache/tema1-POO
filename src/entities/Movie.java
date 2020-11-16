package entities;

import java.util.ArrayList;

public class Movie extends Video {
    private int duration;

    private ArrayList<Double> rating;

    private Double finalRating;

    public Movie(String title,
                 ArrayList<String> cast,
                 ArrayList<String> genres,
                 int releaseYear,
                 int duration) {
        super(title, releaseYear, cast, genres);
        this. duration = duration;
        this.rating = new ArrayList<>();
    }

    public int getDuration() {
        return duration;
    }

    public Double calculateRating() {
        Double ratingSum = 0d;
        Double ratingNo = Double.valueOf(rating.size());

        for (int i = 0; i < rating.size(); i++) {
            ratingSum = Double.sum(ratingSum, rating.get(i));
        }

        if (ratingNo > 0) {
            return ratingSum / ratingNo;
        }

        return 0d;
    }

    public void addRating(Double grade) {
        rating.add(grade);
    }

    public ArrayList<Double> getRating() {
        return rating;
    }

    @Override
    public Double getFinalRating() {
        return finalRating;
    }

    public void setFinalRating() {
        this.finalRating = calculateRating();
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