package com.eventBooking.models.booking;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Booking {
    private static final Logger logger = LoggerFactory.getLogger(Booking.class);

    private final String bookingId;
    private final String username;
    private String providerName;
    private String eventDate;
    private String location;
    private String eventType;
    private String formattedDate;
    private String packageName; // Added for package selection
    private int packagePrice; // Added for package price

    private String status; // pending, confirmed, completed

    // Add new constructor that includes bookingId
    public Booking(String bookingId, String username, String providerName, String eventDate,
                   String location, String eventType, String status) {
        this.bookingId = bookingId;  // Use the provided bookingId instead of generating new one
        this.username = username;
        this.providerName = providerName;
        this.eventDate = eventDate;
        this.location = location;
        this.eventType = eventType;
        this.status = status;
        this.packageName = "";
        this.packagePrice = 0;
        setFormattedDate();
    }

    // Constructor that includes bookingId and package information
    public Booking(String bookingId, String username, String providerName, String eventDate,
                   String location, String eventType, String status, String packageName, int packagePrice) {
        this.bookingId = bookingId;  // Use the provided bookingId instead of generating new one
        this.username = username;
        this.providerName = providerName;
        this.eventDate = eventDate;
        this.location = location;
        this.eventType = eventType;
        this.status = status;
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        setFormattedDate();
    }

    // Keep existing constructor for new bookings
    public Booking(String username, String providerName, String eventDate,
                   String location, String eventType, String status) {
        this.bookingId = UUID.randomUUID().toString();
        this.username = username;
        this.providerName = providerName;
        this.eventDate = eventDate;
        this.location = location;
        this.eventType = eventType;
        this.status = status;
        this.packageName = "";
        this.packagePrice = 0;
        setFormattedDate();
    }

    // Constructor for new bookings with package information
    public Booking(String username, String providerName, String eventDate,
                   String location, String eventType, String status, String packageName, int packagePrice) {
        this.bookingId = UUID.randomUUID().toString();
        this.username = username;
        this.providerName = providerName;
        this.eventDate = eventDate;
        this.location = location;
        this.eventType = eventType;
        this.status = status;
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        setFormattedDate();
    }

    public void setFormattedDate() {
        String dateString = eventDate;
        // Parse the string to LocalDate
        LocalDate date = LocalDate.parse(dateString);

        // Define the output format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy");

        // Format the date
        String formattedDate = date.format(formatter);

        logger.debug("Formatted date: {}", formattedDate);
        this.formattedDate = formattedDate;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getBookingId() {
        return bookingId;
    }
    public String getUsername() { return username; }
    public String getProviderName() { return providerName; }
    public String getEventDate() { return eventDate; }
    public String getLocation() { return location; }
    public String getEventType() { return eventType; }
    public String getStatus() { return status; }
    public String getPackageName() { return packageName; }
    public int getPackagePrice() { return packagePrice; }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toFileString() {
        return bookingId + "," + username + "," + providerName + "," + eventDate + "," + location + "," + eventType + "," + status + "," + packageName + "," + packagePrice;
    }

    public static Booking fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 9) {
            return new Booking(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], Integer.parseInt(parts[8]));
        } else {
            // For backward compatibility with existing data
            return new Booking(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
        }
    }

    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", username='" + username + '\'' +
                ", providerName='" + providerName + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", location='" + location + '\'' +
                ", eventType='" + eventType + '\'' +
                ", formattedDate='" + formattedDate + '\'' +
                ", status='" + status + '\'' +
                ", packageName='" + packageName + '\'' +
                ", packagePrice=" + packagePrice +
                '}';
    }


}
