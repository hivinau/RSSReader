package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import android.os.*;
import android.content.*;
import android.support.annotation.*;
import android.support.v7.preference.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.views.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;

public class UserPreferences extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, ListPreference.OnPreferenceChangeListener, MarqueePreference.OnPreferenceLongClickListener {

    private Preference deleteFeedsPreference = null;
    private ListPreference updateModePreference = null;
    private PreferenceCategory feedsCategory = null;
    private IntentFilter filter = null;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(action != null) {

                if (action.equals(Action.THROW_NEW_CHANNEL) || action.equals(Action.THROW_CHANNEL)) {

                    Bundle bundle = intent.getExtras();

                    if (bundle != null) {

                        final Channel channel = bundle.getParcelable(BundleConstant.CHANNEL);
                        boolean forcing = bundle.getBoolean(BundleConstant.FORCE_REQUEST, false);

                        if (channel != null) {

                            SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                            boolean offline = preferences.getBoolean("user_offline", false);
                            String value = preferences.getString("user_update_mode", "0");

                            if (offline && (forcing || value.equals("2"))) {

                                String link = channel.getLink();

                                if (link != null) {

                                    intent = new Intent();

                                    bundle = new Bundle();
                                    bundle.putString(BundleConstant.URL, link);

                                    intent.setAction(Action.HTTP_REQUEST_WITH_URL);
                                    intent.putExtras(bundle);

                                    getActivity().sendBroadcast(intent);
                                }
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    addChannel(channel);
                                }
                            });
                        }
                    }
                } else if (action.equals(Action.REFRESH)) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            feedsCategory.removeAll();

                            Thread delay = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        Thread.sleep(460);

                                        getActivity().sendBroadcast(new Intent(Action.PULL_CHANNELS));
                                    } catch (Exception ignored) {}
                                }
                            });

                            delay.setPriority(Thread.MAX_PRIORITY);
                            delay.setDaemon(true);
                            delay.setName(UserPreferences.class.getName());
                            delay.start();
                        }
                    });
                }
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(updateModePreference.getValue() == null){

            updateModePreference.setValueIndex(0);
        }

        filter = new IntentFilter();
        filter.addAction(Action.THROW_NEW_CHANNEL);
        filter.addAction(Action.THROW_CHANNEL);
        filter.addAction(Action.REFRESH);
    }

    @Override
    public void onResume() {
        super.onResume();

        deleteFeedsPreference.setOnPreferenceClickListener(this);
        updateModePreference.setOnPreferenceChangeListener(this);

        getActivity().registerReceiver(broadcastReceiver, filter);

        Thread delay = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Thread.sleep(460);

                    SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                    boolean offline = preferences.getBoolean("user_offline", false);
                    String value = preferences.getString("user_update_mode", "0");

                    Intent intent = new Intent();

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(BundleConstant.FORCE_REQUEST, offline && value.equals("2"));

                    intent.setAction(Action.PULL_CHANNELS);
                    intent.putExtras(bundle);

                    getActivity().sendBroadcast(intent);
                } catch (Exception ignored) {}
            }
        });

        delay.setPriority(Thread.MAX_PRIORITY);
        delay.setDaemon(true);
        delay.setName(UserPreferences.class.getName());
        delay.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        deleteFeedsPreference.setOnPreferenceClickListener(null);
        updateModePreference.setOnPreferenceChangeListener(null);

        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();

        feedsCategory.removeAll();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        int count = feedsCategory.getPreferenceCount();

        for(int i = 0; i < count; i++) {

            Preference pref = feedsCategory.getPreference(i);

            String url = pref.getKey();

            if(url != null) {

                Intent intent = new Intent();

                Bundle bundle = new Bundle();
                bundle.putString(BundleConstant.URL, url);

                intent.setAction(Action.CLEAR_ITEMS);
                intent.putExtras(bundle);

                getActivity().sendBroadcast(intent);
            }
        }

        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        getActivity().invalidateOptionsMenu();

        return true;
    }

    @Override
    public boolean onLongClicked(Preference preference, String key) {

        ((MainActivity) getActivity()).askToDeletePreference(key);

        return false;
    }

    private void addChannel(Channel channel) {

        String title = channel.getTitle();
        String link = channel.getLink();

        if(title != null && link != null) {

            if(feedsCategory.findPreference(link) == null) {

                MarqueePreference preference = new MarqueePreference(getActivity());
                preference.setKey(link);
                preference.setTitle(title);
                preference.setSummary(link);
                preference.setEnabled(true);
                preference.setOnPreferenceLongClickListener(this);

                feedsCategory.addPreference(preference);
            }
        }
    }

}
