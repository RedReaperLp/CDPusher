package com.github.redreaperlp.cdpusher.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private static UserManager instance;
    List<User> users = new ArrayList<>();

    private UserManager() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                users.removeIf(User::shouldDump);
            }
        }, "User Dumper");
        t.setDaemon(true);
        t.start();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public Optional<User> getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        var user = new User(username);
        users.add(user);
        return Optional.of(user);
    }
}
