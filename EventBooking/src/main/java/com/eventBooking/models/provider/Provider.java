package com.eventBooking.models.provider;

public class Provider {
    private String name;
    private String specialty;
    private int rating;
    private String resolution; // New attribute
    private ProviderType providerType; // New attribute

    public enum ProviderType {
        PHOTOGRAPHER,
        VIDEOGRAPHER
    }

    public Provider(String name, String specialty, int rating, String resolution, ProviderType providerType) {
        this.name = name;
        this.specialty = specialty;
        this.rating = rating;
        this.resolution = resolution;
        this.providerType = providerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getRating() {
        return rating;
    }



    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public String toFileString() {
        return name + "," + specialty + "," + rating + "," + resolution + "," + providerType;
    }

    public static Provider fromFileString(String line) {
        String[] parts = line.split(",");
        // Assuming resolution is at index 3 and providerType is at index 4
        String name = parts[0];
        String specialty = parts[1];
        int rating = Integer.parseInt(parts[2]);
        String resolution = parts.length > 3 ? parts[3] : "DefaultResolution"; // Provide a sensible default
        ProviderType providerType = parts.length > 4 ? ProviderType.valueOf(parts[4].toUpperCase()) : ProviderType.PHOTOGRAPHER; // Default or handle error
        return new Provider(name, specialty, rating, resolution, providerType);
    }
}