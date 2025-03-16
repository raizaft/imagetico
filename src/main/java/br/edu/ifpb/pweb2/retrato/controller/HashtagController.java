package br.edu.ifpb.pweb2.retrato.controller;

import br.edu.ifpb.pweb2.retrato.model.Hashtag;
import br.edu.ifpb.pweb2.retrato.model.Photo;
import br.edu.ifpb.pweb2.retrato.service.HashtagService;
import br.edu.ifpb.pweb2.retrato.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@Controller
@RequestMapping("/hashtag")
public class HashtagController {

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private PhotoService photoService;

    @PostMapping("/add")
    public String addHashtag(@RequestParam Integer photoId, @RequestParam String hashtagText) {
        // Encontra a foto e a hashtag
        Photo photo = photoService.findById(photoId);
        Hashtag hashtag = hashtagService.findByText(hashtagText);


        if (hashtag == null) {
            hashtag = new Hashtag();
            hashtag.setText(hashtagText);
            hashtagService.save(hashtag);
        }

        // Adiciona a hashtag à foto
        photoService.addHashtag(photo, hashtag);

        return "redirect:photographer/dashboard";
    }

    @PostMapping("/remove")
    public String removeHashtag(@RequestParam Integer photoId, @RequestParam String hashtagText) {
        Photo photo = photoService.findById(photoId);
        Hashtag hashtag = hashtagService.findByText(hashtagText);

        if (hashtag != null) {
            photoService.removeHashtag(photo, hashtag);
        }

        return "redirect:photographer/dashboard";
    }

    @GetMapping("/search")
    public String searchPhotosByHashtag(@RequestParam String hashtagText, Model model) {
        Hashtag hashtag = hashtagService.findByText(hashtagText);

        if (hashtag != null) {
            model.addAttribute("photos", hashtag.getPhotos());
        } else {
            model.addAttribute("photos", new HashSet<Photo>());
        }

        return "hashtag/searchResults";
    }
}