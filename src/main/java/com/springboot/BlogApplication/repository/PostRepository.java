package com.springboot.BlogApplication.repository;

import com.springboot.BlogApplication.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Posts, Integer> {
    @Query(value="select * from posts p where p.title like %:keyword% or p.author like %:keyword% or " +
                "p.content like %:keyword%", nativeQuery = true)
    List<Posts> findByKeyword(@Param("keyword") String Keyword);

    @Query(value="select * from posts where published_at > :dateTime", nativeQuery = true)
    List<Posts> findByPublishedAt(@Param("dateTime") LocalDate date);

    @Query(value="select * from posts p where p.author like %:author%", nativeQuery = true)
    List<Posts> findByAuthor(@Param("author") String author);
}
