package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.model.Administrator;
import br.edu.ifpb.pweb2.retrato.repository.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService {
    @Autowired
    private AdministratorRepository administratorRepository;

    public Administrator login(String email, String password) {
        return administratorRepository.findByEmailAndPassword(email, password).orElse(null);
    }
}
