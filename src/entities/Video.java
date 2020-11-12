package entities;

import java.util.ArrayList;

public class Video {

    private String title;

    private int releaseYear;

    private ArrayList<String> genres;

    private ArrayList<String> cast;

    public Video() {
        title = null;
        releaseYear = 0;
        genres = null;
        cast = null;
    }

    public Video(String title,
                 int releaseYear,
                 ArrayList<String> genres,
                 ArrayList<String> cast) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.cast = cast;
    }

    public String getTitle() {
        return title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    @Override
    public String toString() {
        return "Video{" +
                "title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", genres=" + genres +
                ", cast=" + cast +
                '}';
    }
}