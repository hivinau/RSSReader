package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services;

import java.io.*;
import java.util.*;
import android.os.*;
import android.app.*;
import android.content.*;
import java.util.concurrent.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.events.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.listeners.*;

public class RssRequestService extends BaseService implements SaxHandler.ElementListener {

    public class RssBinder extends Binder {

        private final RssRequestService requestService;

        RssBinder(RssRequestService requestService) {

            this.requestService = requestService;
        }

        @SuppressWarnings({"unused"})
        public RssRequestService getRequestService() {

            return requestService;
        }
    }

    private final HttpClientListener httpListener = new HttpClientListener() {

        @Override
        public void onSuccess(HttpClientEvent event) {

            Object object = event.getRawResponse();

            if(object instanceof String) {

                String xml = (String) object;

                try {

                    InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));

                    XMLParser.parse(stream, RssRequestService.this);

                } catch (UnsupportedEncodingException exception) {

                    sendError(exception.getLocalizedMessage());
                }
            }
        }

        @Override
        public void onFailure(HttpClientEvent event) {

            Object response = event.getRawResponse();

            if(response instanceof Exception) {

                Exception exception = (Exception) response;

                sendError(exception.getLocalizedMessage());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        binder = new RssBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {

            Bundle bundle = intent.getExtras();

            if(bundle != null) {

                String action = intent.getAction();

                switch (action) {
                    case Action.HTTP_REQUEST_WITH_URL:

                        String url = bundle.getString(BundleConstant.URL);

                        HttpClient client = new HttpClient(context, url, HttpClient.GET);
                        client.setListener(httpListener);

                        Future<?> thread = executor.submit(client);
                        threads.add(thread);
                        break;
                    default:
                        break;
                }
            }
        }

        return Service.START_STICKY;
    }

    @Override
    public void onChannelParsed(Map<String, String> content) {

        if(content.size() > 0) {

            RSSChannel channel = new RSSChannel();

            for(Map.Entry<String, String> entry: content.entrySet()) {

                final String node = entry.getKey();
                final String value = entry.getValue();

                if(node.equalsIgnoreCase(RSSChannel.TITLE)) {

                    channel.setTitle(value);

                } else if(node.equalsIgnoreCase(RSSChannel.DESCRIPTION)) {

                    channel.setDescription(value);

                } else if(node.equalsIgnoreCase(RSSChannel.DATE)) {

                    channel.setDate(value);

                } else if(node.equalsIgnoreCase(RSSChannel.LINK)) {

                    channel.setLink(value);
                }
            }

            sendChannel(channel);
        }
    }

    @Override
    public void onItemParsed(Map<String, String> content) {

        if(content.size() > 0) {

            RSSItem item = new RSSItem();

            for(Map.Entry<String, String> entry: content.entrySet()) {

                final String node = entry.getKey();
                final String value = entry.getValue();

                if(node.equalsIgnoreCase(RSSItem.TITLE)) {

                    item.setTitle(value);

                } else if(node.equalsIgnoreCase(RSSItem.DESCRIPTION)) {

                    item.setDescription(value);

                } else if(node.equalsIgnoreCase(RSSItem.DATE)) {

                    item.setDate(value);

                } else if(node.equalsIgnoreCase(RSSItem.LINK)) {

                    item.setLink(value);

                } else if(node.equalsIgnoreCase(RSSItem.GUID)) {

                    item.setGuid(value);
                }
            }

            sendItem(item);
        }
    }

    private void sendChannel(RSSChannel channel) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleConstant.CHANNEL, channel);

        IntentHelper.sendToActivity(context, MainActivity.class, Action.THROW_CHANNEL, bundle);
    }

    private void sendItem(RSSItem item) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleConstant.ITEM, item);
        bundle.putString(BundleConstant.ITEM_SOURCE, RssRequestService.class.getName());

        IntentHelper.sendToActivity(context, MainActivity.class, Action.THROW_ITEM, bundle);
    }

    private void sendError(String error) {

        Bundle bundle = new Bundle();
        bundle.putString(BundleConstant.ERROR_OCCURED, error);

        IntentHelper.sendToActivity(context, MainActivity.class, Action.THROW_ERROR, bundle);
    }

}
