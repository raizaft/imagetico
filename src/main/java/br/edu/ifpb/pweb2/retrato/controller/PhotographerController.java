package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.dto.PhotographerDTO;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/photographer")
public class PhotographerController {
    @Autowired
    private PhotographerService service;

    @GetMapping("/form")
    public String showForm(PhotographerDTO photographerDTO, Model model) {
        model.addAttribute("photographer", photographerDTO);
        return "photographer/form";
    }

    @PostMapping("/register")
    public String save(@Valid PhotographerDTO photographerDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("photographer", photographerDTO);
            return "photographer/form";
        }
        try {
            PhotographerDTO savedPhotographer = service.register(photographerDTO);
            return "redirect:/index";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "photographer/form";
        }
    }
}
