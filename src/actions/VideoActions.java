package actions;

import databases.UserDataBase;

public interface VideoActions {
    /**
     * Calculate the rating of a video by doing the mean of the ratings list.
     *
     * @return Returns the video rating
     */
    Double calculateRating();

    /**
     * Calculate the number of appearances of a video in the users favorite list.
     *
     * @param users The users database
     * @return Returns the video number of favorites
     */
    int getNumberOfFavorites(UserDataBase users);

    /**
     * Calculate the number of views a video has based on the users history.
     *
     * @param users The users database
     * @return Returns the video number of views
     */
    int getNumberOfViews(UserDataBase users);
}
