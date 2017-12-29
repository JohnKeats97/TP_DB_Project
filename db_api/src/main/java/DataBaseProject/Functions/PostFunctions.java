package DataBaseProject.Functions;

import DataBaseProject.Queries.ForumQueries;
import DataBaseProject.Queries.PostQueries;
import DataBaseProject.Queries.ThreadQueries;
import DataBaseProject.Queries.UserQueries;
import DataBaseProject.ResponseModels.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class PostFunctions extends JdbcDaoSupport {

    private RowMapper<PostModel> readPost;
    private RowMapper<UserModel> readUser;
    private RowMapper<ForumModel> readForum;
    private RowMapper<ThreadModel> readThread;

    @Autowired
    public PostFunctions(JdbcTemplate jdbcTemplate) {
        setJdbcTemplate(jdbcTemplate);
        readPost = (rs, rowNum) -> {
            Timestamp timestamp = rs.getTimestamp("created");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return new PostModel(rs.getString("nickname"), dateFormat.format(timestamp),
                    rs.getString("slug"), rs.getInt("id"), rs.getBoolean("is_edited"),
                    rs.getString("message"), rs.getInt("parent"), rs.getInt("thread_id"));
        };
        readUser = (rs, rowNum) ->
                new UserModel(rs.getString("about"), rs.getString("email"),
                        rs.getString("fullname"), rs.getString("nickname"));
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
    }


    public void create(List<PostModel> posts, String slug_or_id) {
        Integer threadId = slug_or_id.matches("\\d+") ? Integer.valueOf(slug_or_id) :
                getJdbcTemplate().queryForObject(ThreadQueries.getThreadId(), Integer.class, slug_or_id);
        Integer forumId = getJdbcTemplate().queryForObject(ThreadQueries.getForumIdQuery(), Integer.class, threadId);
        Timestamp created = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Integer postId;
        try (Connection connection = getJdbcTemplate().getDataSource().getConnection();
            CallableStatement callableStatement = connection.prepareCall(PostQueries.post_insertQuery())) {
            for (PostModel post : posts) {
                postId = getJdbcTemplate().queryForObject(PostQueries.nextvalQuery(), Integer.class);
                callableStatement.setString(1, post.getAuthor());
                callableStatement.setTimestamp(2, created);
                callableStatement.setInt(3, forumId);
                callableStatement.setInt(4, postId);
                callableStatement.setString(5, post.getMessage());
                callableStatement.setInt(6, post.getParent());
                callableStatement.setInt(7, threadId);
                callableStatement.addBatch();
                post.setCreated(dateFormat.format(created));
                post.setId(postId);
            }
            callableStatement.executeBatch();
        } catch (SQLException ex) {
            throw new DataRetrievalFailureException(null);
        }
        getJdbcTemplate().update(ThreadQueries.updateForumsPostsCount(), posts.size(), forumId);
    }

    public PostModel update(String message, Integer id) {
        PostModel post = findById(id);
        getJdbcTemplate().update(PostQueries.updatePost(message.equals(post.getMessage())), message, id);
        if (!message.equals(post.getMessage())) {
            post.setIsEdited(true);
            post.setMessage(message);
        }
        return post;
    }

    public PostModel findById(Integer id) {
        return getJdbcTemplate().queryForObject(PostQueries.getPostQuery(), new Object[]{id}, readPost);
    }

    public PostDetailedModel detailedView(Integer id, String[] related) {
        PostModel post = findById(id);
        UserModel user = null;
        ForumModel forum = null;
        ThreadModel thread = null;
        if (related != null) {
            for (String relation : related) {
                if (relation.equals("user")) {
                    user = getJdbcTemplate().queryForObject(UserQueries.findUserQuery(),
                            new Object[]{post.getAuthor(), null}, readUser);
                }
                if (relation.equals("forum")) {
                    forum = getJdbcTemplate().queryForObject(ForumQueries.getForumQuery(),
                            new Object[]{post.getForum()}, readForum);
                }
                if (relation.equals("thread")) {
                    thread = getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(String.valueOf(post.getThread())),
                            new Object[]{post.getThread()}, readThread);
                }
            }
        }
        return new PostDetailedModel(user, forum, post, thread);
    }

    public List<PostModel> sort(ThreadModel thread, Integer limit, Integer since, String sort, Boolean desc) {
        List<Object> arguments = new ArrayList<>();
        arguments.add(thread.getId());
        if (since != null) {
            arguments.add(since);
        }
        if (limit != null) {
            arguments.add(limit);
        }
        if (sort == null) {
            return getJdbcTemplate().query(PostQueries.getPostsFlat(limit, since, desc), arguments.toArray(), readPost);
        }
        if (sort.equals("flat")) {
            return getJdbcTemplate().query(PostQueries.getPostsFlat(limit, since, desc), arguments.toArray(), readPost);
        }
        if (sort.equals("tree")) {
            return getJdbcTemplate().query(PostQueries.getPostsTree(limit,since,desc), arguments.toArray(), readPost);
        }
        if (sort.equals("parent_tree")) {
            return getJdbcTemplate().query(PostQueries.getPostsParentTree(limit, since, desc), arguments.toArray(), readPost);
        }
        return getJdbcTemplate().query(PostQueries.getPostsFlat(limit, since, desc), arguments.toArray(), readPost);
    }

    public Integer count() {
        return getJdbcTemplate().queryForObject(PostQueries.countPostsQuery(), Integer.class);
    }

    public void clear() {
        getJdbcTemplate().execute(PostQueries.clearTableQuery());
    }
}
