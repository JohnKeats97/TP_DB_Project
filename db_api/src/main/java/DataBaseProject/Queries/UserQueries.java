package DataBaseProject.Queries;


public class UserQueries {

    public static String createUserQuery() {
        return "INSERT INTO users (about, email, fullname, nickname) VALUES(?, ?, ?, ?)";
    }

    public static String findUserQuery() {
        return "SELECT * FROM users WHERE nickname = ? OR email = ?";
    }

    public static String findUserIdQuery() {
        return "SELECT id FROM users WHERE nickname = ?";
    }

    public static String countUsersQuery() {
        return "SELECT COUNT(*) FROM users";
    }

    public static String clearTableQuery() {
        return "DELETE FROM users";
    }

    public static String updateQuery(String about, String email, String fullname) {
        final StringBuilder query = new StringBuilder("UPDATE users SET");
        if (about != null) {
            query.append(" about = ?,");
        }
        if (email != null) {
            query.append(" email = ?,");
        }
        if (fullname != null) {
            query.append(" fullname = ?,");
        }
        query.delete(query.length() - 1, query.length());
        query.append(" WHERE nickname = ?");
        return query.toString();
    }
}
