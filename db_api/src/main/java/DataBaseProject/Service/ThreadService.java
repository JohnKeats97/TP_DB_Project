package DataBaseProject.Service;


import DataBaseProject.Functions.PostFunctions;
import DataBaseProject.Functions.ThreadFunctions;
import DataBaseProject.ResponseModels.PostModel;
import DataBaseProject.ResponseModels.ThreadModel;
import DataBaseProject.ResponseModels.VoteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.util.List;

@Service
public class ThreadService {

    private PostFunctions postFunctions;
    private ThreadFunctions threadFunctions;
    private Object error;

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate) {
        this.postFunctions = new PostFunctions(jdbcTemplate);
        this.threadFunctions = new ThreadFunctions(jdbcTemplate);
        this.error = "{\"message\": \"error\"}";
    }

    public ResponseEntity<Object> create_postsService (List<PostModel> posts, String slug_or_id) {
        HttpStatus status = HttpStatus.CREATED;
        Object body = posts;
        try {
            ThreadModel thread = threadFunctions.findByIdOrSlug(slug_or_id);
            for (PostModel post : posts) {
                if (post.getParent() != 0) {
                    try {
                        PostModel parent = postFunctions.findById(post.getParent());
                        post.setForum(thread.getForum());
                        post.setThread(thread.getId());
                        if (!thread.getId().equals(parent.getThread())) {
                            body = error;
                            status = HttpStatus.CONFLICT;
                            break;
                        }
                    } catch (EmptyResultDataAccessException ex) {
                        body = error;
                        status = HttpStatus.CONFLICT;
                        break;
                    }
                }
                post.setForum(thread.getForum());
                post.setThread(thread.getId());
            }
            postFunctions.create(posts, slug_or_id);
        } catch (DuplicateKeyException ex) {
            body = error;
            status = HttpStatus.CONFLICT;
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> view_threadGetService (String slug_or_id) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            body = threadFunctions.findByIdOrSlug(slug_or_id);
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> view_threadPostService (ThreadModel thread, String slug_or_id) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            threadFunctions.update(thread.getMessage(), thread.getTitle(), slug_or_id);
            body = threadFunctions.findByIdOrSlug(slug_or_id);
        } catch (DuplicateKeyException ex) {
            status = HttpStatus.CONFLICT;
            body = threadFunctions.findByIdOrSlug(slug_or_id);
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> voteService (VoteModel vote, String slug_or_id) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            body = threadFunctions.updateVotes(vote, slug_or_id);
        } catch (DuplicateKeyException ex) {
            status = HttpStatus.CONFLICT;
            body = threadFunctions.findByIdOrSlug(slug_or_id);
        } catch (DataAccessException ex) {
            status = HttpStatus.NOT_FOUND;
            body = error;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> get_posts_sortedService (String slug_or_id, Integer limit, Integer since, String sort, Boolean desc) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            ThreadModel thread = threadFunctions.findByIdOrSlug(slug_or_id);
            body = postFunctions.sort(thread, limit, since, sort, desc);
        } catch (DataAccessException ex) {
            status = HttpStatus.NOT_FOUND;
            body = error;
        }
        return ResponseEntity.status(status).body(body);
    }
}
