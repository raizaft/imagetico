package br.edu.ifpb.pweb2.retrato.controller;


import br.edu.ifpb.pweb2.retrato.model.Photo;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.PhotoService;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/photographer")
public class PhotographerController {

    @Autowired
    private PhotographerService service;

    @Autowired
    private PhotoService servicePhoto;


    @ModelAttribute("photographerLogado")
    public Photographer getLoggedInPhotographer(HttpSession session) {
        return Optional.ofNullable(session.getAttribute("photographerLogado"))
                .map(photographer -> service.findById(((Photographer) photographer).getId()))
                .orElse(null);
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
        List<Photographer> followingPhotographers = new ArrayList<>();

        if (photographer.isAdmin()) {
            List<Photographer> photographers = service.list();
            List<Photo> allPhotos = photographers.stream()
                    .flatMap(p -> p.getPhotos().stream())
                    .collect(Collectors.toList());

            Collections.reverse(allPhotos);
            model.addAttribute("followingPhotos", allPhotos);
        } else {

            Photographer photographerFromDB = service.findById(photographer.getId());
            List<Photo> photos = photographerFromDB.getPhotos();

            followingPhotographers = photographerFromDB.getFollowing();
            List<Photo> followingPhotos = new ArrayList<>(photos);
            for (Photographer followedPhotographer : followingPhotographers) {
                followingPhotos.addAll(followedPhotographer.getPhotos());
            }
            Collections.reverse(followingPhotos);
            model.addAttribute("followingPhotos", followingPhotos);
        }

        List<Photographer> photographers = service.list();
        model.addAttribute("followingPhotographers", followingPhotographers);
        model.addAttribute("photographers", photographers.stream().filter(p -> !p.getId().equals(photographer.getId())).collect(Collectors.toList()));
        model.addAttribute("photographerLogado", service.findById(photographer.getId()));
        model.addAttribute("isAdmin", photographer.isAdmin());
        return "photographer/dashboard";
    }

    @GetMapping("/dashboardAdm")
    public String dashboardAdmin(@ModelAttribute("photographerLogado") Photographer photographer, Model model) {
        if (photographer == null || photographer.getId() == null || !photographer.isAdmin()) {
            return "redirect:/photographer/login";
        }

        List<Photographer> photographers = service.list();
        model.addAttribute("photographers", photographers);
        model.addAttribute("photographerLogado", photographer);
        model.addAttribute("isAdmin", photographer.isAdmin());
        return "administrator/dashboardAdm";
    }

    @PostMapping("/suspend")
    public String suspendPhotographer(@RequestParam Integer photographerId, @ModelAttribute("photographerLogado") Photographer admin) {
        if (admin == null || !admin.isAdmin()) {
            return "redirect:/photographer/login";
        }
        service.suspendPhotographer(photographerId);
        return "redirect:/photographer/dashboardAdm";
    }

    @PostMapping("/activate")
    public String activatePhotographer(@RequestParam Integer photographerId, @ModelAttribute("photographerLogado") Photographer admin) {
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
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        try {
            Photographer followedPhotographer = service.findById(followedId);

            if (photographerLogado.getFollowing().contains(followedPhotographer)) {
                photographerLogado.getFollowing().remove(followedPhotographer);
                redirectAttributes.addFlashAttribute("mensagem", "Você deixou de seguir o fotógrafo!");
            } else {
                photographerLogado.getFollowing().add(followedPhotographer);
                redirectAttributes.addFlashAttribute("mensagem", "Você começou a seguir o fotógrafo!");
            }
            service.save(photographerLogado);
            model.addAttribute("isFollowing", isFollowing(photographerLogado, followedPhotographer));
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

        model.addAttribute("photos", photos);
        model.addAttribute("photographer", photographer);
        model.addAttribute("isFollowing", isFollowing(photographerLogado, photographer));
        return "photographer/view";
    }

    private static boolean isFollowing(Photographer photographerLogado, Photographer photographer) {
        return photographerLogado.getFollowing().stream().anyMatch(follower -> follower.getId().equals(photographer.getId()));
    }

    @GetMapping("/{photographerId}/following")
    public String following(@PathVariable("photographerId") Integer id,
                            @ModelAttribute("photographerLogado") Photographer photographerLogado, Model model) {
        Photographer photographer = service.findById(id);
        List<Photographer> following = photographer.getFollowing();
        model.addAttribute("photographer", photographer);
        model.addAttribute("following", following);
        return "photographer/following";
    }

    @PostMapping("/allow-followers/{allow}")
    public String allowFollower(@ModelAttribute("photographerLogado") Photographer photographerLogado,
                                @PathVariable boolean allow){
        photographerLogado.setFollowAllowed(allow);
        service.save(photographerLogado);
        return "redirect:/photographer/profile";
    }

}
