package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import java.util.*;
import android.os.*;
import android.content.*;
import android.support.v7.preference.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.views.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities.*;

public class UserPreferences extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, ListPreference.OnPreferenceChangeListener, MarqueePreference.OnPreferenceLongClickListener {

    public static final String CHANNELS = "channels";

    private Preference deleteFeedsPreference = null;
    private ListPreference updateModePreference = null;
    private PreferenceCategory feedsCategory = null;

    private SharedPreferences preferences = null;

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HandlerConstant.CHANNEL_ADDED:

                    break;
                case HandlerConstant.CHANNEL_REMOVED:

                    if(msg.obj != null && msg.obj instanceof String) {

                        String key = (String) msg.obj;
                        removeChannel(key);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.user_preferences, rootKey);

        deleteFeedsPreference = findPreference("user_delete_feeds");
        updateModePreference = (ListPreference) findPreference("user_update_mode");
        feedsCategory = (PreferenceCategory) findPreference("user_feeds_category");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).setHandler(handler);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        Set<String> channels = new HashSet<>(preferences.getStringSet(UserPreferences.CHANNELS, new HashSet<String>()));

        for(String channelString: channels) {

            RSSChannel channel = RSSChannel.valueOf(channelString);
            addChannel(channel);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        deleteFeedsPreference.setOnPreferenceClickListener(this);
        updateModePreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        deleteFeedsPreference.setOnPreferenceClickListener(null);
        updateModePreference.setOnPreferenceChangeListener(null);
    }

    @Override
    public void onStop() {
        super.onStop();

        feedsCategory.removeAll();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        IntentHelper.sentToService(getActivity(), DatabaseService.class, Action.DATABASE_REQUESTED_TO_CLEAR_ITEMS, null);

        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        return true;
    }

    @Override
    public boolean onLongClicked(Preference preference, String key) {

        ((MainActivity) getActivity()).askToDeletePreference(key);

        return false;
    }

    private void addChannel(RSSChannel channel) {

        if(channel.getTitle() != null && channel.getLink() != null) {

            MarqueePreference preference = new MarqueePreference(getActivity());
            preference.setKey(channel.getLink());
            preference.setTitle(channel.getTitle());
            preference.setSummary(channel.getLink());
            preference.setEnabled(true);
            preference.setOnPreferenceLongClickListener(this);

            feedsCategory.addPreference(preference);
        }
    }

    private void removeChannel(String key) {

        if(key != null) {

            Set<String> channels = new HashSet<>(preferences.getStringSet(UserPreferences.CHANNELS, new HashSet<String>()));

            Iterator<String> iterator = channels.iterator();

            while(iterator.hasNext()) {

                RSSChannel channel = RSSChannel.valueOf(iterator.next());

                String link = channel.getLink();
                if(link != null && link.equals(key)) {

                    iterator.remove();
                    break;
                }
            }

            MarqueePreference preference = (MarqueePreference) findPreference(key);
            feedsCategory.removePreference(preference);

            SharedPreferences.Editor editor = preferences.edit();

            editor.putStringSet(UserPreferences.CHANNELS, channels);

            editor.apply();
        }

    }
}
