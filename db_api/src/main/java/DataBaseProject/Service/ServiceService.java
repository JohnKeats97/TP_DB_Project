package DataBaseProject.Service;

import DataBaseProject.Functions.ForumFunctions;
import DataBaseProject.Functions.PostFunctions;
import DataBaseProject.Functions.ThreadFunctions;
import DataBaseProject.Functions.UserFunctions;
import DataBaseProject.Models.StatusReturnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ServiceService {

    private final JdbcTemplate jdbcTemplate;
    private final PostFunctions postFunctions;
    private final ForumFunctions forumFunctions;
    private final ThreadFunctions threadFunctions;
    private final UserFunctions userFunctions;

    @Autowired
    public ServiceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.postFunctions = new PostFunctions(jdbcTemplate);
        this.forumFunctions = new ForumFunctions(jdbcTemplate);
        this.threadFunctions = new ThreadFunctions(jdbcTemplate);
        this.userFunctions = new UserFunctions(jdbcTemplate);
    }

    public ResponseEntity<Object> serverStatusService () {
        final Integer forumsCount = forumFunctions.count();
        final Integer postsCount = postFunctions.count();
        final Integer threadsCount = threadFunctions.count();
        final Integer usersCount = userFunctions.count();
        return ResponseEntity.status(HttpStatus.OK).body(new StatusReturnModel(forumsCount, postsCount, threadsCount, usersCount));
    }

    public ResponseEntity<Object> clearServiceService () {
        postFunctions.clear();
        threadFunctions.clear();
        forumFunctions.clear();
        userFunctions.clear();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
