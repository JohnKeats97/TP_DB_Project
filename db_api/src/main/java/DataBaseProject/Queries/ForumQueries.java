package DataBaseProject.Queries;


public class ForumQueries {

    public static String createForumQuery() {
        return "INSERT INTO forums (user_id, slug, title) VALUES((SELECT id FROM users WHERE nickname = ?), ?, ?)";
    }

    public static String getForumQuery() {
        final StringBuilder query = new StringBuilder("SELECT f.posts, f.slug, f.threads, f.title, u.nickname ");
        query.append("FROM forums f "); //
        query.append("  JOIN users u ON (f.user_id = u.id)"); //
//        query.append("  JOIN users u ON (f.user_id = u.id AND f.slug = ?)"); // -2
        query.append("  WHERE f.slug = ?"); //
        //query.append("FROM users u "); // -1
//        query.append("  JOIN forums f ON (u.id = f.user_id AND f.slug = ?)"); // -1
        return query.toString();
    }


    public static String getThreadsByForumQuery() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT u.nickname, t.created, f.slug as f_slug, t.id, t.message, t.slug as t_slug, t.title, t.votes ");
        query.append("FROM threads t ");
        query.append("  JOIN users u ON (t.user_id = u.id)");
        query.append("  JOIN forums f ON (t.forum_id = f.id AND f.slug = ?)");
        return query.toString();
    }

    public static String findAllThreadsQuery (String since, Boolean desc) {
        final StringBuilder query = new StringBuilder(ForumQueries.getThreadsByForumQuery());
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
        final StringBuilder query = new StringBuilder("SELECT u.about, u.email, u.fullname, u.nickname ");
        query.append("FROM users u ");
//        query.append("JOIN forum_users fu ON u.id = fu.user_id AND fu.forum_id = ?"); // поменять местами
        query.append("WHERE u.id IN ("); //
        query.append("  SELECT user_id "); //
        query.append("  FROM forum_users "); //
        query.append("  WHERE forum_id = ?"); //
        query.append(")"); //
        return query.toString();
    }

    public static String findAllUsersQuery(String since, Boolean desc){
        final StringBuilder query = new StringBuilder(ForumQueries.getUsersByForumQuery());
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
        return "SELECT id FROM forums WHERE slug = ?";
    }

    public static String countForumsQuery() {
        return "SELECT COUNT(*) FROM forums";
    }

    public static String clearTableQuery() {
        return "DELETE FROM forums";
    }
}
