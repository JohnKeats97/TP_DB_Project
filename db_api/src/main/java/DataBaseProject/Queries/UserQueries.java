package DataBaseProject.Queries;


public class UserQueries {

    public static String createUserQuery() {
        return "INSERT INTO users (about, email, fullname, nickname) VALUES(?, ?, ?, ?)";
    }

    public static String findUserQuery() {
        final StringBuilder query = new StringBuilder(LowerQueries.find_By_Query("*", "users", "nickname"));
        query.append(" OR a.email = ?");
        return query.toString();
    }

    public static String findUserIdQuery() {
        return LowerQueries.find_By_Query("id", "users", "nickname");
    }

    public static String countUsersQuery() {
        return LowerQueries.count_Query("users");
    }

    public static String clearTableQuery() {
        return LowerQueries.clearTable_Query("users");
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
