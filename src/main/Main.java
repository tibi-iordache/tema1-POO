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

            JSONObject output;

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

                                            arrayResult.add(output);
                                        }

                                        case "duplicate" -> {
                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "error -> " + videoName + " is already in favourite list");

                                            arrayResult.add(output);
                                        }

                                        case "not seen" -> {

                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "error -> " + videoName + " is not seen");

                                            arrayResult.add(output);
                                        }
                                    }
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
                                            arrayResult.add(output);
                                        }
                                        case "not seen" -> {
                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "error -> " + videoName + " is not seen");
                                            arrayResult.add(output);
                                        }
                                        case "already rated" -> {
                                            output = fileWriter.writeFile(actionId,
                                                    null,
                                                    "error -> " + videoName + " has been already rated");
                                            arrayResult.add(output);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                case QUERY -> {
                    String objectType = currentCommand.getObjectType();
                    switch (objectType) {
                        case "users" -> {
                            ArrayList<User> result = users.getUsers().get(0).searchUsersByNumberOfRatings(users.getUsers(),
                                    input.getCommands().get(i).getNumber());

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

                            String sortType = currentCommand.getSortType();

                            User commandUser = new User();

                            switch (commandCriteria) {
                                case "average" -> {
                                    int numberOfActors = currentCommand.getNumber();

                                    ArrayList<String> firstNActors = commandUser.searchAverageActors(users.getUsers(),
                                            movies.getMovies(),
                                            serials.getSerials(),
                                            actors.getActors(),
                                            numberOfActors,
                                            sortType);

                                    StringBuilder builder = new StringBuilder();

                                    for (int k = 0; k < firstNActors.size(); k++) {
                                        builder.append(firstNActors.get(k));

                                        if (k != (firstNActors.size() - 1))
                                            builder.append(", ");
                                    }

                                    output = fileWriter.writeFile(actionId,
                                            null,
                                            "Query result: [" + builder + "]");

                                    arrayResult.add(output);
                                }

                                case "awards" -> {
                                    List<String> awardsToBeSearchedBy = currentCommand.getFilters().get(3);

                                    ArrayList<String> result = commandUser.searchActorsByAwards(actors.getActors(),
                                            awardsToBeSearchedBy,
                                            sortType);

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

                                case "filter_description" -> {
                                    List<String> wordsToBeSearched = currentCommand.getFilters().get(2);

                                    ArrayList<String> result = commandUser.searchActorsByFilterDescription(actors,
                                                                                                wordsToBeSearched,
                                                                                                sortType);

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
                        case "movies" -> {
                            String commandCriteria = currentCommand.getCriteria();

                            String sortType = currentCommand.getSortType();

                            List<String> genresInput = currentCommand.getFilters().get(1);

                            int numberInput = currentCommand.getNumber();

                            User commandUser = new User();

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

                            String sortType = currentCommand.getSortType();

                            List<String> genresInput = currentCommand.getFilters().get(1);

                            int numberInput = currentCommand.getNumber();

                            User commandUser = new User();

                            switch (commandCriteria) {
                                case "ratings" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    ArrayList<String> result = commandUser.listOfSerialToListOfString(commandUser.
                                            searchSerialsByRating(serials,
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

                                case "favorite" -> {
                                    int yearInput = 0;

                                    if (currentCommand.getFilters().get(0).get(0) != null) {
                                        yearInput = Integer.parseInt(currentCommand.getFilters().get(0).get(0));
                                    }

                                    ArrayList<String> result = commandUser.listOfSerialToListOfString(commandUser.
                                            searchSerialsByFavorite(serials,
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

                                    ArrayList<String> result = commandUser.listOfSerialToListOfString(commandUser.
                                            searchSerialsByDuration(serials,
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

                                    ArrayList<String> result = commandUser.listOfSerialToListOfString(commandUser.
                                            searchSerialsByViews(serials,
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
                    }
                }
            }
        }

        fileWriter.closeJSON(arrayResult);
    }
}
