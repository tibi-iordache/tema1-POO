package actions;

import databases.ActorDataBase;
import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import actor.Actor;
import entertainment.Movie;
import entertainment.Serial;
import entertainment.User;
import java.util.ArrayList;
import java.util.List;

public interface Query {
    /**
     * Global search that returns the most active users.
     *
     * @param users The users database
     * @param numberOfUsers The number of users that will be returned
     * @param sortType The sort type for query(asc/desc)
     * @return Returns an ArrayList of Users
     */
    ArrayList<User> searchUsersByNumberOfRatings(UserDataBase users,
                                                 int numberOfUsers,
                                                 String sortType);

    /**
     *  Search actors by the rating mean of the movies and serials they played in.
     *
     * @param movies The movies database
     * @param serials The serials database
     * @param actors The actors database
     * @param sortType The sort type of the list (asc/desc)
     * @return Returns an ArrayList of Actors
     */
    ArrayList<Actor> searchAverageActors(MovieDataBase movies,
                                         SerialDataBase serials,
                                         ActorDataBase actors,
                                         String sortType);

    /**
     * Search all the actors with the awards required. An actor must contain
     * all the awards given from the search call.
     *
     * @param actors The actors database
     * @param awardsSearched The list of awards to be searched by
     * @param sortType The sort type of the search(asc/desc)
     * @return Returns an ArrayList of Actors
     */
    ArrayList<Actor> searchActorsByAwards(ActorDataBase actors,
                                          List<String> awardsSearched,
                                          String sortType);

    /**
     *  Search the actors whose descriptions contain the searched words.
     *
     * @param actors The actors database
     * @param words The words searched
     * @param sortType The sort type(asc/desc)
     * @return Returns an ArrayList of Actors
     */
    ArrayList<Actor> searchActorsByFilterDescription(ActorDataBase actors,
                                                     List<String> words,
                                                     String sortType);

    /**
     * Search the best rated movies from the database.
     *
     * @param movies The movies data base
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @param sortType The sort type(asc/desc)
     * @return Returns an ArrayList of Movies
     */
    ArrayList<Movie> searchMoviesByRating(MovieDataBase movies,
                                           int year,
                                           List<String> genre,
                                           String sortType);

    /**
     * Search the best rated serials from the database.
     *
     * @param serials The serials database
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @param sortType The sort type (asc/desc)
     * @return Returns an ArrayList of Serials
     */
    ArrayList<Serial> searchSerialsByRating(SerialDataBase serials,
                                            int year,
                                            List<String> genre,
                                            String sortType);

    /**
     * Search the most favorite movies from the database.
     *
     * @param movies The movies database
     * @param users The users database
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @param sortType The sort type (asc/desc)
     * @return Returns an ArrayList of Movies
     */
    ArrayList<Movie> searchMoviesByFavorite(MovieDataBase movies,
                                             UserDataBase users,
                                             int year,
                                             List<String> genre,
                                             String sortType);

    /**
     * Search the most favorite serials from the database.
     *
     * @param serials The serial database
     * @param users The user database
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @param sortType The sort type(asc/desc)
     * @return Returns an ArrayList of Serials
     */
    ArrayList<Serial> searchSerialsByFavorite(SerialDataBase serials,
                                             UserDataBase users,
                                             int year,
                                             List<String> genre,
                                             String sortType);

    /**
     * Search the longest movies from the database.
     *
     * @param movies The movies database
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @param sortType The sort type(asc/desc)
     * @return Returns an ArrayList of Movies
     */
    ArrayList<Movie> searchMoviesByDuration(MovieDataBase movies,
                                             int year,
                                             List<String> genre,
                                             String sortType);

    /**
     *  Search the longest serials in the database.
     *
     * @param serials The serials data base
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @param sortType The sort type(asc/desc)
     * @return Returns an ArrayList of Serials
     */
    ArrayList<Serial> searchSerialsByDuration(SerialDataBase serials,
                                             int year,
                                             List<String> genre,
                                             String sortType);

    /**
     *  Search the most viewed movies from the database.
     *
     * @param movies The movies database
     * @param users The users database
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @param sortType The sort type(asc/desc)
     * @return Returns an ArrayList of Movies
     */
    ArrayList<Movie> searchMoviesByViews(MovieDataBase movies,
                                          UserDataBase users,
                                          int year,
                                          List<String> genre,
                                          String sortType);

    /**
     *  Search the most viewed serials from the database.
     *
     * @param serials The serials database
     * @param users The users database
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @param sortType The sort type(asc/desc)
     * @return Returns an ArrayList of Serials
     */
    ArrayList<Serial> searchSerialsByViews(SerialDataBase serials,
                                          UserDataBase users,
                                          int year,
                                          List<String> genre,
                                          String sortType);
}
