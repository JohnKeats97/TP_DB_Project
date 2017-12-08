package DataBaseProject.Service;

import DataBaseProject.Functions.ForumFunctions;
import DataBaseProject.Functions.PostFunctions;
import DataBaseProject.Functions.ThreadFunctions;
import DataBaseProject.Functions.UserFunctions;
import DataBaseProject.ResponseModels.StatusReturnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ServiceService {

    private JdbcTemplate jdbcTemplate;
    private PostFunctions postFunctions;
    private ForumFunctions forumFunctions;
    private ThreadFunctions threadFunctions;
    private UserFunctions userFunctions;

    @Autowired
    public ServiceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.postFunctions = new PostFunctions(jdbcTemplate);
        this.forumFunctions = new ForumFunctions(jdbcTemplate);
        this.threadFunctions = new ThreadFunctions(jdbcTemplate);
        this.userFunctions = new UserFunctions(jdbcTemplate);
    }

    public ResponseEntity<Object> statusService () {
        Integer forumsCount = forumFunctions.count();
        Integer postsCount = postFunctions.count();
        Integer threadsCount = threadFunctions.count();
        Integer usersCount = userFunctions.count();
        return ResponseEntity.status(HttpStatus.OK).body(new StatusReturnModel(forumsCount, postsCount, threadsCount, usersCount));
    }

    public ResponseEntity<Object> clearService () {
        userFunctions.clear();
        forumFunctions.clear();
        threadFunctions.clear();
        postFunctions.clear();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
