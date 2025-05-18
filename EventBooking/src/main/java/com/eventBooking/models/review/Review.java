package com.eventBooking.models.review;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Review {
    private final String reviewId;
    private final String bookingId;
    private final String username;
    private final String providerName;

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review(String bookingId, String username, String providerName, int rating, String comment) {
        this.reviewId = UUID.randomUUID().toString();
        this.bookingId = bookingId;
        this.username = username;
        this.providerName = providerName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for loading from file
    public Review(String reviewId, String bookingId, String username, String providerName, int rating, String comment, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.bookingId = bookingId;
        this.username = username;
        this.providerName = providerName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUsername() {
        return username;
    }

    public String getProviderName() {
        return providerName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }



    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return reviewId + "," + bookingId + "," + username + "," + providerName + "," + rating + "," + comment + "," + createdAt.format(formatter);
    }

    public static Review fromFileString(String line) {
        String[] parts = line.split(",", 7); // Limit to 7 parts to handle commas in comment
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse(parts[6], formatter);
        return new Review(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), parts[5], createdAt);
    }
}