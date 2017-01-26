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
     * Check if table is empty
     * @return if returns <b>true</b>, table is empty
     */
    boolean tableEmpty();

    /**
     * Insert data on database
     * @param contentValues map of {@link String} represents column name and its respective value
     */
    long pushData(Map<String, String> contentValues);

    /**
     * Check if data exist
     * @param args values of data to check existence
     * @return if returns <b>true</b>, data exist
     */
    boolean dataExist(Map<String, String> args);

    /**
     * Retrieve all field of data saved on database
     * @return map of {@String} can be wrapped to key/value object
     */
    List<Map<String, String>> pullData();

    /**
     * Delete data from database
     * @param args values of data to delete
     */
    void dropData(Map<String, String> args);

    /**
     * Update data on database
     * @param column name of column
     * @param value new value of column
     * @param args values of data to update
     */
    void updateData(String column, String value, Map<String, String> args);
}
