package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import android.os.*;
import android.view.*;
import android.content.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.controllers.*;

public class Feeds extends Fragment {

    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    private RSSPagerAdapter adapter = null;
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

                                    adapter.addChannel(channel);
                                    tabLayout.setVisibility(adapter.getCount() > 0 ? View.VISIBLE : View.GONE);
                                }
                            });
                        }
                    }
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.feeds, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new RSSPagerAdapter(getChildFragmentManager());

        filter = new IntentFilter();
        filter.addAction(Action.THROW_NEW_CHANNEL);
        filter.addAction(Action.THROW_CHANNEL);
        filter.addAction(Action.REFRESH);

        tabLayout.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager, true);

        getActivity().registerReceiver(broadcastReceiver, filter);

        Thread delay = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Thread.sleep(460);

                    getActivity().sendBroadcast(new Intent(Action.PULL_FEEDS));
                } catch (Exception ignored) {}
            }
        });

        delay.setPriority(Thread.MAX_PRIORITY);
        delay.setDaemon(true);
        delay.setName(Feeds.class.getName());
        delay.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        viewPager.setAdapter(null);
        tabLayout.setupWithViewPager(null);

        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
