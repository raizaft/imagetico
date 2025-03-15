package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.dto.LoginDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/login")
    public ModelAndView getForm(ModelAndView modelAndView) {
        modelAndView.setViewName("photographer/login");
        modelAndView.addObject("loginRequest", new LoginDTO());
        return modelAndView;
    }
}

