package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.listeners;

import java.util.*;
import android.database.sqlite.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.models.*;

public interface IDatabaseHelperListener {

    int tablesCount(SQLiteDatabase database);

    /**
     * Set name of table
     * @param database database updated
     * @param index index of table
     * @return {@link String} name of table
     */
    String tablename(SQLiteDatabase database, int index);

    /**
     * Set colums of table
     * @param database database updated
     * @param tablename name of table
     * @return
     */
    List<TableColumn> formatTableColums(SQLiteDatabase database, String tablename);

    /**
     * Some actions on database are success
     * @param database database updated
     */
    void onSuccess(SQLiteDatabase database);

    /**
     * Some actions on database are failed
     * @param database database updated
     * @param error error happened during some actions
     */
    void onFailure(SQLiteDatabase database, Throwable error);
}
