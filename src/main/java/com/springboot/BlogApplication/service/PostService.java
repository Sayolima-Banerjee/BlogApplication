package com.springboot.BlogApplication.service;

import com.springboot.BlogApplication.entity.Comments;
import com.springboot.BlogApplication.entity.Posts;
import com.springboot.BlogApplication.entity.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PostService {

    List<Posts> getPosts();

    void savePosts(Posts posts);

    Posts getPostsById(int id);

    void deletePostById(int id);

    @Transactional
    void saveComments(Integer id, Comments comments);

    void addTags(Integer id, Tags tags);

    Page<Posts> findPaginated(int pageNo, int pageSize, Sort sortDirection);

    List<Posts> findByPublishedAt(LocalDate date);

    List<Posts> findAllPosts();

    List<Posts> filterByAuthor(String[] authors);

    List<Posts> findPostByKeyword(String keyword);
}
