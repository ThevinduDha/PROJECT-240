package com.eventBooking.controllers;

import com.eventBooking.models.pricing.Package;
import com.eventBooking.models.pricing.PhotographyPackage;
import com.eventBooking.models.pricing.VideographyPackage;
import com.eventBooking.services.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    // User view - List all packages
    @GetMapping
    public String listPackages(Model model) {
        List<PhotographyPackage> photoPackages = packageService.getPhotographyPackages();
        List<VideographyPackage> videoPackages = packageService.getVideographyPackages();

        model.addAttribute("photoPackages", photoPackages);
        model.addAttribute("videoPackages", videoPackages);

        return "package/packages";
    }

    // User view - View package details
    @GetMapping("/{name}")
    public String viewPackageDetails(@PathVariable String name, Model model) {
        Optional<Package> packageOpt = packageService.getPackageByName(name);

        if (packageOpt.isPresent()) {
            Package pkg = packageOpt.get();
            model.addAttribute("package", pkg);

            if (pkg instanceof PhotographyPackage) {
                model.addAttribute("packageType", "Photography");
                model.addAttribute("photoPackage", (PhotographyPackage) pkg);
            } else if (pkg instanceof VideographyPackage) {
                model.addAttribute("packageType", "Videography");
                model.addAttribute("videoPackage", (VideographyPackage) pkg);
            }

            return "package/package-details";
        } else {
            return "redirect:/packages";
        }
    }
}
