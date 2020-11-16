package actions;

import entities.Movie;
import entities.Serial;

import java.util.ArrayList;

public interface Command {
    String addVideoToFavorite(String video);

    void viewVideo(String video);

    String rateMovie(String movie, Double grade, ArrayList<Movie> movieDataBase);

    String rateShow(String show, int seasonNo, Double grade, ArrayList<Serial> serialDataBase);
}
