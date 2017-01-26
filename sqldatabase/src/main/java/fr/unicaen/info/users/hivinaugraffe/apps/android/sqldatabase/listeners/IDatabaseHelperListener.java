package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.listeners;

import java.util.*;
import android.database.sqlite.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.models.*;

public interface IDatabaseHelperListener {

    /**
     * Set name of table
     * @param database database updated
     * @return {@link String} name of table
     */
    String tablename(SQLiteDatabase database);

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
