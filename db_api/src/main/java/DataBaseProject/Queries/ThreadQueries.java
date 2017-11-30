package DataBaseProject.Queries;


public class ThreadQueries {
    public static String getForumIdQuery() {
        final StringBuilder query = new StringBuilder("SELECT forums.id FROM forums ");
        query.append("JOIN threads ON (threads.forum_id = forums.id) ");
        query.append("WHERE threads.id = ?");
        return query.toString();
    }

    public static String getThreadId() {
        return "SELECT id FROM threads WHERE slug = ?";
    }

    public static String updateForumsPostsCount() {
        return "UPDATE forums SET posts = posts + ? WHERE forums.id = ?";
    }

    public static String getThreadQuery(final String slug_or_id) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT u.nickname, t.created, f.slug AS f_slug, t.id, t.message, t.slug AS t_slug, t.title, t.votes ");
        query.append("FROM threads t ");
        query.append("  JOIN users u ON (t.user_id = u.id)");
        query.append("  JOIN forums f ON (t.forum_id = f.id) ");
        query.append("  WHERE ");
        String id_slug = (slug_or_id.matches("\\d+") ? "t.id = ?" : "t.slug = ?");
        query.append(id_slug);
        return query.toString();
    }

    public static String countThreadsQuery() {
        return "SELECT COUNT(*) FROM threads";
    }

    public static String clearTableQuery() {
        return "DELETE FROM threads";
    }

    public static String thread_insertQuery() {
        return "SELECT thread_insert(?, ?, ?, ?, ?, ?)";
    }

    public static String updateQuery(String message, String title, String slug_or_id) {
        final StringBuilder query = new StringBuilder("UPDATE threads SET");
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
        final StringBuilder query = new StringBuilder("SELECT update_or_insert_votes(");
        query.append(userId);
        query.append(", ");
        query.append(threadId);
        query.append(", ");
        query.append(voiceView);
        query.append(")");
        return query.toString();
    }
}
