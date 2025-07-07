import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDB {
    private static final String DB_FILE = "users.json";

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(DB_FILE);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONArray array = new JSONArray(json.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                users.add(new User(
                        obj.getString("username"),
                        obj.getString("password"),
                        obj.getString("role")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static boolean verifyCredentials(String username, String password) {
        for (User user : loadUsers()) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                return true;
            }
        }
        return false;
    }

    public static String getRole(String username) {
        for (User user : loadUsers()) {
            if (user.getUsername().equals(username)) return user.getRole();
        }
        return "user";
    }

    public static boolean addUser(User newUser) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(newUser.getUsername())) {
                return false; // Already exists
            }
        }

        users.add(newUser);
        saveUsers(users);
        return true;
    }

    private static void saveUsers(List<User> users) {
        JSONArray array = new JSONArray();
        for (User user : users) {
            JSONObject obj = new JSONObject();
            obj.put("username", user.getUsername());
            obj.put("password", user.getPassword());
            obj.put("role", user.getRole());
            array.put(obj);
        }

        try (FileWriter writer = new FileWriter(DB_FILE)) {
            writer.write(array.toString(2)); // pretty print
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
