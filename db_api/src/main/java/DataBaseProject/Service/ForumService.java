package DataBaseProject.Service;

import DataBaseProject.Functions.ForumFunctions;
import DataBaseProject.Functions.ThreadFunctions;
import DataBaseProject.ResponseModels.ForumModel;
import DataBaseProject.ResponseModels.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

@Service
public class ForumService {

    private JdbcTemplate jdbcTemplate;
    private ForumFunctions forumFunctions;
    private ThreadFunctions threadFunctions;

    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.forumFunctions = new ForumFunctions(jdbcTemplate);
        this.threadFunctions = new ThreadFunctions(jdbcTemplate);
    }

    public ResponseEntity<Object> create_forumService(ForumModel forum) {

        try {
            forumFunctions.create(forum.getUser(), forum.getSlug(), forum.getTitle());
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumFunctions.findBySlug(forum.getSlug()));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(forumFunctions.findBySlug(forum.getSlug()));
    }

    public ResponseEntity<Object> create_threadService(ThreadModel thread, String slug) {
        String threadSlug = thread.getSlug();
        try {
            thread = threadFunctions.create(thread.getAuthor(), thread.getCreated(), slug,
                    thread.getMessage(), thread.getSlug(), thread.getTitle());
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadFunctions.findByIdOrSlug(threadSlug));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(thread);
    }

    public ResponseEntity<Object> view_forum_infoService(String slug) {
        ForumModel forum;
        try {
            forum = forumFunctions.findBySlug(slug);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(forum);
    }

    public ResponseEntity<Object> get_forum_threadsService(Integer limit, String since, Boolean desc, String slug) {
        try {
            ForumModel forum = forumFunctions.findBySlug(slug);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(forumFunctions.findAllThreads(slug, limit, since, desc));
    }

    public ResponseEntity<Object> get_forum_usersService(Integer limit, String since, Boolean desc, String slug) {
        try {
            ForumModel forum = forumFunctions.findBySlug(slug);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(forumFunctions.findAllUsers(slug, limit, since, desc));
    }
}
