package DataBaseProject.Service;

import DataBaseProject.Functions.PostFunctions;
import DataBaseProject.ResponseModels.PostDetailedModel;
import DataBaseProject.ResponseModels.PostModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private PostFunctions postFunctions;
    private Object error;

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate) {
        this.postFunctions = new PostFunctions(jdbcTemplate);
        this.error = "{\"message\": \"error\"}";
    }

    public ResponseEntity<Object> get_post_detailedGetService (String[] related, Integer id) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            body = postFunctions.detailedView(id, related);
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> get_post_detailedPostService (PostModel post, Integer id) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            if(post.getMessage() != null) {
                body = postFunctions.update(post.getMessage(), id);
            }
            else {
                body = postFunctions.findById(id);
            }
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }
}
