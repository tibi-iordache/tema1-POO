package utils;

import actor.ActorsAwards;
import common.Constants;
import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import entertainment.Genre;
import actor.Actor;
import entertainment.Movie;
import entertainment.Serial;
import entertainment.User;
import entertainment.Video;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * The class contains static methods that helps with parsing.
 *
 * We suggest you add your static methods here or in a similar class.
 */
public final class Utils {
    /**
     * for coding style
     */
    private Utils() {
    }

    /**
     * Transforms a string into an enum
     * @param genre of video
     * @return an Genre Enum
     */
    public static Genre stringToGenre(final String genre) {
        return switch (genre.toLowerCase()) {
            case "action" -> Genre.ACTION;
            case "adventure" -> Genre.ADVENTURE;
            case "drama" -> Genre.DRAMA;
            case "comedy" -> Genre.COMEDY;
            case "crime" -> Genre.CRIME;
            case "romance" -> Genre.ROMANCE;
            case "war" -> Genre.WAR;
            case "history" -> Genre.HISTORY;
            case "thriller" -> Genre.THRILLER;
            case "mystery" -> Genre.MYSTERY;
            case "family" -> Genre.FAMILY;
            case "horror" -> Genre.HORROR;
            case "fantasy" -> Genre.FANTASY;
            case "science fiction" -> Genre.SCIENCE_FICTION;
            case "action & adventure" -> Genre.ACTION_ADVENTURE;
            case "sci-fi & fantasy" -> Genre.SCI_FI_FANTASY;
            case "animation" -> Genre.ANIMATION;
            case "kids" -> Genre.KIDS;
            case "western" -> Genre.WESTERN;
            case "tv movie" -> Genre.TV_MOVIE;
            default -> null;
        };
    }

    /**
     * Transforms a string into an enum
     * @param award for actors
     * @return an ActorsAwards Enum
     */
    public static ActorsAwards stringToAwards(final String award) {
        return switch (award) {
            case "BEST_SCREENPLAY" -> ActorsAwards.BEST_SCREENPLAY;
            case "BEST_SUPPORTING_ACTOR" -> ActorsAwards.BEST_SUPPORTING_ACTOR;
            case "BEST_DIRECTOR" -> ActorsAwards.BEST_DIRECTOR;
            case "BEST_PERFORMANCE" -> ActorsAwards.BEST_PERFORMANCE;
            case "PEOPLE_CHOICE_AWARD" -> ActorsAwards.PEOPLE_CHOICE_AWARD;
            default -> null;
        };
    }

    /**
     * Transforms an array of JSON's into an array of strings
     * @param array of JSONs
     * @return a list of strings
     */
    public static ArrayList<String> convertJSONArray(final JSONArray array) {
        if (array != null) {
            ArrayList<String> finalArray = new ArrayList<>();
            for (Object object : array) {
                finalArray.add((String) object);
            }
            return finalArray;
        } else {
            return null;
        }
    }

    /**
     * Transforms an array of JSON's into a map
     * @param jsonActors array of JSONs
     * @return a map with ActorsAward as key and Integer as value
     */
    public static Map<ActorsAwards, Integer> convertAwards(final JSONArray jsonActors) {
        Map<ActorsAwards, Integer> awards = new LinkedHashMap<>();

        for (Object iterator : jsonActors) {
            awards.put(stringToAwards((String) ((JSONObject) iterator).get(Constants.AWARD_TYPE)),
                    Integer.parseInt(((JSONObject) iterator).get(Constants.NUMBER_OF_AWARDS)
                            .toString()));
        }

        return awards;
    }

    /**
     * Transforms an array of JSON's into a map
     * @param movies array of JSONs
     * @return a map with String as key and Integer as value
     */
    public static Map<String, Integer> watchedMovie(final JSONArray movies) {
        Map<String, Integer> mapVideos = new LinkedHashMap<>();

        if (movies != null) {
            for (Object movie : movies) {
                mapVideos.put((String) ((JSONObject) movie).get(Constants.NAME),
                        Integer.parseInt(((JSONObject) movie).get(Constants.NUMBER_VIEWS)
                                .toString()));
            }
        } else {
            System.out.println("NU ESTE VIZIONAT NICIUN FILM");
        }

        return mapVideos;
    }

    /**
     *  Converts an ArrayList of Users to an ArrayList of Strings.
     *
     * @param users The list which will be converted
     * @param listSize  The size of the list
     * @return Returns an ArrayList of Strings with each user name
     */
    public static ArrayList<String> listOfUsersToListOfString(final ArrayList<User> users,
                                                              final int listSize) {
        ArrayList<String> result = new ArrayList<String>();

        // check if the size of the ArrayList<String> we want to get is bigger
        // than the list size
        int size = Math.min(listSize, users.size());

        for (int i = 0; i < size; i++) {
            // add the user name
            result.add(users.get(i).getUsername());
        }

        return result;
    }

    /**
     *  Converts an ArrayList of Videos to an ArrayList of Strings.
     *
     * @param videos The list which will be converted
     * @param listSize The size of the list
     * @return Returns an ArrayList of Strings with each video title
     */
    public static ArrayList<String> listOfVideoToListOfString(final ArrayList<Video> videos,
                                                              final int listSize) {
        ArrayList<String> result = new ArrayList<String>();

        // check if the size of the ArrayList<String> we want to get is bigger
        // than the list size
        int size = Math.min(listSize, videos.size());

        for (int i = 0; i < size; i++) {
            // add the video title
            result.add(videos.get(i).getTitle());
        }

        return result;
    }

    /**
     * Converts an ArrayList of Actors to an ArrayList of Strings.
     * @param actors The initial list
     * @param listSize The size of the list
     * @return Returns an ArrayList of Strings with each actor name
     */
    public static ArrayList<String> listOfActorsToListOfString(final ArrayList<Actor> actors,
                                                               final int listSize) {
        ArrayList<String> result = new ArrayList<String>();

        // check if the size of the ArrayList<String> we want to get is bigger
        // than the list size
        int size = Math.min(listSize, actors.size());

        for (int i = 0; i < size; i++) {
            // add the actor name
            result.add(actors.get(i).getName());
        }

        return result;
    }

    /**
     * Check if a Video has the same release year and genres as the filter.
     *
     * @param video The video to be checked
     * @param year The release year of the filter
     * @param genre The genres of the filter
     * @return Return true if the video checks all the filters
     */
    public static boolean checkFiltersForVideo(final Video video,
                                               final int year,
                                               final List<String> genre) {
        boolean checkGenres = true;

        // guard
        if (genre != null) {
            // we iterate through genres
            for (String genreIterator : genre) {
                if (genreIterator != null) {
                    // search the genre in the video genres list
                    boolean videoDoesNotContainGenre = !video.getGenres()
                                                             .contains(Utils
                                                             .stringToGenre(genreIterator));

                    if (videoDoesNotContainGenre) {
                        checkGenres = false;

                        break;
                    }
                }
            }

        }

        if (checkGenres) {
            if (year == 0) {
                // if no year was given to the filter, the video passed the filter
                return true;
            } else if (video.getReleaseYear() == year) {
                    // check if the release year is the same as the filter
                    return true;
                }
        }

        return false;
    }

    /**
     * Builds the output message for a user search.
     *
     * @param functionResult The list of elements found from the search
     * @return Returns a string with all the elements found
     */
    public static String queryResultBuilder(final ArrayList<String> functionResult) {
        // create the builder
        StringBuilder builder = new StringBuilder();

        // iterate through each string from the list
        for (int i = 0; i < functionResult.size(); i++) {
            // add the string to the builder
            builder.append(functionResult.get(i));

            // if we are not at the last word, add a comma between strings
            if (i != (functionResult.size() - 1)) {
                builder.append(", ");
            }
        }

        return "Query result: [" + builder + "]";
    }

    /**
     *  Calculate the most popular genres from the databases.
     *
     * @param users The users database
     * @param movies The movies database
     * @param serials The serials database
     * @return Returns an ArrayList of Genres
     */
    public static ArrayList<Genre> calculateTopGenres(final UserDataBase users,
                                                      final MovieDataBase movies,
                                                      final SerialDataBase serials) {
        // get a list of all the enum values
        Genre[] enums = Genre.values();

        // map that will help us at sorting the popularity of the genres
        Map<Genre, Integer> eachGenrePopularity = new HashMap<>();

        // iterate through each genre
        for (Genre enumsIterator : enums) {
            int genrePopularity = 0;

            // calculate the popularity from the movies
            for (Movie movieIterator : movies.getMovies()) {
                if (movieIterator.getGenres().contains(enumsIterator)) {
                    // add the number of views of each movie that is in the current genre
                    genrePopularity += movieIterator.getNumberOfViews(users);
                }
            }

            // calculate the popularity from the serials
            for (Serial serialIterator : serials.getSerials()) {
                if (serialIterator.getGenres().contains(enumsIterator)) {
                    // add the number of views of each serial that is in the current genre
                    genrePopularity += serialIterator.getNumberOfViews(users);
                }
            }

            // add the total number of views to the map
            eachGenrePopularity.put(enumsIterator, genrePopularity);
        }

        // sort the map
        List<Map.Entry<Genre, Integer>> sortedGenres =
                        new LinkedList<Map.Entry<Genre, Integer>>(eachGenrePopularity.entrySet());

        // sort by the number of views, in descending way
        sortedGenres.sort(new Comparator<Map.Entry<Genre, Integer>>() {
            @Override
            public int compare(final Map.Entry<Genre, Integer> o1,
                               final Map.Entry<Genre, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        }.reversed());

        // create a new list of Genre with the sorted genres
        ArrayList<Genre> result = new ArrayList<>();

        for (Map.Entry<Genre, Integer> iterator : sortedGenres) {
            // add only the genre name in the list
            result.add(iterator.getKey());
        }

        return result;
    }
}
