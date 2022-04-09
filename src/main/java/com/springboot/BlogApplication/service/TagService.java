package com.springboot.BlogApplication.service;

import com.springboot.BlogApplication.entity.Tags;

public interface TagService {
    Tags findTagsByName(String name);

    boolean existsTagByName(String tagName);
}
