package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models;

import android.os.*;

public class RSSChannel extends RSS implements Parcelable {

    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "pubDate";

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

    }

    public RSSChannel() {
        this(null, null, null, null);

    }

    public RSSChannel(RSSItem RSSItem) {
        this(RSSItem.title, RSSItem.description, RSSItem.date, RSSItem.link);

    }

    public RSSChannel(Parcel in) {

        title = in.readString();
        description = in.readString();
        date = in.readString();
        link = in.readString();
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
    }
}
