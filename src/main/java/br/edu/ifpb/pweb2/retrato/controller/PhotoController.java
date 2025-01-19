package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.dto.CommentDTO;
import br.edu.ifpb.pweb2.retrato.dto.LikeDTO;
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

    @PostMapping("/addComment")
    public String addComment(@RequestBody CommentDTO comment) {
        service.addComment(comment.photographerId(), comment.photoId(), comment.commentText());
        return "Comment added";
    }

    @PostMapping("/likePhoto")
    public String likePhoto(@RequestBody LikeDTO like) {
        service.likePhoto(like.photographerId(), like.photoId());
        return "Like added";
    }
}
