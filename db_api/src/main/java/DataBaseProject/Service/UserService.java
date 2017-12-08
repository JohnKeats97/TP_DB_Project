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

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.userFunctions = new UserFunctions(jdbcTemplate);
    }


    public ResponseEntity<Object> create_userService (UserModel user, String nickname) {
        try {
            userFunctions.create(user.getAbout(), user.getEmail(), user.getFullname(), nickname);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(userFunctions.findManyByNickOrMail(nickname, user.getEmail()));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        user.setNickname(nickname);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    public ResponseEntity<Object> viewProfileService (String nickname) {
        UserModel user;
        try {
            user = userFunctions.findSingleByNickOrMail(nickname, null);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    public ResponseEntity<Object> modify_profileService (UserModel user, String nickname) {
        try {
            userFunctions.update(user.getAbout(), user.getEmail(), user.getFullname(), nickname);
            user = userFunctions.findSingleByNickOrMail(nickname, user.getEmail());
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"error\"}");
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"error\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
