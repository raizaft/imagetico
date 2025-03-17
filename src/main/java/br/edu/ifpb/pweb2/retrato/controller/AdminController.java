package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import br.edu.ifpb.pweb2.retrato.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/administrator")
@PreAuthorize("hasRole('ROLE')")
public class AdminController {

    @Autowired
    private PhotographerService service;

    @Autowired
    private UserService userService;

    private Photographer getAuthenticatedPhotographer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return service.getPhotographerByEmail(email);
    }

    @GetMapping("/dashboardAdm")
    public String dashboardAdmin(   @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    Model model) {
        Photographer admin = getAuthenticatedPhotographer();
        if (admin == null || admin.getId() == null || !admin.isAdmin()) {
            return "redirect:/photographer/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Photographer> photographerPage = service.listPaginatedExcludingAdmin(admin.getId(), pageable);

        model.addAttribute("photographers", photographerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", photographerPage.getTotalPages());
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
        return "redirect:/administrator/dashboardAdm";
    }

    @PostMapping("/activate")
    public String activatePhotographer(@RequestParam Integer photographerId) {
        Photographer admin = getAuthenticatedPhotographer();
        if (admin == null || !admin.isAdmin()) {
            return "redirect:/photographer/login";
        }
        service.activatePhotographer(photographerId);
        return "redirect:/administrator/dashboardAdm";
    }

    @PostMapping("/suspend-comments")
    public String suspendComments(@RequestParam Integer photographerId) {
        service.suspendComments(photographerId);
        return "redirect:/administrator/dashboardAdm";
    }

    @PostMapping("/allow-comments")
    public String allowComments(@RequestParam Integer photographerId) {
        service.allowComments(photographerId);
        return "redirect:/administrator/dashboardAdm";
    }

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView) {
        modelAndView.setViewName("admin/form");
        modelAndView.addObject("photographer", new Photographer());
        return modelAndView;
    }

    @PostMapping("/register")
    public String registerPhotographer(@ModelAttribute @Valid Photographer photographer, @RequestParam("password") String password, BindingResult result, RedirectAttributes redirectAttributes) throws IOException {
        if (result.hasErrors()) {
            return "admin/form";
        }

        MultipartFile file = photographer.getProfilePhotoFile();
        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/";
            String fileName = photographer.getEmail() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            photographer.setProfilePhotoPath("/uploads/" + fileName);
        } else {
            photographer.setProfilePhotoPath("/uploads/generic-user.png");
        }

        photographer.setAdmin(true);
        service.register(photographer);
        userService.createUser(photographer, password);

        redirectAttributes.addFlashAttribute("mensagem", "Fotógrafo cadastrado com sucesso!");
        return "redirect:/photographer/login";
    }
}
