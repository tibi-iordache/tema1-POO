package entertainment;

import databases.UserDataBase;
import java.util.ArrayList;

public final class SerialSeason extends Video {
    private final int currentSeason;

    private final int duration;

    private ArrayList<Double> rating;

    public SerialSeason(final int currentSeason,
                        final int duration) {
        this.currentSeason = currentSeason;

        this.duration = duration;

        this.rating = new ArrayList<>();
    }

    @Override
    public Double calculateRating() {
        Double ratingSum = 0d;
        Double ratingNo = Double.valueOf(rating.size());

        for (Double ratingIterator : rating) {
            ratingSum = Double.sum(ratingSum, ratingIterator);
        }
        if (ratingNo > 0) {
            return ratingSum / ratingNo;
        }

        return 0d;
    }

    /**
     * Adds a grade to the season rating list.
     *
     * @param grade The grade to be added
     */
    public void addRating(final Double grade) {
        rating.add(grade);
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

    public int getCurrentSeason() {
        return currentSeason;
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<Double> getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "ShowSeason{"
                + "currentSeason=" + currentSeason
                + ", duration=" + duration
                + '}';
    }
}
