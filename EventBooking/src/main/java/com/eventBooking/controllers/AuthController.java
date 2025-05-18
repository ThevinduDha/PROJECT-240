package com.eventBooking.controllers;

import com.eventBooking.models.user.User;
import com.eventBooking.services.AdminService;
import com.eventBooking.services.BookingService;
import com.eventBooking.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import org.springframework.stereotype.Controller;

@Controller
public class AuthController {
    private final UserService userService = new UserService();
    private final BookingService bookingService = new BookingService();
    private final AdminService adminService = new AdminService();
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/")
    public String home() {
        return "common/index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, HttpServletResponse response) {
        String username = session.getAttribute("username").toString();
        model.addAttribute("bookings", bookingService.getBookingsByUser(username));
        logger.info("User booking fetched in /dashboard: {}", bookingService.getBookingsByUser(username));

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        return "common/dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("error", false);
        return "user/login";
    }

    @GetMapping("/admin/login")
    public String adminLogin(Model model) {
        model.addAttribute("error", false);
        return "admin/admin-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session, Model model) {

        // First check if it's an admin login
        if(adminService.validateAdminLogin(username, password)) {
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            session.setAttribute("role", "admin");
            model.addAttribute("error", false);
            return "admin/admin-dashboard";
        }
        // Then check if it's a regular user login
        else if(userService.validateLogin(username, password)) {
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            model.addAttribute("error", false);

            // Check if the user has admin role in users.txt
            User user = userService.getUserByUsername(username);
            if(user != null && user.getEmail().equals("admin")) {
                session.setAttribute("role", "admin");
                return "admin/admin-dashboard";
            }
            else {
                session.setAttribute("role", "user");
                return "redirect:/dashboard";
            }
        }
        model.addAttribute("message", "Invalid username or password");
        model.addAttribute("error", true);
        return "user/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("error", false);
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           HttpSession session, Model model) {
        User newUser = new User(username, password, "user");
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match");
            return "user/register";
        }
        if (userService.registerUser(newUser)){
            return "common/dashboard";
        }
        else {
            model.addAttribute("message", "User already exists. Login instead");
            model.addAttribute("error", true);
            return "user/login";
        }
    }

    @GetMapping("/about")
    public String about() {
        return "common/about";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:";
    }


}