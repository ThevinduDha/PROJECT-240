package com.eventBooking.services;

import com.eventBooking.models.provider.Provider;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProviderService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderService.class);
    private static final String PROVIDER_FILE = "data/providers.txt"; // Consolidated file

    public List<Provider> getAllProviders() {
        List<Provider> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROVIDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(Provider.fromFileString(line));
            }
        } catch (IOException e) {
            logger.error("Error reading {}: {}", PROVIDER_FILE, e.getMessage());
        }
        return list;
    }

    public List<Provider> getProvidersByType(Provider.ProviderType type) {
        return getAllProviders().stream()
                .filter(provider -> provider.getProviderType() == type)
                .collect(Collectors.toList());
    }

    public void addProvider(Provider provider) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROVIDER_FILE, true))) {
            writer.write(provider.toFileString());
            writer.newLine();
        } catch (IOException e) {
            logger.error("Error writing to {}: {}", PROVIDER_FILE, e.getMessage());
        }
    }

    public boolean removeProvider(String name) {
        List<Provider> allProviders = getAllProviders();
        List<Provider> updatedList = new ArrayList<>();
        boolean removed = false;

        for (Provider provider : allProviders) {
            if (!provider.getName().equalsIgnoreCase(name)) {
                updatedList.add(provider);
            } else {
                removed = true;
            }
        }

        if (removed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROVIDER_FILE))) {
                for (Provider p : updatedList) {
                    writer.write(p.toFileString());
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                logger.error("Error updating {}: {}", PROVIDER_FILE, e.getMessage());
            }
        }
        return false;
    }

    public boolean updateProvider(String name, Provider updatedProvider) {
        List<Provider> allProviders = getAllProviders();
        List<Provider> updatedList = new ArrayList<>();
        boolean updated = false;

        for (Provider provider : allProviders) {
            if (provider.getName().equalsIgnoreCase(name)) {
                updatedList.add(updatedProvider);
                updated = true;
            } else {
                updatedList.add(provider);
            }
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROVIDER_FILE))) {
                for (Provider p : updatedList) {
                    writer.write(p.toFileString());
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                logger.error("Error updating {}: {}", PROVIDER_FILE, e.getMessage());
            }
        }
        return false;
    }

    public List<Provider> getProvidersSortedByRating(Provider.ProviderType type) {
        List<Provider> list = new ArrayList<>(getProvidersByType(type)); // Get providers of a specific type

        // Bubble Sort (Descending Order by Rating)
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j).getRating() < list.get(j + 1).getRating()) {
                    // Swap providers
                    Provider temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        return list;
    }
    
    public List<Provider> getAllProvidersSortedByRating() {
        List<Provider> list = new ArrayList<>(getAllProviders()); // Create a copy to avoid modifying original

        // Bubble Sort (Descending Order by Rating)
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j).getRating() < list.get(j + 1).getRating()) {
                    // Swap providers
                    Provider temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        return list;
    }

    public List<Provider> getAllProvidersSortedByRatingAscending() {
        List<Provider> list = new ArrayList<>(getAllProviders()); // Create a copy to avoid modifying original

        // Bubble Sort (Ascending Order by Rating)
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j).getRating() > list.get(j + 1).getRating()) {
                    // Swap providers
                    Provider temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        return list;
    }
}