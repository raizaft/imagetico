package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.model.Administrator;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.AdministratorService;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/administrator")
@SessionAttributes("admLogado")
public class AdministratorController {

    @Autowired
    private AdministratorService service;

    @Autowired
    private PhotographerService photographerService;

    @ModelAttribute("admLogado")
    public Administrator getAdmlogado() {
        return new Administrator();
    }

    @PostMapping("/loginAdm")
    public String login(@ModelAttribute Administrator adm,
                        BindingResult result,
                        RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return "administrator/loginAdm";
        }
        Administrator admLogado = service.login(adm.getEmail(), adm.getPassword());
        if(admLogado != null) {
            model.addAttribute("admLogado", admLogado);
            redirectAttributes.addFlashAttribute("mensagem", "Usuario logado com sucesso!");
            return "redirect:/administrator/dashboardAdm";
        } else {
            redirectAttributes.addFlashAttribute("mensagem", "Nome ou e-mail inválidos.");
            return "redirect:/administrator/loginAdm";
        }

    }

    @GetMapping("/loginAdm")
    public String loginForm(Model model) {
        model.addAttribute("administrator", new Administrator());
        return "administrator/loginAdm";
    }

    @GetMapping("/dashboardAdm")
    public String dashboard(@SessionAttribute("admLogado") Administrator adm, Model model) {
        if (adm == null || adm.getId() == null) {
            return "redirect:/administrator/loginAdm";
        }
        List<Photographer> photographers = photographerService.list();
        model.addAttribute("photographers", photographers);
        model.addAttribute("administrator", adm);

        return "administrator/dashboardAdm";
    }

    @PostMapping("/suspend")
    public String suspendPhotographer(@RequestParam Integer photographerId) {
        photographerService.suspendPhotographer(photographerId);
        return "redirect:/administrator/dashboardAdm";
    }
}
