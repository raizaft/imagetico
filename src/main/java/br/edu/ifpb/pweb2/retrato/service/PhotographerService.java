package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.dto.PhotographerDTO;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.repository.PhotographerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotographerService {
    @Autowired
    private PhotographerRepository repository;

    public List<PhotographerDTO> list() {
        return repository.findAll().stream().map(PhotographerDTO::new).toList();
    }
}
