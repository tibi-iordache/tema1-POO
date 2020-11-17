package entities;

import java.util.*;

import actions.*;
import actor.ActorsAwards;
import databases.ActorDataBase;
import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import utils.Utils;

public class User implements Command, Query, Suggestions {
    private String username;

    private String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private Map<String, Double> ratingMovieList;

    private Map<String, Map<Integer, Double>> ratingSeasonList;

    public User() {
        this.username = null;
        this.history = null;
        this.favoriteMovies = null;
        this.ratingMovieList = null;
        this.ratingSeasonList = null;
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
        } else {
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
        if (history.containsKey(show)) {
            // check if it already has a rating
            if (ratingSeasonList.containsKey(show)) {
                // check if season has rate
                if (ratingSeasonList.get(show).get(Integer.valueOf(seasonNo)) == null) {
                    Map<Integer, Double> seasonGrade = new HashMap<>();
                    seasonGrade.put(Integer.valueOf(seasonNo), grade);

                    ratingSeasonList.put(show, seasonGrade);

                    for (int i = 0; i < serialDataBase.size(); i++) {
                        if (serialDataBase.get(i).getTitle().equals(show)) {
                            serialDataBase.get(i).getSeasons().get((int) seasonNo - 1).addRating(grade);
                        }
                    }

                    return "success";
                }

                return "already rated";
            } else {
                Map<Integer, Double> seasonGrade = new HashMap<>();
                seasonGrade.put(seasonNo, grade);

                ratingSeasonList.put(show, seasonGrade);

                for (int i = 0; i < serialDataBase.size(); i++) {
                    if (serialDataBase.get(i).getTitle().equals(show)) {
                        serialDataBase.get(i).getSeasons().get((int) seasonNo - 1).addRating(grade);
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

        newDataBase.sort(Comparator.comparing(User::getNumberOfRatings));

        ArrayList<User> firstNUsers = new ArrayList<>(n);

        if (n > newDataBase.size())
            n = newDataBase.size();

        for (int i = 0; i < n; i++) {
            firstNUsers.add(newDataBase.get(i));
        }

        return firstNUsers;
    }

    public ArrayList<String> listOfMovieToListOfString(ArrayList<Movie> movies, int n) {
        ArrayList<String> result = new ArrayList<>();

        if (n > movies.size())
            n = movies.size();

        for (int i = 0; i < n; i++) {
            result.add(movies.get(i).getTitle());
        }

        return result;
    }

    public ArrayList<String> listOfSerialToListOfString(ArrayList<Serial> serials, int n) {
        ArrayList<String> result = new ArrayList<>();

        if (n > serials.size())
            n = serials.size();

        for (int i = 0; i < n; i++) {
            result.add(serials.get(i).getTitle());
        }

        return result;
    }

    public ArrayList<String> listOfActorsToListOfString(ArrayList<Actor> actors, int n) {
        ArrayList<String> result = new ArrayList<>();

        if (n > actors.size())
            n = actors.size();

        for (int i = 0; i < n; i++) {
            result.add(actors.get(i).getName());
        }

        return result;
    }

    public boolean checkFiltersForVideo(Video video, int year, List<String> genre) {
        boolean check = true;

        if (genre != null) {
            for (String s : genre) {
                if (s != null) {
                    boolean containsGenreCheck = false;

                    if (video instanceof Movie)
                        containsGenreCheck = !((Movie)video).getGenres().contains(Utils.stringToGenre(s));

                    if (video instanceof Serial)
                        containsGenreCheck = !((Serial)video).getGenres().contains(Utils.stringToGenre(s));

                    if (containsGenreCheck) {
                        check = false;

                        break;
                    }
                }
            }

        }

        if (check) {
            if (year == 0)
                return true;
            else  {
                int videoYearCheck = 0;

                if (video instanceof Movie)
                    videoYearCheck = ((Movie)video).getReleaseYear();

                if (video instanceof Serial)
                    videoYearCheck = ((Serial)video).getReleaseYear();


                if (videoYearCheck == year)
                    return true;
            }
        }

        return false;
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
            actorsNenul.sort(Comparator.comparing(Actor::getRating).thenComparing(Actor::getName));

        if (sortType.equals("desc"))
            actorsNenul.sort(Comparator.comparing(Actor::getRating).thenComparing(Actor::getName).reversed());


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
                if (currentActorAwards.containsKey(Utils.stringToAwards(awardsSearched.get(i)))) {
                    counter++;
                }
            }

            if (counter == awardsSearched.size())
                newDataBase.add(actorIterator);
        }

        // we will asume that it works for now
        if (sortType.equals("asc"))
            newDataBase.sort(Comparator.comparing(Actor::getAwardsNo).thenComparing(Actor::getName));

        if (sortType.equals("desc"))
            newDataBase.sort(Comparator.comparing(Actor::getAwardsNo).thenComparing(Actor::getName).reversed());

        ArrayList<String> result = new ArrayList<>();

        for (Actor actorIterator : newDataBase) {
            result.add(actorIterator.getName());
        }

        return result;
    }

    @Override
    public ArrayList<String> searchActorsByFilterDescription(ActorDataBase actors, List<String> words, String sortType) {
        ArrayList<Actor> actorsList = actors.getActors();

        ArrayList<Actor> newActorsDataBase = new ArrayList<>();

        boolean check = true;

        for (Actor actorIterator : actorsList) {
            for (String wordIterator : words) {
                if (!actorIterator.getCareerDescription().contains(wordIterator)) {
                    check = false;

                    break;
                }
            }

            if (check) {
                newActorsDataBase.add(actorIterator);
            }
        }

        Collections.sort(newActorsDataBase, Comparator.comparing(Actor::getName));

        if (sortType.equals("desc")) {
            Collections.sort(newActorsDataBase, Collections.reverseOrder());
        }

        ArrayList<String> resultNames = new ArrayList<>();

        for (Actor dbIterator : newActorsDataBase) {
            resultNames.add(dbIterator.getName());
        }

        return resultNames;
    }

    @Override
    public ArrayList<Movie> searchMoviesByRating(MovieDataBase moviesDataBase, int year, List<String> genre, int n, String sortType) {
        ArrayList<Movie> movies = new ArrayList<>();

        for (Movie movieIterator : moviesDataBase.getMovies()) {
            if (this.checkFiltersForVideo(movieIterator, year, genre) &&
                Double.compare(movieIterator.calculateRating(), 0d) > 0)
                movies.add(movieIterator);
        }

        if (sortType.equals("asc"))
            movies.sort(Comparator.comparing(Movie::calculateRating));

        if (sortType.equals("desc"))
            movies.sort(Collections.reverseOrder());

        return movies;
    }

    @Override
    public ArrayList<Serial> searchSerialsByRating(SerialDataBase serialsDataBase,
                                                   int year,
                                                   List<String> genre,
                                                   int n,
                                                   String sortType) {
        ArrayList<Serial> serials = new ArrayList<>();

        for (Serial serialIterator : serialsDataBase.getSerials()) {
            if (this.checkFiltersForVideo(serialIterator, year, genre) &&
                    Double.compare(serialIterator.calculateRating(), 0d) > 0)
                serials.add(serialIterator);
        }

        if (sortType.equals("asc"))
            serials.sort(Comparator.comparing(Serial::calculateRating));

        if (sortType.equals("desc"))
            serials.sort(Comparator.comparing(Serial::calculateRating).reversed());

        return serials;
    }

    @Override
    public ArrayList<Movie> searchMoviesByFavorite(MovieDataBase moviesDataBase,
                                                    UserDataBase usersDataBase,
                                                    int year,
                                                    List<String> genre,
                                                    int n,
                                                    String sortType) {
        ArrayList<Movie> movies = new ArrayList<>();

        for (Movie movieIterator : moviesDataBase.getMovies()) {
            if (this.checkFiltersForVideo(movieIterator, year, genre) &&
                movieIterator.getNumberOfFavorites(usersDataBase) > 0)
                movies.add(movieIterator);
        }

        if (sortType.equals("asc"))
            movies.sort(Comparator.comparing((movie) -> new Movie().getNumberOfFavorites(usersDataBase)));

        if (sortType.equals("desc"))
            movies.sort(Comparator.comparing((movie) -> new Movie().getNumberOfFavorites(usersDataBase)).reversed());

        return movies;
    }

    @Override
    public ArrayList<Serial> searchSerialsByFavorite(SerialDataBase serialsDataBase,
                                              UserDataBase usersDataBase,
                                              int year,
                                              List<String> genre,
                                              int n,
                                              String sortType) {
        ArrayList<Serial> serials = new ArrayList<>();

        for (Serial serialIterator : serialsDataBase.getSerials()) {
            if (this.checkFiltersForVideo(serialIterator, year, genre) &&
                    serialIterator.getNumberOfFavorites(usersDataBase) > 0)
                serials.add(serialIterator);
        }

        if (sortType.equals("asc"))
            serials.sort(Comparator.comparing((serial) -> new Serial().getNumberOfFavorites(usersDataBase)));

        if (sortType.equals("desc"))
            serials.sort(Comparator.comparing((serial) -> new Serial().getNumberOfFavorites(usersDataBase)).reversed());

        return serials;
    }

    @Override
    public ArrayList<Movie> searchMoviesByDuration(MovieDataBase moviesDataBase,
                                                   int year,
                                                   List<String> genre,
                                                   int n,
                                                   String sortType) {
        ArrayList<Movie> movies = new ArrayList<>();

        for (Movie movieIterator : moviesDataBase.getMovies()) {
            if (this.checkFiltersForVideo(movieIterator, year, genre))
                movies.add(movieIterator);
        }

        if (sortType.equals("asc"))
            movies.sort(Comparator.comparing(Movie::getDuration));

        if (sortType.equals("desc"))
            movies.sort(Comparator.comparing(Movie::getDuration).reversed());

        return movies;
    }

    @Override
    public ArrayList<Serial> searchSerialsByDuration(SerialDataBase serialsDataBase,
                                                     int year,
                                                     List<String> genre,
                                                     int n,
                                                     String sortType) {
        ArrayList<Serial> serials = new ArrayList<>();

        for (Serial serialIterator : serialsDataBase.getSerials()) {
            if (this.checkFiltersForVideo(serialIterator, year, genre))
                serials.add(serialIterator);

        }

        if (sortType.equals("asc"))
            serials.sort(Comparator.comparing(Serial::getDuration));

        if (sortType.equals("desc")) {
            serials.sort(Comparator.comparing(Serial::getDuration).reversed());
        }

        return serials;
    }

    @Override
    public ArrayList<Movie> searchMoviesByViews(MovieDataBase moviesDataBase,
                                                 UserDataBase usersDataBase,
                                                 int year,
                                                 List<String> genre,
                                                 int n,
                                                 String sortType) {
        ArrayList<Movie> movies = new ArrayList<>();

        for (Movie movieIterator : moviesDataBase.getMovies()) {
            if (this.checkFiltersForVideo(movieIterator, year, genre) &&
                    movieIterator.getNumberOfViews(usersDataBase) > 0)
                movies.add(movieIterator);
        }

        if (sortType.equals("asc"))
            movies.sort(Comparator.comparing((movie) -> new Movie().getNumberOfViews(usersDataBase)));

        if (sortType.equals("desc"))
            movies.sort(Comparator.comparing((movie) -> new Movie().getNumberOfViews(usersDataBase)).reversed());

        return movies;
    }

    @Override
    public ArrayList<Serial> searchSerialsByViews(SerialDataBase serialsDataBase,
                                                  UserDataBase usersDataBase,
                                                  int year,
                                                  List<String> genre,
                                                  int n,
                                                  String sortType) {
        ArrayList<Serial> serials = new ArrayList<>();

        for (Serial serialIterator : serialsDataBase.getSerials()) {
            if (this.checkFiltersForVideo(serialIterator, year, genre) &&
                    serialIterator.getNumberOfViews(usersDataBase) > 0)
                serials.add(serialIterator);
        }

        if (sortType.equals("asc"))
            serials.sort(Comparator.comparing((serial) -> new Serial().getNumberOfViews(usersDataBase)));

        if (sortType.equals("desc"))
            serials.sort(Comparator.comparing((serial) -> new Serial().getNumberOfViews(usersDataBase)).reversed());

        return serials;
    }

    @Override
    public String recommendationBestUnseen(UserDataBase userDataBase, MovieDataBase movies, SerialDataBase serials) {
//        ArrayList<String> notSeenDataBase = this.searchMoviesByRating()
        return null;
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
