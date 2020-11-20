package entertainment;

import databases.UserDataBase;
import java.util.ArrayList;

public final class Serial extends Video {
    private final int numberOfSeasons;

    private ArrayList<SerialSeason> seasons;

    public Serial() {
        numberOfSeasons = 0;

        seasons = null;
    }

    public Serial(final String title,
                  final ArrayList<String> cast,
                  final ArrayList<Genre> genres,
                  final int numberOfSeasons,
                  final ArrayList<SerialSeason> seasons,
                  final int releaseYear) {
        super(title, releaseYear, cast, genres);

        this.numberOfSeasons = numberOfSeasons;

        this.seasons = seasons;
    }

    @Override
    public Double calculateRating() {
        Double ratingSum = 0d;
        Double ratingNo = Double.valueOf(numberOfSeasons);

        // iterate through each season
        for (int i = 0; i < numberOfSeasons; i++) {
            // add to the sum each season rating
            ratingSum = Double.sum(ratingSum, seasons.get(i).calculateRating());
        }

        // return the mean of the ratings
        return ratingSum / ratingNo;
    }

    /**
     * Calculate the serial total duration
     *
     * @return Returns the serial duration
     */
    public int getDuration() {
        int duration = 0;

        // sum each season duration
        for (SerialSeason seasonIterator : seasons) {
            duration += seasonIterator.getDuration();
        }

        return duration;
    }

    @Override
    public int getNumberOfFavorites(final UserDataBase users) {
        int number = 0;

        // iterate through each user
        for (User userIterator : users.getUsers()) {
            if (userIterator.getFavoriteMovies().contains(this.getTitle())) {
                // increment if the user has added the serial to the favorite list
                number++;
            }
        }

        return number;
    }

    @Override
    public int getNumberOfViews(final UserDataBase userDataBase) {
        int number = 0;

        // iterate through each user
        for (User userIterator : userDataBase.getUsers()) {
            if (userIterator.getHistory().containsKey(this.getTitle())) {
                // if the user watched the serial, add the number of views
                number += userIterator.getHistory().get(this.getTitle());
            }
        }

        return number;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public ArrayList<SerialSeason> getSeasons() {
        return seasons;
    }

    @Override
    public String toString() {
        return "Serial{" + "title = " + super.getTitle()
                + ", year = " + super.getReleaseYear()
                + ", genres = " + super.getGenres()
                + ", cast " + super.getCast()
                + "numberOfSeasons=" + numberOfSeasons
                + ", seasons=" + seasons
                + '}';
    }
}
