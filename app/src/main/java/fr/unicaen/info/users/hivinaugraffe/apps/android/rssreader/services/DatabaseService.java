package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services;

import java.util.*;
import android.os.*;
import android.app.*;
import android.content.*;
import java.util.concurrent.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities.*;

public class DatabaseService extends BaseService {

    public class DatabaseBinder extends Binder {

        private final DatabaseService databaseService;

        DatabaseBinder(DatabaseService databaseService) {

            this.databaseService = databaseService;
        }

        @SuppressWarnings({"unused"})
        public DatabaseService getDatabaseService() {

            return databaseService;
        }
    }

    private final Semaphore semaphore = new Semaphore(1);

    @Override
    public void onCreate() {
        super.onCreate();

        binder = new DatabaseBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {

            String action = intent.getAction();

            Runnable runnable;

            Bundle bundle;

            switch (action) {

                case Action.DATABASE_REQUESTED_TO_PUSH_ITEM:

                    bundle = intent.getExtras();

                    if(bundle != null) {

                        final RSSItem item = bundle.getParcelable(BundleConstant.ITEM_TO_PUSH_ON_DATABASE);

                        if (item != null) {

                            runnable = new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        addItem(item);

                                    } catch (Exception exception) {

                                        sendError(exception.getLocalizedMessage());
                                    }
                                }
                            };

                            run(runnable);
                        }
                    }

                    break;
                case Action.DATABASE_REQUESTED_TO_PULL_ITEM:

                    runnable = new Runnable() {
                        @Override
                        public void run() {

                            try {

                                listItems();

                            } catch (Exception exception) {

                                sendError(exception.getLocalizedMessage());
                            }
                        }
                    };

                    run(runnable);
                    break;
                case Action.DATABASE_REQUESTED_TO_REMOVE_ITEM:

                    bundle = intent.getExtras();

                    if(bundle != null) {

                        final RSSItem item = bundle.getParcelable(BundleConstant.ITEM);

                        if (item != null) {

                            runnable = new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        removeItem(item);

                                    } catch (Exception exception) {

                                        sendError(exception.getLocalizedMessage());
                                    }
                                }
                            };

                            run(runnable);
                        }
                    }

                    break;
                case Action.DATABASE_REQUESTED_TO_CLEAR_ITEMS:

                    runnable = new Runnable() {
                        @Override
                        public void run() {

                            try {

                                clearItems();

                            } catch (Exception exception) {

                                sendError(exception.getLocalizedMessage());
                            }
                        }
                    };

                    run(runnable);
                    break;
                default:
                    break;
            }
        }

        return Service.START_STICKY;
    }

    private Map<String, String> mapItem(RSSItem item)  {

        Map<String, String> map = new HashMap<>();

        String title = item.getTitle();
        if(title != null) {

            map.put(DatabaseConstant.TABLE_COLUMN_TITLE, item.getTitle());
        }

        String description = item.getDescription();
        if(title != null) {

            map.put(DatabaseConstant.TABLE_COLUMN_DESCRIPTION, description);
        }

        String date = item.getDate();
        if(title != null) {

            map.put(DatabaseConstant.TABLE_COLUMN_DATE, date);
        }

        String link = item.getLink();
        if(title != null) {

            map.put(DatabaseConstant.TABLE_COLUMN_LINK, link);
        }

        String guid = item.getGuid();
        if(title != null) {

            map.put(DatabaseConstant.TABLE_COLUMN_GUID, guid);
        }

        return map;
    }

    private void addItem(RSSItem item) throws Exception {

        semaphore.acquire();

        EventManager manager = DatabaseManager.getInstance();

        manager.open();

        Map<String, String> map = mapItem(item);
        manager.pushData(map);

        manager.close();

        semaphore.release();
    }

    private void removeItem(RSSItem item) throws Exception {

        semaphore.acquire();

        EventManager manager = DatabaseManager.getInstance();

        manager.open();

        Map<String, String> map = mapItem(item);
        manager.dropData(map);

        manager.close();

        semaphore.release();
    }

    private void listItems() throws Exception {

        semaphore.acquire();

        EventManager manager = DatabaseManager.getInstance();

        manager.open();

        List<Map<String, String>> maps =  manager.pullData();

        if(maps.size() > 0) {

            for(Map<String, String> map: maps) {

                RSSItem item = new RSSItem();

                for(Map.Entry<String, String> entry: map.entrySet()) {

                    final String key = entry.getKey();
                    final String value = entry.getValue();

                    switch (key) {
                        case DatabaseConstant.TABLE_COLUMN_TITLE:

                            item.setTitle(value);
                            break;
                        case DatabaseConstant.TABLE_COLUMN_DESCRIPTION:

                            item.setDescription(value);
                            break;
                        case DatabaseConstant.TABLE_COLUMN_DATE:

                            item.setDate(value);
                            break;
                        case DatabaseConstant.TABLE_COLUMN_LINK:

                            item.setLink(value);
                            break;
                        case DatabaseConstant.TABLE_COLUMN_GUID:

                            item.setGuid(value);
                            break;
                        default:
                            break;
                    }
                }

                sendItem(item);
            }
        }

        manager.close();

        semaphore.release();
    }

    private void clearItems() throws Exception {

        semaphore.acquire();

        EventManager manager = DatabaseManager.getInstance();

        manager.open();

        manager.drop();

        sendDroppedAction();

        manager.close();

        semaphore.release();
    }

    private void sendItem(RSSItem item) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleConstant.ITEM, item);
        bundle.putString(BundleConstant.ITEM_SOURCE, DatabaseService.class.getName());

        IntentHelper.sendToActivity(context, MainActivity.class, Action.THROW_ITEM, bundle);
    }

    private void sendError(String error) {

        Bundle bundle = new Bundle();
        bundle.putString(BundleConstant.ERROR_OCCURED, error);

        IntentHelper.sendToActivity(context, MainActivity.class, Action.THROW_ERROR, bundle);
    }

    private void sendDroppedAction() {

        IntentHelper.sendToActivity(context, MainActivity.class, Action.THROW_DATABASE_DROPPED_STATE, null);
    }

    private void run(Runnable runnable) {

        Future<?> thread = executor.submit(runnable);
        threads.add(thread);
    }
}
