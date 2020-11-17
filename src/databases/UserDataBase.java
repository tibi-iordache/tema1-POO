package databases;

import entities.User;
import fileio.UserInputData;

import java.util.ArrayList;
import java.util.List;

public class UserDataBase {
    private ArrayList<User> users;

    public UserDataBase(List<UserInputData> usersInput) {
        users = new ArrayList<>();

        for (UserInputData userInputData : usersInput) {
            users.add(new User(userInputData.getUsername(),
                    userInputData.getSubscriptionType(),
                    userInputData.getHistory(),
                    userInputData.getFavoriteMovies()));
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "UserDataBase{" +
                "users=" + users +
                '}';
    }
}
