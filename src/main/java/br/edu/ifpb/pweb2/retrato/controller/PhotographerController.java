package br.edu.ifpb.pweb2.retrato.controller;


import br.edu.ifpb.pweb2.retrato.model.Photo;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.PhotoService;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import br.edu.ifpb.pweb2.retrato.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PhotoService servicePhoto;

    private Photographer getAuthenticatedPhotographer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return service.getPhotographerByEmail(email);
    }

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView) {
        modelAndView.setViewName("photographer/form");
        modelAndView.addObject("photographer", new Photographer());
        return modelAndView;
    }

    @PostMapping("/register")
    public String registerPhotographer(@ModelAttribute @Valid Photographer photographer, @RequestParam("password") String password, BindingResult result, RedirectAttributes redirectAttributes) throws IOException {
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

        userService.createUser(photographer, password);

        redirectAttributes.addFlashAttribute("mensagem", "Fotógrafo cadastrado com sucesso!");
        return "redirect:/photographer/login";
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
    public String dashboard(@RequestParam("photographerId") Integer photographerId, Model model) {
        // Recupera o fotógrafo da página inicial (home)
        Photographer photographerHome = service.findById(photographerId);
        if (photographerHome == null || photographerHome.getId() == null) {
            return "redirect:/photographer/login";
        }

        // Recupera o fotógrafo autenticado
        Photographer photographer = getAuthenticatedPhotographer();
        if (photographer == null || photographer.getId() == null) {
            return "redirect:/photographer/login";
        }

        // Verifica se o fotógrafo autenticado é um administrador
        boolean isAdmin = photographer.isAdmin();
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            // Caso seja administrador, pega todas as fotos de todos os fotógrafos
            List<Photographer> photographers = service.list();
            List<Photo> allPhotos = photographers.stream()
                    .flatMap(p -> p.getPhotos().stream())
                    .collect(Collectors.toList());
            Collections.reverse(allPhotos);  // Reverte a ordem das fotos
            model.addAttribute("followingPhotos", allPhotos);
        } else {
            // Caso não seja administrador, pega as fotos dos fotógrafos seguidos
            Photographer photographerFromDB = service.findById(photographer.getId());
            List<Photo> photos = photographerFromDB.getPhotos();
            List<Photographer> followingPhotographers = photographerFromDB.getFollowing();
            List<Photo> followingPhotos = new ArrayList<>(photos);

            // Adiciona as fotos dos fotógrafos seguidos
            for (Photographer followedPhotographer : followingPhotographers) {
                followingPhotos.addAll(followedPhotographer.getPhotos());
            }

            Collections.reverse(followingPhotos);  // Reverte a ordem das fotos
            model.addAttribute("followingPhotos", followingPhotos);

            // Lista os fotógrafos que o fotógrafo autenticado segue
            model.addAttribute("followingPhotographers", followingPhotographers);
        }

        // Lista todos os fotógrafos, excluindo o fotógrafo logado
        List<Photographer> photographers = service.list();
        model.addAttribute("photographers", photographers.stream()
                .filter(p -> !p.getId().equals(photographer.getId()))
                .collect(Collectors.toList()));

        // Adiciona o fotógrafo logado ao modelo
        model.addAttribute("photographerLogado", service.findById(photographer.getId()));

        return "photographer/dashboard";
    }

    @GetMapping("/profile")
    public String profile( Model model) {
        Photographer photographer = getAuthenticatedPhotographer();
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
                                     RedirectAttributes redirectAttributes,
                                     Model model) {

        Photographer photographerLogado = getAuthenticatedPhotographer();
        if (photographerLogado == null) {
            return "redirect:/photographer/login";
        }

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
    public String viewPhotographer(@PathVariable("photographerId") Integer id, Model model) {
        Photographer photographerLogado = getAuthenticatedPhotographer();
        if (photographerLogado == null) {
            return "redirect:/photographer/login";
        }

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
    public String following(@PathVariable("photographerId") Integer id, Model model) {
        Photographer photographerLogado = getAuthenticatedPhotographer();
        if (photographerLogado == null) {
            return "redirect:/photographer/login";
        }
        Photographer photographer = service.findById(id);
        List<Photographer> following = photographer.getFollowing();
        model.addAttribute("photographer", photographer);
        model.addAttribute("following", following);
        return "photographer/following";
    }

    @PostMapping("/allow-followers/{allow}")
    public String allowFollower(@PathVariable boolean allow){
        Photographer photographerLogado = getAuthenticatedPhotographer();
        if (photographerLogado == null) {
            return "redirect:/photographer/login";
        }

        photographerLogado.setFollowAllowed(allow);
        service.save(photographerLogado);
        return "redirect:/photographer/profile";
    }

    //    @PostMapping("/login")
//    public String login(@ModelAttribute @Valid Photographer photographer,
//                        BindingResult result,
//                        RedirectAttributes redirectAttributes,
//                        HttpSession session) {
//        if (result.hasErrors()) {
//            return "photographer/login";
//        }
//
//        Photographer photographerLogado = service.login(photographer.getName(), photographer.getEmail());
//
//        if (photographerLogado == null) {
//            redirectAttributes.addFlashAttribute("mensagem", "Nome ou e-mail inválidos.");
//            return "redirect:/photographer/login";
//        }
//
//        if (photographerLogado.isSuspended()) {
//            redirectAttributes.addFlashAttribute("mensagem", "Sua conta está suspensa. Entre em contato com o suporte.");
//            return "redirect:/photographer/login";
//        }
//
//        session.setAttribute("photographerLogado", photographerLogado);
//        redirectAttributes.addFlashAttribute("mensagem", "Usuário logado com sucesso!");
//
//        return "redirect:/photographer/dashboard";
//    }

}
