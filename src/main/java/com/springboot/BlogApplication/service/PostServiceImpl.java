package com.springboot.BlogApplication.service;

import com.springboot.BlogApplication.entity.Comments;
import com.springboot.BlogApplication.entity.Posts;
import com.springboot.BlogApplication.entity.Tags;
import com.springboot.BlogApplication.repository.CommentRepository;
import com.springboot.BlogApplication.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService, CommentService{
    @Autowired
    private PostRepository postsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Posts> getPosts() {
        return postsRepository.findAll();
    }

    @Override
    public void savePosts(Posts posts) {
        this.postsRepository.save(posts);
    }

    @Override
    public Posts getPostsById(int id) {
        Optional<Posts> optional = postsRepository.findById(id);
        Posts posts = null;
        if(optional.isPresent()) {
            posts = optional.get();
        } else {
            throw new RuntimeException("Post not found");
        }
        return posts;
    }

    @Override
    public void deletePostById(int id) {
        this.postsRepository.deleteById(id);
    }

    @Override
    //@Transactional
    public void saveComments(Integer id, Comments comments) {
        Optional<Posts> optional = postsRepository.findById(id);
        Posts posts=optional.get();
        posts.addComments(comments);
        this.postsRepository.save(posts);
    }

    @Override
    public void addTags(Integer id, Tags tags) {
        Optional<Posts> optional = postsRepository.findById(id);
        Posts posts=optional.get();
        posts.addTags(tags);
        this.postsRepository.save(posts);
    }

    @Override
    public Page<Posts> findPaginated(int pageNo, int pageSize, Sort sortDirection) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sortDirection);
        return this.postsRepository.findAll(pageable);
    }

    @Override
    public void deleteComment(int id) {
        this.commentRepository.deleteById(id);
    }

    @Override
    public void saveUpdatedComment(Comments comments) {
        this.commentRepository.save(comments);
    }

    @Override
    public Comments getCommentById(int id) {
        Optional<Comments> optional = commentRepository.findById(id);
        Comments comments = null;
        if(optional.isPresent()) {
            comments = optional.get();
        } else {
            throw new RuntimeException("Post not found");
        }
        return comments;
    }

    @Override
    public List<Posts> filterByAuthor(String[] filter) {
        List<Posts> posts = new ArrayList<>();
        for (String author : filter) {
            posts.addAll(postsRepository.findByAuthor(author));
        }
        return posts;
    }

    @Override
    public List<Posts> findPostByKeyword(String keyword) {
        return this.postsRepository.findByKeyword(keyword);
    }

    @Override
    public List<Posts> findByPublishedAt(LocalDate date) {
        return postsRepository.findByPublishedAt(date);
    }

    @Override
    public List<Posts> findAllPosts() {
        return null;
    }

}
