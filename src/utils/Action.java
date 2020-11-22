package utils;

import databases.ActorDataBase;
import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import entertainment.User;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import java.util.ArrayList;
import java.util.List;

import static common.Constants.SUCCESS;
import static common.Constants.DUPLICATE;
import static common.Constants.NOT_SEEN;
import static common.Constants.ALREADY_RATED;
import static common.Constants.USERS;
import static common.Constants.ACTORS;
import static common.Constants.MOVIES;
import static common.Constants.SHOWS;
import static common.Constants.AVERAGE;
import static common.Constants.AWARDS;
import static common.Constants.FILTER_DESCRIPTIONS;
import static common.Constants.RATINGS;
import static common.Constants.LONGEST;
import static common.Constants.FAVORITE;
import static common.Constants.MOST_VIEWED;
import static common.Constants.STANDARD;
import static common.Constants.CANNOT_BE_APPLIED;
import static common.Constants.BEST_UNSEEN;
import static common.Constants.POPULAR;
import static common.Constants.SEARCH;
import static common.Constants.FIRST_FILTER;
import static common.Constants.SECOND_FILTER;
import static common.Constants.THIRD_FILTER;
import static common.Constants.FOURTH_FILTER;
import static common.Constants.COMMAND;
import static common.Constants.COMMAND_TYPE_FAVORITE;
import static common.Constants.COMMAND_TYPE_RATING;
import static common.Constants.COMMAND_TYPE_VIEW;
import static common.Constants.QUERY;
import static common.Constants.RECOMMENDATION;


public final class Action {
    private final UserDataBase users;

    private final ActorDataBase actors;

    private final MovieDataBase movies;

    private final SerialDataBase serials;

    public Action(final List<UserInputData> usersInput,
                  final List<ActorInputData> actorsInput,
                  final List<MovieInputData> moviesInput,
                  final List<SerialInputData> serialsInput) {
        users = new UserDataBase(usersInput);

        actors = new ActorDataBase(actorsInput);

        movies = new MovieDataBase(moviesInput);

        serials = new SerialDataBase(serialsInput);
    }

    /**
     * Performs the action from the input and computes the output message.
     *
     * @param action Action that will be performed
     * @return The output message
     */
    public String doAction(final ActionInputData action) {
        // the output message to be returned
        String outputMessage = null;

        // get all the action info
        String actionType = action.getActionType();

        String commandType = action.getType();

        String username = action.getUsername();

        String objectType = action.getObjectType();

        String sortType = action.getSortType();

        String criteria = action.getCriteria();

        String title = action.getTitle();

        String genre = action.getGenre();

        int number = action.getNumber();

        double grade = action.getGrade();

        int seasonNumber = action.getSeasonNumber();

        List<List<String>> filters = action.getFilters();

        int filterYear = 0;

        List<String> filterGenres = null;

        List<String> filterWords = null;

        List<String> filterAwards = null;

        // get each filter from the input
        if (filters != null && filters.size() > 0) {
            if (filters.get(FIRST_FILTER) != null) {
                if (filters.get(FIRST_FILTER).get(FIRST_FILTER) != null) {
                    // convert from string to int
                    filterYear = Integer.parseInt(filters.get(FIRST_FILTER).get(FIRST_FILTER));
                }
            }

            if (filters.get(SECOND_FILTER) != null) {
                filterGenres = filters.get(SECOND_FILTER);
            }

            if (filters.get(THIRD_FILTER) != null) {
                filterWords = filters.get(THIRD_FILTER);
            }

            if (filters.get(FOURTH_FILTER) != null) {
                filterAwards = filters.get(FOURTH_FILTER);
            }
        }

        // execute action depending on the type
        switch (actionType) {
            default -> {
                return outputMessage;
            }

            case COMMAND -> {
                outputMessage = this.doCommand(commandType, username, title, grade, seasonNumber);
            }

            case QUERY -> {
                outputMessage = this.doQuery(objectType, criteria, sortType, number, filterYear,
                                                        filterGenres, filterWords, filterAwards);
            }

            case RECOMMENDATION -> {
                outputMessage = this.doRecommendation(commandType, username, genre);
            }
        }

        return outputMessage;
    }

    /**
     *  Performs the command from the input depending on the type of the command given.
     *
     * @param commandType The type of command to be performed
     * @param username The user name who will perform the command
     * @param title The title of the video
     * @param grade The grade required for rating
     * @param seasonNumber The season number required for rating
     * @return Returns the output message
     */
    public String doCommand(final String commandType,
                            final String username,
                            final String title,
                            final double grade,
                            final int seasonNumber) {
        String commandOutputMessage = null;

        // used for method calls
        String methodResult;

        // execute command depending on the type
        switch (commandType) {
            default -> {
                return commandOutputMessage;
            }

            case COMMAND_TYPE_FAVORITE -> {
                // find the user with the name from the action info
                for (User usersIterator : users.getUsers()) {
                    if (usersIterator.getUsername().equals(username)) {
                        methodResult = usersIterator.addVideoToFavorite(title);

                        // check what the method did
                        switch (methodResult) {
                            default -> {
                                return  commandOutputMessage;
                            }

                            case SUCCESS -> {
                                commandOutputMessage = "success -> " + title
                                                                + " was added as favourite";
                            }

                            case DUPLICATE -> {
                                commandOutputMessage =  "error -> " + title
                                                                + " is already in favourite list";
                            }

                            case NOT_SEEN -> {
                                commandOutputMessage = "error -> " + title
                                                                + " is not seen";
                            }
                        }
                    }
                }
            }

            case COMMAND_TYPE_VIEW -> {
                // find the user with the name from the action info
                for (User usersIterator : users.getUsers()) {
                    if (usersIterator.getUsername().equals(username)) {
                        usersIterator.viewVideo(title);

                        commandOutputMessage = "success -> " + title
                                                + " was viewed with total views of "
                                                + usersIterator.getHistory().get(title);

                        return commandOutputMessage;
                    }
                }
            }

            case COMMAND_TYPE_RATING -> {
                // find the user with the name from the action info
                for (User usersIterator : users.getUsers()) {
                    if (usersIterator.getUsername().equals(username)) {
                        // check the type of the video
                        if (seasonNumber != 0) {
                            // serial case
                            methodResult = usersIterator.rateShow(title,
                                                                  seasonNumber,
                                                                  grade,
                                                                  serials.getSerials());
                        } else {
                            // movie case
                            methodResult = usersIterator.rateMovie(title,
                                                                  grade,
                                                                  movies.getMovies());
                        }

                        // check what the method did
                        switch (methodResult) {
                            default -> {
                                return commandOutputMessage;
                            }

                            case SUCCESS -> {
                                commandOutputMessage = "success -> " + title
                                                        + " was rated with " + grade
                                                        + " by " + username;
                            }

                            case NOT_SEEN -> {
                                commandOutputMessage = "error -> " + title
                                                        + " is not seen";
                            }

                            case ALREADY_RATED -> {
                                commandOutputMessage = "error -> " + title
                                                        + " has been already rated";
                            }
                        }

                        return commandOutputMessage;
                    }
                }
            }
        }

        return commandOutputMessage;
    }

    /**
     * Performs a search depending on the object type, criteria and filters given.
     *
     * @param objectType The type of object to be searched
     * @param criteria The criteria of the search
     * @param sortType The sort type of the search(asc/desc)
     * @param number The number of objects to be searched
     * @param filterYear The filter release year to be searched by
     * @param filterGenres The filter genres to be searched by
     * @param filterWords The filter words to be searched by
     * @param filterAwards The filter awards to be searched by
     * @return Returns the result of the search
     */
    public String doQuery(final String objectType,
                          final String criteria,
                          final String sortType,
                          final int number,
                          final int filterYear,
                          final List<String> filterGenres,
                          final List<String> filterWords,
                          final List<String> filterAwards) {
        String queryOutputMessage = null;

        ArrayList<String> methodResult;

        // execute query depending on the type
        switch (objectType) {
            default -> {
                return queryOutputMessage;
            }

            case USERS -> {
                methodResult = doQueryUsers(number, sortType);
            }

            case ACTORS -> {
                methodResult = doQueryActors(criteria, number, sortType, filterWords,
                                                                        filterAwards);
            }

            case MOVIES -> {
                methodResult = doQueryMovies(criteria, number, sortType, filterYear,
                                                                        filterGenres);
            }

            case SHOWS -> {
                methodResult = doQuerySerials(criteria, number, sortType, filterYear,
                                                                        filterGenres);
            }
        }

        // check if the query found something
        if (methodResult != null) {
            return Utils.queryResultBuilder(methodResult);
        }

        return null;
    }

    /**
     * Search the first n users from the data base(Query first case).
     *
     * @param number The number of users to be returned
     * @param sortType The sort type(asc/desc)
     * @return Returns an ArrayList with the user names
     */
    public ArrayList<String> doQueryUsers(final int number,
                                          final String sortType) {
        // dummy user used only for method call
        User dummyUser = new User();

        ArrayList<User> listOfUsers = dummyUser.searchUsersByNumberOfRatings(users,
                                                                             number,
                                                                             sortType);

        return Utils.listOfUsersToListOfString(listOfUsers, number);
    }

    /**
     *  Search the first n actors based on criteria and filters(Query second case).
     *
     * @param criteria The criteria to search by
     * @param number The number of actors to be searched
     * @param sortType The sort type(asc/desc)
     * @param filterWords The filter words to be searched by
     * @param filterAwards The filter awards to be searched by
     * @return Returns an ArrayList with the actor names
     */
    public ArrayList<String> doQueryActors(final String criteria,
                                           final int number,
                                           final String sortType,
                                           final List<String> filterWords,
                                           final List<String> filterAwards) {
        // dummy user used only for method call
        User dummyUser = new User();

        // search actors depending on criteria
        switch (criteria) {
            default -> {
                return null;
            }

            case AVERAGE -> {
                return Utils.listOfActorsToListOfString(dummyUser
                                                        .searchAverageActors(movies,
                                                                             serials,
                                                                             actors,
                                                                             sortType), number);
            }

            case AWARDS -> {
                return Utils.listOfActorsToListOfString(dummyUser
                                                        .searchActorsByAwards(actors,
                                                                              filterAwards,
                                                                              sortType), number);
            }

            case FILTER_DESCRIPTIONS -> {
                return Utils.listOfActorsToListOfString(dummyUser
                            .searchActorsByFilterDescription(actors,
                                                             filterWords,
                                                             sortType), number);
            }
        }
    }

    /**
     * Search the first n movies based on criteria and filters(Query third case).
     *
     * @param criteria The criteria to search by
     * @param number The number of movies to be searched
     * @param sortType The sort type(asc/desc)
     * @param filterYear The filter year to search by
     * @param filterGenres The filter genres to search by
     * @return Returns an ArrayList with the titles of the movies
     */
    public ArrayList<String> doQueryMovies(final String criteria,
                                           final int number,
                                           final String sortType,
                                           final int filterYear,
                                           final List<String> filterGenres) {
        // dummy user used only for method call
        User dummyUser = new User();

        // in this list we will store the movies, until we convert them into a list of Strings
        ArrayList<Video> videoList;

        // search movies depending on criteria
        switch (criteria) {
            default -> {
                return null;
            }

            case RATINGS -> {
                videoList = new ArrayList<Video>(dummyUser.searchMoviesByRating(movies,
                                                                                filterYear,
                                                                                filterGenres,
                                                                                sortType));

                return Utils.listOfVideoToListOfString(videoList, number);
            }

            case FAVORITE -> {
                videoList = new ArrayList<Video>(dummyUser.searchMoviesByFavorite(movies,
                                                                                  users,
                                                                                  filterYear,
                                                                                  filterGenres,
                                                                                  sortType));

                return Utils.listOfVideoToListOfString(videoList, number);
            }

            case LONGEST -> {
                videoList = new ArrayList<Video>(dummyUser.searchMoviesByDuration(movies,
                                                                                  filterYear,
                                                                                  filterGenres,
                                                                                  sortType));

                return Utils.listOfVideoToListOfString(videoList, number);
            }

            case MOST_VIEWED -> {
                videoList = new ArrayList<Video>(dummyUser.searchMoviesByViews(movies,
                                                                               users,
                                                                               filterYear,
                                                                               filterGenres,
                                                                               sortType));

                return Utils.listOfVideoToListOfString(videoList, number);
            }
        }
    }

    /**
     *  Search the first n serials by criteria and filters(Query fourth case).
     *
     * @param criteria The criteria to search by
     * @param number The number of serials to be searched
     * @param sortType The sort type(asc/desc)
     * @param filterYear The filter year to be searched by
     * @param filterGenres The filter genres to be searched by
     * @return Returns an ArrayList with the serials titles
     */
    public ArrayList<String> doQuerySerials(final String criteria,
                                            final int number,
                                            final String sortType,
                                            final int filterYear,
                                            final List<String> filterGenres) {
        // dummy user used only for method call
        User dummyUser = new User();

        // in this list we will store the serials, until we convert them into a list of Strings
        ArrayList<Video> videoList;

        switch (criteria) {
            default -> {
                return null;
            }

            case RATINGS -> {
                videoList = new ArrayList<Video>(dummyUser.searchSerialsByRating(serials,
                                                                                 filterYear,
                                                                                 filterGenres,
                                                                                 sortType));

                return Utils.listOfVideoToListOfString(videoList, number);
            }

            case FAVORITE -> {
                videoList = new ArrayList<Video>(dummyUser.searchSerialsByFavorite(serials,
                                                                                   users,
                                                                                   filterYear,
                                                                                   filterGenres,
                                                                                   sortType));

                return Utils.listOfVideoToListOfString(videoList, number);
            }

            case LONGEST -> {
                videoList = new ArrayList<>(dummyUser.searchSerialsByDuration(serials,
                                                                              filterYear,
                                                                              filterGenres,
                                                                              sortType));

                return Utils.listOfVideoToListOfString(videoList, number);
            }

            case MOST_VIEWED -> {
                videoList = new ArrayList<Video>(dummyUser.searchSerialsByViews(serials,
                                                                                users,
                                                                                filterYear,
                                                                                filterGenres,
                                                                                sortType));

                return Utils.listOfVideoToListOfString(videoList, number);
            }
        }
    }

    /**
     * Recommends one or more videos bases on user subscription type and genre of the video.
     *
     * @param commandType The type of recommendation to recommend by
     * @param username The user name
     * @param genre The genre to recommend by
     * @return Returns the output message
     */
    public String doRecommendation(final String commandType,
                                   final String username,
                                   final String genre) {
        String methodResult = null;

        switch (commandType) {
            default -> {
                return methodResult;
            }

            case STANDARD -> {
                // find the user
                for (User userIterator : users.getUsers()) {
                    if (userIterator.getUsername().equals(username)) {
                         methodResult = userIterator.recommendationStandard(movies,
                                                                            serials);

                        break;
                    }
                }

                // check what the recommendation result was
                if (methodResult != null && methodResult.equals(CANNOT_BE_APPLIED)) {
                    return "StandardRecommendation " + methodResult;
                } else {
                    return "StandardRecommendation result: " + methodResult;
                }
            }

            case BEST_UNSEEN -> {
                for (User userIterator : users.getUsers()) {
                    if (userIterator.getUsername().equals(username)) {
                        methodResult = userIterator.recommendationBestUnseen(movies,
                                                                             serials);

                        break;
                    }
                }

                if (methodResult != null && methodResult.equals(CANNOT_BE_APPLIED)) {
                    return "BestRatedUnseenRecommendation " + methodResult;
                } else {
                    return  "BestRatedUnseenRecommendation result: " + methodResult;
                }
            }

            case POPULAR -> {
                for (User userIterator : users.getUsers()) {
                    if (userIterator.getUsername().equals(username)) {
                        methodResult = userIterator.recommendationPopular(users,
                                                                          movies,
                                                                          serials);
                    }
                }

                if (methodResult != null && methodResult.equals(CANNOT_BE_APPLIED)) {
                    return "PopularRecommendation " + methodResult;
                } else {
                    return "PopularRecommendation result: " + methodResult;
                }
            }

            case FAVORITE -> {
                for (User userIterator : users.getUsers()) {
                    if (userIterator.getUsername().equals(username)) {
                        methodResult = userIterator.recommendationFavorite(users,
                                                                           movies,
                                                                           serials);
                    }
                }

                if (methodResult != null && methodResult.equals(CANNOT_BE_APPLIED)) {
                    return  "FavoriteRecommendation " + methodResult;
                } else {
                    return "FavoriteRecommendation result: " + methodResult;
                }
            }

            case SEARCH -> {
                // in this list we will store the method result
                ArrayList<String> resultSearch = null;

                for (User userIterator : users.getUsers()) {
                    if (userIterator.getUsername().equals(username)) {
                        resultSearch = userIterator.recommendationSearch(users,
                                                                         movies,
                                                                         serials,
                                                                         genre);
                    }
                }

                // check if the recommendation method returned something
                if (resultSearch == null || resultSearch.size() == 0) {
                    return "SearchRecommendation cannot be applied!";
                } else {
                    // construct the output message
                    StringBuilder builder = new StringBuilder();

                    for (int k = 0; k < resultSearch.size(); k++) {
                        builder.append(resultSearch.get(k));

                        if (k != (resultSearch.size() - 1)) {
                            builder.append(", ");
                        }
                    }

                    return "SearchRecommendation result: [" + builder + "]";
                }
            }
        }
    }
}

