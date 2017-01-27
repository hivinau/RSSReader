package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models;

import java.util.*;
import android.os.*;

public class RSSChannel extends RSS implements Parcelable {

    private static final String SEPARATOR = "###";

    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "pubDate";

    private final Set<ChannelItem> items;

    public static final Parcelable.Creator<RSS> CREATOR = new Parcelable.Creator<RSS>() {

        @Override
        public RSSChannel createFromParcel(Parcel in) {

            return new RSSChannel(in);
        }

        @Override
        public RSSChannel[] newArray(int size) {

            return new RSSChannel[size];
        }
    };

    public RSSChannel(String title, String description, String date, String link) {
        super(title, description, date, link);

        items = new HashSet<>();
    }

    public RSSChannel() {
        this(null, null, null, null);

    }

    public RSSChannel(ChannelItem RSSItem) {
        this(RSSItem.title, RSSItem.description, RSSItem.date, RSSItem.link);

    }

    public RSSChannel(Parcel in) {
        this(in.readString(), in.readString(), in.readString(), in.readString());

        Object[] objects = in.readArray(ChannelItem.class.getClassLoader());

        if(objects != null) {

            for(Object object: objects) {

                items.add((ChannelItem) object);
            }
        }

    }

    @Override
    public int hashCode() {

        int code = 0;

        if(title != null) {

            code += title.length();
        }

        if(description != null) {

            code += description.length();
        }

        if(date != null) {

            code += date.length();
        }

        if(link != null) {

            code += link.length();
        }

        return code * 2;
    }

    @Override
    public boolean equals(Object obj) {

        boolean equals = false;

        if(obj instanceof RSSChannel) {

            RSSChannel channel = (RSSChannel) obj;

            if(title != null) {

                equals = title.equals(channel.title);
            }

            if(description != null) {

                equals = equals && description.equals(channel.description);
            }

            if(date != null) {

                equals = equals && date.equals(channel.date);
            }

            if(link != null) {

                equals = equals && link.equals(channel.link);
            }
        }

        return equals;
    }

    @Override
    public String toString() {
        String representation = "";

        representation = concatValue(representation, title);
        representation = concatValue(representation, description);
        representation = concatValue(representation, date);
        representation = concatValue(representation, link);

        return representation;
    }

    public void addItem(ChannelItem item) {

        items.add(item);
    }

    public void removeItem(ChannelItem item) {

        items.remove(item);
    }

    public ChannelItem[] getItems() {

        return items.toArray(new ChannelItem[items.size()]);
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(link);
        dest.writeArray(items.toArray());
    }

    public static RSSChannel valueOf(String channelString) {

        RSSChannel channel = new RSSChannel();

        String[] split = channelString.split(RSSChannel.SEPARATOR);

        channel.title = split.length > 0 ? split[0] : null;
        channel.description = split.length > 1 ? split[1] : null;
        channel.date = split.length > 2 ? split[2] : null;
        channel.link = split.length > 3 ? split[3] : null;

        return channel;
    }

    private String concatValue(String representation, String value) {

        if(value != null) {

            if(representation.length() > 0) {

                representation += String.format(Locale.FRANCE, "%s%s", RSSChannel.SEPARATOR, value);

            } else {

                representation += String.format(Locale.FRANCE, "%s", value);
            }
        }

        return representation;
    }
}
