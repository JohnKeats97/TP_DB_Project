package DataBaseProject.Queries;

public class LowerQueries {

    public static String find_By_Query (String whatSelect, String table, String where) {
        final StringBuilder query = new StringBuilder("SELECT a.");
        query.append(whatSelect);
        query.append(" FROM ");
        query.append(table);
        query.append(" AS a WHERE a.");
        query.append(where);
        query.append(" = ?");
        return query.toString();
    }

    public static String count_Query(String table) {
        final StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM ");
        query.append(table);
        return query.toString();
    }

    public static String clearTable_Query(String table) {
        final StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(table);
        return query.toString();
    }

}
