package entities;

import java.awt.image.VolatileImage;
import java.rmi.MarshalledObject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
            if (iterator.getNumberOfRatings() > 0)
                newDataBase.add(iterator);
        }

        if (sortType.equals("asc"))
            newDataBase.sort(Comparator.comparing(User::getNumberOfRatings).thenComparing(User::getUsername));
        if (sortType.equals("desc"))
            newDataBase.sort(Comparator.comparing(User::getNumberOfRatings).thenComparing(User::getUsername).reversed());

        ArrayList<User> firstNUsers = new ArrayList<>();

        if (n > newDataBase.size())
            n = newDataBase.size();

        for (int i = 0; i < n; i++) {
            firstNUsers.add(newDataBase.get(i));
        }

        return firstNUsers;
    }

    public ArrayList<String> listOfVideoToListOfString(ArrayList<Video> videos, int n) {
        ArrayList<String> result = new ArrayList<>();

        if (n > videos.size())
            n = videos.size();

        for (int i = 0; i < n; i++) {
            result.add(videos.get(i).getTitle());
        }

        return result;
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


    public ArrayList<Actor> searchAverageActors(MovieDataBase movies,
                                                 SerialDataBase serials,
                                                 ActorDataBase actors,
                                                 int n,
                                                 String sortType) {

        ArrayList<Actor> actorsNenul = new ArrayList<>();

        // for each actor
        for (Actor actor : actors.getActors()) {
            double actorRating = actor.calculateRating(movies, serials);
            if (Double.compare(actorRating, 0d) > 0)
                actorsNenul.add(actor);
        }

        if (sortType.equals("asc"))
            actorsNenul.sort(Comparator.comparing((actor) -> ((Actor)actor).calculateRating(movies, serials))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return ((Actor)o1).getName().compareTo(((Actor)o2).getName());
                        }
                    }));

        if (sortType.equals("desc"))
            actorsNenul.sort(Comparator.comparing((actor) -> ((Actor)actor).calculateRating(movies, serials))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return ((Actor)o1).getName().compareTo(((Actor)o2).getName());
                        }
                    })
                    .reversed());

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
        ArrayList<Actor> newActorsDataBase = new ArrayList<>();

        for (Actor actorIterator : actors.getActors()) {
            boolean check = true;

            for (String wordIterator : words) {
                Pattern pattern = Pattern.compile("[^a-z]" +  wordIterator + "[^a-z]", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(actorIterator.getCareerDescription());
                boolean matchFound = matcher.find();

                if (!matchFound) {
                    check = false;
                    break;
                }

//                if (!actorIterator.getCareerDescription().toLowerCase().contains(" " + wordIterator.toLowerCase() + " ")) {
//                    check = false;
//
//                    break;
//                }
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

        // IT WORKS NOW!!!!!

        if (sortType.equals("asc"))
            movies.sort(Comparator.comparing((movie) -> ((Movie)movie).getNumberOfFavorites(usersDataBase.getUsers()))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return ((Movie)o1).getTitle().compareTo(((Movie)o2).getTitle());
                        }
                    }));

        if (sortType.equals("desc"))
            movies.sort(Comparator.comparing((movie) -> ((Movie)movie).getNumberOfFavorites(usersDataBase.getUsers()))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Movie)o1).getTitle()).compareTo(((Movie)o2).getTitle());
                        }
                    }).reversed());

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
            serials.sort(Comparator.comparing((serial) -> ((Serial)serial).getNumberOfFavorites(usersDataBase.getUsers()))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Serial)o1).getTitle()).compareTo(((Serial)o2).getTitle());
                        }
                    }));

        if (sortType.equals("desc"))
            serials.sort(Comparator.comparing((serial) -> ((Serial)serial).getNumberOfFavorites(usersDataBase.getUsers()))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Serial)o1).getTitle()).compareTo(((Serial)o2).getTitle());
                        }
                    }).reversed());

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
            movies.sort(Comparator.comparing(Movie::getDuration)
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Movie)o1).getTitle()).compareTo(((Movie)o2).getTitle());
                        }
                    }));

        if (sortType.equals("desc"))
            movies.sort(Comparator.comparing(Movie::getDuration)
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Movie)o1).getTitle()).compareTo(((Movie)o2).getTitle());
                        }
                    })
                    .reversed());

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
            serials.sort(Comparator.comparing(Serial::getDuration)
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Serial)o1).getTitle()).compareTo(((Serial)o2).getTitle());
                        }
                    }));

        if (sortType.equals("desc")) {
            serials.sort(Comparator.comparing(Serial::getDuration)
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Serial)o1).getTitle()).compareTo(((Serial)o2).getTitle());
                        }
                    })
                    .reversed());
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
            movies.sort(Comparator.comparing((movie) -> ((Movie)movie).getNumberOfViews(usersDataBase))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Movie)o1).getTitle()).compareTo(((Movie)o2).getTitle());
                        }
                    }));

        if (sortType.equals("desc"))
            movies.sort(Comparator.comparing((movie) -> ((Movie)movie).getNumberOfViews(usersDataBase))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Movie)o1).getTitle()).compareTo(((Movie)o2).getTitle());
                        }
                    })
                    .reversed());

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
            serials.sort(Comparator.comparing((serial) -> ((Serial)serial).getNumberOfViews(usersDataBase))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Serial)o1).getTitle()).compareTo(((Serial)o2).getTitle());
                        }
                    }));

        if (sortType.equals("desc"))
            serials.sort(Comparator.comparing((serial) -> ((Serial)serial).getNumberOfViews(usersDataBase))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return (((Serial)o1).getTitle()).compareTo(((Serial)o2).getTitle());
                        }
                    })
                    .reversed());

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

        return "cannot be applied!";
    }

    @Override
    public String recommendationBestUnseen(User user, MovieDataBase movies, SerialDataBase serials) {
        // group all the videos
        ArrayList<Video> videos = new ArrayList<>();

        videos.addAll(movies.getMovies());

        videos.addAll(serials.getSerials());

        // sort by the rating, in descending way
        videos.sort(Comparator.comparing((video) -> {
            double n = 0d;
            if (video instanceof Movie)
                n = ((Movie)video).calculateRating();

            if (video instanceof Serial)
                n = ((Serial)video).calculateRating();

            return n;
        }).reversed());

        // iterate through the videos until we find one that's unseen by the user
        for (Video videoIterator : videos)
            if (!user.getHistory().containsKey(videoIterator.getTitle()))
                return videoIterator.getTitle();

        // if we didn't find any video, return error message
        return "cannot be applied!";
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
        ArrayList<Video> videos = new ArrayList<>();

        videos.addAll(movies.getMovies());
        videos.addAll(serials.getSerials());

        videos.sort(Comparator.comparing((video) -> {
            int n = 0;
            if (video instanceof Movie)
                n = ((Movie)video).getNumberOfFavorites(users.getUsers());
            if (video instanceof Serial)
                n = ((Serial)video).getNumberOfFavorites(users.getUsers());

            return n;
        }).reversed());

        for (User userIterator : users.getUsers()) {
            if (userIterator.getUsername().equals(username) &&
                    userIterator.getSubscriptionType().equals("PREMIUM")) {
                for (Video videoIterator : videos)
                    if (!userIterator.getHistory().containsKey(videoIterator.getTitle()))
                        return videoIterator.getTitle();
            }
        }

        return "cannot be applied!";
    }

    @Override
    public ArrayList<String> recommendationSearch(String userName,
                                                  UserDataBase users,
                                                  MovieDataBase movies,
                                                  SerialDataBase serials,
                                                  String genre) {
        for (User userIterator : users.getUsers()) {
            if (userIterator.getUsername().equals(userName)) {
                ArrayList<Video> result = new ArrayList<>();

                for (Movie movieIterator : movies.getMovies())
                    if (movieIterator.getGenres().contains(Utils.stringToGenre(genre)) &&
                            !userIterator.getHistory().containsKey(movieIterator.getTitle()))
                        result.add(movieIterator);

                for (Serial serialIterator : serials.getSerials())
                    if (serialIterator.getGenres().contains(Utils.stringToGenre(genre)) &&
                            !userIterator.getHistory().containsKey(serialIterator.getTitle()))
                        result.add(serialIterator);

                result.sort(Comparator.comparing((v) -> {
                    double n = 0d;
                    if (v instanceof Movie)
                         n = ((Movie)v).calculateRating();
                    else
                        n = ((Serial)v).calculateRating();

                    return n;
                })
                        .thenComparing((v) -> ((Video)v).getTitle()));

                return listOfVideoToListOfString(result, result.size());
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
