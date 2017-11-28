package DataBaseProject;

import DataBaseProject.Service.ForumService;
import DataBaseProject.Service.PostService;
import DataBaseProject.Views.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DataBaseProject.Views.StatusView;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

@RestController
@RequestMapping(value = "api")
public class Routers extends LowerRouter {

    private final ForumService forumSer;
    private final PostService postSer;

    @Autowired
    public Routers(ForumService forumService, PostService postService) {
        this.forumSer = forumService;
        this.postSer = postService;
    }

    /**
     *  forum
     */
    @RequestMapping(value = "/forum/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createForum(@RequestBody final ForumView forum) {

        return forumSer.createForum(forum);
    }

    @RequestMapping(value = "/forum/{slug}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createSlug(@RequestBody ThreadView thread,
                                             @PathVariable(value = "slug") final String slug) {

        return forumSer.createSlug(thread, slug);
    }

    @RequestMapping(value = "/forum/{slug}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewForum(@PathVariable("slug") final String slug) {
        final ForumView forum;
        try {
            forum = forumFunctions.findBySlug(slug);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(forum);
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/forum/{slug}/threads", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewThreads(
            @RequestParam(value = "limit", required = false, defaultValue = "100") final Integer limit,
            @RequestParam(value = "since", required = false) final String since,
            @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
            @PathVariable("slug") final String slug) {
        try {
            final ForumView forum = forumFunctions.findBySlug(slug);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(forumFunctions.findAllThreads(slug, limit, since, desc));
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/forum/{slug}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewUsers(
            @RequestParam(value = "limit", required = false, defaultValue = "100") final Integer limit,
            @RequestParam(value = "since", required = false) final String since,
            @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
            @PathVariable("slug") final String slug) {
        try {
            final ForumView forum = forumFunctions.findBySlug(slug);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(forumFunctions.findAllUsers(slug, limit, since, desc));
    }




    /**
     *  post
     */
    @RequestMapping(value = "/post/{id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewForumGet(
            @RequestParam(value = "related", required = false) String[] related, @PathVariable("id") final Integer id) {
        return postSer.viewForumGet(related, id);
    }

    @RequestMapping(value = "/post/{id}/details", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewForumPost(@RequestBody PostView post, @PathVariable("id") final Integer id) {
        try {
            post = post.getMessage() != null ? postFunctions.update(post.getMessage(), id) : postFunctions.findById(id);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    /**
     *  service
     */
    @RequestMapping("/service/status")
    public ResponseEntity<Object> serverStatus() {
        final Integer forumsCount = forumFunctions.count();
        final Integer postsCount = postFunctions.count();
        final Integer threadsCount = threadFunctions.count();
        final Integer usersCount = userFunctions.count();
        return ResponseEntity.status(HttpStatus.OK).body(new StatusView(forumsCount, postsCount, threadsCount, usersCount));
    }

    @RequestMapping("/service/clear")
    public ResponseEntity<Object> clearService() {
        postFunctions.clear();
        threadFunctions.clear();
        forumFunctions.clear();
        userFunctions.clear();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    /**
     *  thread
     */
    @RequestMapping(value = "/thread/{slug_or_id}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createPosts(@RequestBody List<PostView> posts,
                                              @PathVariable(value = "slug_or_id") final String slug_or_id) {
        try {
            ThreadView thread = threadFunctions.findByIdOrSlug(slug_or_id);
            if (posts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(posts);
            }
            for (PostView post : posts) {
                if (post.getParent() != 0) {
                    try {
                        PostView parent = postFunctions.findById(post.getParent());
                        post.setForum(thread.getForum());
                        post.setThread(thread.getId());
                        if (!thread.getId().equals(parent.getThread())) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"error\"}");
                        }
                    } catch (EmptyResultDataAccessException ex) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"error\"}");
                    }
                }
                post.setForum(thread.getForum());
                post.setThread(thread.getId());
            }
            postFunctions.create(posts, slug_or_id);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"error\"}");
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(posts);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewThread(@PathVariable(value = "slug_or_id") final String slug_or_id) {
        final ThreadView thread;
        try {
            thread = threadFunctions.findByIdOrSlug(slug_or_id);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/details", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateThread(@RequestBody ThreadView thread,
                                               @PathVariable(value = "slug_or_id") final String slug_or_id) {
        try {
            threadFunctions.update(thread.getMessage(), thread.getTitle(), slug_or_id);
            thread = threadFunctions.findByIdOrSlug(slug_or_id);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadFunctions.findByIdOrSlug(slug_or_id));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/vote", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> voteForThread(@RequestBody final VoteView vote,
                                                @PathVariable("slug_or_id") final String slug_or_id) {
        final ThreadView thread;
        try {
            thread = threadFunctions.updateVotes(vote, slug_or_id);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadFunctions.findByIdOrSlug(slug_or_id));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/posts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getPostsSorted(@PathVariable(value = "slug_or_id") final String slug_or_id,
                                         @RequestParam(value = "limit", required = false) final Integer limit,
                                         @RequestParam(value = "since", required = false) final Integer since,
                                         @RequestParam(value = "sort", required = false) final String sort,
                                         @RequestParam(value = "desc", required = false) final Boolean desc) {
        try {
            ThreadView thread = threadFunctions.findByIdOrSlug(slug_or_id);
            List<PostView> result = postFunctions.sort(thread, slug_or_id, limit, since, sort, desc);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
    }


    /**
     *  user
     */
    @RequestMapping(value = "/user/{nickname}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@RequestBody UserView user,
                                             @PathVariable(value = "nickname") String nickname) {
        try {
            userFunctions.create(user.getAbout(), user.getEmail(), user.getFullname(), nickname);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(userFunctions.findManyByNickOrMail(nickname, user.getEmail()));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        user.setNickname(nickname);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(value = "/user/{nickname}/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewProfile(@PathVariable(value = "nickname") String nickname) {
        final UserView user;
        try {
            user = userFunctions.findSingleByNickOrMail(nickname, null);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @RequestMapping(value = "/user/{nickname}/profile", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> modifyProfile(@RequestBody UserView user,
                                                @PathVariable(value = "nickname") String nickname) {
        try {
            userFunctions.update(user.getAbout(), user.getEmail(), user.getFullname(), nickname);
            user = userFunctions.findSingleByNickOrMail(nickname, user.getEmail());
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"error\"}");
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
