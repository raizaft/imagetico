package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/administrator")
public class AdminController {

    @Autowired
    private PhotographerService service;

    private Photographer getAuthenticatedPhotographer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return service.getPhotographerByEmail(email);
    }

    @GetMapping("/dashboardAdm")
    public String dashboardAdmin(Model model) {
        Photographer admin = getAuthenticatedPhotographer();
        if (admin == null || admin.getId() == null || !admin.isAdmin()) {
            return "redirect:/photographer/login";
        }

        List<Photographer> photographers = service.list();
        model.addAttribute("photographers", photographers);
        model.addAttribute("photographerLogado", admin);
        model.addAttribute("isAdmin", admin.isAdmin());
        return "administrator/dashboardAdm";
    }

    @PostMapping("/suspend")
    public String suspendPhotographer(@RequestParam Integer photographerId) {
        Photographer admin = getAuthenticatedPhotographer();
        if (admin == null || !admin.isAdmin()) {
            return "redirect:/photographer/login";
        }
        service.suspendPhotographer(photographerId);
        return "redirect:/photographer/dashboardAdm";
    }

    @PostMapping("/activate")
    public String activatePhotographer(@RequestParam Integer photographerId) {
        Photographer admin = getAuthenticatedPhotographer();
        if (admin == null || !admin.isAdmin()) {
            return "redirect:/photographer/login";
        }
        service.activatePhotographer(photographerId);
        return "redirect:/photographer/dashboardAdm";
    }

    @PostMapping("/suspend-comments")
    public String suspendComments(@RequestParam Integer photographerId) {
        service.suspendComments(photographerId);
        return "redirect:/photographer/dashboardAdm";
    }

    @PostMapping("/allow-comments")
    public String allowComments(@RequestParam Integer photographerId) {
        service.allowComments(photographerId);
        return "redirect:/photographer/dashboardAdm";
    }
}
