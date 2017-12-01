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

import java.util.List;

@Service
public class ThreadService {

    private final JdbcTemplate jdbcTemplate;
    private final PostFunctions postFunctions;
    private final ThreadFunctions threadFunctions;

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.postFunctions = new PostFunctions(jdbcTemplate);
        this.threadFunctions = new ThreadFunctions(jdbcTemplate);
    }

    public ResponseEntity<Object> create_postsService (List<PostModel> posts, String slug_or_id) {
        try {
            ThreadModel thread = threadFunctions.findByIdOrSlug(slug_or_id);
            if (posts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(posts);
            }
            for (PostModel post : posts) {
                if (post.getParent() != 0) {
                    try {
                        PostModel parent = postFunctions.findById(post.getParent());
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

    public ResponseEntity<Object> view_threadGetService (String slug_or_id) {
        final ThreadModel thread;
        try {
            thread = threadFunctions.findByIdOrSlug(slug_or_id);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread);
    }

    public ResponseEntity<Object> view_threadPostService (ThreadModel thread, String slug_or_id) {
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

    public ResponseEntity<Object> voteService (VoteModel vote, String slug_or_id) {
        final ThreadModel thread;
        try {
            thread = threadFunctions.updateVotes(vote, slug_or_id);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadFunctions.findByIdOrSlug(slug_or_id));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread);
    }

    public ResponseEntity<Object> get_posts_sortedService (String slug_or_id, Integer limit, Integer since, String sort, Boolean desc) {
        try {
            ThreadModel thread = threadFunctions.findByIdOrSlug(slug_or_id);
            List<PostModel> result = postFunctions.sort(thread, slug_or_id, limit, since, sort, desc);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
    }
}
