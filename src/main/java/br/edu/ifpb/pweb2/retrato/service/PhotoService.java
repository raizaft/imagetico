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

    public Photo publish(Photo photo) {
        if (photo.getPhotographer() == null || photo.getPhotographer().getId() == null) {
            throw new RuntimeException("ID do fotógrafo não pode ser nulo.");
        }
        Photographer photographer = photographerRepository.findById(photo.getPhotographer().getId())
                .orElseThrow(() -> new RuntimeException("Fotógrafo não encontrado."));

        photo.setPhotographer(photographer);
        photo.setComments(new ArrayList<>());
        photo.setLikes(new ArrayList<>());

        if (photo.getPhotoPath() != null) {
            return photoRepository.save(photo);
        } else {
            throw new RuntimeException("Caminho da foto não está definido.");
        }
    }

    public void addComment(Integer photographerId, Integer photoId, String comment) {
        Photo photo = photoRepository.findById(photoId).orElseThrow();
        Photographer photographer = photographerRepository.findById(photographerId).orElseThrow();

        if (!photographer.isCanComment() && !photo.getPhotographer().getId().equals(photographerId)) {
            throw new RuntimeException("Você está suspenso de comentar em fotos de terceiros.");
        }

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
            Like likeToRemove = likeExists.get();
            photo.getLikes().remove(likeToRemove);
            likeRepository.delete(likeToRemove);
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

    public Photo findById(Integer id) {
        return photoRepository.findById(id).orElse(null);
    }
}
