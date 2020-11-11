package entities;

import java.util.ArrayList;

public class Video {

    private String title;

    private int releaseYear;

    private ArrayList<String> genres;

    public Video() {
        title = null;
        releaseYear = 0;
        genres = null;
    }

    public Video(String title, int releaseYear, ArrayList<String> genres) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.genres = genres;
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
}