package com.springboot.BlogApplication.service;

import com.springboot.BlogApplication.entity.Tags;
import com.springboot.BlogApplication.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService{
    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tags findTagsByName(String name) {
        List<Tags> tagsList = tagRepository.findAll();
        Tags tags=new Tags();
        for(Tags tag:tagsList) {
            if(tag.getName().equalsIgnoreCase(name)) {
                tags=tag;
            }
        }
        return tags;
    }

    @Override
    public boolean existsTagByName(String tagName) {
        List<Tags> tagsList=tagRepository.findAll();
        for(Tags tag:tagsList) {
            if(tag.getName().equalsIgnoreCase(tagName)) {
                return true;
            }
        }
        return false;
    }
}
