package com.eventBooking.models.pricing;


public class VideographyPackage extends Package {
    private int maxVideoDuration;

    public int getMaxVideoDuration() { return maxVideoDuration; }

    public void setMaxVideoDuration(int maxVideoDuration) {
        this.maxVideoDuration = maxVideoDuration;
    }

    public VideographyPackage(String name, int price, int duration, int maxVideoDuration) {
        super(name, price, duration);
        this.maxVideoDuration = maxVideoDuration;
        setPackageType("VIDEOGRAPHER");
    }

    public String toFileString() {
        return super.toFileString() + "," + maxVideoDuration;
    }

    public static VideographyPackage fromFileString(String line) {
        String[] parts = line.split(",");
        VideographyPackage pkg = new VideographyPackage(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        // packageType is already set in the constructor
        return pkg;
    }
}
