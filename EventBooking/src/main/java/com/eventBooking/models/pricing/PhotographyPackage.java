package com.eventBooking.models.pricing;

public class PhotographyPackage extends Package {
    private int maxPhotoCount;

    public PhotographyPackage(String name, int price, int duration, int maxPhotoCount) {
        super(name, price, duration);
        this.maxPhotoCount = maxPhotoCount;
        setPackageType("PHOTOGRAPHER");
    }

    public int getMaxPhotoCount() { return maxPhotoCount; }
    public void setMaxPhotoCount(int maxPhotoCount) { this.maxPhotoCount = maxPhotoCount; }

    public String toFileString() {
        return super.toFileString() + "," + maxPhotoCount;
    }

    public static PhotographyPackage fromFileString(String line) {
        String[] parts = line.split(",");
        PhotographyPackage pkg = new PhotographyPackage(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        // packageType is already set in the constructor
        return pkg;
    }
}
