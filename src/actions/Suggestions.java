package actions;

import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import java.util.ArrayList;

public interface Suggestions {
    /**
     * Recommends the first video that a user has not seen.
     *
     * @param movies The movies database
     * @param serials The serials database
     * @return Returns the title of the video
     */
    String recommendationStandard(MovieDataBase movies,
                                  SerialDataBase serials);

    /**
     *  Recommends the best rated video that the user has not seen.
     *
     * @param movies The movies database
     * @param serials The serial database
     * @return Returns the title of the video
     */
    String recommendationBestUnseen(MovieDataBase movies,
                                    SerialDataBase serials);

    /**
     *  Recommends the first unseen video from the most popular genre.
     *
     * @param users The users database
     * @param movies The movies database
     * @param serials The serials database
     * @return Returns the title of the video
     */
    String recommendationPopular(UserDataBase users,
                                 MovieDataBase movies,
                                 SerialDataBase serials);

    /**
     * Returns the video with the most occurrences in the users favorite list.
     *
     * @param users The users database
     * @param movies The movies database
     * @param serials The serials database
     * @return Returns the title of the video
     */
    String recommendationFavorite(UserDataBase users,
                                  MovieDataBase movies,
                                  SerialDataBase serials);

    /**
     * Returns all the videos unseen by a user from a specific genre.
     *
     * @param users The users database
     * @param movies The movies database
     * @param serials The serials database
     * @param genre The genre to recommend by
     * @return Returns an ArrayList of Strings with each video title
     */
    ArrayList<String> recommendationSearch(UserDataBase users,
                                           MovieDataBase movies,
                                           SerialDataBase serials,
                                           String genre);
}
