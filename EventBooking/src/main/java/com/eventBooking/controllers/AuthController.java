package com.eventBooking.controllers;

import com.eventBooking.models.users.User;
import com.eventBooking.services.AdminService;
import com.eventBooking.services.BookingService;
import com.eventBooking.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import java.time.LocalDate;
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
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        String username = session.getAttribute("username").toString();

        model.addAttribute("bookings", bookingService.getBookingsByUser(username));
        logger.info("User booking fetched in /dashboard: {}", bookingService.getBookingsByUser(username));
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("error", false);
        return "login";
    }

    @GetMapping("/admin/login")
    public String adminLogin(Model model) {
        model.addAttribute("error", false);
        return "admin-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session, Model model) {

        // First check if it's an admin login
        if(adminService.validateAdminLogin(username, password)) {
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            session.setAttribute("role", "com/eventBooking/models/admin");
            model.addAttribute("error", false);
            return "admin-dashboard";
        }
        // Then check if it's a regular user login
        else if(userService.validateLogin(username, password)) {
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            model.addAttribute("error", false);

            // Check if the user has admin role in users.txt
            User user = userService.getUserByUsername(username);
            if(user != null && user.getEmail().equals("com/eventBooking/models/admin")) {
                session.setAttribute("role", "com/eventBooking/models/admin");
                return "admin-dashboard";
            }
            else {
                session.setAttribute("role", "com/eventBooking/models/user");
                return "redirect:/dashboard";
            }
        }
        else {
            model.addAttribute("message", "Invalid username or password");
            model.addAttribute("error", true);
            return "login";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("error", false);
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           HttpSession session, Model model) {
        User newUser = new User(username, password, "com/eventBooking/models/user");
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match");
            return "register";
        }
        if (userService.registerUser(newUser)){
            return "dashboard";
        }
        else {
            model.addAttribute("message", "User already exists. Login instead");
            model.addAttribute("error", true);
            return "login";
        }
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:";
    }


}
