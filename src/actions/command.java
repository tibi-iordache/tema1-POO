package actions;

import entities.*;

public interface command {

    void addVideoToFavorite(Video video);

    void viewVideo(Video video);

    void rateVideo(Video video);
}
