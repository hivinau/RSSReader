package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase;

import android.content.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.listeners.*;

public class DatabaseManager {

    private static EventManager eventManager = null;

    private DatabaseManager() {}

    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    @Override
    public Object clone() throws CloneNotSupportedException {

        throw new CloneNotSupportedException();
    }

    public static void initConfiguration(Context context, int version, EventManagerListener listener) {

        DatabaseManager.initConfiguration(context, "", version, listener);
    }

    public static void initConfiguration(Context context, String database, int version, EventManagerListener listener) {

        eventManager = new EventManager(context, database, version, listener);
    }

    public static synchronized EventManager getInstance() throws Exception {

        if(eventManager == null) {

            throw new Exception("initConfiguration must be called before");
        }

        return eventManager;
    }
}
