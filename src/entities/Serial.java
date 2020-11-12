package entities;

import entertainment.Season;

import java.util.ArrayList;

public class Serial extends Video{

    private int numberOfSeasons;

    private ArrayList<Season> seasons;

    public Serial(String title, ArrayList<String> cast,
                  ArrayList<String> genres, int releaseYear,
                  int numberOfSeasons, ArrayList<Season> seasons) {
        super(title, releaseYear, genres, cast);

        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    @Override
    public String toString() {
        return "Serial{" +
                "numberOfSeasons=" + numberOfSeasons +
                ", seasons=" + seasons +
                '}';
    }
}
