package entities;

import java.util.*;
import java.util.stream.Collectors;

import actions.*;
import actor.ActorsAwards;
import databases.ActorDataBase;
import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import entertainment.Genre;
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

                for (Movie value : movieDataBase) {
                    if (value.getTitle().equals(movie)) {
                        value.addRating(grade);
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
                if (ratingSeasonList.get(show).get(seasonNo) == null) {
                    Map<Integer, Double> seasonGrade = new HashMap<>();
                    seasonGrade.put(seasonNo, grade);

                    ratingSeasonList.put(show, seasonGrade);

                    for (Serial serial : serialDataBase) {
                        if (serial.getTitle().equals(show)) {
                            serial.getSeasons().get(seasonNo - 1).addRating(grade);
                        }
                    }

                    return "success";
                }

                return "already rated";
            } else {
                Map<Integer, Double> seasonGrade = new HashMap<>();
                seasonGrade.put(seasonNo, grade);

                ratingSeasonList.put(show, seasonGrade);

                for (Serial serial : serialDataBase) {
                    if (serial.getTitle().equals(show)) {
                        serial.getSeasons().get(seasonNo - 1).addRating(grade);
                    }
                }

                return "success";
            }
        }

        return "not seen";
    }

    public ArrayList<User> searchUsersByNumberOfRatings(ArrayList<User> dataBase, int n, String sortType) {
        ArrayList<User> newDataBase = new ArrayList<>();

        for (User iterator : dataBase) {
            if ((iterator.getRatingMovieList().size() > 0) || (iterator.getRatingSeasonList().size() > 0))
                newDataBase.add(iterator);
        }

        if (sortType.equals("asc"))
            newDataBase.sort(Comparator.comparing(User::getNumberOfRatings));
        if (sortType.equals("desc"))
            newDataBase.sort(Comparator.comparing(User::getNumberOfRatings).reversed());

        ArrayList<User> firstNUsers = new ArrayList<>();

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


    public ArrayList<Actor> searchAverageActors(ArrayList<User> userDataBase,
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

        return actorsNenul;
    }

    public ArrayList<Actor> searchActorsByAwards(ArrayList<Actor> actorsDataBase, List<String> awardsSearched, String sortType) {
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

        return newDataBase;
    }

    @Override
    public ArrayList<Actor> searchActorsByFilterDescription(ActorDataBase actors, List<String> words, String sortType) {
        ArrayList<Actor> actorsList = actors.getActors();

        ArrayList<Actor> newActorsDataBase = new ArrayList<>();



        for (Actor actorIterator : actorsList) {
            boolean check = true;

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

        if (sortType.equals("asc"))
            newActorsDataBase.sort(Comparator.comparing(Actor::getName));

        if (sortType.equals("desc"))
            newActorsDataBase.sort(Comparator.comparing(Actor::getName).reversed());

        return newActorsDataBase;
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
                movieIterator.getNumberOfFavorites(usersDataBase.getUsers()) > 0)
                movies.add(movieIterator);
        }

        if (sortType.equals("asc"))
            movies.sort(Comparator.comparing((movie) -> new Movie().getNumberOfFavorites(usersDataBase.getUsers())));

        if (sortType.equals("desc"))
            movies.sort(Comparator.comparing((movie) -> new Movie().getNumberOfFavorites(usersDataBase.getUsers())).reversed());

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
                    serialIterator.getNumberOfFavorites(usersDataBase.getUsers()) > 0)
                serials.add(serialIterator);
        }

        if (sortType.equals("asc"))
            serials.sort(Comparator.comparing((serial) -> new Serial().getNumberOfFavorites(usersDataBase.getUsers())));

        if (sortType.equals("desc"))
            serials.sort(Comparator.comparing((serial) -> new Serial().getNumberOfFavorites(usersDataBase.getUsers())).reversed());

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
    public String recommendationStandard(User user, MovieDataBase movies, SerialDataBase serials) {
        for (Movie movieIterator : movies.getMovies()) {
            if (!user.getHistory().containsKey(movieIterator.getTitle()))
                return movieIterator.getTitle();
        }

        for (Serial serialIterator : serials.getSerials()) {
            if (!user.getHistory().containsKey(serialIterator.getTitle()))
                return serialIterator.getTitle();
        }

        return null;
    }

    @Override
    public String recommendationBestUnseen(User user, MovieDataBase movies, SerialDataBase serials) {
        // create the video data base first
        ArrayList<Movie> movieRatingDataBase = movies.getMovies();

        movieRatingDataBase.sort(Comparator.comparing(Movie::calculateRating));

        ArrayList<Serial> serialRatingDataBase = serials.getSerials();

        serialRatingDataBase.sort(Comparator.comparing(Serial::calculateRating));

        String result = null;

        for (Movie movieIterator : movieRatingDataBase) {
            if (!user.getHistory().containsKey(movieIterator.getTitle()))
                return movieIterator.getTitle();
        }

        for (Serial serialIterator : serialRatingDataBase) {
            if (!user.getHistory().containsKey(serialIterator.getTitle()))
                return serialIterator.getTitle();
        }

        return result;
    }

    public ArrayList<Genre> calculateTopGenres(UserDataBase users,
                                               MovieDataBase movies,
                                               SerialDataBase serials) {
        Genre[] enums = Genre.values();

        Map<Genre, Integer> eachGenrePopularity = new HashMap<>();

        for (Genre enumsIterator : enums) {
            int genrePopularity = 0;

            for (Movie movieIterator : movies.getMovies()) {
                if (movieIterator.getGenres().contains(enumsIterator)) {
                    genrePopularity += movieIterator.getNumberOfViews(users);
                }
            }

            for (Serial serialIterator : serials.getSerials()) {
                if (serialIterator.getGenres().contains(enumsIterator)) {
                    genrePopularity += serialIterator.getNumberOfViews(users);
                }
            }

            eachGenrePopularity.put(enumsIterator, genrePopularity);
        }

        List<Map.Entry<Genre, Integer>> sortedGenres =
                new LinkedList<Map.Entry<Genre, Integer>>(eachGenrePopularity.entrySet());

        Collections.sort(sortedGenres, new Comparator<Map.Entry<Genre, Integer>>() {
            @Override
            public int compare(Map.Entry<Genre, Integer> o1, Map.Entry<Genre, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        }.reversed());

        ArrayList<Genre> result = new ArrayList<>();

        for (Map.Entry<Genre, Integer> iterator : sortedGenres) {
            result.add(iterator.getKey());
        }

        return result;
    }

    @Override
    public String recommendationPopular(String userName,
                                        UserDataBase users,
                                        MovieDataBase movies,
                                        SerialDataBase serials) {
        ArrayList<Genre> topGenres = this.calculateTopGenres(users, movies, serials);

        for (User userIterator : users.getUsers()) {
            if (userIterator.getUsername().equals(userName)) {
                if (userIterator.getSubscriptionType().equals("PREMIUM")) {
                    for (Genre genreIterator : topGenres) {
                        for (Movie movieIterator : movies.getMovies()) {
                            if (movieIterator.getGenres().contains(genreIterator))
                                if (!userIterator.getHistory().containsKey(movieIterator.getTitle()))
                                    return movieIterator.getTitle();
                        }

                        for (Serial serialIterator : serials.getSerials()) {
                            if (serialIterator.getGenres().contains(genreIterator) &&
                                    !userIterator.getHistory().containsKey(serialIterator.getTitle()))
                                return serialIterator.getTitle();
                        }
                    }
                }
                else
                    return "cannot be applied!";
            }
        }

        return "cannot be applied!";
    }

    @Override
    public String recommendationFavorite(String username, UserDataBase users, MovieDataBase movies, SerialDataBase serials) {
        ArrayList<Movie> movieFavoriteDataBase = movies.getMovies();

        // might not need to all the movies, one with no favorites mabye should not be included
        movieFavoriteDataBase.sort(Comparator.comparing((movie) -> new Movie().getNumberOfFavorites(users.getUsers())).reversed());

        ArrayList<Serial> serialFavoriteDataBase = serials.getSerials();

        serialFavoriteDataBase.sort(Comparator.comparing((serial) -> new Serial().getNumberOfFavorites(users.getUsers())).reversed());

        for (User userIterator : users.getUsers()) {
            if (userIterator.getUsername().equals(username) &&
                    userIterator.getSubscriptionType().equals("PREMIUM")) {
                for (Movie movieIterator : movieFavoriteDataBase) {
                    if (!userIterator.getFavoriteMovies().contains(movieIterator.getTitle()))
                        return movieIterator.getTitle();
                }

                for (Serial serialIterator : serialFavoriteDataBase) {
                    if (!userIterator.getFavoriteMovies().contains(serialIterator.getTitle()))
                        return serialIterator.getTitle();
                }
            }
        }

        return null;
    }

    @Override
    public ArrayList<String> recommendationSearch(String userName, UserDataBase users, MovieDataBase movies, SerialDataBase serials, String genre) {
        for (User userIterator : users.getUsers()) {
            if (userIterator.getUsername().equals(userName)) {
                ArrayList<Movie> moviesByGenre = new ArrayList<>();

                for (Movie movieIterator : movies.getMovies())
                    if (movieIterator.getGenres().contains(Utils.stringToGenre(genre)))
                        moviesByGenre.add(movieIterator);

                ArrayList<Serial> serialByGenre = new ArrayList<>();

                for (Serial serialIterator : serials.getSerials())
                    if (serialIterator.getGenres().contains(Utils.stringToGenre(genre)))
                        serialByGenre.add(serialIterator);

                moviesByGenre.sort(Comparator.comparing(Movie::calculateRating).thenComparing(Movie::getTitle));

                serialByGenre.sort(Comparator.comparing(Serial::calculateRating).thenComparing(Serial::getTitle));

                ArrayList<String> result = new ArrayList<>();

                for (Movie movieIterator : moviesByGenre)
                    if (!userIterator.getHistory().containsKey(movieIterator.getTitle()))
                        result.add(movieIterator.getTitle());

                for (Serial serialIterator : serialByGenre)
                    if (!userIterator.getHistory().containsKey(serialIterator.getTitle()))
                        result.add(serialIterator.getTitle());

                return result;
            }
        }

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
