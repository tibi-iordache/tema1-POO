package entities;

import databases.UserDataBase;
import entertainment.Genre;

import java.util.ArrayList;

public class Serial extends Video {
    private int numberOfSeasons;

    private ArrayList<SerialSeason> seasons;

    private Double finalRating;

    public Serial() {

    }

    public Serial(String title,
                  ArrayList<String> cast,
                  ArrayList<Genre> genres,
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

    public int getDuration() {
        int duration = 0;

        for (SerialSeason seasonIterator : seasons) {
            duration += seasonIterator.getDuration();
        }

        return duration;
    }

    public int getNumberOfFavorites(UserDataBase usersDataBase) {
        int number = 0;

        for(User userIterator : usersDataBase.getUsers()) {
            if (userIterator.getFavoriteMovies().contains(this.getTitle()))
                number++;
        }

        return number;
    }

    public int getNumberOfViews(UserDataBase userDataBase) {
        int number = 0;

        for (User userIterator : userDataBase.getUsers()) {
            if (userIterator.getHistory().containsKey(this.getTitle())) {
                number += userIterator.getHistory().get(this.getTitle());
            }
        }

        return number;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public ArrayList<SerialSeason> getSeasons() {
        return seasons;
    }

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
