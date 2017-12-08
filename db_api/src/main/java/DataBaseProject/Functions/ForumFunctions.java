package DataBaseProject.Functions;

import DataBaseProject.Queries.ForumQueries;
import DataBaseProject.ResponseModels.ForumModel;
import DataBaseProject.ResponseModels.ThreadModel;
import DataBaseProject.ResponseModels.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


@Service
public class ForumFunctions extends JdbcDaoSupport {

    private RowMapper<ForumModel> readForum;
    private RowMapper<ThreadModel> readThread;
    private RowMapper<UserModel> readUser;

    @Autowired
    public ForumFunctions(JdbcTemplate jdbcTemplate) {
        setJdbcTemplate(jdbcTemplate);
        readForum = (rs, rowNum) ->
                new ForumModel(rs.getInt("posts"), rs.getString("slug"),
                        rs.getInt("threads"), rs.getString("title"), rs.getString("nickname"));
        readThread = (rs, rowNum) -> {
            Timestamp timestamp = rs.getTimestamp("created");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return new ThreadModel(rs.getString("nickname"), dateFormat.format(timestamp.getTime()),
                    rs.getString("f_slug"), rs.getInt("id"), rs.getString("message"),
                    rs.getString("t_slug"), rs.getString("title"), rs.getInt("votes"));
        };
        readUser = (rs, rowNum) ->
                new UserModel(rs.getString("about"), rs.getString("email"),
                        rs.getString("fullname"), rs.getString("nickname"));
    }


    public void create(String username, String slug, String title) {
        getJdbcTemplate().update(ForumQueries.createForumQuery(), username, slug, title);
    }

    public ForumModel findBySlug(String slug) {
        return getJdbcTemplate().queryForObject(ForumQueries.getForumQuery(), new Object[]{slug}, readForum);
    }

    public List<ThreadModel> findAllThreads(String slug, Integer limit, String since, Boolean desc) {
        List<Object> args = new ArrayList<>();
        args.add(slug);
        if (since != null) {
            args.add(since);
        }
        args.add(limit);
        return getJdbcTemplate().query(ForumQueries.findAllThreadsQuery(since, desc),
                args.toArray(new Object[args.size()]), readThread);
    }

    public List<UserModel> findAllUsers(String slug, Integer limit, String since, Boolean desc) {
        List<Object> args = new ArrayList<>();
        Integer forumId = getJdbcTemplate().queryForObject(ForumQueries.findIdBySlugQuery(), Integer.class, slug);
        args.add(forumId);
        if (since != null) {
            args.add(since);
        }
        args.add(limit);
        return getJdbcTemplate().query(ForumQueries.findAllUsersQuery(since, desc), args.toArray(), readUser);
    }

    public Integer count() {
        return getJdbcTemplate().queryForObject(ForumQueries.countForumsQuery(), Integer.class);
    }

    public void clear() {
        getJdbcTemplate().execute(ForumQueries.clearTableQuery());
    }
}
