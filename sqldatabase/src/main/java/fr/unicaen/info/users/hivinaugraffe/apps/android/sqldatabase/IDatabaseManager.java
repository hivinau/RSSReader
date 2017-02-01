package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase;

import java.util.*;

public interface IDatabaseManager {

    /**
     * Open database
     */
    void open();

    /**
     * Close database
     */
    void close();

    /**
     * Delete table
     */
    void drop();

    /**
     * Insert data on database
     * @param tablename name of table to push data
     * @param contentValues map of {@link String} represents column name and its respective value
     */
    long pushData(String tablename, Map<String, String> contentValues);

    /**
     * Check if data exist
     * @param tablename name of table to find data
     * @param args values of data to check existence
     * @return if returns <b>true</b>, data exist
     */
    boolean dataExist(String tablename, Map<String, String> args);

    /**
     * Retrieve all field of data saved on database
     * @param tablename name of table to retrieve data
     * @return map of {@link String} can be wrapped to key/value object
     */
    List<Map<String, String>> pullData(String tablename);

    /**
     * Retrieve some data saved on database depending on query
     * @param request raw query
     * @param args args if need, may include ?s in where clause in the query
     * @return map of {@link String} can be wrapped to key/value object
     */
    List<Map<String, String>> rawQuery(String request, String[] args);

    /**
     * Retrieve some data saved on database depending on query
     * @param distinct true if you want each row to be unique, false otherwise.
     * @param table The table name to compile the query against.
     * @param columns A list of which columns to return. Passing null will
     *            return all columns, which is discouraged to prevent reading
     *            data from storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an
     *            SQL WHERE clause (excluding the WHERE itself). Passing null
     *            will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *         replaced by the values from selectionArgs, in order that they
     *         appear in the selection. The values will be bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL
     *            GROUP BY clause (excluding the GROUP BY itself). Passing null
     *            will cause the rows to not be grouped.
     * @param having A filter declare which row groups to include in the cursor,
     *            if row grouping is being used, formatted as an SQL HAVING
     *            clause (excluding the HAVING itself). Passing null will cause
     *            all row groups to be included, and is required when row
     *            grouping is not being used.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause
     *            (excluding the ORDER BY itself). Passing null will use the
     *            default sort order, which may be unordered.
     * @param limit Limits the number of rows returned by the query,
     *            formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return map of {@link String} can be wrapped to key/value object
     */
    List<Map<String, String>> query(boolean distinct, String table, String[] columns,
                                    String selection, String[] selectionArgs, String groupBy,
                                    String having, String orderBy, String limit);

    /**
     * Delete data from database
     * @param tablename name of table to delete data
     * @param args values of data to delete
     */
    void dropData(String tablename, Map<String, String> args);

    /**
     * Update data on database
     * @param tablename name of table
     * @param column name of column
     * @param value new value of column
     * @param args values of data to update
     */
    void updateData(String tablename , String column, String value, Map<String, String> args);


}
