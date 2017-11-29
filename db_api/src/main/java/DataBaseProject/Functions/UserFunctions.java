package DataBaseProject.Functions;

import DataBaseProject.Queries.UserQueries;
import DataBaseProject.Models.UserModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserFunctions extends LowerFunctions {
    public UserFunctions(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void create(final String about, final String email, final String fullname, final String nickname) {
        getJdbcTemplate().update(UserQueries.createUserQuery(), about, email, fullname, nickname);
    }

    public void update(final String about, final String email, final String fullname, final String nickname) {
        final StringBuilder sql = new StringBuilder("UPDATE users SET");
        final List<Object> args = new ArrayList<>();
        if (about != null) {
            sql.append(" about = ?,");
            args.add(about);
        }
        if (email != null) {
            sql.append(" email = ?,");
            args.add(email);
        }
        if (fullname != null) {
            sql.append(" fullname = ?,");
            args.add(fullname);
        }
        if (!args.isEmpty()) {
            sql.delete(sql.length() - 1, sql.length());
            sql.append(" WHERE nickname = ?");
            args.add(nickname);
            getJdbcTemplate().update(sql.toString(), args.toArray());
        }
    }

    public UserModel findSingleByNickOrMail(final String nickname, final String email) {
        return getJdbcTemplate().queryForObject(UserQueries.findUserQuery(), new Object[]{nickname, email}, readUser);
    }

    public List<UserModel> findManyByNickOrMail(final String nickname, final String email) {
        return getJdbcTemplate().query(UserQueries.findUserQuery(), new Object[]{nickname, email}, readUser);
    }

    public Integer count() {
        return getJdbcTemplate().queryForObject(UserQueries.countUsersQuery(), Integer.class);
    }

    public void clear() {
        getJdbcTemplate().execute(UserQueries.clearTableQuery());
    }
}
