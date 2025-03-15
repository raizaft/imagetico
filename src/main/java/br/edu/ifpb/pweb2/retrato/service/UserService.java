package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.model.Authority;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.model.User;
import br.edu.ifpb.pweb2.retrato.repository.AuthorityRepository;
import br.edu.ifpb.pweb2.retrato.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(Photographer photographer, String password) {
        User user = new User();
        user.setEmail(photographer.getEmail());
        user.setPassword(passwordEncoder.encode(password));

        String authorityName = photographer.isAdmin() ? "ADMIN" : "USER";
        Authority authority = authorityRepository.findByAuthority(authorityName)
                .orElseGet(() -> authorityRepository.save(new Authority(authorityName)));
        user.getAuthorities().add(authority);
        userRepository.save(user);
    }

    public boolean isAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ADMIN"));
    }
}
