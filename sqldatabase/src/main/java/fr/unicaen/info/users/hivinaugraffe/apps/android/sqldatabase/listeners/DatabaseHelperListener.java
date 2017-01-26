package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.listeners;

import android.database.sqlite.*;

public abstract class DatabaseHelperListener implements IDatabaseHelperListener {

    /**
     * Fire event if database is updated
     * @param database database updated
     * @param currentVersion version of database before update
     * @param newVersion version of database after update
     */
    public void onUpgraded(SQLiteDatabase database, int currentVersion, int newVersion) {

    }
}
