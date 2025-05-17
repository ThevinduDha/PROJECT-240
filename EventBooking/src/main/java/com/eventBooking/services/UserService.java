package com.eventBooking.services;

import com.eventBooking.models.users.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String USER_FILE = "data/users.txt";

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(User.fromFileString(line));
            }
        } catch (IOException e) {
            logger.error("Error reading users.txt: {}", e.getMessage());
            System.out.println(new File("data/users.txt").getAbsolutePath());
        }
        return users;
    }

    public boolean registerUser(User user) {
        if (getUserByUsername(user.getUsername()) != null) {
            return false; // already exists
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(user.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            logger.error("Error writing to users.txt: {}", e.getMessage());
            return false;
        }
    }

    public User getUserByUsername(String username) {
        for (User user : getAllUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean validateLogin(String username, String password) {
        User user = getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public boolean updateUser(String username, User updatedUser) {
        List<User> users = getAllUsers();
        List<User> updatedList = new ArrayList<>();
        boolean updated = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                updatedList.add(updatedUser);
                updated = true;
            } else {
                updatedList.add(user);
            }
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
                for (User user : updatedList) {
                    writer.write(user.toFileString());
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                logger.error("Error updating users.txt: {}", e.getMessage());
                return false;
            }
        }

        return false;
    }

    public boolean deleteUser(String username) {
        List<User> users = getAllUsers();
        List<User> updatedList = new ArrayList<>();
        boolean deleted = false;

        for (User user : users) {
            if (!user.getUsername().equals(username)) {
                updatedList.add(user);
            } else {
                deleted = true;
            }
        }

        if (deleted) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
                for (User user : updatedList) {
                    writer.write(user.toFileString());
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                logger.error("Error updating users.txt: {}", e.getMessage());
                return false;
            }
        }

        return false;
    }
}
