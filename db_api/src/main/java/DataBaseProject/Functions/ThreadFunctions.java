package DataBaseProject.Functions;

import DataBaseProject.Queries.ThreadQueries;
import DataBaseProject.Queries.UserQueries;
import DataBaseProject.ResponseModels.ThreadModel;
import DataBaseProject.ResponseModels.VoteModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadFunctions extends LowerFunctions{

    public ThreadFunctions(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public ThreadModel create(final String author, final String created, final String forum,
                              final String message, final String slug, final String title) {
        final Integer threadId = getJdbcTemplate().queryForObject(ThreadQueries.thread_insertQuery(),
                new Object[]{author, created, forum, message, slug, title}, Integer.class);
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(threadId.toString()),
                new Object[]{threadId}, readThread);
    }

    public void update(final String message, final String title, final String slug_or_id) {
        final List<Object> args = new ArrayList<>();
        if (message != null) {
            args.add(message);
        }
        if (title != null) {
            args.add(title);
        }
        if (!args.isEmpty()) {
            args.add(slug_or_id);
            getJdbcTemplate().update(ThreadQueries.updateQuery(message, title, slug_or_id),
                    args.toArray());
        }
    }

    public ThreadModel findByIdOrSlug(final String slug_or_id) {
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(slug_or_id), new Object[]{slug_or_id}, readThread);
    }

    public ThreadModel updateVotes(final VoteModel view, final String slug_or_id) {
        final Integer userId = getJdbcTemplate().queryForObject(UserQueries.findUserIdQuery(), Integer.class, view.getNickname());
        final Integer threadId = slug_or_id.matches("\\d+") ? Integer.valueOf(slug_or_id) :
                getJdbcTemplate().queryForObject(ThreadQueries.getThreadId(), Integer.class, slug_or_id);
        getJdbcTemplate().execute(ThreadQueries.updateVotesQuery(userId.toString(),
                threadId.toString(), view.getVoice()));
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(slug_or_id), new Object[]{slug_or_id}, readThread);
    }

    public Integer count() {
        return getJdbcTemplate().queryForObject(ThreadQueries.countThreadsQuery(), Integer.class);
    }

    public void clear() {
        getJdbcTemplate().execute(ThreadQueries.clearTableQuery());
    }
}
