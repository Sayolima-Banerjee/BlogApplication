package com.springboot.BlogApplication.service;

import com.springboot.BlogApplication.entity.Comments;

public interface CommentService {

    void deleteComment(int id);

    void saveUpdatedComment(Comments comments);

    Comments getCommentById(int id);
}
