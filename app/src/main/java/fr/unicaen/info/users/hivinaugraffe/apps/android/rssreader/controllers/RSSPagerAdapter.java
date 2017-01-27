package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.controllers;

import java.util.*;
import android.support.v4.app.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments.*;

public class RSSPagerAdapter extends FragmentStatePagerAdapter {

    private final Set<String> channels;

    public RSSPagerAdapter(FragmentManager manager) {
        super(manager);

        channels = new HashSet<>();
    }

    public void addChannel(String channel) {

        channels.add(channel);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new Items();

        return fragment;
    }

    @Override
    public int getCount() {

        return channels.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Object[] titles = channels.toArray();

        return (CharSequence) titles[position];
    }
}
