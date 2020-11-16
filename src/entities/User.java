package entities;

import java.util.*;

import actions.*;
import actor.ActorsAwards;
import common.Constants;

public class User implements Command, Query {
    private String username;

    private String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private Map<String, Double> ratingMovieList;

    private Map<String, Map<Integer, Double>> ratingSeasonList;

    public User() {
    }

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

                return "success";
            }

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

    public String rateMovie(String movie, Double grade, ArrayList<Movie> movieDataBase) {
        if (history.containsKey(movie)) {
            // check if it already has a rating
            if (!ratingMovieList.containsKey(movie)) {
                ratingMovieList.put(movie, grade);

                for (int i = 0; i < movieDataBase.size(); i++) {
                    if (movieDataBase.get(i).getTitle().equals(movie)) {
                        movieDataBase.get(i).addRating(grade);
                    }
                }

                return "success";
            }

            return "already rated";
        }

        return "not seen";
    }

    public String rateShow(String show, int seasonNo, Double grade, ArrayList<Serial> serialDataBase) {
        if(history.containsKey(show)) {
            // check if it already has a rating
            if (ratingSeasonList.containsKey(show)) {
                // check if season has rate
                if (ratingSeasonList.get(show).get(Integer.valueOf(seasonNo)) == null) {
                    Map<Integer, Double> seasonGrade = new HashMap<>();
                    seasonGrade.put(Integer.valueOf(seasonNo), grade);

                    ratingSeasonList.put(show, seasonGrade);

                    for (int i = 0; i < serialDataBase.size(); i++) {
                        if (serialDataBase.get(i).getTitle().equals(show)) {
                            serialDataBase.get(i).getSeasons().get((int)seasonNo - 1).addRating(grade);
                        }
                    }

                    return "success";
                }

                return "already rated";
            }
            else {
                Map<Integer, Double> seasonGrade = new HashMap<>();
                seasonGrade.put(seasonNo, grade);

                ratingSeasonList.put(show, seasonGrade);

                for (int i = 0; i < serialDataBase.size(); i++) {
                    if (serialDataBase.get(i).getTitle().equals(show)) {
                        serialDataBase.get(i).getSeasons().get((int)seasonNo - 1).addRating(grade);
                    }
                }

                return "success";
            }
        }

        return "not seen";
    }

    public ArrayList<User> searchUsersByNumberOfRatings(ArrayList<User> dataBase, int n) {
        ArrayList<User> newDataBase = new ArrayList<>();

        for (User iterator : dataBase) {
            if ((iterator.getRatingMovieList().size() > 0) || (iterator.getRatingSeasonList().size() > 0))
                newDataBase.add(iterator);
        }

        Collections.sort(newDataBase, Comparator.comparing(User::getNumberOfRatings));

        ArrayList<User> firstNUsers = new ArrayList<>(n);

        if (n > newDataBase.size())
            n = newDataBase.size();

        for(int i = 0; i < n; i++) {
            firstNUsers.add(newDataBase.get(i));
        }

        return  firstNUsers;
    }

    public ArrayList<String> searchAverageActors(ArrayList<User> userDataBase,
                                                 ArrayList<Movie> moviesDataBase,
                                                 ArrayList<Serial> serialsDataBase,
                                                 ArrayList<Actor> actorsDataBase,
                                                 int n,
                                                 String sortType) {


        // for each actor
        for (int i = 0; i < actorsDataBase.size(); i++) {
            Double ratingSum = 0d;

            Double ratingsDifferentFromZero = 0d;
            // we calculate the rating
            // we take every video he is in
            ArrayList<String> actorVideos = actorsDataBase.get(i).getFilmography();

            for (String actorVideosIterator : actorVideos) {
                // we take every movie from the data base
                for (Movie movieDataBaseIterator : moviesDataBase) {
                    if (movieDataBaseIterator.getTitle().equals(actorVideosIterator)) {
                        // add the ratings
                        Double movieRating = movieDataBaseIterator.calculateRating();
                        ratingSum = Double.valueOf(ratingSum.sum(ratingSum, movieRating));

                        if (!(Double.compare(movieRating, 0.0) == 0))
                            ratingsDifferentFromZero++;
                    }
                }

                // every serial he is in
                for (Serial serialDataBaseIterator : serialsDataBase) {
                    if (serialDataBaseIterator.getTitle().equals(actorVideosIterator)) {
                        // add the ratings
                        Double serialRating = serialDataBaseIterator.calculateRating();
                        ratingSum = Double.valueOf(ratingSum.sum(ratingSum, serialRating));
                        if (!(Double.compare(serialRating, 0.0) == 0))
                            ratingsDifferentFromZero++;
                    }
                }
            }

            actorsDataBase.get(i).setRating(Double.valueOf(ratingSum / ratingsDifferentFromZero));
        }

        ArrayList<Actor> actorsNenul = new ArrayList<>();

        for (int i = 0; i < actorsDataBase.size(); i++) {
            if (Double.compare(actorsDataBase.get(i).getRating(), Double.valueOf(0.0)) > 0)
                actorsNenul.add(actorsDataBase.get(i));
        }

        if (sortType.equals("asc"))
            Collections.sort(actorsNenul);
        if (sortType.equals("dsc"))
            Collections.sort(actorsNenul, Collections.reverseOrder());

        ArrayList<String> result = new ArrayList<>();

        if (n > actorsNenul.size())
            n = actorsNenul.size();

        for (int i = 0; i < n; i++) {
            result.add(actorsNenul.get(i).getName());
        }

        return result;
    }

    public ArrayList<String> searchActorsByAwards(ArrayList<Actor> actorsDataBase, List<String> awardsSearched, String sortType) {
        ArrayList<Actor> newDataBase = new ArrayList<>();

        for (Actor actorIterator : actorsDataBase) {
            Map<ActorsAwards, Integer> currentActorAwards = actorIterator.getAwards();

            int counter = 0;

            for (int i = 0; i < awardsSearched.size(); i++) {

                if (currentActorAwards.containsKey(ActorsAwards.valueOf(awardsSearched.get(i)))) {
                    counter++;
                }
            }

            if (counter == awardsSearched.size())
                newDataBase.add(actorIterator);
        }

//        Collections.sort(newDataBase, Comparator.comparing(Actor::getAwards).thenComparing(Actor::getName));
        // todo

        ArrayList<String> result = new ArrayList<>();

        for(Actor actorIterator : newDataBase) {
            result.add(actorIterator.getName());
        }

        return result;
    }

    public ArrayList<String> searchVideosByRating(ArrayList<Movie> movieDataBase, ArrayList<Serial> serialDataBase, int n) {
        ArrayList<Video> videoDataBase = new ArrayList<>();

        for (Movie movieIterator : movieDataBase) {
            movieIterator.setFinalRating();

            if (movieIterator.getFinalRating() > 0)
                videoDataBase.add(movieIterator);
        }

        for (Serial serialIterator : serialDataBase) {
            serialIterator.setFinalRating();

            if (serialIterator.getFinalRating() > 0)
                videoDataBase.add(serialIterator);
        }

        Collections.sort(videoDataBase, Comparator.comparing(Video::getFinalRating));

        ArrayList<String> result = new ArrayList<>();

        if (n > videoDataBase.size())
            n = videoDataBase.size();

        for (int i = 0; i < n; i++) {
            result.add(videoDataBase.get(i).getTitle());
        }
        // todo
        return result;
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

    public int getNumberOfRatings() {
        return ratingMovieList.size() + ratingSeasonList.size();
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
