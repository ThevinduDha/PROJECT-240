package com.eventBooking.controllers;

import com.eventBooking.models.booking.Booking;
import com.eventBooking.models.review.Review;
import com.eventBooking.services.BookingService;
import com.eventBooking.services.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final BookingService bookingService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(BookingService bookingService, ReviewService reviewService) {
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    @GetMapping("/new/{bookingId}")
    public String showNewReviewForm(@PathVariable String bookingId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/user/login";
        }

        Optional<Booking> bookingOpt = bookingService.findBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
            return "redirect:/bookings/manage";
        }
        
        Booking booking = bookingOpt.get();
        if (!booking.getUsername().equals(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only review your own bookings.");
            return "redirect:/bookings/manage";
        }

        if (!"completed".equalsIgnoreCase(booking.getStatus())) {
             redirectAttributes.addFlashAttribute("errorMessage", "You can only review completed bookings.");
             return "redirect:/bookings/manage";
        }

        model.addAttribute("booking", booking);
        return "review/review-form";
    }
    
    @PostMapping("/submit")
    public String submitReview(@RequestParam String bookingId,
                               @RequestParam String providerName, // This comes from the hidden input in the form
                               @RequestParam int rating,
                               @RequestParam String comment,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/user/login";
        }

        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rating must be between 1 and 5.");
            return "redirect:/reviews/new/" + bookingId; 
        }
        if (comment == null || comment.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Comment cannot be empty.");
            return "redirect:/reviews/new/" + bookingId;
        }
        
        // Create Review object using the constructor that generates reviewId and createdAt
        Review review = new Review(bookingId, username, providerName, rating, comment);
        boolean success = reviewService.createReview(review); // Using your existing method name

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully!");
            return "redirect:/reviews/provider/" + providerName;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit review. Please try again.");
            return "redirect:/reviews/new/" + bookingId;
        }
    }

    @GetMapping("/provider/{providerName}")
    public String viewProviderReviews(@PathVariable String providerName, Model model, HttpSession session) {
        List<Review> reviews = reviewService.getReviewsByProvider(providerName);
        double averageRating = reviewService.getAverageRatingForProvider(providerName);

        model.addAttribute("providerName", providerName);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        
        return "review/provider-reviews";
    }
}