package DataBaseProject.Functions;

import DataBaseProject.Queries.ForumQueries;
import DataBaseProject.Models.ForumModel;
import DataBaseProject.Models.ThreadModel;
import DataBaseProject.Models.UserModel;
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
        final StringBuilder sql = new StringBuilder(ForumQueries.getThreadsByForumQuery());
        final List<Object> args = new ArrayList<>();
        args.add(slug);
        if (since != null) {
            sql.append(" AND t.created ");
            sql.append(desc == Boolean.TRUE ? "<= ?" : ">= ?");
            args.add(since);
        }
        sql.append(" ORDER BY t.created");
        sql.append(desc == Boolean.TRUE ? " DESC" : "");
        sql.append(" LIMIT ?");
        args.add(limit);
        return getJdbcTemplate().query(sql.toString(), args.toArray(new Object[args.size()]), readThread);
    }

    public List<UserModel> findAllUsers(String slug, Integer limit, String since, Boolean desc) {
        final Integer forumId = getJdbcTemplate().queryForObject("SELECT id FROM forums WHERE slug = ?", Integer.class, slug);
        final StringBuilder sql = new StringBuilder(ForumQueries.getUsersByForumQuery());
        final List<Object> args = new ArrayList<>();
        args.add(forumId);
        if (since != null) {
            sql.append(" AND u.nickname ");
            sql.append(desc == Boolean.TRUE ? "< ?" : "> ?");
            args.add(since);
        }
        sql.append(" ORDER BY u.nickname COLLATE ucs_basic");
        sql.append(desc == Boolean.TRUE ? " DESC" : "");
        sql.append(" LIMIT ?");
        args.add(limit);
        return getJdbcTemplate().query(sql.toString(), args.toArray(), readUser);
    }

    public Integer count() {
        return getJdbcTemplate().queryForObject(ForumQueries.countForumsQuery(), Integer.class);
    }

    public void clear() {
        getJdbcTemplate().execute(ForumQueries.clearTableQuery());
    }
}
