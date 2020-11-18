package actions;

import databases.ActorDataBase;
import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import entities.Actor;
import entities.Movie;
import entities.Serial;
import entities.User;

import java.util.ArrayList;
import java.util.List;

public interface Query {
    ArrayList<User> searchUsersByNumberOfRatings(ArrayList<User> dataBase,
                                                 int n,
                                                 String sortType);

    ArrayList<Actor> searchAverageActors(MovieDataBase movies,
                                         SerialDataBase serials,
                                         ActorDataBase actors,
                                         int n,
                                         String sortType);

    ArrayList<Actor> searchActorsByAwards(ArrayList<Actor> actorsDataBase,
                                           List<String> awardsSearched,
                                           String sortType);

    ArrayList<Actor> searchActorsByFilterDescription(ActorDataBase actors,
                                                      List<String> words,
                                                      String sortType);

    ArrayList<Movie> searchMoviesByRating(MovieDataBase moviesDataBase,
                                           int year,
                                           List<String> genre,
                                           int n,
                                           String sortType);

    ArrayList<Serial> searchSerialsByRating(SerialDataBase serialsDataBase,
                                            int year,
                                            List<String> genre,
                                            int n,
                                            String sortType);

    ArrayList<Movie> searchMoviesByFavorite(MovieDataBase moviesDataBase,
                                             UserDataBase usersDataBase,
                                             int year,
                                             List<String> genre,
                                             int n,
                                             String sortType);

    ArrayList<Serial> searchSerialsByFavorite(SerialDataBase serialsDataBase,
                                             UserDataBase usersDataBase,
                                             int year,
                                             List<String> genre,
                                             int n,
                                             String sortType);

    ArrayList<Movie> searchMoviesByDuration(MovieDataBase moviesDataBase,
                                             int year,
                                             List<String> genre,
                                             int n,
                                             String sortType);

    ArrayList<Serial> searchSerialsByDuration(SerialDataBase serialsDataBase,
                                             int year,
                                             List<String> genre,
                                             int n,
                                             String sortType);

    ArrayList<Movie> searchMoviesByViews(MovieDataBase moviesDataBase,
                                          UserDataBase usersDataBase,
                                          int year,
                                          List<String> genre,
                                          int n,
                                          String sortType);

    ArrayList<Serial> searchSerialsByViews(SerialDataBase serialsDataBase,
                                          UserDataBase usersDataBase,
                                          int year,
                                          List<String> genre,
                                          int n,
                                          String sortType);
}
