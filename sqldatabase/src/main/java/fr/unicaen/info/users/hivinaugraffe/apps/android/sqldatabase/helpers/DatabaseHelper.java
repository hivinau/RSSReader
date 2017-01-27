package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.helpers;

import java.util.*;
import android.content.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.listeners.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final DatabaseHelperListener listener;

    /**
     * Constructor: create a new instance of {@link DatabaseHelper}
     * @param context context of application
     * @param database name of database
     * @param version version of database
     * @param listener {@link DatabaseHelperListener}
     */
    public DatabaseHelper(Context context, String database, int version, DatabaseHelperListener listener) {
        this(context, database, null, version, listener);

    }

    /**
     * Constructor: create a new instance of {@link DatabaseHelper}
     * @param context context of application
     * @param database name of database
     * @param cursorFactory method factory
     * @param version version of database
     * @param listener {@link DatabaseHelperListener}
     */
    public DatabaseHelper(Context context, String database, CursorFactory cursorFactory, int version, DatabaseHelperListener listener) {
        super(context, database, cursorFactory, version);

        this.listener = listener;
    }

    /**
     * Get listener
     * @return {@link DatabaseHelperListener}
     */
    public DatabaseHelperListener getDatabaseHelperListener() {

        return listener;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        try {

            int count = listener.tablesCount(database);

            if(count > 0) {

                for(int index = 0; index < count; index++) {

                    String tablename = listener.tablename(database, index);

                    List<TableColumn> columns = listener.formatTableColums(database, tablename);

                    String columnsName = "id INTEGER PRIMARY KEY AUTOINCREMENT,";

                    if(columns != null) {

                        for(TableColumn column: columns) {

                            columnsName += String.format("%s %s,", column.getName(), column.getType());
                        }
                    }

                    if (columnsName.endsWith(",")) {

                        columnsName = columnsName.substring(0, columnsName.length() - 1); //remove last ','
                    }

                    String request = String.format("CREATE TABLE IF NOT EXISTS %s (%s)", tablename, columnsName);

                    database.execSQL(request);
                }

                listener.onSuccess(database);
            }


        } catch(Exception exception) {

            listener.onFailure(database, exception);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int currentVersion, int newVersion) {

        dropTable(database);

        listener.onUpgraded(database, currentVersion, newVersion);
    }

    public void dropTable(SQLiteDatabase database) {

        try {

            int count = listener.tablesCount(database);

            if(count > 0) {

                for (int index = 0; index < count; index++) {

                    String request = String.format("DROP TABLE IF EXISTS '%s'", listener.tablename(database, index));

                    database.execSQL(request);
                }
            }
            onCreate(database);

        } catch(Exception exception) {

            listener.onFailure(database, exception);
        }
    }

}
