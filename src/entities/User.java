package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import actions.*;
import entertainment.Season;

public class User implements command {

    private String username;

    private String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private Map<String, Double> ratingMovieList;

    private Map<String, Map<Integer, Double>> ratingSeasonList;

    public User(String username,
                String subscriptionType,
                Map<String, Integer> history,
                ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
        this.ratingMovieList = new HashMap<>();
        this.ratingSeasonList = new HashMap<>();
    }

    public String addVideoToFavorite(String video) {
        if (history.containsKey(video)) {
            if (!favoriteMovies.contains(video)) {
                favoriteMovies.add(video);

                return "succes";
            }
            else
                return "duplicate";
        }

        return "not seen";
    }

    public void viewVideo(String video) {
        if (history.containsKey(video)) {
            history.put(video, history.get(video) + 1);
        }
        else {
            history.put(video, 1);
        }
    }

    public void rateMovie(String movie, Double grade) {
        if (history.containsKey(movie)) {
            // check if it already has a rating
            if (!ratingMovieList.containsKey(movie)) {
                ratingMovieList.put(movie, grade);
            }
        }
    }

    public void rateShow(String show, Integer seasonNo, Double grade) {
        if(history.containsKey(show)) {
            // check if it already has a rating
            if (!ratingSeasonList.containsKey(show)) {
                Map<Integer, Double> seasonGrade = new HashMap<>();
                seasonGrade.put(seasonNo, grade);

                ratingSeasonList.put(show, seasonGrade);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public Map<String, Double> getRatingMovieList() {
        return ratingMovieList;
    }

    public Map<String, Map<Integer, Double>> getRatingSeasonList() {
        return ratingSeasonList;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", history=" + history +
                ", favoriteMovies=" + favoriteMovies +
                ", ratingMovieList=" + ratingMovieList +
                ", ratingSeasonList=" + ratingSeasonList +
                '}';
    }
}
