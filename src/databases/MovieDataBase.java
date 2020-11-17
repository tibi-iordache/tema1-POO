package databases;

import entertainment.Genre;
import entities.Movie;
import fileio.MovieInputData;
import jdk.jshell.execution.Util;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieDataBase {
    private ArrayList<Movie> movies;

    public MovieDataBase(List<MovieInputData> moviesInput) {
        movies = new ArrayList<>();



        for (MovieInputData movieInputData : moviesInput) {
            ArrayList<Genre> genres = new ArrayList<>();

            for (int i = 0; i < movieInputData.getGenres().size(); i++)
                genres.add(Utils.stringToGenre(movieInputData.getGenres().get(i)));

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

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "MovieDataBase{" +
                "movies=" + movies +
                '}';
    }
}
