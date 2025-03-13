package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.dto.LoginDTO;
import br.edu.ifpb.pweb2.retrato.model.User;
import br.edu.ifpb.pweb2.retrato.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean authenticate(LoginDTO loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isPresent()) {
            User usuario = optionalUser.get();
            return passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword());
        }
        return false;
    }

    public User authenticateAndAccess(LoginDTO loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(usuario -> passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword()))
                .orElse(null);
    }
}
