package main;

import actor.ActorsAwards;
import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import databases.ActorDataBase;
import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import entertainment.Season;
import entities.Actor;
import entities.Movie;
import entities.Serial;
import entities.SerialSeason;
import entities.User;
import entities.Video;
import fileio.ActionInputData;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static common.Constants.*;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        //TODO add here the entry point to your implementation

        /*
        * Forming the data base
        * */
        UserDataBase users = new UserDataBase(input.getUsers());

        MovieDataBase movies = new MovieDataBase(input.getMovies());

        SerialDataBase serials = new SerialDataBase(input.getSerials());

        ActorDataBase actors = new ActorDataBase(input.getActors());

        for (int i = 0; i < input.getCommands().size(); i++) {
            ActionInputData currentCommand = input.getCommands().get(i);

            int actionId = currentCommand.getActionId();

            String actionType = currentCommand.getActionType();

            String userName = currentCommand.getUsername();

            String videoName = currentCommand.getTitle();

            String sortType =currentCommand.getSortType();

            JSONObject output = null;

            switch (actionType) {
                case COMMAND -> {
                    String commandType = currentCommand.getType();

                    switch (commandType) {
                        case "favorite" -> {
                            for (User user : users.getUsers()) {
                                if (user.getUsername().equals(userName)) {
                                    String check = user.addVideoToFavorite(videoName);

                                    switch (check) {
                                        case "success" -> {
                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "success -> " + videoName + " was added as favourite");


                                        }

                                        case "duplicate" -> {
                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "error -> " + videoName + " is already in favourite list");

                                        }

                                        case "not seen" -> {

                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "error -> " + videoName + " is not seen");

                                        }
                                    }

                                    arrayResult.add(output);

                                    break;
                                }
                            }
                        }

                        case "view" -> {
                            for (User user : users.getUsers()) {
                                if (user.getUsername().equals(userName)) {
                                    user.viewVideo(videoName);

                                    output = fileWriter.writeFile(actionId,
                                            null,
                                            "success -> " + videoName + " was viewed with total views of "
                                                    + user.getHistory().get(videoName));

                                    arrayResult.add(output);

                                    break;
                                }
                            }
                        }

                        case "rating" -> {
                            for (User user : users.getUsers()) {
                                if (user.getUsername().equals(userName)) {
                                    String check;

                                    if (input.getCommands().get(i).getSeasonNumber() != 0) {
                                        // serial rating
                                        check = user.rateShow(videoName,
                                                input.getCommands().get(i).getSeasonNumber(),
                                                input.getCommands().get(i).getGrade(),
                                                serials.getSerials());
                                    } else {
                                        // movie rating
                                        check = user.rateMovie(videoName, input.getCommands().get(i).getGrade(), movies.getMovies());
                                    }

                                    switch (check) {
                                        case "success" -> {
                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "success -> " + videoName + " was rated with "
                                                            + input.getCommands().get(i).getGrade() + " by " + userName);
                                        }
                                        case "not seen" -> {
                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "error -> " + videoName + " is not seen");
                                        }
                                        case "already rated" -> {
                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "error -> " + videoName + " has been already rated");
                                        }
                                    }

                                    arrayResult.add(output);

                                    break;
                                }
                            }
                        }
                    }
                }
                case QUERY -> {
                    String objectType = currentCommand.getObjectType();

                    int numberInput = input.getCommands().get(i).getNumber();

                    User commandUser = new User();

                    switch (objectType) {
                        case "users" -> {
                            ArrayList<User> result = commandUser.searchUsersByNumberOfRatings(users.getUsers(),
                                                                                                numberInput,
                                                                                                sortType);

                            StringBuilder builder = new StringBuilder();

                            for (int k = 0; k < result.size(); k++) {
                                builder.append(result.get(k).getUsername());

                                if (k != (result.size() - 1))
                                    builder.append(", ");
                            }

                            output = fileWriter.writeFile(actionId,
                                    null,
                                    "Query result: [" + builder + "]");

                            arrayResult.add(output);
                        }

                        case "actors" -> {
                            String commandCriteria = currentCommand.getCriteria();

                            ArrayList<String> result = null;

                            switch (commandCriteria) {
                                case "average" -> {
                                    int numberOfActors = currentCommand.getNumber();

                                    result = commandUser.
                                            listOfActorsToListOfString(commandUser.searchAverageActors(movies,
                                                                                        serials,
                                                                                        actors,
                                                                                        numberOfActors,
                                                                                        sortType), numberOfActors);
                                }

                                case "awards" -> {
                                    List<String> awardsToBeSearchedBy = currentCommand.getFilters().get(3);

                                    ArrayList<Actor> actorAwardsResult = commandUser.
                                            searchActorsByAwards(actors.getActors(),
                                            awardsToBeSearchedBy,
                                            sortType);

                                    result = commandUser.listOfActorsToListOfString(actorAwardsResult,
                                            actorAwardsResult.size());
                                }

                                case "filter_description" -> {
                                    List<String> wordsToBeSearched = currentCommand.getFilters().get(2);

                                    ArrayList<Actor> actorsFilterResult = commandUser.
                                            searchActorsByFilterDescription(actors,
                                            wordsToBeSearched,
                                            sortType);

                                    result = commandUser.listOfActorsToListOfString(actorsFilterResult,
                                                                                    actorsFilterResult.size());

                                }
                            }

                            StringBuilder builder = new StringBuilder();

                            for (int k = 0; k < result.size(); k++) {
                                builder.append(result.get(k));

                                if (k != (result.size() - 1))
                                    builder.append(", ");
                            }

                            output = fileWriter.writeFile(actionId,
                                    null,
                                    "Query result: [" + builder + "]");

                            arrayResult.add(output);
                        }

                        case "movies" -> {
                            String commandCriteria = currentCommand.getCriteria();

                            List<String> genresInput = currentCommand.getFilters().get(1);

                            switch (commandCriteria) {
                                case "ratings" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                         yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    ArrayList<String> result = commandUser.listOfMovieToListOfString(commandUser.
                                            searchMoviesByRating(movies,
                                                    yearInput,
                                                    genresInput,
                                                    numberInput,
                                                    sortType),numberInput);

                                    StringBuilder builder = new StringBuilder();

                                    for (int k = 0; k < result.size(); k++) {
                                        builder.append(result.get(k));

                                        if (k != (result.size() - 1))
                                            builder.append(", ");
                                    }

                                    output = fileWriter.writeFile(actionId,
                                            null,
                                            "Query result: [" + builder + "]");

                                    arrayResult.add(output);
                                }

                                case "favorite" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    ArrayList<String> result = commandUser.listOfMovieToListOfString(commandUser.
                                            searchMoviesByFavorite(movies,
                                                    users,
                                                    yearInput,
                                                    genresInput,
                                                    numberInput,
                                                    sortType), numberInput);

                                    StringBuilder builder = new StringBuilder();

                                    for (int k = 0; k < result.size(); k++) {
                                        builder.append(result.get(k));

                                        if (k != (result.size() - 1))
                                            builder.append(", ");
                                    }

                                    output = fileWriter.writeFile(actionId,
                                            null,
                                            "Query result: [" + builder + "]");

                                    arrayResult.add(output);
                                }

                                case "longest" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    ArrayList<String> result = commandUser.listOfMovieToListOfString(commandUser.
                                            searchMoviesByDuration(movies,
                                                    yearInput,
                                                    genresInput,
                                                    numberInput,
                                                    sortType), numberInput);

                                    StringBuilder builder = new StringBuilder();

                                    for (int k = 0; k < result.size(); k++) {
                                        builder.append(result.get(k));

                                        if (k != (result.size() - 1))
                                            builder.append(", ");
                                    }

                                    output = fileWriter.writeFile(actionId,
                                            null,
                                            "Query result: [" + builder + "]");

                                    arrayResult.add(output);
                                }

                                case "most_viewed" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    ArrayList<String> result = commandUser.listOfMovieToListOfString(commandUser.
                                            searchMoviesByViews(movies,
                                            users,
                                            yearInput,
                                            genresInput,
                                            numberInput,
                                            sortType), numberInput);


                                    StringBuilder builder = new StringBuilder();

                                    for (int k = 0; k < result.size(); k++) {
                                        builder.append(result.get(k));

                                        if (k != (result.size() - 1))
                                            builder.append(", ");
                                    }

                                    output = fileWriter.writeFile(actionId,
                                            null,
                                            "Query result: [" + builder + "]");

                                    arrayResult.add(output);
                                }
                            }



                        }

                        case "shows" -> {
                            String commandCriteria = currentCommand.getCriteria();

                            List<String> genresInput = currentCommand.getFilters().get(1);

                            ArrayList<String> result = null;

                            switch (commandCriteria) {
                                case "ratings" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    result = commandUser.listOfSerialToListOfString(commandUser.
                                            searchSerialsByRating(serials,
                                                    yearInput,
                                                    genresInput,
                                                    numberInput,
                                                    sortType), numberInput);
                                }

                                case "favorite" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    result = commandUser.listOfSerialToListOfString(commandUser.
                                            searchSerialsByFavorite(serials,
                                                    users,
                                                    yearInput,
                                                    genresInput,
                                                    numberInput,
                                                    sortType), numberInput);
                                }

                                case "longest" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    result = commandUser.listOfSerialToListOfString(commandUser.
                                            searchSerialsByDuration(serials,
                                                    yearInput,
                                                    genresInput,
                                                    numberInput,
                                                    sortType), numberInput);
                                }

                                case "most_viewed" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    result = commandUser.listOfSerialToListOfString(commandUser.
                                            searchSerialsByViews(serials,
                                            users,
                                            yearInput,
                                            genresInput,
                                            numberInput,
                                            sortType), numberInput);
                                }
                            }

                            StringBuilder builder = new StringBuilder();

                            for (int k = 0; k < result.size(); k++) {
                                builder.append(result.get(k));

                                if (k != (result.size() - 1))
                                    builder.append(", ");
                            }

                            output = fileWriter.writeFile(actionId,
                                    null,
                                    "Query result: [" + builder + "]");

                            arrayResult.add(output);
                        }
                    }
                }

                case RECOMMENDATION -> {
                    String type = currentCommand.getType();

                    User commandUser = new User();

                    String userNameInput = currentCommand.getUsername();

                    String result = null;

                    switch (type) {
                        case "standard" -> {

                            for (User userIterator : users.getUsers()) {
                                if (userIterator.getUsername().equals(userNameInput)) {
                                    result = commandUser.recommendationStandard(userIterator,
                                                                                movies,
                                                                                serials);

                                    break;
                                }
                            }

                            if (result.equals("cannot be applied!"))
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "StandardRecommendation " + result);

                            else
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "StandardRecommendation result: " + result);

                            arrayResult.add(output);

                        }

                        case "best_unseen" -> {

                            for (User userIterator : users.getUsers()) {
                                if (userIterator.getUsername().equals(userNameInput)) {
                                    result = commandUser.recommendationBestUnseen(userIterator,
                                            movies,
                                            serials);

                                    break;
                                }
                            }

                            if (result.equals("cannot be applied!"))
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "BestRatedUnseenRecommendation " + result);

                            else
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "BestRatedUnseenRecommendation result: " + result);

                            arrayResult.add(output);
                        }

                        case "popular" -> {
                            result = commandUser.recommendationPopular(userNameInput,
                                                                        users,
                                                                        movies,
                                                                        serials);
                            if (result.equals("cannot be applied!")) {
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "PopularRecommendation " + result);
                            }
                            else {
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "PopularRecommendation result: " + result);
                            }

                            arrayResult.add(output);
                        }

                        case "favorite" -> {
                            result = commandUser.recommendationFavorite(userNameInput,
                                                                        users,
                                                                        movies,
                                                                        serials);
                            if (result.equals("cannot be applied!"))
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "FavoriteRecommendation " + result);

                            else
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "FavoriteRecommendation result: " + result);

                            arrayResult.add(output);
                        }

                        case "search" -> {
                            String genre = currentCommand.getGenre();

                            ArrayList<String> resultSearch = commandUser.recommendationSearch(userNameInput,
                                    users,
                                    movies,
                                    serials,
                                    genre);;
                            if (resultSearch == null || resultSearch.size() == 0) {
                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "SearchRecommendation cannot be applied!");
                            }
                            else {
                                StringBuilder builder = new StringBuilder();

                                for (int k = 0; k < resultSearch.size(); k++) {
                                    builder.append(resultSearch.get(k));

                                    if (k != (resultSearch.size() - 1))
                                        builder.append(", ");
                                }

                                output = fileWriter.writeFile(actionId,
                                        null,
                                        "SearchRecommendation result: [" + builder + "]");
                            }

                            arrayResult.add(output);
                        }
                    }
                }
            }
        }

        fileWriter.closeJSON(arrayResult);
    }
}
