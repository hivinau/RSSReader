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
     * @return map of {@String} can be wrapped to key/value object
     */
    List<Map<String, String>> pullData(String tablename);

    /**
     * Retrieve some data saved on database depending on query
     * @param request raw query
     * @param args args if need, may include ?s in where clause in the query
     * @return map of {@String} can be wrapped to key/value object
     */
    List<Map<String, String>> rawQuery(String request, String[] args);

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
