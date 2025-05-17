package com.eventBooking.controllers;

import com.eventBooking.models.provider.Provider;
import com.eventBooking.services.ProviderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/gallery")
public class GalleryController {
    ProviderService providerService = new ProviderService();
    /**
     * Returns the gallery page with all photographers.
     * @param model Spring MVC model to pass data to the view
     * @param session HTTP session to get the user's username
     * @return the gallery page
     */
    @GetMapping("/")
    public String gallery(Model model, HttpSession session) {
        // Check if the user is logged in
        if (session.getAttribute("username") == null) {
            return "user/login";
        }
        // Get all photographers from the database
        List<Provider> providers = providerService.getAllProviders();
        List<Provider> sortedProviders = providerService.getAllProvidersSortedByRating();
        List<Provider> sortedProvidersReverse = providerService.getAllProvidersSortedByRatingAscending();


        // Pass the list of providers to the view
        model.addAttribute("providers", providers);
        model.addAttribute("sortedProviders", sortedProviders);
        model.addAttribute("sortedProvidersReverse", sortedProvidersReverse);


        // Return gallery page
        return "provider/gallery";
    }
}
