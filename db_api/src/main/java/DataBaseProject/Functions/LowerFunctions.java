package DataBaseProject.Functions;

import DataBaseProject.Models.ForumModel;
import DataBaseProject.Models.PostModel;
import DataBaseProject.Models.ThreadModel;
import DataBaseProject.Models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class LowerFunctions extends JdbcDaoSupport {
    @Autowired
    public LowerFunctions(JdbcTemplate jdbcTemplate) {
        setJdbcTemplate(jdbcTemplate);
    }

    protected RowMapper<UserModel> readUser = (rs, rowNum) ->
            new UserModel(rs.getString("about"), rs.getString("email"),
                    rs.getString("fullname"), rs.getString("nickname"));

    protected RowMapper<ForumModel> readForum = (rs, rowNum) ->
            new ForumModel(rs.getInt("posts"), rs.getString("slug"),
                    rs.getInt("threads"), rs.getString("title"), rs.getString("nickname"));

    protected RowMapper<ThreadModel> readThread = (rs, rowNum) -> {
        final Timestamp timestamp = rs.getTimestamp("created");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new ThreadModel(rs.getString("nickname"), dateFormat.format(timestamp.getTime()),
                rs.getString("f_slug"), rs.getInt("id"), rs.getString("message"),
                rs.getString("t_slug"), rs.getString("title"), rs.getInt("votes"));
    };

    protected RowMapper<PostModel> readPost = (rs, rowNum) -> {
        final Timestamp timestamp = rs.getTimestamp("created");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new PostModel(rs.getString("nickname"), dateFormat.format(timestamp),
                rs.getString("slug"), rs.getInt("id"), rs.getBoolean("is_edited"),
                rs.getString("message"), rs.getInt("parent"), rs.getInt("thread_id"));
    };
}
