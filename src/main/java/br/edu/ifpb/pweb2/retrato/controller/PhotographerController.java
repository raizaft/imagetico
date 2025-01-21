package br.edu.ifpb.pweb2.retrato.controller;


import br.edu.ifpb.pweb2.retrato.model.Photo;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/photographer")
public class PhotographerController {
    @Autowired
    private PhotographerService service;

    @ModelAttribute("photographerLogado")
    public Photographer getLoggedInPhotographer(HttpSession session) {
        return (Photographer) session.getAttribute("photographerLogado");
    }

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView) {
        modelAndView.setViewName("photographer/form");
        modelAndView.addObject("photographer", new Photographer());
        return modelAndView;
    }

    @PostMapping("/register")
    public String registerPhotographer(@ModelAttribute @Valid Photographer photographer, BindingResult result, RedirectAttributes redirectAttributes) throws IOException {
        if (result.hasErrors()) {
            return "photographer/form";
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

        service.register(photographer);
        redirectAttributes.addFlashAttribute("mensagem", "Fotógrafo cadastrado com sucesso!");
        return "redirect:/photographer/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute @Valid Photographer photographer,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        if (result.hasErrors()) {
            return "photographer/login";
        }

        Photographer photographerLogado = service.login(photographer.getName(), photographer.getEmail());

        if (photographerLogado == null) {
            redirectAttributes.addFlashAttribute("mensagem", "Nome ou e-mail inválidos.");
            return "redirect:/photographer/login";
        }

        if (photographerLogado.isSuspended()) {
            redirectAttributes.addFlashAttribute("mensagem", "Sua conta está suspensa. Entre em contato com o suporte.");
            return "redirect:/photographer/login";
        }

        session.setAttribute("photographerLogado", photographerLogado);
        redirectAttributes.addFlashAttribute("mensagem", "Usuário logado com sucesso!");
        return "redirect:/photographer/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("photographerLogado");
        session.invalidate();
        return "redirect:/photographer/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("photographer", new Photographer());
        return "photographer/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@ModelAttribute("photographerLogado") Photographer photographer, Model model) {
        if (photographer == null || photographer.getId() == null) {
            return "redirect:/photographer/login";
        }

        Photographer photographerFromDB = service.findById(photographer.getId());
        List<Photo> photos = photographerFromDB.getPhotos();
        Collections.reverse(photos);

        List<Photographer> followingPhotographers = photographerFromDB.getFollowing();
        List<Photo> followingPhotos = new ArrayList<>();
        for (Photographer followedPhotographer : followingPhotographers) {
            followingPhotos.addAll(followedPhotographer.getPhotos());
        }

        List<Photographer> photographers = service.list();
        model.addAttribute("followingPhotos", followingPhotos);
        model.addAttribute("photos", photos);
        model.addAttribute("photographers", photographers.stream().filter(p -> !p.getId().equals(photographer.getId())).collect(Collectors.toList()));
        model.addAttribute("photographerLogado", service.findById(photographer.getId()));
        return "photographer/dashboard";
    }

    @GetMapping("/profile")
    public String profile(@ModelAttribute("photographerLogado") Photographer photographer, Model model) {
        if (photographer == null || photographer.getId() == null) {
            return "redirect:/photographer/login";
        }

        Photographer photographerFromDB = service.findById(photographer.getId());
        List<Photo> photos = photographerFromDB.getPhotos();
        Collections.reverse(photos);

        List<Photographer> following = service.findById(photographer.getId()).getFollowing();

        model.addAttribute("photographerLogado", photographer);
        model.addAttribute("following", following);
        model.addAttribute("photos", photos);
        return "photographer/profile";
    }


    @PostMapping("/follow/{followedId}")
    public String followPhotographer(@PathVariable("followedId") Integer followedId,
                                     @ModelAttribute("photographerLogado") Photographer photographerLogado,
                                     RedirectAttributes redirectAttributes) {
        try {
            Photographer managedPhotographer = service.findById(photographerLogado.getId());
            Photographer followedPhotographer = service.findById(followedId);

            if (managedPhotographer.getFollowing().contains(followedPhotographer)) {
                managedPhotographer.getFollowing().remove(followedPhotographer);
                redirectAttributes.addFlashAttribute("mensagem", "Você deixou de seguir o fotógrafo!");
            } else {
                managedPhotographer.getFollowing().add(followedPhotographer);
                redirectAttributes.addFlashAttribute("mensagem", "Você começou a seguir o fotógrafo!");
            }

            service.save(managedPhotographer);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao tentar seguir/deixar de seguir o fotógrafo: " + e.getMessage());
        }
        return "redirect:/photographer/{followedId}/view";
    }

    @GetMapping("/{photographerId}/view")
    public String viewPhotographer(@PathVariable("photographerId") Integer id,
                                   @ModelAttribute("photographerLogado") Photographer photographerLogado, Model model) {
        Photographer photographer = service.findById(id);

        List<Photo> photos = photographer.getPhotos();
        Collections.reverse(photos);

        boolean isFollowing = photographerLogado.getFollowing().contains(photographer);

        model.addAttribute("photos", photos);
        model.addAttribute("photographer", photographer);
        model.addAttribute("isFollowing", isFollowing);
        return "photographer/view";
    }

    @PostMapping("/allow-followers/{allow}")
    public String allowFollower(@ModelAttribute("photographerLogado") Photographer photographerLogado,
                                @PathVariable boolean allow){
        photographerLogado.setFollowAllowed(allow);
        service.save(photographerLogado);
        return "redirect:/photographer/profile";
    }

}
