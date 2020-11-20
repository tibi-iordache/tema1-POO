package databases;

import entertainment.Genre;
import entertainment.Movie;
import fileio.MovieInputData;
import utils.Utils;
import java.util.ArrayList;
import java.util.List;

public final class MovieDataBase {
    private final ArrayList<Movie> movies;

    public MovieDataBase(final List<MovieInputData> moviesInput) {
        // create the movies list
        movies = new ArrayList<>();

        // iterate through each input
        for (MovieInputData movieInputData : moviesInput) {
            // fist create the movie genre list
            ArrayList<Genre> genres = new ArrayList<Genre>();

            for (int i = 0; i < movieInputData.getGenres().size(); i++) {
                genres.add(Utils.stringToGenre(movieInputData.getGenres().get(i)));
            }

            // add the new movie to the list
            movies.add(new Movie(movieInputData.getTitle(),
                        movieInputData.getCast(),
                        genres,
                        movieInputData.getYear(),
                        movieInputData.getDuration()));
        }
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    @Override
    public String toString() {
        return "MovieDataBase{"
                + "movies="
                + movies
                + '}';
    }
}
