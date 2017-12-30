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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


@Service
public class PostFunctions {

    private JdbcTemplate template;
    private RowMapper<PostModel> readPost;
    private RowMapper<UserModel> readUser;
    private RowMapper<ForumModel> readForum;
    private RowMapper<ThreadModel> readThread;

    @Autowired
    public PostFunctions(JdbcTemplate template) {
        this.template = template;
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
                template.queryForObject(ThreadQueries.getThreadId(), Integer.class, slug_or_id);
        Integer forumId = template.queryForObject(ThreadQueries.getForumIdQuery(), Integer.class, threadId);
        if (posts.size() == 0) {
            return;
        }
        String currentTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        try (Connection connection = template.getDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareCall(PostQueries.post_insertQuery());
            for (PostModel post : posts) {
                Integer postId = template.queryForObject(PostQueries.nextvalQuery(), Integer.class);
                preparedStatement.setString(1, post.getAuthor());
                preparedStatement.setString(2, currentTime);
                preparedStatement.setInt(3, forumId);
                preparedStatement.setInt(4, postId);
                preparedStatement.setString(5, post.getMessage());
                preparedStatement.setInt(6, post.getParent());
                preparedStatement.setInt(7, threadId);
                preparedStatement.addBatch();
                post.setCreated(currentTime);
                post.setId(postId);
            }
            preparedStatement.executeBatch();
        } catch (SQLException ex) {
            throw new DataRetrievalFailureException(null);
        }
        template.update(ThreadQueries.updateForumsPostsCount(), posts.size(), forumId);
    }

    public PostModel update(String message, Integer id) {
        PostModel post = findById(id);
        template.update(PostQueries.updatePost(message.equals(post.getMessage())), message, id);
        if (!message.equals(post.getMessage())) {
            post.setIsEdited(true);
            post.setMessage(message);
        }
        return post;
    }

    public PostModel findById(Integer id) {
        return template.queryForObject(PostQueries.getPostQuery(), new Object[]{id}, readPost);
    }

    public PostDetailedInfoModel detailedView(Integer id, String[] related) {
        PostModel post = findById(id);
        UserModel user = null;
        ForumModel forum = null;
        ThreadModel thread = null;
        if (related != null) {
            for (String relation : related) {
                if (relation.equals("user")) {
                    user = template.queryForObject(UserQueries.findUserQuery(),
                            new Object[]{post.getAuthor(), null}, readUser);
                }
                if (relation.equals("forum")) {
                    forum = template.queryForObject(ForumQueries.getForumQuery(),
                            new Object[]{post.getForum()}, readForum);
                }
                if (relation.equals("thread")) {
                    thread = template.queryForObject(ThreadQueries.getThreadQuery(String.valueOf(post.getThread())),
                            new Object[]{post.getThread()}, readThread);
                }
            }
        }
        return new PostDetailedInfoModel(user, forum, post, thread);
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
            return template.query(PostQueries.getPostsFlat(limit, since, desc), arguments.toArray(), readPost);
        }
        if (sort.equals("flat")) {
            return template.query(PostQueries.getPostsFlat(limit, since, desc), arguments.toArray(), readPost);
        }
        if (sort.equals("tree")) {
            return template.query(PostQueries.getPostsTree(limit,since,desc), arguments.toArray(), readPost);
        }
        if (sort.equals("parent_tree")) {
            return template.query(PostQueries.getPostsParentTree(limit, since, desc), arguments.toArray(), readPost);
        }
        return template.query(PostQueries.getPostsFlat(limit, since, desc), arguments.toArray(), readPost);
    }

    public Integer count() {
        return template.queryForObject(PostQueries.countPostsQuery(), Integer.class);
    }

    public void clear() {
        template.execute(PostQueries.clearTableQuery());
    }
}
