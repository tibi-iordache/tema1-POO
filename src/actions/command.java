package actions;

public interface command {

    String addVideoToFavorite(String video);

    void viewVideo(String video);

    void rateMovie(String movie, Double grade);

    void rateShow(String show, Integer seasonNo, Double grade);
}
