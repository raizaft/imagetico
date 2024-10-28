package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.dto.PhotographerDTO;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/photographers")
public class PhotographerController {
    @Autowired
    private PhotographerService service;

    @GetMapping
    public ResponseEntity<List<PhotographerDTO>> list() {
        List<PhotographerDTO> photographers = this.service.list();
        return ResponseEntity.ok(photographers);
    }

    @PostMapping("/register")
    public ResponseEntity<PhotographerDTO> register(@RequestBody PhotographerDTO photographerDTO) {
        PhotographerDTO savedPhotographer = service.register(photographerDTO);
        return ResponseEntity.ok(savedPhotographer);
    }
}
