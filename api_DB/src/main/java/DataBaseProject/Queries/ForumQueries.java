package DataBaseProject.Queries;


public class ForumQueries {

    public static String createForumQuery() {
        StringBuilder query = new StringBuilder("INSERT INTO forums (user_id, slug, title) VALUES((");
        query.append(LowerQueries.find_By_Query("id", "users", "nickname"));
        query.append("), ?, ?)");
        return query.toString();
    }

    public static String createForumInAllQuery() {
        StringBuilder query = new StringBuilder("INSERT INTO forum_users (forum_id, user_id) VALUES(?, (");
        query.append(LowerQueries.find_By_Query("id", "users", "nickname"));
        query.append("))");
        return query.toString();
    }

    public static String getForumQuery() {
        StringBuilder query = new StringBuilder("SELECT f.posts, f.slug, f.threads, f.title, u.nickname ");
        query.append("FROM users AS u ");
        query.append("  JOIN forums AS f ON f.slug = ? AND u.id = f.user_id ");
        return query.toString();
    }

    public static String getThreadsByForumQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT u.nickname, t.created, f.slug as f_slug, t.id, t.message, t.slug as t_slug, t.title, t.votes ");
        query.append("FROM users AS u ");
        query.append("  JOIN forum_users AS fu ON u.id = fu.user_id");
        query.append("  JOIN forums AS f ON f.slug = ? AND fu.forum_id = f.id");
        query.append("  JOIN threads AS t ON u.id = t.user_id AND t.forum_id = f.id ");
        return query.toString();
    }

    public static String findAllThreadsQuery (String since, Boolean desc) {
        StringBuilder query = new StringBuilder(ForumQueries.getThreadsByForumQuery());
        if (since != null) {
            query.append(" AND t.created ");
            String sign = (desc == Boolean.TRUE ? "<= ?" : ">= ?");
            query.append(sign);
        }
        query.append(" ORDER BY t.created");
        String order = (desc == Boolean.TRUE ? " DESC" : "");
        query.append(order);
        query.append(" LIMIT ?");
        return query.toString();
    }

    public static String getUsersByForumQuery() {
        StringBuilder query = new StringBuilder("SELECT u.about, u.email, u.fullname, u.nickname ");
        query.append("FROM users AS u ");
//        query.append("JOIN forum_users fu ON fu.forum_id = ? AND u.id = fu.user_id");
        query.append("WHERE u.id IN ("); //
        query.append("  SELECT user_id "); //
        query.append("  FROM forum_users "); //
        query.append("  WHERE forum_id = ?"); //
        query.append(")"); //
        return query.toString();
    }

    public static String findAllUsersQuery(String since, Boolean desc){
        StringBuilder query = new StringBuilder(ForumQueries.getUsersByForumQuery());
        if (since != null) {
            query.append(" AND u.nickname ");
            query.append(desc == Boolean.TRUE ? "< ?" : "> ?");
        }
        query.append(" ORDER BY u.nickname COLLATE ucs_basic");
        query.append(desc == Boolean.TRUE ? " DESC" : "");
        query.append(" LIMIT ?");
        return query.toString();
    }

    public static String findIdBySlugQuery () {
        return LowerQueries.find_By_Query("id", "forums", "slug");
    }

    public static String countForumsQuery() {
        return LowerQueries.count_Query("forums");
    }

    public static String clearTableQuery() {
        return LowerQueries.clearTable_Query("forums");
    }
}
