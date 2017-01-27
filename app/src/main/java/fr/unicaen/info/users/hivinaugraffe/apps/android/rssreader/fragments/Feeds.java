package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import android.os.*;
import android.view.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.controllers.*;

public class Feeds extends Fragment {

    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    private RSSPagerAdapter adapter = null;

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HandlerConstant.ITEM_AVAILABLE:

                    if(msg.obj != null && msg.obj instanceof String) {

                        adapter.addChannel((String) msg.obj);
                    }
                    break;
                default:
                    break;
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

        ((MainActivity) getActivity()).setHandler(handler);
    }

    @Override
    public void onStart() {
        super.onStart();

        //IntentHelper.sentToService(getActivity(), DatabaseService.class, Action.DATABASE_REQUESTED_TO_PULL_ITEM, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onPause() {
        super.onPause();

        viewPager.setAdapter(null);
        tabLayout.setupWithViewPager(null);
    }
}
