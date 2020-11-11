package entities;

public class Movie extends Video {
    private int duration;

    private double rating;

    public Movie() {
        duration = 0;
        rating = 0;
    }

    public Movie(int duration, double rating) {
        this. duration = duration;
        this.rating = rating;
    }

    public int getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }
}