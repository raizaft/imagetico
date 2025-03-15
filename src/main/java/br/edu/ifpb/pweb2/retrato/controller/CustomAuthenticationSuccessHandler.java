package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final PhotographerService photographerService;
    public CustomAuthenticationSuccessHandler(PhotographerService photographerService) {
        this.photographerService = photographerService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String email = authentication.getName();

        Photographer photographer = photographerService.getPhotographerByEmail(email);
        if (photographer != null) {
            request.getSession().setAttribute("loggedPhotographer", photographer);
            response.sendRedirect(request.getContextPath() + "/photographer/dashboard?photographerId=" + photographer.getId());
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login?error");
        }
    }
}