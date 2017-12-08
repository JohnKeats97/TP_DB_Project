package DataBaseProject.Queries;


public class ThreadQueries {

    public static String getForumIdQuery() {
        StringBuilder query = new StringBuilder("SELECT f.id FROM forums AS f ");
        query.append("JOIN threads AS t ON t.id = ? AND t.forum_id = f.id");
        return query.toString();
    }

    public static String getThreadId() {
        return LowerQueries.find_By_Query("id", "threads", "slug");
    }

    public static String updateForumsPostsCount() {
        return "UPDATE forums SET posts = posts + ? WHERE forums.id = ?";
    }

    public static String getThreadQuery(final String slug_or_id) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT u.nickname, t.created, f.slug AS f_slug, t.id, t.message, t.slug AS t_slug, t.title, t.votes ");
        // медленно
        query.append("FROM forums AS f "); // user
        query.append("  JOIN forum_users AS fu ON fu.forum_id = f.id");
        query.append("  JOIN users AS u ON u.id = fu.user_id"); // forum
        query.append("  JOIN threads AS t ON ");
        String id_slug = (slug_or_id.matches("\\d+") ? "t.id = ? " : "t.slug = ? ");
        query.append(id_slug);
        query.append("AND u.id = t.user_id AND t.forum_id = f.id ");
        return query.toString();
    }

    public static String countThreadsQuery() {
        return LowerQueries.count_Query("threads");
    }

    public static String clearTableQuery() {
        return LowerQueries.clearTable_Query("threads");
    }

    public static String thread_insertQuery() {
        return "SELECT thread_insert(?, ?, ?, ?, ?, ?)";
    }

    public static String updateQuery(String message, String title, String slug_or_id) {
        StringBuilder query = new StringBuilder("UPDATE threads SET");
        if (message != null) {
            query.append(" message = ?,");
        }
        if (title != null) {
            query.append(" title = ?,");
        }
        query.delete(query.length() - 1, query.length());
        String id_slug = (slug_or_id.matches("\\d+") ? " WHERE id = ?" : " WHERE slug = ?");
        query.append(id_slug);
        return query.toString();
    }

    public static String updateVotesQuery (String userId, String threadId, Integer voiceView) {
        StringBuilder query = new StringBuilder("SELECT update_or_insert_votes(");
        query.append(userId);
        query.append(", ");
        query.append(threadId);
        query.append(", ");
        query.append(voiceView);
        query.append(")");
        return query.toString();
    }
}
