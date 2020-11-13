package entities;

import java.util.ArrayList;

public class Video {

    private String title;

    private int releaseYear;

    private ArrayList<String> cast;

    private ArrayList<String> genres;

    public Video() {
        title = null;
        releaseYear = 0;
        genres = null;
        cast = null;
    }

    public Video(String title,
                 int releaseYear,
                 ArrayList<String> cast,
                 ArrayList<String> genres) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.cast = cast;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public ArrayList<String> getGenres() {
        return genres;
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