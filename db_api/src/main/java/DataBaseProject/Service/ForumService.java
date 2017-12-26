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

    private ForumFunctions forumFunctions;
    private ThreadFunctions threadFunctions;
    private Object error;

    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.forumFunctions = new ForumFunctions(jdbcTemplate);
        this.threadFunctions = new ThreadFunctions(jdbcTemplate);
        this.error = "{\"message\": \"error\"}";
    }

    public ResponseEntity<Object> create_forumService(ForumModel forum) {
        HttpStatus status = HttpStatus.CREATED;
        Object body;
        try {
            forumFunctions.create(forum.getUser(), forum.getSlug(), forum.getTitle());
            body = forumFunctions.findBySlug(forum.getSlug());
        } catch (DuplicateKeyException ex) {
            status = HttpStatus.CONFLICT;
            body = forumFunctions.findBySlug(forum.getSlug());
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> create_threadService(ThreadModel thread, String slug) {
        String threadSlug = thread.getSlug();
        HttpStatus status = HttpStatus.CREATED;
        Object body;
        try {
            thread = threadFunctions.create(thread.getAuthor(), thread.getCreated(), slug,
                    thread.getMessage(), thread.getSlug(), thread.getTitle());
            body = thread;
        } catch (DuplicateKeyException ex) {
            status = HttpStatus.CONFLICT;
            body = threadFunctions.findByIdOrSlug(threadSlug);
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> view_forum_infoService(String slug) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            // ForumModel forum = forumFunctions.findBySlug(slug);
            // body = forum;
            body = forumFunctions.findBySlug(slug);
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> get_forum_threadsService(Integer limit, String since, Boolean desc, String slug) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            forumFunctions.findBySlug(slug);
            body = forumFunctions.findAllThreads(slug, limit, since, desc);
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> get_forum_usersService(Integer limit, String since, Boolean desc, String slug) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            forumFunctions.findBySlug(slug);
            body = forumFunctions.findAllUsers(slug, limit, since, desc);
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }
}
