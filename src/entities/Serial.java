package entities;

import entertainment.Season;

import java.util.ArrayList;

public class Serial extends Video{

    private int numberOfSeasons;

    private ArrayList<Season> seasons;

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

    @Override
    public String toString() {
        return "SerialInputData{" + " title= "
                + super.getTitle() + " " + " year= "
                + super.getReleaseYear() + " cast {"
                + super.getCast() + " }\n" + " genres {"
                + super.getGenres() + " }\n "
                + " numberSeason= " + numberOfSeasons
                + ", seasons=" + seasons + "\n\n" + '}';
    }

}
