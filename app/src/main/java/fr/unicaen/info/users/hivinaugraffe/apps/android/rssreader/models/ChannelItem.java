package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models;

import android.os.*;

public class ChannelItem extends RSS implements Parcelable {

    public static final String CHANNEL = "url";
    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "pubDate";
    public static final String GUID = "guid";

    private String channel = null;
    private String guid = null;
    private String image = null;

    public static final Creator<RSS> CREATOR = new Creator<RSS>() {

        @Override
        public ChannelItem createFromParcel(Parcel in) {

            return new ChannelItem(in);
        }

        @Override
        public ChannelItem[] newArray(int size) {

            return new ChannelItem[size];
        }
    };

    public ChannelItem(String title, String description, String date, String link, String channel, String guid, String image) {
        super(title, description, date, link);

        this.channel = channel;
        this.guid = guid;
        this.image = image;
    }

    public ChannelItem() {
        this(null, null, null, null, null, null, null);

    }

    public ChannelItem(ChannelItem item) {
        this(item.title, item.description, item.date, item.link, item.channel, item.guid, item.image);

    }

    public ChannelItem(Parcel in) {

        title = in.readString();
        description = in.readString();
        date = in.readString();
        link = in.readString();
        channel = in.readString();
        guid = in.readString();
        image = in.readString();
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
        dest.writeString(image);
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

    public String getImage() {

        return image;
    }

    public void setImage(String image) {

        this.image = image;
    }
}
