package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import entities.*;
import actions.*;
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
import java.util.Objects;

import static common.Constants.COMMAND;
import static common.Constants.USER;

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

//        System.out.println(input.getUsers());
//        System.out.println(input.getMovies());
//        System.out.println(input.getSerials());
//        System.out.println(input.getActors());
//        System.out.println(input.getCommands());

        /*
        * Forming the data base
        * */
        ArrayList<User> users = new ArrayList<>();

        for(int i = 0; i < input.getUsers().size(); i++) {
            users.add(new User(input.getUsers().get(i).getUsername(),
                    input.getUsers().get(i).getSubscriptionType(),
                    input.getUsers().get(i).getHistory(),
                    input.getUsers().get(i).getFavoriteMovies()));
        }

        ArrayList<Movie> movies = new ArrayList<>();

        for(int i = 0; i < input.getMovies().size(); i++) {
            movies.add(new Movie(input.getMovies().get(i).getTitle(),
                                 input.getMovies().get(i).getCast(),
                                 input.getMovies().get(i).getGenres(),
                                 input.getMovies().get(i).getYear(),
                                 input.getMovies().get(i).getDuration()));
        }

        ArrayList<Serial> serials = new ArrayList<>();

        for(int i = 0; i < input.getSerials().size(); i++) {
            serials.add(new Serial(input.getSerials().get(i).getTitle(),
                                   input.getSerials().get(i).getCast(),
                                   input.getSerials().get(i).getGenres(),
                                   input.getSerials().get(i).getNumberSeason(),
                                   input.getSerials().get(i).getSeasons(),
                                   input.getSerials().get(i).getYear()));
        }

        ArrayList<Actor> actors = new ArrayList<>();

        for(int i = 0; i < input.getActors().size(); i++) {
            actors.add(new Actor(input.getActors().get(i).getName(),
                                 input.getActors().get(i).getCareerDescription(),
                                 input.getActors().get(i).getFilmography(),
                                 input.getActors().get(i).getAwards()));
        }

        for(int i = 0; i < input.getCommands().size(); i++) {
            if (input.getCommands().get(i).getActionType().equals(COMMAND)) {
                if (input.getCommands().get(i).getType().equals("favorite")) {
                    String userName = input.getCommands().get(i).getUsername();

                    String videoName = input.getCommands().get(i).getTitle();

                    for (User user : users) {
                        if (user.getUsername().equals(userName)) {
                            String check = user.addVideoToFavorite(videoName);

                            if (check.equals("succes")) {
                                JSONObject output = fileWriter.writeFile(input.getCommands().get(i).getActionId(),
                                        null,
                                        "success -> " + videoName + " was added as favourite");

                                arrayResult.add(output);
                            }

                            if (check.equals("duplicate")) {
                                JSONObject output = fileWriter.writeFile(input.getCommands().get(i).getActionId(),
                                        null,
                                        "error -> " + videoName + " is already in favourite list");

                                arrayResult.add(output);
                            }

                            if (check.equals("not seen")) {
                                JSONObject output = fileWriter.writeFile(input.getCommands().get(i).getActionId(),
                                        null,
                                        "error -> " + videoName + " is not seen");

                                arrayResult.add(output);
                            }
                        }
                    }

                    continue;
                }

                if (input.getCommands().get(i).getType().equals("view")) {
                    String userName = input.getCommands().get(i).getUsername();

                    String videoName = input.getCommands().get(i).getTitle();

                    for (User user : users) {
                        if (user.getUsername().equals(userName)) {
                            user.viewVideo(videoName);

                            JSONObject output = fileWriter.writeFile(input.getCommands().get(i).getActionId(),
                                    null,
                                    "success -> " + videoName + " was viewed with total views of " + user.getHistory().get(videoName));

                            arrayResult.add(output);
                        }
                    }

                    continue;
                }

                if (input.getCommands().get(i).getType().equals("rating")) {
                    String userName = input.getCommands().get(i).getUsername();

                    String videoName = input.getCommands().get(i).getTitle();

                    for (User user : users) {
                        if (user.getUsername().equals(userName)) {
                            if (input.getCommands().get(i).getSeasonNumber() != 0) {
                                // serial rating
                                user.rateShow(videoName, input.getCommands().get(i).getSeasonNumber(), input.getCommands().get(i).getGrade());

                                JSONObject output = fileWriter.writeFile(input.getCommands().get(i).getActionId(),
                                        null,
                                        "success -> " + videoName + " was rated with " + input.getCommands().get(i).getGrade() + " by " + userName);

                                arrayResult.add(output);
                            }
                            else {
                                // movie rating
                                user.rateMovie(videoName, input.getCommands().get(i).getGrade());

                                JSONObject output = fileWriter.writeFile(input.getCommands().get(i).getActionId(),
                                        null,
                                        "success -> " + videoName + " was rated with " + input.getCommands().get(i).getGrade() + " by " + userName);

                                arrayResult.add(output);

                            }
                        }
                    }

                    continue;
                }

            }
        }

        System.out.println(movies);
        System.out.println(serials);

        fileWriter.closeJSON(arrayResult);
    }
}
