package entities;

import java.util.ArrayList;

public class Movie extends Video {

    private int duration;

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

    @Override
    public String toString() {
        return "MovieInputData{" + "title= "
                + super.getTitle() + "year= "
                + super.getReleaseYear() + "duration= "
                + duration + "cast {"
                + super.getCast() + " }\n"
                + "genres {" + super.getGenres() + " }\n ";
    }
}