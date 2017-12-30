package DataBaseProject.Functions;

import DataBaseProject.Queries.ThreadQueries;
import DataBaseProject.Queries.UserQueries;
import DataBaseProject.ResponseModels.ThreadModel;
import DataBaseProject.ResponseModels.VoteModel;
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
public class ThreadFunctions extends JdbcDaoSupport {

    private RowMapper<ThreadModel> readThread;

    @Autowired
    public ThreadFunctions(JdbcTemplate jdbcTemplate) {
        setJdbcTemplate(jdbcTemplate);
        readThread = (rs, rowNum) -> {
            Timestamp timestamp = rs.getTimestamp("created");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return new ThreadModel(rs.getString("nickname"), dateFormat.format(timestamp.getTime()),
                    rs.getString("f_slug"), rs.getInt("id"), rs.getString("message"),
                    rs.getString("t_slug"), rs.getString("title"), rs.getInt("votes"));
        };
    }


    public ThreadModel create(String author, String created, String forum,
                              String message, String slug, String title) {
        Integer threadId = getJdbcTemplate().queryForObject(ThreadQueries.thread_insertQuery(),
                new Object[]{author, created, forum, message, slug, title}, Integer.class);
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(threadId.toString()),
                new Object[]{threadId}, readThread);
    }

    public void update(String message, String title, String slug_or_id) {
        List<Object> args = new ArrayList<>();
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

    public ThreadModel findByIdOrSlug(String slug_or_id) {
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(slug_or_id), new Object[]{slug_or_id}, readThread);
    }

    public ThreadModel updateVotes(VoteModel view, String slug_or_id) {
        Integer userId = getJdbcTemplate().queryForObject(UserQueries.findUserIdQuery(), Integer.class, view.getNickname());
        Integer threadId = slug_or_id.matches("\\d+") ? Integer.valueOf(slug_or_id) :
                getJdbcTemplate().queryForObject(ThreadQueries.getThreadId(), Integer.class, slug_or_id);
        getJdbcTemplate().execute(ThreadQueries.updateVotesQuery(userId, threadId, view.getVoice()));
        return getJdbcTemplate().queryForObject(ThreadQueries.getThreadQuery(slug_or_id), new Object[]{slug_or_id}, readThread);
    }

    public Integer count() {
        return getJdbcTemplate().queryForObject(ThreadQueries.countThreadsQuery(), Integer.class);
    }

    public void clear() {
        getJdbcTemplate().execute(ThreadQueries.clearTableQuery());
    }
}
