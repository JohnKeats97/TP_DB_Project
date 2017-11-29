package DataBaseProject.Functions;

import DataBaseProject.Queries.ForumQueries;
import DataBaseProject.Queries.PostQueries;
import DataBaseProject.Queries.ThreadQueries;
import DataBaseProject.Queries.UserQueries;
import DataBaseProject.Models.*;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class PostFunctions extends LowerFunctions {
    public PostFunctions(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void create(final List<PostModel> posts, final String slug_or_id) {
        final Integer threadId = slug_or_id.matches("\\d+") ? Integer.valueOf(slug_or_id) :
                getJdbcTemplate().queryForObject(ThreadQueries.getThreadId(), Integer.class, slug_or_id);
        final Integer forumId = getJdbcTemplate().queryForObject(ThreadQueries.getForumIdQuery(), Integer.class, threadId);
        final Timestamp created = new Timestamp(System.currentTimeMillis());
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Integer postId = 0;
        try (Connection connection = getJdbcTemplate().getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            try (CallableStatement callableStatement = connection.prepareCall(PostQueries.post_insertQuery())) {
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
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw new DataRetrievalFailureException(null);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new DataRetrievalFailureException(null);
        }
        if (postId.equals(1000000)) {
            getJdbcTemplate().execute(PostQueries.createPostIndex());
        }
        getJdbcTemplate().update(ThreadQueries.updateForumsPostsCount(), posts.size(), forumId);
    }

    public PostModel update(final String message, final Integer id) {
        final PostModel post = findById(id);
        final StringBuilder query = new StringBuilder("UPDATE posts SET message = ?"); //
        if (!message.equals(post.getMessage())) {
            query.append(", is_edited = TRUE"); //
            post.setIsEdited(true);
            post.setMessage(message);
        }
        query.append(" WHERE id = ?"); //
        getJdbcTemplate().update(query.toString(), message, id);
//        getJdbcTemplate().update(PostQueries.updatePost(message.equals(post.getMessage())), message, id);
        return post;
    }

    public final PostModel findById(final Integer id) {
        return getJdbcTemplate().queryForObject(PostQueries.getPostQuery(), new Object[]{id}, readPost);
    }

    public PostDetailedModel detailedView(final Integer id, final String[] related) {
        final PostModel post = findById(id);
        UserModel user = null;
        ForumModel forum = null;
        ThreadModel thread = null;
        if (related != null) {
            for (String relation : related) {
                switch (relation) {
                    case "user":
                        user = getJdbcTemplate().queryForObject(UserQueries.findUserQuery(),
                                new Object[]{post.getAuthor(), null}, readUser);
                        break;
                    case "forum":
                        forum = getJdbcTemplate().queryForObject(ForumQueries.getForumQuery(),
                                new Object[]{post.getForum()}, readForum);
                        break;
                    case "thread":
                        thread = getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(String.valueOf(post.getThread())),
                                new Object[]{post.getThread()}, readThread);
                }
            }
        }
        return new PostDetailedModel(user, forum, post, thread);
    }

    public List<PostModel> sort(final ThreadModel thread, final String slug_or_id, final Integer limit, final Integer since, final String sort, final Boolean desc) {
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
        switch (sort) {
            case "flat" :
                return getJdbcTemplate().query(PostQueries.getPostsFlat(limit, since, desc), arguments.toArray(), readPost);
            case "tree" :
                return getJdbcTemplate().query(PostQueries.getPostsTree(limit,since,desc), arguments.toArray(), readPost);
            case "parent_tree" :
                return getJdbcTemplate().query(PostQueries.getPostsParentTree(limit, since, desc), arguments.toArray(), readPost);
            default:
                break;
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
