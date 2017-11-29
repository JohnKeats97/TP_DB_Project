package DataBaseProject.Service;

import DataBaseProject.Functions.PostFunctions;
import DataBaseProject.Models.PostDetailedModel;
import DataBaseProject.Models.PostModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final JdbcTemplate jdbcTemplate;
    private final PostFunctions postFunctions;

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.postFunctions = new PostFunctions(jdbcTemplate);
    }

    public ResponseEntity<Object> viewForumGet (String[] related, final Integer id) {
        final PostDetailedModel post;
        try {
            post = postFunctions.detailedView(id, related);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    public ResponseEntity<Object> viewForumPost (PostModel post, Integer id) {
        try {
            post = post.getMessage() != null ? postFunctions.update(post.getMessage(), id) : postFunctions.findById(id);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }
}
