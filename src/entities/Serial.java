package entities;

import entertainment.Season;

import java.util.ArrayList;

public class Serial extends Video{

    private int numberOfSeasons;

    private ArrayList<Season> seasons;

    private float rating;

    public Serial(String title,
                  ArrayList<String> cast,
                  ArrayList<String> genres,
                  int numberOfSeasons,
                  ArrayList<Season> seasons,
                  int releaseYear) {
        super(title, releaseYear, cast, genres);

        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Serial{" + "title = " + super.getTitle()
                + ", year = " + super.getReleaseYear()
                + ", genres = " + super.getGenres()
                + ", cast " + super.getCast()
                + "numberOfSeasons=" + numberOfSeasons
                + ", seasons=" + seasons
                + ", rating=" + rating
                + '}';
    }
}
