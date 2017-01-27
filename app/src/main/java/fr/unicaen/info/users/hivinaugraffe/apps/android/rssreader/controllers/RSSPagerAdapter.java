package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.controllers;

import java.util.*;
import android.os.*;
import android.support.v4.app.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;

public class RSSPagerAdapter extends FragmentStatePagerAdapter {

    private final Set<Channel> channels;

    public RSSPagerAdapter(FragmentManager manager) {
        super(manager);

        channels = new HashSet<>();
    }

    public void addChannel(Channel channel) {

        channels.add(channel);
        notifyDataSetChanged();
    }

    @SuppressWarnings({"unused"})
    public void remove(Channel channel) {

        channels.remove(channel);
        notifyDataSetChanged();
    }

    public void removeAll() {

        channels.clear();
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {

        ArrayList<Channel> channels = new ArrayList<>();

        for(Channel channel: this.channels) {

            channels.add(channel);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BundleConstant.ITEMS, channels.get(position).getItems());

        Fragment fragment = new Items();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {

        return channels.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Channel[] channels = this.channels.toArray(new Channel[this.channels.size()]);

        return channels[position].getTitle();
    }
}
