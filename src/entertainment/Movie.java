package entertainment;

import databases.UserDataBase;
import java.util.ArrayList;

public final class Movie extends Video {
    private final int duration;

    private ArrayList<Double> ratingsList;

    public Movie() {
        duration = 0;

        ratingsList = null;
    }

    public Movie(final String title,
                 final ArrayList<String> cast,
                 final ArrayList<Genre> genres,
                 final int releaseYear,
                 final int duration) {
        super(title, releaseYear, cast, genres);

        this.duration = duration;

        this.ratingsList = new ArrayList<>();
    }

    @Override
    public Double calculateRating() {
        Double ratingSum = 0d;
        Double ratingNo = Double.valueOf(ratingsList.size());

        // iterate through ratings list
        for (Double ratingsIterator : ratingsList) {
            // add each rating
            ratingSum = Double.sum(ratingSum, ratingsIterator);
        }

        if (ratingNo > 0) {
            // if the video has ratings return the mean
            return ratingSum / ratingNo;
        }

        // else return 0
        return 0d;
    }

    @Override
    public int getNumberOfFavorites(final UserDataBase users) {
        int number = 0;

        // iterate through each user
        for (User userIterator : users.getUsers()) {
            if (userIterator.getFavoriteMovies().contains(this.getTitle())) {
                // increment if the user has added the movie to the favorite list
                number++;
            }
        }

        return number;
    }

    @Override
    public int getNumberOfViews(final UserDataBase users) {
        int number = 0;

        // iterate through each user history
        for (User userIterator : users.getUsers()) {
            if (userIterator.getHistory().containsKey(this.getTitle())) {
                // if the user watched the movie, add the number of views
                number += userIterator.getHistory().get(this.getTitle());
            }
        }

        return number;
    }

    /**
     * Add a grade to the movie rating list.
     *
     * @param grade The grade to be added
     */
    public void addRating(final Double grade) {
        ratingsList.add(grade);
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<Double> getRatingsList() {
        return ratingsList;
    }

    @Override
    public String toString() {
        return "Movie{"
                + "title = " + super.getTitle()
                + ", year = " + super.getReleaseYear()
                + ", genres = " + super.getGenres()
                + ", cast " + super.getCast()
                + ", duration=" + duration
                + ", rating=" + ratingsList
                + '}';
    }
}
