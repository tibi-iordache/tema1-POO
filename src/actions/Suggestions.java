package actions;

import databases.MovieDataBase;
import databases.SerialDataBase;
import databases.UserDataBase;
import entities.Serial;

public interface Suggestions {
    String recommendationBestUnseen(UserDataBase userDataBase,
                                    MovieDataBase movies,
                                    SerialDataBase serials);

}
