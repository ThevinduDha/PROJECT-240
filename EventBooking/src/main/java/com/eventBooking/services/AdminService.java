package com.eventBooking.services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.eventBooking.models.admin.Admin;


@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private static final String ADMIN_FILE = "data/admin.txt";

    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    // Format: username,password
                    admins.add(new Admin(parts[0], parts[1], "admin"));
                }
            }
        } catch (IOException e) {
            logger.error("Error reading admin.txt: {}", e.getMessage());
        }
        return admins;
    }

    public Admin getAdminByUsername(String username) {
        for (Admin admin : getAllAdmins()) {
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }
        return null;
    }

    public boolean validateAdminLogin(String username, String password) {
        Admin admin = getAdminByUsername(username);
        return admin != null && admin.getPassword().equals(password);
    }

    public boolean registerAdmin(Admin admin) {
        if (getAdminByUsername(admin.getUsername()) != null) {
            return false; // already exists
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE, true))) {
            writer.write(admin.getUsername() + "," + admin.getPassword());
            writer.newLine();
            return true;
        } catch (IOException e) {
            logger.error("Error writing to admin.txt: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteAdmin(String username) {
        List<Admin> admins = getAllAdmins();
        List<String> adminLines = new ArrayList<>();
        boolean found = false;

        for (Admin admin : admins) {
            if (!admin.getUsername().equals(username)) {
                adminLines.add(admin.getUsername() + "," + admin.getPassword());
            } else {
                found = true;
            }
        }

        if (!found) {
            return false; // Admin not found
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE))) {
            for (String line : adminLines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            logger.error("Error writing to admin.txt: {}", e.getMessage());
            return false;
        }
    }
}
