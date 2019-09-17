package k4unl.minecraft.fastTravel.lib;

import java.util.ArrayList;
import java.util.List;

public class Users {

    private static List<User> userList;

    public static void init() {

        userList = new ArrayList<User>();
    }

    public static User getUserByName(String username) {

        for (User u : userList) {
            if (u.getUserName().equals(username)) {
                return u;
            }
        }
        User nUser = new User(username);
        userList.add(nUser);
        return nUser;
    }

    public static List<User> getUserList() {

        return userList;
    }

	public static void addUser(User user) {
		userList.add(user);
    }
}
