package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.controllers;

import java.util.*;
import android.os.*;
import android.support.v4.app.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;

public class RSSPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Channel> channels;

    public RSSPagerAdapter(FragmentManager manager) {
        super(manager);

        channels = new ArrayList<>();
    }

    public void addChannel(Channel channel) {

        if(!channels.contains(channel)) {

            channels.add(channel);
            notifyDataSetChanged();
        }
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

        Bundle bundle = new Bundle();

        int i = 0;
        for(Channel channel: channels) {

            if(i == position) {

                bundle.putParcelableArrayList(BundleConstant.ITEMS, channel.getItems());
                break;
            }

            i++;
        }

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
