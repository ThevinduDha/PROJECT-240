package com.eventBooking.services;

import com.eventBooking.models.review.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    // Assuming REVIEW_FILE is defined in your original class, e.g.
    private static final String REVIEW_FILE = "data/reviews.dat"; // Ensure this directory and file are accessible

    public ReviewService() {
        // Ensure the data directory and file exist
        try {
            Files.createDirectories(Paths.get(REVIEW_FILE).getParent());
            if (!Files.exists(Paths.get(REVIEW_FILE))) {
                Files.createFile(Paths.get(REVIEW_FILE));
            }
        } catch (IOException e) {
            logger.error("Error ensuring review file/directory exists: {}", e.getMessage(), e);
        }
    }

    // Your existing method
    public boolean createReview(Review review) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEW_FILE, true))) {
            writer.write(review.toFileString());
            writer.newLine();
            logger.info("Review created successfully for booking ID: {}", review.getBookingId());
            return true;
        } catch (IOException e) {
            logger.error("Error writing to reviews file {}: {}", REVIEW_FILE, e.getMessage(), e);
            return false;
        }
    }

    // New method to get all reviews (reads file every time)
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(REVIEW_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                try {
                    Review review = Review.fromFileString(line);
                    reviews.add(review);
                } catch (Exception e) {
                    logger.error("Error parsing review from line: '{}'. Error: {}", line, e.getMessage(), e);
                    // Optionally skip this review or handle error differently
                }
            }
        } catch (IOException e) {
            logger.error("Error reading reviews file {}: {}", REVIEW_FILE, e.getMessage(), e);
        }
        return reviews;
    }

    // New method to get reviews by provider name
    public List<Review> getReviewsByProvider(String providerName) {
        return getAllReviews().stream()
                .filter(review -> review.getProviderName().equalsIgnoreCase(providerName))
                .collect(Collectors.toList());
    }

    /**
     * Gets the average rating for a specific provider
     *
     * @param providerName The name of the provider
     * @return The average rating as a double, returns 0.0 if no reviews exist
     */
    public double getAverageRatingForProvider(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            logger.warn("Provider name is null or empty");
            return 0.0;
        }

        List<Review> providerReviews = getReviewsByProvider(providerName);
        if (providerReviews.isEmpty()) {
            logger.info("No reviews found for provider: {}", providerName);
            return 0.0;
        }

        return providerReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public boolean updateReview(String reviewId, Review review) {
        List<Review> reviews = getAllReviews();
        boolean found = false;

        for (Review r : reviews) {
            if (r.getReviewId().equals(reviewId)) {
                r.setRating(review.getRating());
                r.setComment(review.getComment());
                ;
                found = true;
                break;
            }
        }

        if (found) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEW_FILE))) {
                for (Review r : reviews) {
                    writer.write(r.toFileString());
                    writer.newLine();
                }
                logger.info("Review updated successfully: {}", reviewId);
                return true;
            } catch (IOException e) {
                logger.error("Error updating review {}: {}", reviewId, e.getMessage(), e);
                return false;
            }
        }

        logger.warn("Review not found for update: {}", reviewId);
        return false;
    }

    public boolean deleteReview(String reviewId) {
        List<Review> reviews = getAllReviews();
        int initialSize = reviews.size();

        reviews.removeIf(review -> review.getReviewId().equals(reviewId));

        if (reviews.size() < initialSize) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEW_FILE))) {
                for (Review r : reviews) {
                    writer.write(r.toFileString());
                    writer.newLine();
                }
                logger.info("Review deleted successfully: {}", reviewId);
                return true;
            } catch (IOException e) {
                logger.error("Error deleting review {}: {}", reviewId, e.getMessage(), e);
                return false;
            }
        }

        logger.warn("Review not found for deletion: {}", reviewId);
        return false;
    }


}