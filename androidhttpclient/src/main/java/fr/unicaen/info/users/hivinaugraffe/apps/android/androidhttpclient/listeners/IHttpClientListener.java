package fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.listeners;

import fr.unicaen.info.users.hivinaugraffe.apps.android.androidhttpclient.events.*;

public interface IHttpClientListener {

    /**
     * Handle event on success state
     * @param event {@link HttpClientEvent} event fired on success state
     */
    void onSuccess(HttpClientEvent event);

    /**
     * Handle event on failure state
     * @param event {@link HttpClientEvent} event fired on failure state
     */
    void onFailure(HttpClientEvent event);
}
