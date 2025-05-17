package com.eventBooking.services;

import com.eventBooking.models.pricing.Package;
import com.eventBooking.models.pricing.PhotographyPackage;
import com.eventBooking.models.pricing.VideographyPackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PackageService {
    private static final String PACKAGES_FILE = "data/packages.txt";
    
    // Load all packages from file
    public List<Package> getAllPackages() {
        List<Package> packages = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PACKAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String type = parts[0];
                    if ("PHOTO".equals(type)) {
                        packages.add(PhotographyPackage.fromFileString(line.substring(line.indexOf(",") + 1)));
                    } else if ("VIDEO".equals(type)) {
                        packages.add(VideographyPackage.fromFileString(line.substring(line.indexOf(",") + 1)));
                    }
                }
            }
        } catch (IOException e) {
            // If file doesn't exist yet, return empty list
            return packages;
        }
        
        return packages;
    }
    
    // Get package by name
    public Optional<Package> getPackageByName(String name) {
        return getAllPackages().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }
    
    // Add a new package
    public void addPackage(String type, Package pkg) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PACKAGES_FILE, true))) {
            writer.write(type + "," + pkg.toFileString());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to add package", e);
        }
    }
    
    // Update an existing package
    public boolean updatePackage(String oldName, String type, Package updatedPackage) {
        List<Package> packages = getAllPackages();
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PACKAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    lines.add(line);
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String pkgType = parts[0];
                    String pkgName = parts[1];
                    
                    if (pkgName.equals(oldName)) {
                        lines.add(type + "," + updatedPackage.toFileString());
                        found = true;
                    } else {
                        lines.add(line);
                    }
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }
        
        if (!found) {
            return false;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PACKAGES_FILE))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            return false;
        }
        
        return true;
    }
    
    // Delete a package
    public boolean deletePackage(String name) {
        List<Package> packages = getAllPackages();
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PACKAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    lines.add(line);
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String pkgName = parts[1];
                    
                    if (!pkgName.equals(name)) {
                        lines.add(line);
                    } else {
                        found = true;
                    }
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }
        
        if (!found) {
            return false;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PACKAGES_FILE))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            return false;
        }
        
        return true;
    }
    
    // Get all photography packages
    public List<PhotographyPackage> getPhotographyPackages() {
        List<PhotographyPackage> photoPackages = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PACKAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 4 && "PHOTO".equals(parts[0])) {
                    photoPackages.add(PhotographyPackage.fromFileString(line.substring(line.indexOf(",") + 1)));
                }
            }
        } catch (IOException e) {
            // If file doesn't exist yet, return empty list
            return photoPackages;
        }
        
        return photoPackages;
    }
    
    // Get all videography packages
    public List<VideographyPackage> getVideographyPackages() {
        List<VideographyPackage> videoPackages = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PACKAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 4 && "VIDEO".equals(parts[0])) {
                    videoPackages.add(VideographyPackage.fromFileString(line.substring(line.indexOf(",") + 1)));
                }
            }
        } catch (IOException e) {
            // If file doesn't exist yet, return empty list
            return videoPackages;
        }
        
        return videoPackages;
    }
}