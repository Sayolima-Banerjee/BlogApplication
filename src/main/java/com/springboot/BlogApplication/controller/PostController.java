package com.springboot.BlogApplication.controller;

import com.springboot.BlogApplication.entity.Comments;
import com.springboot.BlogApplication.entity.Posts;
import com.springboot.BlogApplication.entity.Tags;
import com.springboot.BlogApplication.entity.User;
import com.springboot.BlogApplication.service.CommentService;
import com.springboot.BlogApplication.service.PostService;
import com.springboot.BlogApplication.service.TagService;
import com.springboot.BlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
public class PostController {
    @Autowired
    private PostService postsService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String viewPage(Model model) {
        return viewPage(1, "asc", model);
//        model.addAttribute("listOfPosts", postsService.getPosts());
//        return "posts";
    }

    @RequestMapping("/NewPost")
    public String createNewPost(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        Posts posts = new Posts();
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "newPost";
    }

    @PostMapping("/publishNewPost")
    public String publishNewPost(@RequestParam String tagString, @ModelAttribute Posts posts, Authentication authentication, Model model) {
        Date LocalDate = new Date();
        String[] tagsList = tagString.split(" ");
        for(String tag : tagsList) {
            Tags newTag = new Tags();
            if(tagService.existsTagByName(tag)) {
                newTag = tagService.findTagsByName(tag);
            }
            else {
                newTag.setName(tag);
                newTag.setCreatedAt(LocalDate());
            }
            if(posts.getTags()==null) {
                posts.addTags(newTag);
            } else if((posts.getTags()!=null) && !(posts.getTags().contains(newTag))) {
                posts.addTags(newTag);
            }
        }
        User user = userService.findByUsername(authentication.getName());
        if(user.getRoles().contains(userService.getRole("ROLE_Author"))) {
            posts.setAuthor(user.getName());
        }
        posts.setExcerpt(posts.getContent());
        posts.setPublished(true);
        posts.setUpdatedAt(LocalDateTime.now());
        model.addAttribute("posts", posts);
        postsService.savePosts(posts);
        return "redirect:/";
    }

    private LocalDate LocalDate() {
        return null;
    }

    @RequestMapping(value = "/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable (value = "id") int id,Authentication authentication, Model model) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(name);
        Posts posts = postsService.getPostsById(id);
        if(user.getRoles().contains(userService.getRole("ROLE_Author"))) {
            if(posts.getAuthor().equals(user.getName())) {
                List<Tags> tagsList=posts.getTags();
                String tags="";
                for (Tags tag:tagsList) {
                    tags=tags + tag.getName() + " ";
                }
                model.addAttribute("tags", tags);
                model.addAttribute("posts", posts);
                return "updatePost";
            } else {
                return "redirect:/blog/" + id;
            }
        }
        List<Tags> tagsList=posts.getTags();
        String tags="";
        for (Tags tag:tagsList) {
            tags=tags + tag.getName() + " ";
        }
        model.addAttribute("tags", tags);
        model.addAttribute("posts", posts);
        model.addAttribute("permissible", true);
        System.out.println(authentication.getName());
        return "updatePost";
    }

    @RequestMapping("/deletePost/{id}")
    public String deletePost(@PathVariable (value = "id") int id, Authentication authentication, Model model) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(name);
        if(user.getRoles().contains(userService.getRole("ROLE_Author"))) {
            Posts posts = postsService.getPostsById(id);
            String status;
            if(posts.getAuthor().equals(user.getName())) {
                postsService.deletePostById(id);
                status = "Post Deleted";
                model.addAttribute("status", status);
            } else {
                status = "Not permitted to delete this post";
                model.addAttribute("status", status);
                return "redirect:/blog" + id;
            }
        } else {
            postsService.deletePostById(id);
        }
        return "redirect:/";
    }

    @RequestMapping("/showBlog")
    public String showBlog(Model model) {
        Posts posts = new Posts();
        model.addAttribute("posts", posts);
        return "blogPost";
    }

    @RequestMapping("/blog/{id}")
    public String showBlog(@PathVariable("id") int id, @ModelAttribute Comments comments, @ModelAttribute Tags tags, Model model) {
        Posts posts = postsService.getPostsById(id);
        model.addAttribute("posts", posts);
        List<Comments> commentsList = posts.getComments();
        model.addAttribute("commentsList", commentsList);
        model.addAttribute("status", "");
        return "blogPost";
    }

    @PostMapping("/postComments/{id}")
    public String postComments(@PathVariable (value = "id") int id, @ModelAttribute Comments comments, Model model) {
        //Posts posts=postsService.getPostsById(id);
        comments.setCreatedAt(LocalDate.now());
        postsService.saveComments(id, comments);
        return "redirect:/blog/" + id;
    }

    @RequestMapping("updateComment/{id}/{commentId}")
    public String updateComment(@PathVariable (value = "id") int id, @PathVariable (value = "commentId") int commentId,
                                Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        Posts posts = postsService.getPostsById(id);
        if(user.getRoles().contains(userService.getRole("ROLE_Author"))) {
            if(posts.getAuthor().equals(user.getName())) {
                Comments comments = commentService.getCommentById(commentId);
                model.addAttribute("comments", comments);
                model.addAttribute("posts", posts);
                return "editComment";
            } else {
                return "redirect:/blog/" + id;
            }
        }
        Comments comments = commentService.getCommentById(commentId);
        model.addAttribute("comments", comments);
        model.addAttribute("posts", posts);
        return "editComment";
    }

    @PostMapping("/editComment/{id}")
    public String editComment(@PathVariable (value = "id") int id, @ModelAttribute Comments comments, Model model) {
        commentService.saveUpdatedComment(comments);
        return "redirect:/blog/" + id;
    }

    @RequestMapping("/deleteComment/{id}/{commentId}")
    public String deleteComment(@PathVariable("id") int postId, @PathVariable (value = "commentId") int commentId,
                                    Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        Posts posts = postsService.getPostsById(postId);
        if(user.getRoles().contains(userService.getRole("ROLE_Author"))) {
            if(!(posts.getAuthor().equals(user.getName()))) {
                return "redirect:/blog/" + postId;
            }
        }
        this.commentService.deleteComment(commentId);
        return "redirect:/blog/" + postId;
    }

   @RequestMapping("/page/{pageNo}")
    public String viewPage(@PathVariable(value = "pageNo") int pageNo, @RequestParam String sortDir, Model model) {
        int pageSize = 6;
        Sort sort= Sort.by("publishedAt");
        if(sortDir.equals("desc")) {
            sort=Sort.by("publishedAt").descending();
       }
        Page<Posts> page = postsService.findPaginated(pageNo, pageSize, sort);
        List<Posts> listOfPosts = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listOfPosts", listOfPosts);
        return "posts";
    }

    @RequestMapping("/filter")
    public String filterParameter(@RequestParam("filter") String[] filter, Model model) {
        List<Posts> posts=postsService.getPosts();
        Set<String> authors=new HashSet<>();
        for(Posts post : posts) {
            authors.add(post.getAuthor());
        }
        model.addAttribute("listOfAuthors", authors);
        model.addAttribute("filter", filter);
        model.addAttribute("ListOfPosts", posts);
        return "posts";
    }

    @RequestMapping("filterByTags")
    public String filterUsingTags(@RequestParam("tags") String tags, Model model) {
        Tags tag=tagService.findTagsByName(tags);
        List<Posts> posts=tag.getPosts();
        model.addAttribute("listOfPosts", posts);
        return "posts";
    }

    @RequestMapping("filterByAuthor")
    public String filterByAuthor(@RequestParam("authors") String[] authors, Model model) {
        List<Posts> posts=authors == null ? postsService.findAllPosts() : postsService.filterByAuthor(authors);
        model.addAttribute("listOfPosts", posts);
        return "posts";
    }

    @RequestMapping("filterByPublishedDate")
    public String filterByPublishedDate(@RequestParam("interval") String interval, Model model) {
        LocalDate date=LocalDate.now();
        switch (interval) {
            case "yesterday":
                date=date.minusDays(1);
                break;
            case "lastWeek":
                date=date.minusWeeks(1);
                break;
            case "lastMonth":
                date=date.minusMonths(1);
                break;
            case "lastYear":
                date=date.minusYears(1);
                break;
        }
        List<Posts> posts=postsService.findByPublishedAt(date);
        model.addAttribute("listOfPosts", posts);
        return "posts";
    }

    @RequestMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<Posts> postsList;
        postsList = keyword == null ? postsService.findAllPosts() : postsService.findPostByKeyword(keyword);
        Tags tags=tagService.findTagsByName(keyword);
        if(postsList.isEmpty()) {
            postsList = tags.getPosts();
        }
        model.addAttribute("listOfPosts", postsList);
        return "posts";
    }

}
