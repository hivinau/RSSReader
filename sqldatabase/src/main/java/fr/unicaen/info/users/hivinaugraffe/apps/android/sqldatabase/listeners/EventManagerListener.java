package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.listeners;

import android.database.sqlite.*;

public abstract class EventManagerListener extends DatabaseHelperListener {

    /**
     * Fire event when table was dropped
     * @param database database updated
     * @param tablename name of table dropped
     */
    public void tableDropped(SQLiteDatabase database, String tablename) {

    }
}
