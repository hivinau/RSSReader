package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader;

import java.util.*;
import android.os.*;
import android.app.*;
import android.database.sqlite.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.listeners.*;

public class RSSReaderApplication extends Application {

    private EventManagerListener eventManagerListener = new EventManagerListener() {

        @Override
        public String tablename(SQLiteDatabase database) {

            return "RSS_table";
        }

        @Override
        public List<TableColumn> formatTableColums(SQLiteDatabase database, String tablename) {

            List<TableColumn> columns = new ArrayList<>();

            columns.add(new TableColumn(DatabaseConstant.TABLE_COLUMN_TITLE, "TEXT"));
            columns.add(new TableColumn(DatabaseConstant.TABLE_COLUMN_DESCRIPTION, "TEXT"));
            columns.add(new TableColumn(DatabaseConstant.TABLE_COLUMN_DATE, "TEXT"));
            columns.add(new TableColumn(DatabaseConstant.TABLE_COLUMN_LINK, "TEXT"));
            columns.add(new TableColumn(DatabaseConstant.TABLE_COLUMN_COPYRIGHT, "TEXT"));
            columns.add(new TableColumn(DatabaseConstant.TABLE_COLUMN_GUID, "TEXT"));
            columns.add(new TableColumn(DatabaseConstant.TABLE_COLUMN_IMAGE, "TEXT"));

            return columns;
        }

        @Override
        public void onSuccess(SQLiteDatabase database) {

            sendIntent(true, null);
        }

        @Override
        public void onFailure(SQLiteDatabase database, Throwable error) {

            sendIntent(false, error.getMessage());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseManager.initConfiguration(getApplicationContext(), getPackageName(), BuildConfig.VERSION_CODE, eventManagerListener);
    }

    private void sendIntent(boolean succeed, String error) {

        Bundle bundle = new Bundle();

        bundle.putBoolean(BundleConstant.DATABASE_ACTION_SUCCEED, succeed);

        if(error != null) {

            bundle.putString(BundleConstant.DATABASE_ERROR_OCCURED, error);
        }

        IntentHelper.sendToActivity(RSSReaderApplication.this, MainActivity.class, Action.DATABASE_HANDLE_EVENT, bundle);
    }
}
