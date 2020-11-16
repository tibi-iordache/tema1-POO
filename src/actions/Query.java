package actions;

import entities.Actor;
import entities.Movie;
import entities.Serial;
import entities.User;

import java.util.ArrayList;
import java.util.List;

public interface Query {
    ArrayList<User> searchUsersByNumberOfRatings(ArrayList<User> dataBase, int n);

    ArrayList<String> searchAverageActors(ArrayList<User> userDataBase,
                                          ArrayList<Movie> moviesDataBase,
                                          ArrayList<Serial> serialsDataBase,
                                          ArrayList<Actor> actorsDataBase,
                                          int n,
                                          String sortType);

    ArrayList<String> searchActorsByAwards(ArrayList<Actor> actorsDataBase, List<String> awardsSearched, String sortType);

    ArrayList<String> searchVideosByRating(ArrayList<Movie> movieDataBase, ArrayList<Serial> serialDataBase, int n);
}
