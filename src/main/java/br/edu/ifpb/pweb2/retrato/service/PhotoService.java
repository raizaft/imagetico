package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.dto.PhotoDTO;
import br.edu.ifpb.pweb2.retrato.model.Comment;
import br.edu.ifpb.pweb2.retrato.model.Like;
import br.edu.ifpb.pweb2.retrato.model.Photo;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.repository.CommentRepository;
import br.edu.ifpb.pweb2.retrato.repository.LikeRepository;
import br.edu.ifpb.pweb2.retrato.repository.PhotoRepository;
import br.edu.ifpb.pweb2.retrato.repository.PhotographerRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotographerRepository photographerRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    public PhotoDTO publish(PhotoDTO photoDTO) {
        Photographer photographer = photographerRepository.findById(photoDTO.photographerId())
                .orElseThrow(() -> new RuntimeException("Fotógrafo não encontrado."));

        Photo photo = Photo.builder()
                .description(photoDTO.photoDescription())
                .comments(new ArrayList<>())
                .likes(new ArrayList<>())
                .photographer(photographer)
                .build();

        return new PhotoDTO(photoRepository.save(photo));
    }

    public void addComment(Integer photographerId, Integer photoId, String comment) {
        Photo photo = photoRepository.findById(photoId).orElseThrow();
        Photographer photographer = photographerRepository.findById(photographerId).orElseThrow();
        Comment commentToAdd = Comment.builder()
                .commentText(comment)
                .photographer(photographer)
                .photo(photo)
                .build();
        commentRepository.save(commentToAdd);
        photo.getComments().add(commentToAdd);
        photoRepository.save(photo);
    }

    public void likePhoto(Integer photographerId, Integer photoId) {
        Photo photo = photoRepository.findById(photoId).orElseThrow();
        Photographer photographer = photographerRepository.findById(photographerId).orElseThrow();
        Optional<Like> likeExists = likeRepository.findByPhotographerAndPhoto(photographer, photo);

        if (likeExists.isPresent()) {
            photo.getLikes().remove(likeExists.get());
            likeRepository.delete(likeExists.get());
        } else {
            Like likeToAdd = Like.builder()
                    .photographer(photographer)
                    .photo(photo)
                    .build();
            likeRepository.save(likeToAdd);
            photo.getLikes().add(likeToAdd);
        }
        photoRepository.save(photo);
    }
}
