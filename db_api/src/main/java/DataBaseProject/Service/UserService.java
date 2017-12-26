package DataBaseProject.Service;

import DataBaseProject.Functions.UserFunctions;
import DataBaseProject.ResponseModels.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserFunctions userFunctions;
    private Object error;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.userFunctions = new UserFunctions(jdbcTemplate);
        this.error = "{\"message\": \"error\"}";
    }


    public ResponseEntity<Object> create_userService (UserModel user, String nickname) {
        HttpStatus status = HttpStatus.CREATED;
        Object body = user;
        try {
            userFunctions.create(user.getAbout(), user.getEmail(), user.getFullname(), nickname);
        } catch (DuplicateKeyException ex) {
            status = HttpStatus.CONFLICT;
            body = userFunctions.findManyByNickOrMail(nickname, user.getEmail());
        } catch (DataAccessException ex) {
            status = HttpStatus.NOT_FOUND;
            body = error;
        }
        user.setNickname(nickname);
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> viewProfileService (String nickname) {
        HttpStatus status = HttpStatus.OK;
        Object body;
        try {
            body = userFunctions.findSingleByNickOrMail(nickname, null);
        } catch (DataAccessException ex) {
            body = error;
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Object> modify_profileService (UserModel user, String nickname) {
        HttpStatus status = HttpStatus.OK;
        Object body = error;
        try {
            userFunctions.update(user.getAbout(), user.getEmail(), user.getFullname(), nickname);
            body = userFunctions.findSingleByNickOrMail(nickname, user.getEmail());
        } catch (DuplicateKeyException ex) {
            status = HttpStatus.CONFLICT;
        } catch (DataAccessException ex) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(body);
    }
}
