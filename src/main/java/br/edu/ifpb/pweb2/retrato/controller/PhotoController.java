package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.dto.PhotoDTO;
import br.edu.ifpb.pweb2.retrato.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photos")
public class PhotoController {
    @Autowired
    private PhotoService service;

    @PostMapping("/upload")
    public ResponseEntity<PhotoDTO> uploadPhoto(@RequestBody PhotoDTO photoDTO) {
        PhotoDTO savedPhoto = service.publish(photoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPhoto);
    }
}
