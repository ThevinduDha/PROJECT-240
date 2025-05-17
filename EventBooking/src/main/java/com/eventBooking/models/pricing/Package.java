package com.eventBooking.models.pricing;

public class Package {
    private String name;
    private int price;
    private int duration;
    private String packageType;


    public Package(String name, int price, int duration) {
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.packageType = "GENERIC";
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getDuration() { return duration; }
    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }

    public String toFileString() {
        return name + "," + price + "," + duration + "," + packageType;
    }

    public static Package fromFileString(String line) {
        String[] parts = line.split(",");
        Package pkg = new Package(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        if (parts.length > 3) {
            pkg.setPackageType(parts[3]);
        }
        return pkg;
    }
}
