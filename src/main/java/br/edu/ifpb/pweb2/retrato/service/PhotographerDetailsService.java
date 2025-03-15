package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.model.User;
import br.edu.ifpb.pweb2.retrato.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class PhotographerDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PhotographerService photographerService;
    public PhotographerDetailsService(UserRepository userRepository, PhotographerService photographerService) {
        this.userRepository = userRepository;
        this.photographerService = photographerService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Procurando usuário com o email: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        System.out.println("Usuário encontrado: " + user.getEmail());
        System.out.println("Senha salva no banco: " + user.getPassword());

        Photographer photographer = photographerService.getPhotographerByEmail(email);
        if (photographer.isSuspended()) {
            throw new DisabledException("Conta suspensa");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
                        .stream()
                        .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getAuthority()))
                        .collect(Collectors.toList())
        );
    }
}
