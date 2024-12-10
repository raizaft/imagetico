package br.edu.ifpb.pweb2.retrato.controller;


import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
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
    public Photographer getLoggedInPhotographer() {
        return new Photographer();
    }

    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView modelAndView){
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
                        RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return "photographer/login";
        }
        Photographer photographerLogado = service.login(photographer.getName(), photographer.getEmail());
        if(photographerLogado != null) {
            model.addAttribute("photographerLogado", photographerLogado);
            redirectAttributes.addFlashAttribute("mensagem", "Usuario logado com sucesso!");
            return "redirect:/photographer/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("mensagem", "Nome ou e-mail inválidos.");
            return "redirect:/photographer/login";
        }

    }
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("photographer", new Photographer());
        return "photographer/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@ModelAttribute("loggedInPhotographer") Photographer photographer, Model model) {
        model.addAttribute("photographer", photographer);
        return "photographer/dashboard";
    }
}
