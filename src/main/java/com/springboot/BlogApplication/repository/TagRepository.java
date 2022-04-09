package com.springboot.BlogApplication.repository;

import com.springboot.BlogApplication.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tags, Integer> {
}
