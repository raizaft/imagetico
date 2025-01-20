package br.edu.ifpb.pweb2.retrato.controller;


import br.edu.ifpb.pweb2.retrato.model.Photographer;
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
import java.util.List;

@Controller
@RequestMapping("/photographer")
@SessionAttributes("photographerLogado")
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
        }


        service.register(photographer);
        redirectAttributes.addFlashAttribute("mensagem", "Fotógrafo cadastrado com sucesso!");
        return "redirect:/photographer/success";
    }

    @GetMapping("/success")
    public String success(Model model) {
        List<Photographer> photographers = service.list();
        model.addAttribute("photographers", photographers);
        return "photographer/success";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute @Valid Photographer photographer,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,  HttpSession session) {
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

        List<Photographer> photographers = service.list();
        model.addAttribute("photographers", photographers);
        model.addAttribute("photographerLogado", service.findById(photographer.getId()));
        return "photographer/dashboard";
    }

    @GetMapping("/profile")
    public String profile(@ModelAttribute("photographerLogado") Photographer photographer, Model model) {
        model.addAttribute("photographerLogado", photographer);
        return "photographer/profile";
    }

    @PostMapping("/follow/{followedId}")
    public String followPhotographer(@PathVariable("followedId") Integer followedId,
                                     @ModelAttribute("photographerLogado") Photographer photographerLogado,
                                     RedirectAttributes redirectAttributes) {
        try {
            Integer followerId = photographerLogado.getId();
            service.followPhotographer(followerId, followedId);
            redirectAttributes.addFlashAttribute("mensagem", "Você começou a seguir o fotógrafo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao tentar seguir o fotógrafo: " + e.getMessage());
        }
        return "redirect:/photographer/profile";
    }

    @GetMapping("/following")
    public String listFollowing(@ModelAttribute("photographerLogado") Photographer photographerLogado, Model model) {
        List<Photographer> following = photographerLogado.getFollowing();
        model.addAttribute("following", following);
        return "photographer/profile";
    }

    @GetMapping("/{id}/view")
    public String viewPhotographer(@PathVariable Integer id, Model model) {
        Photographer photographer = service.findById(id);
        model.addAttribute("photographer", photographer);
        return "photographer/view";
    }
}
