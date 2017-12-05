package DataBaseProject.Queries;


public class PostQueries {

    public static String createPostsQuery() {
        final StringBuilder query = new StringBuilder();
        query.append("INSERT INTO posts (user_id, created, forum_id, id, message, parent, thread_id, path) VALUES(");
        query.append("(SELECT u.id FROM users AS u WHERE u.nickname = ?), ?, ?, ?, ?, ?, ?, ");
        query.append("array_append((SELECT p.path FROM posts AS p WHERE p.id = ?), ?))");
        return query.toString();
    }

    public static String getPostQuery() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT u.nickname, p.created, f.slug, p.id, p.is_edited, p.message, p.parent, p.thread_id ");
        query.append("FROM users AS u");
        query.append("  JOIN posts AS p ON p.id = ? AND p.user_id = u.id  "); // поменять местами??
        query.append("  JOIN forums AS f ON p.forum_id = f.id  ");
        return query.toString();
    }

    public static String getPostsFlat(final Integer limit, final Integer since, final Boolean desc ) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT u.nickname, p.created, f.slug, p.id, p.is_edited, p.message, p.parent, p.thread_id ");
        query.append("FROM users AS u JOIN posts p ON u.id = p.user_id ");
        query.append("JOIN forums AS f ON f.id = p.forum_id ");
        query.append("WHERE p.thread_id = ? ");
        if (since != null) {
            String sign = (desc == Boolean.TRUE ? " < " : " > ");
            query.append(" AND p.id").append(sign).append("? ");
        }
        String order = (desc == Boolean.TRUE ? " DESC " : " ASC ");
        query.append("ORDER BY p.id ").append(order);
        if (limit != null) {
            query.append("LIMIT ?");
        }
        return query.toString();
    }
// AS
    public static String getPostsTree(final Integer limit, final Integer since, final Boolean desc ) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT u.nickname, p.created, f.slug, p.id, p.is_edited, p.message, p.parent, p.thread_id ");
        query.append("FROM users AS u JOIN posts p ON u.id = p.user_id ");
        query.append("JOIN forums AS f ON f.id = p.forum_id ");
        query.append("WHERE p.thread_id = ? ");
        if (since != null) {
            String sign = (desc == Boolean.TRUE ? " < " : " > ");
            query.append(" AND p.path ").append(sign).append("(SELECT path FROM posts WHERE id = ?) ");
        }
        String order = (desc == Boolean.TRUE ? " DESC " : " ASC ");
        query.append("ORDER BY p.path ").append(order);
        if (limit != null) {
            query.append("LIMIT ?");
        }
        return query.toString();
    }

    public static String getPostsParentTree(final Integer limit, final Integer since, final Boolean desc ) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT u.nickname, p.created, f.slug, p.id, p.is_edited, p.message, p.parent, p.thread_id ");
        query.append("FROM users AS u JOIN posts p ON (u.id = p.user_id) ");
        query.append("JOIN forums AS f ON f.id = p.forum_id ");
        query.append("WHERE p.root_id IN (SELECT id FROM posts WHERE thread_id = ? AND parent = 0 ");
        if (since != null) {
            String sign = (desc == Boolean.TRUE ? " < " : " > ");
            query.append(" AND path ").append(sign).append("(SELECT path FROM posts WHERE id = ?) ");
        }
        String order = (desc == Boolean.TRUE ? " DESC " : " ASC ");
        query.append("ORDER BY id ").append(order);
        if (limit != null) {
            query.append(" LIMIT ?");
        }
        query.append(") ");
        query.append("ORDER BY p.path ").append(order);
        return query.toString();
    }

    public static String countPostsQuery() {
        return LowerQueries.count_Query("posts");
    }

    public static String clearTableQuery() {
        return LowerQueries.clearTable_Query("posts");
    }

    public static String nextvalQuery() {
        return "SELECT nextval('posts_id_seq')";
    }

    public static String post_insertQuery() {
        return "{call post_insert(?, ?, ?, ?, ?, ?, ?)}";
    }

    public static String updatePost(Boolean equal) {
        final StringBuilder query = new StringBuilder("UPDATE posts SET message = ?");
        if (!equal) {
            query.append(", is_edited = TRUE");
        }
        query.append(" WHERE id = ?");
        return query.toString();
    }
}
