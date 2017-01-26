package fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient;

import fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.events.*;

public interface IHttpClient {

    /**
     * Send response throught listener
     * @param event event that contain method name and object data
     * @param failed flag to prevent an error occured or not
     */
    void sendResponse(final HttpClientEvent event, final boolean failed);
}
