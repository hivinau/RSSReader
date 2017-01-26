package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import java.util.*;
import android.os.*;
import android.content.*;
import android.support.v7.preference.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.views.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities.*;

public class UserPreferences extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, ListPreference.OnPreferenceChangeListener, MarqueePreference.OnPreferenceLongClickListener {

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

        String titlesKey = getString(R.string.channels_title);
        String linksKey = getString(R.string.channels_summary);

        Set<String> titlesSet = new HashSet<>(preferences.getStringSet(titlesKey, new HashSet<String>()));
        Set<String> linksSet = new HashSet<>(preferences.getStringSet(linksKey, new HashSet<String>()));

        Object[] titles = titlesSet.toArray();
        Object[] links = linksSet.toArray();

        for(int i = 0, j = 0; i < titles.length && j < links.length;  i++, j++) {

            addChannel((String) titles[i], (String) links[i]);
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

    private void addChannel(String title, String summary) {

        if(title != null && summary != null) {

            MarqueePreference preference = new MarqueePreference(getActivity());
            preference.setKey(title);
            preference.setTitle(title);
            preference.setSummary(summary);
            preference.setEnabled(true);
            preference.setOnPreferenceLongClickListener(this);

            feedsCategory.addPreference(preference);
        }
    }

    private void removeChannel(String title) {

        if(title != null) {

            MarqueePreference preference = (MarqueePreference) findPreference(title);

            feedsCategory.removePreference(preference);

            String titlesKey = getString(R.string.channels_title);
            String linksKey = getString(R.string.channels_summary);

            Set<String> titlesSet = new HashSet<>(preferences.getStringSet(titlesKey, new HashSet<String>()));
            Set<String> linksSet = new HashSet<>(preferences.getStringSet(linksKey, new HashSet<String>()));

            titlesSet.remove(String.format("%s", preference.getTitle()));
            linksSet.remove(String.format("%s", preference.getSummary()));

            SharedPreferences.Editor editor = preferences.edit();

            editor.putStringSet(titlesKey, titlesSet);
            editor.putStringSet(linksKey, linksSet);

            editor.apply();
        }
    }
}
