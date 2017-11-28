package DataBaseProject.Functions;

import DataBaseProject.Queries.ThreadQueries;
import DataBaseProject.Queries.UserQueries;
import DataBaseProject.Views.ThreadView;
import DataBaseProject.Views.VoteView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadFunctions extends LowerFunctions{
    public ThreadFunctions(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public ThreadView create(final String author, final String created, final String forum,
                                   final String message, final String slug, final String title) {
        final Integer threadId = getJdbcTemplate().queryForObject("SELECT thread_insert(?, ?, ?, ?, ?, ?)",
                new Object[]{author, created, forum, message, slug, title}, Integer.class);
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(threadId.toString()),
                new Object[]{threadId}, readThread);
    }

    public void update(final String message, final String title, final String slug_or_id) {
        final StringBuilder sql = new StringBuilder("UPDATE threads SET");
        final List<Object> args = new ArrayList<>();
        if (message != null) {
            sql.append(" message = ?,");
            args.add(message);
        }
        if (title != null) {
            sql.append(" title = ?,");
            args.add(title);
        }
        if (!args.isEmpty()) {
            sql.delete(sql.length() - 1, sql.length());
            sql.append(slug_or_id.matches("\\d+") ? " WHERE id = ?" : " WHERE slug = ?");
            args.add(slug_or_id);
            getJdbcTemplate().update(sql.toString(), args.toArray());
        }
    }

    public ThreadView findByIdOrSlug(final String slug_or_id) {
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(slug_or_id), new Object[]{slug_or_id}, readThread);
    }

    public ThreadView updateVotes(final VoteView view, final String slug_or_id) {
        final Integer userId = getJdbcTemplate().queryForObject(UserQueries.findUserIdQuery(), Integer.class, view.getNickname());
        final Integer threadId = slug_or_id.matches("\\d+") ? Integer.valueOf(slug_or_id) :
                getJdbcTemplate().queryForObject(ThreadQueries.getThreadId(), Integer.class, slug_or_id);
        final StringBuilder query = new StringBuilder("SELECT update_or_insert_votes(");
        query.append(userId.toString()).append(", ").append(threadId.toString())
                .append(", ").append(view.getVoice()).append(")");
        getJdbcTemplate().execute(query.toString());
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(slug_or_id), new Object[]{slug_or_id}, readThread);
    }

    public Integer count() {
        return getJdbcTemplate().queryForObject(ThreadQueries.countThreadsQuery(), Integer.class);
    }

    public void clear() {
        getJdbcTemplate().execute(ThreadQueries.clearTableQuery());
    }
}
