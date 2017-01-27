package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services;

import java.io.*;
import android.os.*;
import android.app.*;
import android.content.*;
import android.support.annotation.Nullable;

import java.util.concurrent.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;
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

    private BroadcastReceiver urlReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(action != null && action.equals(Action.HTTP_REQUEST_WITH_URL)) {

                Bundle bundle = intent.getExtras();

                if(bundle != null) {

                    String url = bundle.getString(BundleConstant.URL);

                    if(url != null) {

                        HttpClient client = new HttpClient(RssRequestService.this.context, url, HttpClient.GET);
                        client.setListener(httpListener);

                        Future<?> thread = executor.submit(client);
                        threads.add(thread);
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        binder = new RssBinder(this);

        filter.addAction(Action.HTTP_REQUEST_WITH_URL);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        registerReceiver(urlReceiver, filter);

        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        unregisterReceiver(urlReceiver);

        return super.onUnbind(intent);
    }

    @Override
    public void onParsingFinished(Channel channel) {

        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleConstant.CHANNEL, channel);

        intent.setAction(Action.THROW_NEW_CHANNEL);
        intent.putExtras(bundle);

        sendBroadcast(intent);
    }

}
