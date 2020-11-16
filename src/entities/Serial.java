package entities;

import java.util.ArrayList;

public class Serial extends Video {
    private int numberOfSeasons;

    private ArrayList<SerialSeason> seasons;

    private Double finalRating;

    public Serial(String title,
                  ArrayList<String> cast,
                  ArrayList<String> genres,
                  int numberOfSeasons,
                  ArrayList<SerialSeason> seasons,
                  int releaseYear) {
        super(title, releaseYear, cast, genres);

        this.numberOfSeasons = numberOfSeasons;

        this.seasons = seasons;
    }

    public Double calculateRating() {
        Double ratingSum = 0d;
        Double ratingNo = Double.valueOf(numberOfSeasons);

        for (int i = 0; i < numberOfSeasons; i++) {
            ratingSum = ratingSum.sum(ratingSum, seasons.get(i).calculateRating());
        }

        if (ratingNo > 0)
            return ratingSum / ratingNo;

        return 0d;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public ArrayList<SerialSeason> getSeasons() {
        return seasons;
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
        return "Serial{" + "title = " + super.getTitle()
                + ", year = " + super.getReleaseYear()
                + ", genres = " + super.getGenres()
                + ", cast " + super.getCast()
                + "numberOfSeasons=" + numberOfSeasons
                + ", seasons=" + seasons
                + ", rating=" + finalRating
                + '}';
    }
}
