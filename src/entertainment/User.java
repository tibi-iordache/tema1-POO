package entertainment;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import actions.Command;
import actions.Query;
import actions.Suggestions;
import actor.Actor;
import actor.ActorsAwards;
import databases.ActorDataBase;
import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import utils.Utils;

import static common.Constants.SUCCESS;
import static common.Constants.DUPLICATE;
import static common.Constants.ALREADY_RATED;
import static common.Constants.NOT_SEEN;
import static common.Constants.ASCENDING;
import static common.Constants.DESCENDING;
import static common.Constants.CANNOT_BE_APPLIED;
import static common.Constants.PREMIUM;

public final class User implements Command, Query, Suggestions {
    private final String username;

    private final String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private Map<String, Double> ratingMovieList;

    private Map<String, Map<Integer, Double>> ratingSerialList;

    public User() {
        username = null;

        subscriptionType = null;

        history = null;

        favoriteMovies = null;

        ratingMovieList = null;

        ratingMovieList = null;
    }

    public User(final String username,
                final String subscriptionType,
                final Map<String, Integer> history,
                final ArrayList<String> favoriteMovies) {
        this.username = username;

        this.subscriptionType = subscriptionType;

        this.history = history;

        this.favoriteMovies = favoriteMovies;

        this.ratingMovieList = new HashMap<>();

        this.ratingSerialList = new HashMap<>();
    }

    @Override
    public String addVideoToFavorite(final String video) {
        // check if the user watched the video
        if (history.containsKey(video)) {
            // check if the video it is already in the favorite list
            if (!favoriteMovies.contains(video)) {
                favoriteMovies.add(video);

                return SUCCESS;
            }

            return DUPLICATE;
        }

        return NOT_SEEN;
    }

    @Override
    public void viewVideo(final String video) {
        // check if the user has already watched the video
        if (history.containsKey(video)) {
            // if yes, we increment the number of views
            history.put(video, history.get(video) + 1);
        } else {
            history.put(video, 1);
        }
    }

    @Override
    public String rateMovie(final String movie,
                            final Double grade,
                            final ArrayList<Movie> moviesDataBase) {
        // check if the user watched the movie
        if (history.containsKey(movie)) {
            // check if it already has a rating
            if (!ratingMovieList.containsKey(movie)) {
                // if not, we add the movie to the user ratings list
                ratingMovieList.put(movie, grade);

                // we also add the value of the rating to the movie own ratings list
                for (Movie value : moviesDataBase) {
                    if (value.getTitle().equals(movie)) {
                        value.addRating(grade);
                    }
                }

                return SUCCESS;
            }

            return ALREADY_RATED;
        }

        return NOT_SEEN;
    }

    @Override
    public String rateShow(final String serialName,
                           final int seasonNo,
                           final Double grade,
                           final ArrayList<Serial> serialsDataBase) {
        // check if the user watched the serial
        if (history.containsKey(serialName)) {
            // check if the serial has any rating
            if (ratingSerialList.containsKey(serialName)) {
                // check if the specified season has been already rated
                if (ratingSerialList.get(serialName).get(seasonNo) == null) {
                    // if not, we add the grade to the user rating list
                    Map<Integer, Double> seasonGrade = new HashMap<Integer, Double>();

                    seasonGrade.put(seasonNo, grade);

                    ratingSerialList.put(serialName, seasonGrade);

                    // we also add the value of the grade to the serial own ratings list
                    for (Serial serial : serialsDataBase) {
                        if (serial.getTitle().equals(serialName)) {
                            serial.getSeasons().get(seasonNo - 1).addRating(grade);
                        }
                    }

                    return SUCCESS;
                }

                return ALREADY_RATED;
            } else {
                // else we add the grade to the user serial rating list
                Map<Integer, Double> seasonGrade = new HashMap<Integer, Double>();

                seasonGrade.put(seasonNo, grade);

                ratingSerialList.put(serialName, seasonGrade);

                // also add the rating to the serial own ratings list
                for (Serial serial : serialsDataBase) {
                    if (serial.getTitle().equals(serialName)) {
                        serial.getSeasons().get(seasonNo - 1).addRating(grade);
                    }
                }

                return SUCCESS;
            }
        }

        return NOT_SEEN;
    }

    @Override
    public ArrayList<User> searchUsersByNumberOfRatings(final UserDataBase users,
                                                        final int numberOfUsers,
                                                        final String sortType) {
        // a new list in which we will add only the users who gave ratings
        ArrayList<User> raters = new ArrayList<User>();

        // iterate through the data base
        for (User iterator : users.getUsers()) {
            if (iterator.getNumberOfRatings() > 0) {
                // add only the users who gave ratings
                raters.add(iterator);
            }
        }

        // sort the list accordingly to the required sort type
        if (sortType.equals(ASCENDING)) {
            // first criteria is the number of rating, the second is the username
            raters.sort(Comparator.comparing(User::getNumberOfRatings)
                                            .thenComparing(User::getUsername));
        }

        if (sortType.equals(DESCENDING)) {
            raters.sort(Comparator.comparing(User::getNumberOfRatings)
                                            .thenComparing(User::getUsername).reversed());
        }

        // the array that will contain the first n users searched
        ArrayList<User> firstNUsers = new ArrayList<User>();

        // we check if the number of users found is less than the number required
        // and make sure we return the minimum number of users between the two
        int size = Math.min(numberOfUsers, raters.size());

        // add the users to the final array
        for (int i = 0; i < size; i++) {
            firstNUsers.add(raters.get(i));
        }

        // return the result
        return firstNUsers;
    }

    @Override
    public ArrayList<Actor> searchAverageActors(final MovieDataBase movies,
                                                final SerialDataBase serials,
                                                final ActorDataBase actors,
                                                final String sortType) {
        // new list which will contain all the actors whose mean of movies and
        // serials is greater than zero
        ArrayList<Actor> actorsWithRatings = new ArrayList<Actor>();

        // iterate through each actor
        for (Actor actorIterator : actors.getActors()) {
            // calculate it's rating
            double actorRating = actorIterator.calculateRating(movies, serials);

            // if it's greater than zero, we add it to new arraylist
            if (Double.compare(actorRating, 0d) > 0) {
                actorsWithRatings.add(actorIterator);
            }
        }

        // sort the list accordingly to the sort type required
        if (sortType.equals(ASCENDING)) {
            // the first criteria is the rating and the second it's the name of the actor
            actorsWithRatings.sort(Comparator.comparing((actor) -> ((Actor) actor)
                             .calculateRating(movies, serials))
                             .thenComparing(new Comparator<Object>() {
                                 @Override
                                 public int compare(final Object o1, final Object o2) {
                                     return ((Actor) o1).getName()
                                             .compareTo(((Actor) o2).getName());
                                 }
                             }));
        }

        if (sortType.equals(DESCENDING)) {
            actorsWithRatings.sort(Comparator.comparing((actor) -> ((Actor) actor)
                             .calculateRating(movies, serials))
                             .thenComparing(new Comparator<Object>() {
                                 @Override
                                 public int compare(final Object o1, final Object o2) {
                                     return ((Actor) o1).getName()
                                             .compareTo(((Actor) o2).getName());
                                 }
                             })
                             .reversed());
        }

        return actorsWithRatings;
    }

    @Override
    public ArrayList<Actor> searchActorsByAwards(final ActorDataBase actors,
                                                 final List<String> awardsSearched,
                                                 final String sortType) {
        // the list with all the actors which have the awards searched
        ArrayList<Actor> awardedActors = new ArrayList<Actor>();

        // iterate through the actors data base
        for (Actor actorIterator : actors.getActors()) {
            // get each actor awards map
            Map<ActorsAwards, Integer> currentActorAwards = actorIterator.getAwards();

            int counter = 0;

            // iterate through the awards list from the filter
            for (String awardIterator : awardsSearched) {
                // if an actor contains an award increment the counter
                if (currentActorAwards.containsKey(Utils.stringToAwards(awardIterator))) {
                    counter++;
                }
            }

            // if an actor has all the awards add it to the list
            if (counter == awardsSearched.size()) {
                awardedActors.add(actorIterator);
            }
        }

        // sort the list
        if (sortType.equals(ASCENDING)) {
            // first criteria is the number of awards, second is the actor name
            awardedActors.sort(Comparator.comparing(Actor::getAwardsNo)
                         .thenComparing(Actor::getName));
        }

        if (sortType.equals(DESCENDING)) {
            awardedActors.sort(Comparator.comparing(Actor::getAwardsNo)
                         .thenComparing(Actor::getName).reversed());
        }

        return awardedActors;
    }

    @Override
    public ArrayList<Actor> searchActorsByFilterDescription(final ActorDataBase actors,
                                                            final List<String> words,
                                                            final String sortType) {
        // new list which will contain the actors with the awards searched
        ArrayList<Actor> actorsWithWordsInDescription = new ArrayList<Actor>();

        // iterate through actors
        for (Actor actorIterator : actors.getActors()) {
            boolean allWordsFound = true;

            // iterate through the searched words
            for (String wordIterator : words) {
                // construct the pattern we want to find
                Pattern wordSearched = Pattern.compile("[^a-z]" +  wordIterator
                                                           + "[^a-z]", Pattern.CASE_INSENSITIVE);

                // set the matcher to the actor description
                Matcher matcher = wordSearched.matcher(actorIterator.getCareerDescription());

                // search the word in description
                boolean wordFound = matcher.find();

                // if we didn't found the word, set the aux variable to false
                if (!wordFound) {
                    allWordsFound = false;

                    break;
                }
            }

            if (allWordsFound) {
                // if we found all the words, add the actor to the new list
                actorsWithWordsInDescription.add(actorIterator);
            }
        }

        // sort the database by the sort type required
        if (sortType.equals(ASCENDING)) {
            // the criteria is the actor name
            actorsWithWordsInDescription.sort(Comparator.comparing(Actor::getName));
        }

        if (sortType.equals(DESCENDING)) {
            actorsWithWordsInDescription.sort(Comparator.comparing(Actor::getName).reversed());
        }

        return actorsWithWordsInDescription;
    }

    @Override
    public ArrayList<Movie> searchMoviesByRating(final MovieDataBase movies,
                                                 final int year,
                                                 final List<String> genre,
                                                 final String sortType) {
        // form a new list that will contain only the movies with ratings
        ArrayList<Movie> ratedMovies = new ArrayList<Movie>();

        // iterate through each movie
        for (Movie movieIterator : movies.getMovies()) {
            // check if the movie passes all the filters and has a rating
            if (Utils.checkFiltersForVideo(movieIterator, year, genre)
                    && Double.compare(movieIterator.calculateRating(), 0d) > 0) {
                // add it to the new list
                ratedMovies.add(movieIterator);
            }

        }

        // sort the list
        if (sortType.equals(ASCENDING)) {
            // the criteria is the movie rating
            ratedMovies.sort(Comparator.comparing(Movie::calculateRating));
        }

        if (sortType.equals(DESCENDING)) {
            ratedMovies.sort(Comparator.comparing(Movie::calculateRating).reversed());
        }

        return ratedMovies;
    }

    @Override
    public ArrayList<Serial> searchSerialsByRating(final SerialDataBase serials,
                                                   final int year,
                                                   final List<String> genre,
                                                   final String sortType) {
        // form the list that will contain only the serials with ratings
        ArrayList<Serial> ratedSerials = new ArrayList<Serial>();

        for (Serial serialIterator : serials.getSerials()) {
            if (Utils.checkFiltersForVideo(serialIterator, year, genre)
                    && Double.compare(serialIterator.calculateRating(), 0d) > 0) {
                // add only the serials that pass the filter and have a rating
                ratedSerials.add(serialIterator);
            }
        }

        // sort the list
        if (sortType.equals(ASCENDING)) {
            // the criteria is rating
            ratedSerials.sort(Comparator.comparing(Serial::calculateRating));
        }

        if (sortType.equals(DESCENDING)) {
            ratedSerials.sort(Comparator.comparing(Serial::calculateRating).reversed());
        }

        return ratedSerials;
    }

    @Override
    public ArrayList<Movie> searchMoviesByFavorite(final MovieDataBase movies,
                                                    final UserDataBase users,
                                                    final int year,
                                                    final List<String> genre,
                                                    final String sortType) {
        // form the list with the movies that appear in the user favorite list
        ArrayList<Movie> favoriteMoviesList = new ArrayList<Movie>();

        for (Movie movieIterator : movies.getMovies()) {
            if (Utils.checkFiltersForVideo(movieIterator, year, genre)
                    && movieIterator.getNumberOfFavorites(users) > 0) {
                // add the movie in the list if it passes the filter and
                // the movies appears in a user favorite list
                favoriteMoviesList.add(movieIterator);
            }
        }

        // sort the list
        if (sortType.equals(ASCENDING)) {
            // the first criteria is number of favorites, second is the movie title
            favoriteMoviesList.sort(Comparator.comparing((movie) -> ((Movie) movie)
                    .getNumberOfFavorites(users))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return ((Movie) o1).getTitle().compareTo(((Movie) o2).getTitle());
                        }
                    }));
        }

        if (sortType.equals(DESCENDING)) {
            favoriteMoviesList.sort(Comparator.comparing((movie) -> ((Movie) movie)
                    .getNumberOfFavorites(users))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Movie) o1).getTitle()).compareTo(((Movie) o2).getTitle());
                        }
                    }).reversed());
        }

        return favoriteMoviesList;
    }

    @Override
    public ArrayList<Serial> searchSerialsByFavorite(final SerialDataBase serials,
                                              final UserDataBase users,
                                              final int year,
                                              final List<String> genre,
                                              final String sortType) {
        // form the list with the serials that appear in the user favorite list
        ArrayList<Serial> favoriteSerialsList = new ArrayList<Serial>();

        for (Serial serialIterator : serials.getSerials()) {
            if (Utils.checkFiltersForVideo(serialIterator, year, genre)
                    && serialIterator.getNumberOfFavorites(users) > 0) {
                // add to the list if it passes the filters and the serial appears in users
                // favorite serials list
                favoriteSerialsList.add(serialIterator);
            }
        }

        // sort the list
        if (sortType.equals(ASCENDING)) {
            // the first criteria is the number of favorites, the second is the serial title
            favoriteSerialsList.sort(Comparator.comparing((serial) -> ((Serial) serial)
                    .getNumberOfFavorites(users))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Serial) o1).getTitle()).compareTo(((Serial) o2).getTitle());
                        }
                    }));
        }

        if (sortType.equals(DESCENDING)) {
            favoriteSerialsList.sort(Comparator.comparing((serial) -> ((Serial) serial)
                    .getNumberOfFavorites(users))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Serial) o1).getTitle()).compareTo(((Serial) o2).getTitle());
                        }
                    }).reversed());
        }

        return favoriteSerialsList;
    }

    @Override
    public ArrayList<Movie> searchMoviesByDuration(final MovieDataBase movies,
                                                   final int year,
                                                   final List<String> genre,
                                                   final String sortType) {
        // the new list with the movies that will pass the filters
        ArrayList<Movie> longestMovies = new ArrayList<Movie>();

        for (Movie movieIterator : movies.getMovies()) {
            if (Utils.checkFiltersForVideo(movieIterator, year, genre)) {
                // add the movie in the list if it passes the filters
                longestMovies.add(movieIterator);
            }
        }

        // sort the list
        if (sortType.equals(ASCENDING)) {
            // the first criteria is the duration of the movie, the second is the movie title
            longestMovies.sort(Comparator.comparing(Movie::getDuration)
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Movie) o1).getTitle()).compareTo(((Movie) o2).getTitle());
                        }
                    }));
        }

        if (sortType.equals(DESCENDING)) {
            longestMovies.sort(Comparator.comparing(Movie::getDuration)
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Movie) o1).getTitle()).compareTo(((Movie) o2).getTitle());
                        }
                    })
                    .reversed());
        }

        return longestMovies;
    }

    @Override
    public ArrayList<Serial> searchSerialsByDuration(final SerialDataBase serials,
                                                     final int year,
                                                     final List<String> genre,
                                                     final String sortType) {
        // new list with the serials that pass the filters
        ArrayList<Serial> longestSerials = new ArrayList<Serial>();

        for (Serial serialIterator : serials.getSerials()) {
            if (Utils.checkFiltersForVideo(serialIterator, year, genre)) {
                // add the serials to the list if it passes the filters
                longestSerials.add(serialIterator);
            }
        }

        // sort the list
        if (sortType.equals(DESCENDING)) {
            // the first criteria is the serial duration, second is the title
            longestSerials.sort(Comparator.comparing(Serial::getDuration)
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Serial) o1).getTitle()).compareTo(((Serial) o2).getTitle());
                        }
                    }));
        }

        if (sortType.equals(DESCENDING)) {
            longestSerials.sort(Comparator.comparing(Serial::getDuration)
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Serial) o1).getTitle()).compareTo(((Serial) o2).getTitle());
                        }
                    })
                    .reversed());
        }

        return longestSerials;
    }

    @Override
    public ArrayList<Movie> searchMoviesByViews(final MovieDataBase movies,
                                                 final UserDataBase users,
                                                 final int year,
                                                 final List<String> genre,
                                                 final String sortType) {
        // the new list with the movies that have been watched by the users
        ArrayList<Movie> mostViewedMovies = new ArrayList<Movie>();

        for (Movie movieIterator : movies.getMovies()) {
            if (Utils.checkFiltersForVideo(movieIterator, year, genre)
                    && movieIterator.getNumberOfViews(users) > 0) {
                // add the movie to the list if it passes the filters and has been watched
                // by the users
                mostViewedMovies.add(movieIterator);
            }
        }

        // sort the list
        if (sortType.equals(ASCENDING)) {
            // the first criteria is number of views, second the movie title
            mostViewedMovies.sort(Comparator.comparing((movie) -> ((Movie) movie)
                    .getNumberOfViews(users))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Movie) o1).getTitle()).compareTo(((Movie) o2).getTitle());
                        }
                    }));
        }

        if (sortType.equals(DESCENDING)) {
            mostViewedMovies.sort(Comparator.comparing((movie) -> ((Movie) movie)
                    .getNumberOfViews(users))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Movie) o1).getTitle()).compareTo(((Movie) o2).getTitle());
                        }
                    })
                    .reversed());
        }

        return mostViewedMovies;
    }

    @Override
    public ArrayList<Serial> searchSerialsByViews(final SerialDataBase serials,
                                                  final UserDataBase usersDataBase,
                                                  final int year,
                                                  final List<String> genre,
                                                  final String sortType) {
        // the new list with the serials that have been watched by the users
        ArrayList<Serial> mostViewedSerials = new ArrayList<Serial>();

        for (Serial serialIterator : serials.getSerials()) {
            if (Utils.checkFiltersForVideo(serialIterator, year, genre)
                    && serialIterator.getNumberOfViews(usersDataBase) > 0) {
                // adds the serial to the list if it passes the filter and has been watched by
                // the users
                mostViewedSerials.add(serialIterator);
            }
        }

        // sort the list
        if (sortType.equals(ASCENDING)) {
            // first criteria is number of views, second is the title of the serial
            mostViewedSerials.sort(Comparator.comparing((serial) -> ((Serial) serial)
                    .getNumberOfViews(usersDataBase))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Serial) o1).getTitle()).compareTo(((Serial) o2).getTitle());
                        }
                    }));
        }

        if (sortType.equals(DESCENDING)) {
            mostViewedSerials.sort(Comparator.comparing((serial) -> ((Serial) serial)
                    .getNumberOfViews(usersDataBase))
                    .thenComparing(new Comparator<Object>() {
                        @Override
                        public int compare(final Object o1, final Object o2) {
                            return (((Serial) o1).getTitle()).compareTo(((Serial) o2).getTitle());
                        }
                    })
                    .reversed());
        }

        return mostViewedSerials;
    }

    @Override
    public String recommendationStandard(final MovieDataBase movies,
                                         final SerialDataBase serials) {
        // iterate through each database
        for (Movie movieIterator : movies.getMovies()) {
            if (!this.getHistory().containsKey(movieIterator.getTitle())) {
                // if the user has not seen the movie, return the title
                return movieIterator.getTitle();
            }
        }

        for (Serial serialIterator : serials.getSerials()) {
            if (!this.getHistory().containsKey(serialIterator.getTitle())) {
                // if the user has not seen the serial, return the title
                return serialIterator.getTitle();
            }
        }

        // the user saw all the videos from the database
        return CANNOT_BE_APPLIED;
    }

    @Override
    public String recommendationBestUnseen(final MovieDataBase movies,
                                           final SerialDataBase serials) {
        // group all the videos
        ArrayList<Video> videos = new ArrayList<Video>();

        videos.addAll(movies.getMovies());

        videos.addAll(serials.getSerials());

        // sort by the rating, in descending way
        videos.sort(Comparator.comparing((video) -> ((Video) video).calculateRating()).reversed());

        // iterate through the videos until we find one that's unseen by the user
        for (Video videoIterator : videos) {
            if (!this.getHistory().containsKey(videoIterator.getTitle())) {
                return videoIterator.getTitle();
            }
        }

        // if we didn't find any video, return error message
        return CANNOT_BE_APPLIED;
    }

    @Override
    public String recommendationPopular(final UserDataBase users,
                                        final MovieDataBase movies,
                                        final SerialDataBase serials) {
        // first compute the top genres list
        ArrayList<Genre> topGenres = Utils.calculateTopGenres(users, movies, serials);

        // check if the subscription is premium
        if (this.getSubscriptionType() != null
                && this.getSubscriptionType().equals(PREMIUM)) {
            // iterate through the top genres
            for (Genre genreIterator : topGenres) {
                // then through the movies database
                for (Movie movieIterator : movies.getMovies()) {
                    if (movieIterator.getGenres().contains(genreIterator)
                            && !this.getHistory()
                            .containsKey(movieIterator.getTitle())) {
                        // the first unseen movie by the user
                        return movieIterator.getTitle();
                    }
                }

                for (Serial serialIterator : serials.getSerials()) {
                    if (serialIterator.getGenres().contains(genreIterator)
                            && !this.getHistory()
                                    .containsKey(serialIterator.getTitle())) {
                        // the first unseen serial by the user
                        return serialIterator.getTitle();
                    }
                }
            }
        }

        return CANNOT_BE_APPLIED;
    }

    @Override
    public String recommendationFavorite(final UserDataBase users,
                                         final MovieDataBase movies,
                                         final SerialDataBase serials) {
        // add all the videos to a new list
        ArrayList<Video> videos = new ArrayList<Video>();

        videos.addAll(movies.getMovies());
        videos.addAll(serials.getSerials());

        // sort the list by number of favorites, in descending way
        videos.sort(Comparator.comparing((video) -> ((Video) video)
                                                    .getNumberOfFavorites(users)).reversed());

        // check the subscription type
        if (this.getSubscriptionType() != null
                && this.getSubscriptionType().equals(PREMIUM)) {
            for (Video videoIterator : videos) {
                if (!this.getHistory().containsKey(videoIterator.getTitle())) {
                    // return the first unseen video
                    return videoIterator.getTitle();
                }
            }
        }


        return CANNOT_BE_APPLIED;
    }

    @Override
    public ArrayList<String> recommendationSearch(final UserDataBase users,
                                                  final MovieDataBase movies,
                                                  final SerialDataBase serials,
                                                  final String genre) {
        // check the subscription type
        if (this.getSubscriptionType() != null
                && this.getSubscriptionType().equals(PREMIUM)) {
            // form a new list in which we will add only the unseen videos by the user
            // from a specific genre
            ArrayList<Video> result = new ArrayList<Video>();

            for (Movie movieIterator : movies.getMovies()) {
                if (movieIterator.getGenres().contains(Utils.stringToGenre(genre))
                        && !this.getHistory().containsKey(movieIterator.getTitle())) {
                    result.add(movieIterator);
                }
            }

            for (Serial serialIterator : serials.getSerials()) {
                if (serialIterator.getGenres().contains(Utils.stringToGenre(genre))
                        && !this.getHistory().containsKey(serialIterator.getTitle())) {
                    result.add(serialIterator);
                }
            }

            // sort the list
            // first criteria is rating, second is the video title
            result.sort(Comparator.comparing((v) -> ((Video) v).calculateRating())
                                  .thenComparing((v) -> ((Video) v).getTitle()));

            // return the titles of the list
            return Utils.listOfVideoToListOfString(result, result.size());
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

    public Map<String, Map<Integer, Double>> getRatingSerialList() {
        return ratingSerialList;
    }

    public int getNumberOfRatings() {
        return ratingMovieList.size() + ratingSerialList.size();
    }

    @Override
    public String toString() {
        return "User{" + "username='" + username + '\''
                + ", subscriptionType='" + subscriptionType + '\''
                + ", history=" + history
                + ", favoriteMovies=" + favoriteMovies
                + ", ratingMovieList=" + ratingMovieList
                + ", ratingSeasonList=" + ratingSerialList
                + '}';
    }

}
