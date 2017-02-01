package fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models;

import android.os.*;

public class Item extends RSSBase implements Parcelable {

    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "pubDate";
    public static final String GUID = "guid";

    private String channel = null;
    private String guid = null;

    public static final Creator<Item> CREATOR = new Creator<Item>() {

        @Override
        public Item createFromParcel(Parcel in) {

            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {

            return new Item[size];
        }
    };

    public Item(String title, String description, String date, String link, String channel, String guid) {
        super(title, description, date, link);

        this.channel = channel;
        this.guid = guid;
    }

    public Item() {
        this(null, null, null, null, null, null);

    }

    public Item(Item item) {
        this(item.title, item.description, item.date, item.link, item.channel, item.guid);

    }

    public Item(Parcel in) {
        this(in.readString(), in.readString(), in.readString(), in.readString(), in.readString(), in.readString());

    }

    @Override
    public int hashCode() {

        int titleLength = title != null ? title.length() : 2;
        int dateLength = date != null ? date.length() : 1;
        int descriptionLength = description != null ? description.length() : 0;

        return (titleLength + dateLength) * descriptionLength + 10;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (!(obj instanceof Item)) return false;

        final Item item = (Item) obj;

        return (title != null && title.equals(item.title))
                && (description != null && description.equals(item.description))
                && (date != null && date.equals(item.date))
                && (link != null && link.equals(item.link));
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
        dest.writeString(channel);
        dest.writeString(guid);
    }

    public String getChannel() {

        return channel;
    }

    public void setChannel(String channel) {

        this.channel = channel;
    }

    public String getGuid() {

        return guid;
    }

    public void setGuid(String guid) {

        this.guid = guid;
    }
}
