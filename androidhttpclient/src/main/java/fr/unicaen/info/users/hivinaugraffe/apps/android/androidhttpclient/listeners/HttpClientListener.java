package fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.listeners;

import java.io.Reader;
import java.net.URLConnection;

public abstract class HttpClientListener implements IHttpClientListener {

    /**
     * Check reader and connection before establish communication
     * @param reader {@link Reader} for characters streaming
     * @param connection {@link URLConnection} to establish server/client communication
     */
    public void onPrepareRequest(Reader reader, URLConnection connection) {

    }
}
