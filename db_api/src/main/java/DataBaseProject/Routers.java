package DataBaseProject;

import DataBaseProject.Service.*;
import DataBaseProject.ResponseModels.*;
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

    private ForumService forumSer;
    private PostService postSer;
    private ServiceService serviceSer;
    private ThreadService threadSer;
    private UserService userSer;

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
    public ResponseEntity<Object> create_forum(@RequestBody ForumModel forum) {

        return forumSer.create_forumService(forum);
    }

    @RequestMapping(value = "/forum/{slug}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create_thread(@RequestBody ThreadModel thread,
                                             @PathVariable(value = "slug") String slug) {

        return forumSer.create_threadService(thread, slug);
    }

    @RequestMapping(value = "/forum/{slug}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> view_forum_info(@PathVariable("slug") String slug) {

        return forumSer.view_forum_infoService(slug);
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/forum/{slug}/threads", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> get_forum_threads(
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "since", required = false) String since,
            @RequestParam(value = "desc", required = false) Boolean desc,
            @PathVariable("slug") String slug) {

        return forumSer.get_forum_threadsService(limit, since, desc, slug);
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/forum/{slug}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> get_forum_users(
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "since", required = false) String since,
            @RequestParam(value = "desc", required = false) Boolean desc,
            @PathVariable("slug") String slug) {

        return forumSer.get_forum_usersService(limit, since, desc, slug);
    }



    /**
     *  post
     */
    @RequestMapping(value = "/post/{id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> get_post_detailedGet(
            @RequestParam(value = "related", required = false) String[] related, @PathVariable("id") Integer id) {

        return postSer.get_post_detailedGetService(related, id);
    }

    @RequestMapping(value = "/post/{id}/details", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> get_post_detailedPost(@RequestBody PostModel post, @PathVariable("id") Integer id) {

        return postSer.get_post_detailedPostService(post, id);
    }

    /**
     *  service
     */
    @RequestMapping("/service/status")
    public ResponseEntity<Object> status() {

        return serviceSer.statusService();
    }

    @RequestMapping("/service/clear")
    public ResponseEntity<Object> clear() {

        return serviceSer.clearService();
    }


    /**
     *  thread
     */
    @RequestMapping(value = "/thread/{slug_or_id}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create_posts(@RequestBody List<PostModel> posts,
                                              @PathVariable(value = "slug_or_id") String slug_or_id) {

        return threadSer.create_postsService(posts, slug_or_id);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> view_threadGet(@PathVariable(value = "slug_or_id") String slug_or_id) {

        return threadSer.view_threadGetService(slug_or_id);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/details", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> view_threadPost(@RequestBody ThreadModel thread,
                                               @PathVariable(value = "slug_or_id") String slug_or_id) {

        return threadSer.view_threadPostService(thread, slug_or_id);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/vote", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> vote(@RequestBody  VoteModel vote,
                                                @PathVariable("slug_or_id") String slug_or_id) {

        return threadSer.voteService(vote, slug_or_id);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/posts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> get_posts_sorted(@PathVariable(value = "slug_or_id") String slug_or_id,
                                         @RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "since", required = false) Integer since,
                                         @RequestParam(value = "sort", required = false) String sort,
                                         @RequestParam(value = "desc", required = false) Boolean desc) {

        return threadSer.get_posts_sortedService(slug_or_id, limit, since, sort, desc);
    }


    /**
     *  user
     */
    @RequestMapping(value = "/user/{nickname}/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create_user(@RequestBody UserModel user,
                                             @PathVariable(value = "nickname") String nickname) {

        return userSer.create_userService(user, nickname);
    }

    @RequestMapping(value = "/user/{nickname}/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> viewProfile(@PathVariable(value = "nickname") String nickname) {

        return userSer.viewProfileService(nickname);
    }

    @RequestMapping(value = "/user/{nickname}/profile", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> modify_profile(@RequestBody UserModel user,
                                                @PathVariable(value = "nickname") String nickname) {

        return userSer.modify_profileService(user, nickname);
    }

}
