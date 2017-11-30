package DataBaseProject.Functions;

import DataBaseProject.Queries.ForumQueries;
import DataBaseProject.ResponseModels.ForumModel;
import DataBaseProject.ResponseModels.ThreadModel;
import DataBaseProject.ResponseModels.UserModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ForumFunctions extends LowerFunctions {
    public ForumFunctions(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void create(String username, String slug, String title) {
        getJdbcTemplate().update(ForumQueries.createForumQuery(), username, slug, title);
    }

    public ForumModel findBySlug(String slug) {
        return getJdbcTemplate().queryForObject(ForumQueries.getForumQuery(), new Object[]{slug}, readForum);
    }

    public List<ThreadModel> findAllThreads(String slug, Integer limit, String since, Boolean desc) {
        final List<Object> args = new ArrayList<>();
        args.add(slug);
        if (since != null) {
            args.add(since);
        }
        args.add(limit);
        return getJdbcTemplate().query(ForumQueries.findAllThreadsQuery(since, desc),
                args.toArray(new Object[args.size()]), readThread);
    }

    public List<UserModel> findAllUsers(String slug, Integer limit, String since, Boolean desc) {
        final List<Object> args = new ArrayList<>();
        final Integer forumId = getJdbcTemplate().queryForObject(ForumQueries.findIdBySlugQuery(), Integer.class, slug);
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
