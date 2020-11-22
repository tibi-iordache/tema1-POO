package entertainment;

import actions.VideoActions;
import databases.UserDataBase;
import java.util.ArrayList;

public class Video implements VideoActions {
    private final String title;

    private final int releaseYear;

    private final ArrayList<String> cast;

    private final ArrayList<Genre> genres;

    public Video() {
        title = null;
        releaseYear = 0;
        genres = null;
        cast = null;
    }

    public Video(final String title,
                 final int releaseYear,
                 final ArrayList<String> cast,
                 final ArrayList<Genre> genres) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.cast = cast;
        this.genres = genres;
    }

    /**
     * Calculate the rating of a video by doing the mean of the ratings list.
     * For a movie class that will extend this class will calculate the mean of the ratings list.
     * For a serial season class wil calculate the mean of the ratings list.
     * For a serial class will calculate the mean of the seasons ratings.
     *
     * @return Returns the video rating
     */
    @Override
    public Double calculateRating() {
        return 0d;
    }

    /**
     * Calculate the number of appearances of a video in the users favorite list.
     *
     * @param users The users database
     * @return Returns the video number of favorites
     */
    @Override
    public int getNumberOfFavorites(final UserDataBase users) {
        return 0;
    }

    /**
     * Calculate the number of views a video has based on the users history.
     *
     * @param users The users database
     * @return Returns the video number of views
     */
    @Override
    public int getNumberOfViews(final UserDataBase users) {
        return 0;
    }

    /**
     * Returns the video title.
     *
     * @return Returns the video title as a String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the video release year.
     *
     * @return Returns the video release year as an int
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * Returns the video cast.
     *
     * @return Returns the video cast as an ArrayList of Strings
     */
    public ArrayList<String> getCast() {
        return cast;
    }

    /**
     * Returns the video genres.
     *
     * @return Returns the video genres as an ArrayList of Genre
     */
    public ArrayList<Genre> getGenres() {
        return genres;
    }

    /**
     * Returns the video object info
     * @return Returns the video object info as a String
     */
    @Override
    public String toString() {
        return "Video{" + "title='" + title + '\''
                + ", releaseYear=" + releaseYear
                + ", genres=" + genres
                + ", cast=" + cast
                + '}';
    }
}
