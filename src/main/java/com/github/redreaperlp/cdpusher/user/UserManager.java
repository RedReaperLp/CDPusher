package com.github.redreaperlp.cdpusher.user;

import com.github.redreaperlp.cdpusher.util.logger.types.TestPrinter;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    List<User> users = new ArrayList<>();

    private UserManager() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                users.removeIf(User::shouldDump);
            }
        }, "User Dumper");
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void addUser(User user) {
        new TestPrinter().append("Adding user: " + user.getUsername()).print();
        if (users.contains(user)) {
            int index = users.indexOf(user);
            user.apply(users.get(index));
            users.set(index, user);
            return;
        }
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public User getUser(String username) {
        for (User user : users) {
            new TestPrinter().append(username + " - " + user.getUsername()).print();
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
