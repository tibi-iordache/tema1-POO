package databases;

import entertainment.User;
import fileio.UserInputData;
import java.util.ArrayList;
import java.util.List;

public final class UserDataBase {
    private final ArrayList<User> users;

    public UserDataBase(final List<UserInputData> usersInput) {
        // create the users list
        users = new ArrayList<User>();

        // iterate through each input
        for (UserInputData userInputData : usersInput) {
            // add the new user to the list
            users.add(new User(userInputData.getUsername(),
                    userInputData.getSubscriptionType(),
                    userInputData.getHistory(),
                    userInputData.getFavoriteMovies()));
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "UserDataBase{"
                + "users="
                + users
                + '}';
    }
}
