package actions;

import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import entities.Serial;
import entities.User;

import java.util.ArrayList;

public interface Suggestions {
    String recommendationStandard(User user,
                                  MovieDataBase movies,
                                  SerialDataBase serials);

    String recommendationBestUnseen(User user,
                                    MovieDataBase movies,
                                    SerialDataBase serials);

    String recommendationPopular(String userName,
                                 UserDataBase users,
                                 MovieDataBase movies,
                                 SerialDataBase serials);

    String recommendationFavorite(String username,
                                  UserDataBase users,
                                  MovieDataBase movies,
                                  SerialDataBase serials);

    ArrayList<String> recommendationSearch(String userName,
                                           UserDataBase users,
                                           MovieDataBase movies,
                                           SerialDataBase serials,
                                           String genre);
}
