package actions;

import entertainment.Movie;
import entertainment.Serial;
import java.util.ArrayList;

public interface Command {
    /**
     *  Adds a video to the user favorite videos list, only if the user has
     * watched the video.
     *
     * @param video The name of the video which will be added
     * @return The result of the operation(success/duplicate/not seen)
     */
    String addVideoToFavorite(String video);

    /**
     *  Adds a video to the user history. If the user has already watched the video,
     * the number of views will be increased.
     *
     * @param video The name of the video which will be added
     */
    void viewVideo(String video);

    /**
     *  Gives a movie a rating, only if the user watched the movie.
     *
     * @param movie The name of the movie
     * @param grade The rating that a user wants to give
     * @param moviesDataBase The movie database
     * @return The result of the operation(success/already rated/not seen)
     */
    String rateMovie(String movie, Double grade, ArrayList<Movie> moviesDataBase);

    /**
     *  Gives a serial season a rating, only if the user watched the serial.
     *
     * @param serialName The name of the serial
     * @param seasonNo The number of the season
     * @param grade The rating which the user want to give
     * @param serialsDataBase The serial database
     * @return The result of the operation(success/already rated/not seen)
     */
    String rateShow(String serialName, int seasonNo, Double grade,
                    ArrayList<Serial> serialsDataBase);
}
