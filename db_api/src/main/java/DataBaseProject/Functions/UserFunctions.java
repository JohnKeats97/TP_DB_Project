package DataBaseProject.Functions;

import DataBaseProject.Queries.UserQueries;
import DataBaseProject.ResponseModels.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserFunctions extends JdbcDaoSupport {

    private RowMapper<UserModel> readUser;

    @Autowired
    public UserFunctions(JdbcTemplate jdbcTemplate) {
        setJdbcTemplate(jdbcTemplate);
        readUser = (rs, rowNum) ->
                new UserModel(rs.getString("about"), rs.getString("email"),
                        rs.getString("fullname"), rs.getString("nickname"));
    }


    public void create(final String about, String email, String fullname, String nickname) {
        getJdbcTemplate().update(UserQueries.createUserQuery(), about, email, fullname, nickname);
    }

    public void update(String about, String email, String fullname, String nickname) {
        List<Object> args = new ArrayList<>();
        if (about != null) {
            args.add(about);
        }
        if (email != null) {
            args.add(email);
        }
        if (fullname != null) {
            args.add(fullname);
        }
        if (!args.isEmpty()) {
            args.add(nickname);
            getJdbcTemplate().update(UserQueries.updateQuery(about, email, fullname)
                    , args.toArray());
        }
    }

    public UserModel findSingleByNickOrMail(String nickname, String email) {
        return getJdbcTemplate().queryForObject(UserQueries.findUserQuery(), new Object[]{nickname, email}, readUser);
    }

    public List<UserModel> findManyByNickOrMail(String nickname, String email) {
        return getJdbcTemplate().query(UserQueries.findUserQuery(), new Object[]{nickname, email}, readUser);
    }

    public Integer count() {
        return getJdbcTemplate().queryForObject(UserQueries.countUsersQuery(), Integer.class);
    }

    public void clear() {
        getJdbcTemplate().execute(UserQueries.clearTableQuery());
    }
}
