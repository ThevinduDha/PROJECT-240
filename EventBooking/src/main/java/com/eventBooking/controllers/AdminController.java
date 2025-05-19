package com.eventBooking.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import com.eventBooking.models.booking.Booking;
import com.eventBooking.services.BookingService;
import com.eventBooking.models.provider.Provider;
import com.eventBooking.services.ProviderService;
import com.eventBooking.models.pricing.Package;
import com.eventBooking.models.pricing.PhotographyPackage;
import com.eventBooking.models.pricing.VideographyPackage;
import com.eventBooking.services.PackageService;
import com.eventBooking.models.user.User;
import com.eventBooking.services.UserService;
import com.eventBooking.models.review.Review;
import com.eventBooking.services.ReviewService;
import com.eventBooking.models.admin.Admin;
import com.eventBooking.services.AdminService;



@Controller
@RequestMapping("/admin")
public class AdminController {
    private final BookingService bookingService = new BookingService();
    private final ProviderService providerService = new ProviderService();
    private final UserService userService = new UserService();
    private final ReviewService reviewService = new ReviewService();
    private final AdminService adminService = new AdminService();

    @Autowired
    private PackageService packageService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        List<Booking> bookings = bookingService.getAllBookings();
        List<Provider> providers = providerService.getAllProviders();
        model.addAttribute("bookings", bookings);
        model.addAttribute("providers", providers);

        return "admin/admin-dashboard";
    }

    // Booking management methods

    @GetMapping("/bookings")
    public String adminBookings(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);

        return "booking/admin-bookings";

    }

    @GetMapping("/bookings/create")
    public String showCreateBookingForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        List<User> users = userService.getAllUsers();
        List<Provider> providers = providerService.getAllProviders();
        List<Package> packages = packageService.getAllPackages();

        model.addAttribute("users", users);
        model.addAttribute("providers", providers);
        model.addAttribute("packages", packages);
        model.addAttribute("isEdit", false);

        return "booking/booking-form";
    }

    @PostMapping("/bookings/create")
    public String createBooking(
            @RequestParam String username,
            @RequestParam String providerName,
            @RequestParam String eventDate,
            @RequestParam String location,
            @RequestParam String eventType,
            @RequestParam(required = false) String packageName,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        try {
            // Create booking object using the helper method
            Booking booking = bookingService.createBookingObject(username, providerName, eventDate, location, eventType, packageName, packageService);

            boolean success = bookingService.createBooking(booking);

            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Booking created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create booking.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create booking: " + e.getMessage());
        }

        return "redirect:/admin/bookings";
    }

    @GetMapping("/bookings/delete/{bookingId}")
    public String deleteBooking(
            @PathVariable String bookingId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        // Find the booking to get the username
        String username = "";
        for (Booking booking : bookingService.getAllBookings()) {
            if (booking.getBookingId().equals(bookingId)) {
                username = booking.getUsername();
                break;
            }
        }

        if (!username.isEmpty()) {
            bookingService.deleteBooking(username, bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Booking deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete booking. Booking not found.");
        }

        return "redirect:/admin/bookings";
    }

    @PostMapping("/confirm")
    public String confirm(@RequestParam String providerName, @RequestParam String username, HttpSession session) {
        if (!isAdmin(session)) return "user/login";
        bookingService.updateBookingStatus(username, providerName, "confirmed");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/complete")
    public String complete(@RequestParam String bookingId, HttpSession session) {
        if (!isAdmin(session)) return "user/login";
        bookingService.completeBooking(bookingId);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/add-provider")
    public String addProvider(@RequestParam String providerName, @RequestParam String speciality, @RequestParam int rating,
                              @RequestParam Provider.ProviderType providerType, @RequestParam String resolution, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        Provider provider = new Provider(providerName, speciality, rating, resolution, providerType);

        providerService.addProvider(provider);

        return "redirect:/admin/dashboard";
    }



    @PostMapping("/remove-provider")
    public String removeProvider(@RequestParam String providerName, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        providerService.removeProvider(providerName);
        return "redirect:/admin/dashboard";
    }


    // Add this method to your AdminController.java
    @PostMapping("/bookings/complete/{bookingId}")
    public String completeBookingAsAdmin(@PathVariable String bookingId, 
                                         RedirectAttributes redirectAttributes, 
                                         HttpSession session) {
        if (!isAdmin(session)) { // Assuming you have an isAdmin() helper method
            return "redirect:/user/login"; // Or your access denied page
        }

        try {
            bookingService.completeBooking(bookingId); // bookingService should be injected
            redirectAttributes.addFlashAttribute("successMessage", "Booking marked as completed successfully!");
        } catch (Exception e) {
            // Log the exception e
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to mark booking as completed: " + e.getMessage());
        }
        return "redirect:/admin/bookings"; // Or the relevant admin page
    }

    // Ensure you have a helper method like this or similar logic for checking admin role
    private boolean isAdmin(HttpSession session) {
        // Your logic to check if the logged-in user is an admin
        // For example, checking a session attribute:
        // return "admin".equals(session.getAttribute("userRole"));
        // This is just a placeholder, implement based on your authentication setup.
        String userRole = (String) session.getAttribute("userRole");
        return "ADMIN".equalsIgnoreCase(userRole) || "admin".equals(session.getAttribute("username")); // Adjust as per your role management
    }

    // User management methods

    @GetMapping("/providers")
    public String adminProviders(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        List<Provider> providers = providerService.getAllProviders();
        model.addAttribute("providers", providers);

        return "provider/admin-providers";
    }

    @GetMapping("/users")
    public String adminUsers(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "user/admin-users";
    }

    @GetMapping("/users/create")
    public String showCreateUserForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        model.addAttribute("isEdit", false);
        return "admin/user-form";
    }

    @GetMapping("/users/edit/{username}")
    public String showEditUserForm(@PathVariable String username, Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        User user = userService.getUserByUsername(username);
        if (user != null) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("isEdit", true);

            return "admin/user-form";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/create")
    public String createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        try {
            User user = new User(username, password, email);
            boolean success = userService.registerUser(user);

            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create user. Username may already exist.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/update")
    public String updateUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        try {
            User updatedUser = new User(username, password, email);
            boolean success = userService.updateUser(username, updatedUser);

            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{username}")
    public String deleteUser(
            @PathVariable String username,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        boolean success = userService.deleteUser(username);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user.");
        }

        return "redirect:/admin/users";
    }

    // Package management methods

    // Admin view - List all packages for management
    @GetMapping("/packages")
    public String adminPackages(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        List<PhotographyPackage> photoPackages = packageService.getPhotographyPackages();
        List<VideographyPackage> videoPackages = packageService.getVideographyPackages();

        model.addAttribute("photoPackages", photoPackages);
        model.addAttribute("videoPackages", videoPackages);

        return "package/admin-packages";
    }

    // Admin view - Show form to create a new package
    @GetMapping("/packages/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        model.addAttribute("isEdit", false);
        return "package/package-form";
    }

    // Admin view - Show form to edit an existing package
    @GetMapping("/packages/edit/{name}")
    public String showEditForm(@PathVariable String name, Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        Optional<Package> packageOpt = packageService.getPackageByName(name);

        if (packageOpt.isPresent()) {
            Package pkg = packageOpt.get();
            model.addAttribute("packageName", pkg.getName());
            model.addAttribute("packagePrice", pkg.getPrice());
            model.addAttribute("packageDuration", pkg.getDuration());

            if (pkg instanceof PhotographyPackage) {
                model.addAttribute("packageType", "PHOTO");
                model.addAttribute("maxPhotoCount", ((PhotographyPackage) pkg).getMaxPhotoCount());
            } else if (pkg instanceof VideographyPackage) {
                model.addAttribute("packageType", "VIDEO");
                model.addAttribute("maxVideoDuration", ((VideographyPackage) pkg).getMaxVideoDuration());
            }

            model.addAttribute("isEdit", true);
            model.addAttribute("oldName", name);

            return "package/package-form";
        } else {
            return "redirect:/admin/packages";
        }
    }

    // Admin action - Create a new package
    @PostMapping("/packages/create")
    public String createPackage(
            @RequestParam String packageType,
            @RequestParam String packageName,
            @RequestParam int packagePrice,
            @RequestParam int packageDuration,
            @RequestParam(required = false) Integer maxPhotoCount,
            @RequestParam(required = false) Integer maxVideoDuration,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        try {
            if ("PHOTO".equals(packageType)) {
                PhotographyPackage pkg = new PhotographyPackage(
                        packageName, packagePrice, packageDuration, maxPhotoCount != null ? maxPhotoCount : 0);
                packageService.addPackage(packageType, pkg);
            } else if ("VIDEO".equals(packageType)) {
                VideographyPackage pkg = new VideographyPackage(
                        packageName, packagePrice, packageDuration, maxVideoDuration != null ? maxVideoDuration : 0);
                packageService.addPackage(packageType, pkg);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Package created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create package: " + e.getMessage());
        }

        return "redirect:/admin/packages";
    }

    // Admin action - Update an existing package
    @PostMapping("/packages/update")
    public String updatePackage(
            @RequestParam String oldName,
            @RequestParam String packageType,
            @RequestParam String packageName,
            @RequestParam int packagePrice,
            @RequestParam int packageDuration,
            @RequestParam(required = false) Integer maxPhotoCount,
            @RequestParam(required = false) Integer maxVideoDuration,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        try {
            if ("PHOTO".equals(packageType)) {
                PhotographyPackage pkg = new PhotographyPackage(
                        packageName, packagePrice, packageDuration, maxPhotoCount != null ? maxPhotoCount : 0);
                packageService.updatePackage(oldName, packageType, pkg);
            } else if ("VIDEO".equals(packageType)) {
                VideographyPackage pkg = new VideographyPackage(
                        packageName, packagePrice, packageDuration, maxVideoDuration != null ? maxVideoDuration : 0);
                packageService.updatePackage(oldName, packageType, pkg);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Package updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update package: " + e.getMessage());
        }

        return "redirect:/admin/packages";
    }

    // Admin action - Delete a package
    @GetMapping("/packages/delete/{name}")
    public String deletePackage(@PathVariable String name, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        boolean deleted = packageService.deletePackage(name);

        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Package deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete package.");
        }

        return "redirect:/admin/packages";
    }

    // Review management methods

    @GetMapping("/reviews")
    public String adminReviews(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        List<Review> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);

        return "review/admin-reviews";
    }

    @GetMapping("/reviews/edit/{reviewId}")
    public String showEditReviewForm(@PathVariable String reviewId, Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        Review review = null;
        for (Review r : reviewService.getAllReviews()) {
            if (r.getReviewId().equals(reviewId)) {
                review = r;
                break;
            }
        }

        if (review != null) {
            model.addAttribute("reviewId", review.getReviewId());
            model.addAttribute("bookingId", review.getBookingId());
            model.addAttribute("username", review.getUsername());
            model.addAttribute("providerName", review.getProviderName());
            model.addAttribute("rating", review.getRating());
            model.addAttribute("comment", review.getComment());
            model.addAttribute("isEdit", true);

            return "review/admin-review-form";
        } else {
            return "redirect:/admin/reviews";
        }
    }

    @PostMapping("/reviews/update")
    public String updateReview(
            @RequestParam String reviewId,
            @RequestParam String bookingId,
            @RequestParam String username,
            @RequestParam String providerName,
            @RequestParam int rating,
            @RequestParam String comment,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        try {
            // Find the original review to get its creation date
            Review originalReview = null;
            for (Review r : reviewService.getAllReviews()) {
                if (r.getReviewId().equals(reviewId)) {
                    originalReview = r;
                    break;
                }
            }

            if (originalReview != null) {
                Review updatedReview = new Review(
                    reviewId, 
                    bookingId, 
                    username, 
                    providerName, 
                    rating, 
                    comment, 
                    originalReview.getCreatedAt()
                );

                boolean success = reviewService.updateReview(reviewId, updatedReview);

                if (success) {
                    redirectAttributes.addFlashAttribute("successMessage", "Review updated successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to update review.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Review not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update review: " + e.getMessage());
        }

        return "redirect:/admin/reviews";
    }

    @GetMapping("/reviews/delete/{reviewId}")
    public String deleteReview(
            @PathVariable String reviewId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        boolean success = reviewService.deleteReview(reviewId);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete review.");
        }

        return "redirect:/admin/reviews";
    }

    // Admin management methods

    @GetMapping("/admins")
    public String adminAdmins(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        List<Admin> admins = adminService.getAllAdmins();
        model.addAttribute("admins", admins);

        return "admin/admin-admins";
    }

    @GetMapping("/admins/create")
    public String showCreateAdminForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "user/login";

        model.addAttribute("isEdit", false);
        return "admin/admin-form";
    }

    @PostMapping("/admins/create")
    public String createAdmin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        try {
            Admin admin = new Admin(username, password, email);
            boolean success = adminService.registerAdmin(admin);

            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Admin created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create admin. Username may already exist.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create admin: " + e.getMessage());
        }

        return "redirect:/admin/admins";
    }

    @GetMapping("/admins/delete/{username}")
    public String deleteAdmin(
            @PathVariable String username,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!isAdmin(session)) return "user/login";

        // Don't allow deleting the current admin
        String currentUsername = (String) session.getAttribute("username");
        if (username.equals(currentUsername)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own admin account.");
            return "redirect:/admin/admins";
        }

        boolean success = adminService.deleteAdmin(username);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Admin deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete admin.");
        }

        return "redirect:/admin/admins";
    }
}
