package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.model.*;
import br.edu.ifpb.pweb2.retrato.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private HashtagRepository hashtagRepository;

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

    public void addHashtag(Photo photo, Hashtag hashtag) {
        photo.getHashtags().add(hashtag);
        photoRepository.save(photo);
    }

    public void removeHashtag(Photo photo, Hashtag hashtag) {
        photo.getHashtags().remove(hashtag);
        photoRepository.save(photo);
    }

    public int getLikesCount(Integer photoId) {
        Photo photo = photoRepository.findById(photoId).orElseThrow(() -> new RuntimeException("Photo not found"));
        return photo.getLikes().size();
    }

    public List<Photo> listAllPhotos() {
        return photoRepository.findAll();
    }

    public Photo findById(Integer id) {
        return photoRepository.findById(id).orElse(null);
    }

    public Photo getPhotoById(Integer photoId) {
        return photoRepository.findById(photoId).orElseThrow(() -> new RuntimeException("Foto não encontrada."));
    }

    public void updateComment(Integer commentId, String newText) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado."));

        comment.setCommentText(newText);
        commentRepository.save(comment);
    }

    public void deleteComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado."));

        commentRepository.delete(comment);
    }

    public Comment findCommentById(Integer commentId) {
        // Tenta encontrar o comentário com o ID fornecido
        Optional<Comment> commentOptional = commentRepository.findById(commentId);

        // Se o comentário for encontrado, retorna ele, caso contrário, lança uma exceção ou retorna null
        return commentOptional.orElseThrow(() -> new RuntimeException("Comentário não encontrado"));
    }
}
