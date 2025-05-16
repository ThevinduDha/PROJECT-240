package com.eventBooking.controllers;


import com.eventBooking.models.booking.Booking;
import com.eventBooking.models.pricing.Package;
import com.eventBooking.models.provider.Provider;
import com.eventBooking.services.PackageService;
import com.eventBooking.services.ProviderService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.eventBooking.services.BookingService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;



@Controller
@RequestMapping("/bookings")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService = new BookingService();
    private final ProviderService providerService = new ProviderService();
    private final PackageService packageService = new PackageService();

    @GetMapping("/new")
    public String showBookingForm(Model model, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "user/login";
        }

        List<Provider> providers = providerService.getAllProviders();
        List<Package> packages = packageService.getAllPackages();
        model.addAttribute("providers", providers);
        model.addAttribute("packages", packages);
        return "booking/booking";
    }

    @PostMapping("/create")
    public String createBooking(@RequestParam String providerName,
                                @RequestParam String eventDate,
                                @RequestParam String location,
                                @RequestParam String type,
                                @RequestParam(required = false) String packageName,
                                Model model,
                                HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "user/login";
        }

        Booking booking;
        if (packageName != null && !packageName.isEmpty()) {
            // Get the package details
            Package selectedPackage = packageService.getPackageByName(packageName).orElse(null);
            if (selectedPackage != null) {
                booking = new Booking(username, providerName, eventDate, location, type, "pending",
                        selectedPackage.getName(), selectedPackage.getPrice());
            } else {
                booking = new Booking(username, providerName, eventDate, location, type, "pending");
            }
        } else {
            booking = new Booking(username, providerName, eventDate, location, type, "pending");
        }

        boolean success = bookingService.createBooking(booking);
        List<Booking> bookings = bookingService.getBookingsByUser(username);
        model.addAttribute("bookings", bookings);
        return "booking/success";
    }

    @GetMapping("/success")
    public String success(){
        return "booking/success";
    }

    @GetMapping("/manage")
    public String viewBookings(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "user/login";
        }

        List<Booking> bookings = bookingService.getBookingsByUser(username);
        model.addAttribute("bookings", bookings);
        return "booking/manage";
    }


    @GetMapping("/{bookingId}")
    public String showBooking(@PathVariable String bookingId,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        logger.debug("Attempting to find booking {} for user {}", bookingId, username);
        Booking booking = bookingService.getBookingById(bookingId, username);

        model.addAttribute("booking", booking);
        redirectAttributes.addFlashAttribute("booking", booking);
        return "booking/bookingDetails";
    }

    @GetMapping("/cancel/{bookingId}")
    public String cancelBooking(@PathVariable String bookingId, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "user/login";
        }
        bookingService.deleteBooking(username, bookingId);
        model.addAttribute("message", "Booking cancelled successfully");
        return "redirect:/bookings/manage";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "common/dashboard";
    }

    @GetMapping("/upcoming")
    public String upcomingBookings(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "user/login";
        }

        List<Booking> bookings = bookingService.getBookingsByUser(username);
        model.addAttribute("bookings", bookings);
        return "booking/upcoming-bookings";
    }

}
