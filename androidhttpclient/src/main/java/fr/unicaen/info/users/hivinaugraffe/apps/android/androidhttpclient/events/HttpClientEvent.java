package fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.events;

import java.net.*;
import java.util.*;

@SuppressWarnings({"serial"})
public final class HttpClientEvent extends EventObject {

    private final URL url;
    private final Object rawResponse;
    /**
     * Constructor : create a new instance of {@link ConnectionEvent}
     * @param source the object which fired the event
     * @param url url for string representation where fired event
     * @param rawResponse raw result of request
     */
    public HttpClientEvent(Object source, URL url, Object rawResponse) {
        super(source);

        this.url = url;
        this.rawResponse = rawResponse;
    }

    /**
     * Getter : get url where fired event
     * @return {@link Url} url for string representation
     */
    public URL getUrl() {

        return url;
    }

    /**
     * Getter : raw result of request
     * @return {@link Object} raw result of request
     */
    public Object getRawResponse() {

        return rawResponse;
    }

}
