package br.edu.ifpb.pweb2.retrato.service;

import br.edu.ifpb.pweb2.retrato.model.Hashtag;
import br.edu.ifpb.pweb2.retrato.repository.HashtagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HashtagService {

    @Autowired
    private HashtagRepository hashtagRepository;

    public Hashtag findByText(String text) {
        return hashtagRepository.findByText(text);
    }

    public void save(Hashtag hashtag) {
        hashtagRepository.save(hashtag);
    }

    public List<Hashtag> searchHashtags(String query) {
        return hashtagRepository.findByTextStartingWith(query);
    }
}
