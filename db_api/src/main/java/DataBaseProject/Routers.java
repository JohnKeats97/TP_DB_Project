package DataBaseProject;

import DataBaseProject.Service.*;
import DataBaseProject.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api")
public class Routers {

    private final ForumService forumSer;
    private final PostService postSer;
    private final ServiceService serviceSer;
    private final ThreadService threadSer;
    private final UserService userSer;

    @Autowired
    public Routers(ForumService forumService, PostService postService, ServiceService serviceService,
                   ThreadService threadService, UserService userService) {
        this.forumSer = forumService;
        this.postSer = postService;
        this.serviceSer = serviceService;
        this.threadSer = threadService;
        this.userSer = userService;
    }


    /**
     *  forum
     */
    @RequestMapping(value = "/forum/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createForum(@RequestBody final ForumModel forum) {

        return forumSer.createForum(forum);
    }

    @RequestMapping(value = "/forum/{slug}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createSlug(@RequestBody ThreadModel thread,
                                             @PathVariable(value = "slug") final String slug) {

        return forumSer.createSlug(thread, slug);
    }

    @RequestMapping(value = "/forum/{slug}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewForum(@PathVariable("slug") final String slug) {

        return forumSer.viewForum(slug);
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/forum/{slug}/threads", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewThreads(
            @RequestParam(value = "limit", required = false, defaultValue = "100") final Integer limit,
            @RequestParam(value = "since", required = false) final String since,
            @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
            @PathVariable("slug") final String slug) {

        return forumSer.viewThreads(limit, since, desc, slug);
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/forum/{slug}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewUsers(
            @RequestParam(value = "limit", required = false, defaultValue = "100") final Integer limit,
            @RequestParam(value = "since", required = false) final String since,
            @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
            @PathVariable("slug") final String slug) {

        return forumSer.viewUsers(limit, since, desc, slug);
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
    public ResponseEntity<Object> viewForumPost(@RequestBody PostModel post, @PathVariable("id") final Integer id) {

        return postSer.viewForumPost(post, id);
    }

    /**
     *  service
     */
    @RequestMapping("/service/status")
    public ResponseEntity<Object> serverStatus() {

        return serviceSer.serverStatus();
    }

    @RequestMapping("/service/clear")
    public ResponseEntity<Object> clearService() {

        return serviceSer.clearService();
    }


    /**
     *  thread
     */
    @RequestMapping(value = "/thread/{slug_or_id}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createPosts(@RequestBody List<PostModel> posts,
                                              @PathVariable(value = "slug_or_id") final String slug_or_id) {

        return threadSer.createPosts(posts, slug_or_id);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewThread(@PathVariable(value = "slug_or_id") final String slug_or_id) {

        return threadSer.viewThread(slug_or_id);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/details", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateThread(@RequestBody ThreadModel thread,
                                               @PathVariable(value = "slug_or_id") final String slug_or_id) {

        return threadSer.updateThread(thread, slug_or_id);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/vote", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> voteForThread(@RequestBody final VoteModel vote,
                                                @PathVariable("slug_or_id") final String slug_or_id) {

        return threadSer.voteForThread(vote, slug_or_id);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/posts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPostsSorted(@PathVariable(value = "slug_or_id") final String slug_or_id,
                                         @RequestParam(value = "limit", required = false) final Integer limit,
                                         @RequestParam(value = "since", required = false) final Integer since,
                                         @RequestParam(value = "sort", required = false) final String sort,
                                         @RequestParam(value = "desc", required = false) final Boolean desc) {

        return threadSer.getPostsSorted(slug_or_id, limit, since, sort, desc);
    }


    /**
     *  user
     */
    @RequestMapping(value = "/user/{nickname}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@RequestBody UserModel user,
                                             @PathVariable(value = "nickname") String nickname) {

        return userSer.createUser(user, nickname);
    }

    @RequestMapping(value = "/user/{nickname}/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewProfile(@PathVariable(value = "nickname") String nickname) {

        return userSer.viewProfile(nickname);
    }

    @RequestMapping(value = "/user/{nickname}/profile", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> modifyProfile(@RequestBody UserModel user,
                                                @PathVariable(value = "nickname") String nickname) {

        return userSer.modifyProfile(user, nickname);
    }

}
