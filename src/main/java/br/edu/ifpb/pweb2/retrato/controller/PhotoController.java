package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.model.Comment;
import br.edu.ifpb.pweb2.retrato.model.Photo;
import br.edu.ifpb.pweb2.retrato.model.Photographer;
import br.edu.ifpb.pweb2.retrato.service.PhotoService;
import br.edu.ifpb.pweb2.retrato.service.PhotographerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/photo")
public class PhotoController {

    @Autowired
    private PhotographerService photographerService;

    @Autowired
    private PhotoService service;

    @GetMapping("/form")
    public ModelAndView getForm(RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Photographer photographerLogado = photographerService.getPhotographerByEmail(email);

        if (photographerLogado == null || photographerLogado.getId() == null) {
            redirectAttributes.addFlashAttribute("mensagem", "Você precisa estar logado para acessar esta página.");
            return new ModelAndView("redirect:/photographer/login");
        }

        ModelAndView modelAndView = new ModelAndView("photo/upload");
        modelAndView.addObject("photo", new Photo());
        return modelAndView;
    }

    @PostMapping("/upload")
    public String upload(
            @ModelAttribute @Valid Photo photo,
            BindingResult result,
            RedirectAttributes redirectAttributes) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Photographer photographerLogado = photographerService.getPhotographerByEmail(email);

        if (photographerLogado == null || photographerLogado.getId() == null) {
            redirectAttributes.addFlashAttribute("mensagem", "Você precisa estar logado para publicar uma foto.");
            return "redirect:/photographer/login";
        }

        if (result.hasErrors()) {
            return "photo/upload";
        }

        photo.setPhotographer(photographerLogado);

        MultipartFile file = photo.getPhotoFile();
        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/" + photo.getPhotographer().getId();
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            photo.setPhotoPath("/" + uploadDir + "/" + fileName);
        }
        Photo savedPhoto = service.publish(photo);
        redirectAttributes.addFlashAttribute("mensagem", "Foto compartilhada!");
        return "redirect:/photographer/dashboard";
    }

    @PostMapping("/addComment")
    public String addComment(@RequestParam("commentText") String commentText,
                             @RequestParam("photographerId") Integer photographerId,
                             @RequestParam("photoId") Integer photoId, RedirectAttributes redirectAttributes) {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Photographer photographerLogado = photographerService.getPhotographerByEmail(email);

        if (photographerLogado == null || photographerLogado.getId() == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para comentar.");
            return "redirect:/photographer/dashboard";
        }

        // Verifica se o fotógrafo está suspenso para comentar
        if (!photographerLogado.isCanComment()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Você está suspenso e não pode realizar comentários.");
            return "redirect:/photographer/dashboard";
        }
        try {
            service.addComment(photographerId, photoId, commentText);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/photographer/dashboard";
    }

    @PostMapping("/likePhoto")
    @ResponseBody
    public ResponseEntity<?> likePhoto(@RequestBody Map<String, Integer> payload) {
        Integer photographerId = payload.get("photographerId");
        Integer photoId = payload.get("photoId");

        try {
            service.likePhoto(photographerId, photoId);

            Photo photo = service.getPhotoById(photoId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "likesCount", photo.getLikes().size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/editComment")
    public String editComment(@RequestParam("commentId") Integer commentId,
                              @RequestParam("commentText") String commentText,
                              @RequestParam("deleteFlag") boolean deleteFlag,
                              RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Photographer photographerLogado = photographerService.getPhotographerByEmail(email);

        if (photographerLogado == null || photographerLogado.getId() == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para editar um comentário.");
            return "redirect:/photographer/dashboard";
        }

        try {
            if (deleteFlag) { // Se deleteFlag for true, apaga o comentário
                service.deleteComment(commentId, photographerLogado.getId());
                redirectAttributes.addFlashAttribute("mensagem", "Comentário excluído com sucesso!");
            } else {
                service.updateComment(commentId, photographerLogado.getId(), commentText);
                redirectAttributes.addFlashAttribute("mensagem", "Comentário editado com sucesso!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/photographer/dashboard";
    }

    @GetMapping("/editComment/{commentId}")
    public ModelAndView editCommentForm(@PathVariable Integer commentId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Photographer photographerLogado = photographerService.getPhotographerByEmail(email);

        if (photographerLogado == null || photographerLogado.getId() == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para editar um comentário.");
            return new ModelAndView("redirect:/photographer/dashboard");
        }

        Comment comment = service.findCommentById(commentId);
        if (comment == null || !comment.getPhotographer().getId().equals(photographerLogado.getId())) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Comentário não encontrado ou você não tem permissão para editá-lo.");
            return new ModelAndView("redirect:/photographer/dashboard");
        }

        ModelAndView modelAndView = new ModelAndView("photo/editComment");
        modelAndView.addObject("comment", comment);
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable("id") Integer commentId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Photographer photographerLogado = photographerService.getPhotographerByEmail(email);

        if (photographerLogado == null || photographerLogado.getId() == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Você precisa estar logado para excluir um comentário.");
            return "redirect:/photographer/dashboard";
        }

        try {
            service.deleteComment(commentId, photographerLogado.getId());
            redirectAttributes.addFlashAttribute("mensagem", "Comentário excluído com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/photographer/dashboard";
    }
}
