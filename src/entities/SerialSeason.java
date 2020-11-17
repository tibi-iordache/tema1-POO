package entities;

import java.util.ArrayList;

public class SerialSeason {
    private int currentSeason;

    private int duration;

    private ArrayList<Double> rating;

    private Double finalRating;

    public SerialSeason(int currentSeason,
                        int duration) {
        this.currentSeason = currentSeason;
        this.duration = duration;
        this.rating = new ArrayList<>();
    }

    public Double calculateRating() {
        Double ratingSum = 0d;
        Double ratingNo = Double.valueOf(rating.size());

        for (int i = 0; i < rating.size(); i++) {
            ratingSum = ratingSum.sum(ratingSum, rating.get(i));
        }
        if (ratingNo > 0) {
            return ratingSum / ratingNo;
        }

        return 0d;
    }

    public void addRating(Double grade) {
        rating.add(grade);
    }

    public int getCurrentSeason() {
        return currentSeason;
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<Double> getRating() {
        return rating;
    }

    public Double getFinalRating() {
        return finalRating;
    }

    public void setFinalRating() {
        this.finalRating = calculateRating();
    }

    @Override
    public String toString() {
        return "ShowSeason{" +
                "currentSeason=" + currentSeason +
                ", duration=" + duration +
                '}';
    }
}
