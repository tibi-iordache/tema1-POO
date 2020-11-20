package actor;

import databases.MovieDataBase;
import databases.SerialDataBase;
import entertainment.Movie;
import entertainment.Serial;
import java.util.ArrayList;
import java.util.Map;

public final class Actor {
    private final String name;

    private final String careerDescription;

    private final ArrayList<String> filmography;

    private final Map<ActorsAwards, Integer> awards;

    public Actor(final String name,
                 final String careerDescription,
                 final ArrayList<String> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;

        this.careerDescription = careerDescription;

        this.filmography = filmography;

        this.awards = awards;
    }

    /**
     * Calculate the total number of awards of an actor.
     *
     * @return The number of awards
     */
    public int getAwardsNo() {
        int number = 0;

        // iterate through the map
        for (Map.Entry<ActorsAwards, Integer> iterator : this.getAwards().entrySet()) {
            // add the key value
            number += iterator.getValue();
        }

        return number;
    }

    /**
     * Calculate the actor rating based on the videos he/she played in.
     *
     * @param movies The movies in which the actor played
     * @param serials The serials in which the actor played
     * @return The actor rating
     */
    public Double calculateRating(final MovieDataBase movies,
                                  final SerialDataBase serials) {
        double ratingsSum = 0d;
        double ratingsCounter = 0d;

        // iterate through each video
        for (Movie movieIterator : movies.getMovies()) {
            // check if the actor played in the video
            if (movieIterator.getCast().contains(this.getName())) {
                // check if the movie is rated
                if (Double.compare(movieIterator.calculateRating(), 0d) > 0) {
                    // add the rate to the actor rating sum
                    ratingsSum += movieIterator.calculateRating();

                    ratingsCounter++;
                }
            }
        }

        for (Serial serialIterator : serials.getSerials()) {
            if (serialIterator.getCast().contains(this.getName())) {
                if (Double.compare(serialIterator.calculateRating(), 0d) > 0) {
                    ratingsSum += serialIterator.calculateRating();

                    ratingsCounter++;
                }
            }
        }

        // check if the actor has any ratings
        if (Double.compare(ratingsCounter, 0d) == 0) {
            return 0d;
        } else {
            // if it does, calculate the mean of the ratings
            return ratingsSum / ratingsCounter;
        }
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    @Override
    public String toString() {
        return "Actor{" + "name='" + name + '\''
                + ", careerDescription='" + careerDescription + '\''
                + ", filmography=" + filmography
                + ", awards=" + awards
                + '}';
    }
}
