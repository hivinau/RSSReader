package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase;

import java.util.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.listeners.*;

public class EventManager implements IDatabaseManager {

    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public EventManager(Context context, int version, EventManagerListener listener) {
        this(context, "", version, listener);
    }

    public EventManager(Context context, String database, int version, EventManagerListener listener) {

        databaseHelper = new DatabaseHelper(context, database, version, listener);
    }

    @Override
    public synchronized void open() {

        if(database == null || !database.isOpen()) {

            database = databaseHelper.getWritableDatabase();
        }
    }

    @Override
    public synchronized void close() {

        if(database != null) {

            if(database.isOpen()) {

                databaseHelper.close();
            }
        }
    }

    @Override
    public synchronized void drop() {

        String tablename = databaseHelper.getDatabaseHelperListener().tablename(database);

        //database.delete(tablename, null, null);
        databaseHelper.dropTable(database);

        ((EventManagerListener) databaseHelper.getDatabaseHelperListener()).tableDropped(database, tablename);
    }

    @Override
    public synchronized boolean tableEmpty() {
        Cursor cursor = null;

        boolean empty = true;

        try {

            String tablename = databaseHelper.getDatabaseHelperListener().tablename(database);

            String request = String.format("SELECT COUNT(*) FROM '%s'", tablename);

            cursor = database.rawQuery(request, null);

            if(cursor != null) {

                if(cursor.moveToFirst()) {

                    int count = cursor.getInt(0);

                    empty = count <= 0;
                }
            }

        } catch (Exception exception) {

            databaseHelper.getDatabaseHelperListener().onFailure(database, exception);

        } finally {

            if(cursor != null) {

                cursor.close();
            }
        }

        return empty;
    }

    @Override
    public synchronized List<Map<String, String>> pullData() {

        List<Map<String, String>> maps = new ArrayList<>();

        Cursor cursor = null;
        try {

            String tablename = databaseHelper.getDatabaseHelperListener().tablename(database);

            List<TableColumn> columns = databaseHelper.getDatabaseHelperListener().formatTableColums(database, tablename);

            if(columns != null) {

                cursor = database.query(tablename, null, null, null, null, null, null);

                if(cursor != null) {

                    if(cursor.moveToFirst()) {

                        while(!cursor.isAfterLast()) {

                            Map<String, String> map = new HashMap<>();

                            for(TableColumn column: columns) {

                                String columnName = column.getName();

                                int index = cursor.getColumnIndex(columnName);
                                map.put(columnName, cursor.getString(index));
                            }

                            maps.add(map);

                            cursor.moveToNext();
                        }
                    }
                }
            }

        } catch (Exception exception) {

            databaseHelper.getDatabaseHelperListener().onFailure(database, exception);

        } finally {

            if(cursor != null) {

                cursor.close();
            }
        }

        return maps;
    }

    @Override
    public synchronized long pushData(Map<String, String> values) {

        long id = -1;

        try {

            String tablename = databaseHelper.getDatabaseHelperListener().tablename(database);

            if(values != null) {

                ContentValues contentValues = new ContentValues();

                for(Map.Entry<String, String> entry: values.entrySet()) {

                    String key = entry.getKey();
                    String value = entry.getValue();

                    if(value != null) {

                        contentValues.put(key, value);
                    } else {

                        String message = String.format("Value for '%s' must be not null", key);
                        databaseHelper.getDatabaseHelperListener().onFailure(database, new Exception(message));
                    }
                }

                id = database.replace(tablename, null, contentValues);

                databaseHelper.getDatabaseHelperListener().onSuccess(database);

            } else {

                throw new Exception("Content values must be set before insertion");
            }

        } catch(Exception exception) {

            databaseHelper.getDatabaseHelperListener().onFailure(database, exception);
        }

        return id;
    }

    @Override
    public synchronized boolean dataExist(Map<String, String> args) {

        boolean exist = false;
        Cursor cursor = null;

        try {

            String tablename = databaseHelper.getDatabaseHelperListener().tablename(database);

            if(args != null && args.size() > 0) {

                String where = "";
                String[] arguments = new String[args.size()];

                int i = 0;
                for(Map.Entry<String, String> entry: args.entrySet()) {

                    where += entry.getKey() + "=? AND ";
                    arguments[i++] = entry.getValue();
                }

                if (where.endsWith(" AND ")) {

                    where = where.substring(0, where.length() - 5);
                }

                String selectString = "SELECT * FROM " + tablename + where;

                cursor = database.rawQuery(selectString, arguments);

                if(cursor != null) {

                    exist = cursor.moveToFirst();
                }
            }

        } catch (Exception exception) {

            databaseHelper.getDatabaseHelperListener().onFailure(database, exception);

        } finally {

            if(cursor != null) {

                cursor.close();
            }
        }

        return exist;
    }

    @Override
    public synchronized void updateData(String column, String value, Map<String, String> args) {

        if(value != null) {

            ContentValues contentValues = new ContentValues();

            contentValues.put(column, value);

            String tablename = databaseHelper.getDatabaseHelperListener().tablename(database);

            if(args != null && args.size() > 0) {

                String where = "";
                String[] arguments = new String[args.size()];

                int i = 0;
                for(Map.Entry<String, String> entry: args.entrySet()) {

                    where += entry.getKey() + "=? AND ";
                    arguments[i++] = entry.getValue();
                }

                if (where.endsWith(" AND ")) {

                    where = where.substring(0, where.length() - 5);
                }

                database.update(tablename, contentValues, where, arguments);

            } else {

                database.update(tablename, contentValues, null, null);
            }

            databaseHelper.getDatabaseHelperListener().onSuccess(database);
        } else {

            String message = String.format("Value for '%s' must be not null", column);
            databaseHelper.getDatabaseHelperListener().onFailure(database, new Exception(message));
        }

    }

    @Override
    public synchronized void dropData(Map<String, String> args) {

        String tablename = databaseHelper.getDatabaseHelperListener().tablename(database);

        if(args != null && args.size() > 0) {

            String where = "";
            String[] arguments = new String[args.size()];

            int i = 0;
            for(Map.Entry<String, String> entry: args.entrySet()) {

                where += entry.getKey() + "=? AND ";
                arguments[i++] = entry.getValue();
            }

            if (where.endsWith(" AND ")) {

                where = where.substring(0, where.length() - 5);
            }

            database.delete(tablename, where, arguments);

        } else {

            database.delete(tablename, null, null);
        }

        databaseHelper.getDatabaseHelperListener().onSuccess(database);
    }
}