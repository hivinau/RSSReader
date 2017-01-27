package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services;

import java.util.*;
import android.os.*;
import android.app.*;
import android.content.*;
import java.util.concurrent.*;
import android.support.annotation.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;

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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if(action != null) {

                Bundle bundle;
                Future<?> thread;
                switch (action) {
                    case Action.THROW_NEW_CHANNEL:

                        bundle = intent.getExtras();

                        if(bundle != null) {

                            final Channel channel = bundle.getParcelable(BundleConstant.CHANNEL);

                            if(channel != null) {

                                thread = executor.submit(new Runnable() {

                                    @Override
                                    public void run() {

                                        try {

                                            saveChannel(channel);

                                        } catch (Exception exception) {

                                            sendError(exception.getLocalizedMessage());
                                        }
                                    }
                                });

                                threads.add(thread);
                            }
                        }
                        break;
                    case Action.PULL_FEEDS:

                        thread = executor.submit(new Runnable() {

                            @Override
                            public void run() {

                                try {

                                    Set<Channel> channels = fetchChannels(true);

                                    for (Channel channel : channels) {

                                        final Intent i = new Intent();

                                        final Bundle b = new Bundle();

                                        b.putParcelable(BundleConstant.CHANNEL, channel);

                                        i.setAction(Action.THROW_CHANNEL);
                                        i.putExtras(b);

                                        sendBroadcast(i);
                                    }

                                } catch (Exception exception) {

                                    sendError(exception.getLocalizedMessage());
                                }
                            }
                        });

                        threads.add(thread);

                        break;
                    case Action.PULL_CHANNELS:

                        bundle = intent.getExtras();

                        if(bundle != null) {

                            final boolean forcing = bundle.getBoolean(BundleConstant.FORCE_REQUEST, false);

                            thread = executor.submit(new Runnable() {

                                @Override
                                public void run() {

                                    try {

                                        Set<Channel> channels = fetchChannels(false);

                                        for (Channel channel : channels) {

                                            final Intent i = new Intent();

                                            final Bundle b = new Bundle();
                                            b.putParcelable(BundleConstant.CHANNEL, channel);
                                            b.putBoolean(BundleConstant.FORCE_REQUEST, forcing);

                                            i.setAction(Action.THROW_CHANNEL);
                                            i.putExtras(b);

                                            sendBroadcast(i);
                                        }

                                    } catch (Exception exception) {

                                        sendError(exception.getLocalizedMessage());
                                    }
                                }
                            });

                            threads.add(thread);
                        }
                        break;
                    case Action.CLEAR_ITEMS:

                        bundle = intent.getExtras();

                        if(bundle != null) {

                            final String url = bundle.getString(BundleConstant.URL);

                            if(url != null) {

                                thread = executor.submit(new Runnable() {

                                    @Override
                                    public void run() {

                                        try {

                                            deleteFromLink(DatabaseConstant.TABLE_ITEMS, DatabaseConstant.TABLE_COLUMN_CHANNEL, url);

                                        } catch (Exception exception) {

                                            sendError(exception.getLocalizedMessage());
                                        }
                                    }
                                });

                                threads.add(thread);
                            }
                        }
                        break;
                    case Action.DELETE_FEED:

                        bundle = intent.getExtras();

                        if(bundle != null) {

                            final String url = bundle.getString(BundleConstant.URL);

                            if(url != null) {

                                thread = executor.submit(new Runnable() {

                                    @Override
                                    public void run() {

                                        try {

                                            deleteFromLink(DatabaseConstant.TABLE_ITEMS, DatabaseConstant.TABLE_COLUMN_CHANNEL, url);
                                            deleteFromLink(DatabaseConstant.TABLE_CHANNELS, DatabaseConstant.TABLE_COLUMN_LINK, url);

                                            Thread delay = new Thread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    try {

                                                        Thread.sleep(460);

                                                        sendBroadcast(new Intent(Action.REFRESH));
                                                    } catch (Exception ignored) {}
                                                }
                                            });

                                            delay.setPriority(Thread.MAX_PRIORITY);
                                            delay.setDaemon(true);
                                            delay.setName(DatabaseService.class.getName());
                                            delay.start();

                                        } catch (Exception exception) {

                                            sendError(exception.getLocalizedMessage());
                                        }
                                    }
                                });

                                threads.add(thread);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        binder = new DatabaseBinder(this);

        filter.addAction(Action.THROW_NEW_CHANNEL);
        filter.addAction(Action.PULL_FEEDS);
        filter.addAction(Action.PULL_CHANNELS);
        filter.addAction(Action.CLEAR_ITEMS);
        filter.addAction(Action.DELETE_FEED);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        registerReceiver(broadcastReceiver, filter);

        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        unregisterReceiver(broadcastReceiver);

        return super.onUnbind(intent);
    }

    private void saveChannel(Channel channel) throws Exception {

        semaphore.acquire();

        DatabaseManager.getInstance().open();

        DatabaseManager.getInstance().pushData(DatabaseConstant.TABLE_CHANNELS, parseChannel(channel));

        List<Item> items = channel.getItems();

        for(Item item: items) {

            item.setChannel(channel.getLink());
            DatabaseManager.getInstance().pushData(DatabaseConstant.TABLE_ITEMS, parseItem(item));
        }

        DatabaseManager.getInstance().close();

        semaphore.release();
    }

    private Map<String, String> parseChannel(Channel channel) {

        Map<String, String> parse = new HashMap<>();

        parse.put(DatabaseConstant.TABLE_COLUMN_TITLE, channel.getTitle());
        parse.put(DatabaseConstant.TABLE_COLUMN_DATE, channel.getDate());
        parse.put(DatabaseConstant.TABLE_COLUMN_DESCRIPTION, channel.getDescription());
        parse.put(DatabaseConstant.TABLE_COLUMN_LINK, channel.getLink());

        return parse;
    }

    private Map<String, String> parseItem(Item item) {

        Map<String, String> parse = new HashMap<>();

        parse.put(DatabaseConstant.TABLE_COLUMN_TITLE, item.getTitle());
        parse.put(DatabaseConstant.TABLE_COLUMN_DATE, item.getDate());
        parse.put(DatabaseConstant.TABLE_COLUMN_DESCRIPTION, item.getDescription());
        parse.put(DatabaseConstant.TABLE_COLUMN_LINK, item.getLink());
        parse.put(DatabaseConstant.TABLE_COLUMN_GUID, item.getGuid());
        parse.put(DatabaseConstant.TABLE_COLUMN_CHANNEL, item.getChannel());

        return parse;
    }

    private Set<Channel> fetchChannels(boolean fetchingItems) throws Exception {

        semaphore.acquire();

        DatabaseManager.getInstance().open();

        Set<Channel> channels = new HashSet<>();

        List<Map<String, String>> channelsMapped = DatabaseManager.getInstance().pullData(DatabaseConstant.TABLE_CHANNELS);

        for(Map<String, String> channelMapped: channelsMapped) {

            Channel channel = new Channel();

            for(Map.Entry<String, String> entry: channelMapped.entrySet()) {

                final String key = entry.getKey();
                final String value = entry.getValue();

                switch (key) {
                    case DatabaseConstant.TABLE_COLUMN_TITLE:

                        channel.setTitle(value);
                        break;
                    case DatabaseConstant.TABLE_COLUMN_DATE:

                        channel.setDate(value);
                        break;
                    case DatabaseConstant.TABLE_COLUMN_DESCRIPTION:

                        channel.setDescription(value);
                        break;
                    case DatabaseConstant.TABLE_COLUMN_LINK:

                        channel.setLink(value);
                        break;
                    default:
                        break;
                }
            }

            if(fetchingItems) {

                List<Map<String, String>> itemsMapped = DatabaseManager.getInstance().rawQuery("SELECT * FROM " + DatabaseConstant.TABLE_ITEMS + " WHERE " + DatabaseConstant.TABLE_COLUMN_CHANNEL + " = '" + channel.getLink() + "'", null);

                for(Map<String, String> itemMapped: itemsMapped) {

                    Item item = new Item();

                    for(Map.Entry<String, String> entry: itemMapped.entrySet()) {

                        final String key = entry.getKey();
                        final String value = entry.getValue();

                        switch (key) {
                            case DatabaseConstant.TABLE_COLUMN_TITLE:

                                item.setTitle(value);
                                break;
                            case DatabaseConstant.TABLE_COLUMN_DATE:

                                item.setDate(value);
                                break;
                            case DatabaseConstant.TABLE_COLUMN_DESCRIPTION:

                                item.setDescription(value);
                                break;
                            case DatabaseConstant.TABLE_COLUMN_LINK:

                                item.setLink(value);
                                break;
                            case DatabaseConstant.TABLE_COLUMN_GUID:

                                item.setGuid(value);
                                break;
                            case DatabaseConstant.TABLE_COLUMN_CHANNEL:

                                item.setChannel(value);
                                break;
                            default:
                                break;
                        }
                    }

                    channel.addItem(item);
                }
            }

            channels.add(channel);
        }

        DatabaseManager.getInstance().close();

        semaphore.release();

        return channels;
    }

    private void deleteFromLink(String tablename, String column, String url) throws Exception {

        semaphore.acquire();

        DatabaseManager.getInstance().open();

        Map<String, String> arguments = new HashMap<>();

        arguments.put(column, url);

        DatabaseManager.getInstance().dropData(tablename, arguments);

        DatabaseManager.getInstance().close();

        semaphore.release();
    }
}
