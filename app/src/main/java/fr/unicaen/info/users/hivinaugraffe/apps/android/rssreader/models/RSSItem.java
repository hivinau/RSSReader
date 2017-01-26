package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models;

import android.os.*;

public class RSSItem extends RSS implements Parcelable {

    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "pubDate";
    public static final String GUID = "guid";

    private String guid = null;
    private String image = null;

    public static final Creator<RSS> CREATOR = new Creator<RSS>() {

        @Override
        public RSSItem createFromParcel(Parcel in) {

            return new RSSItem(in);
        }

        @Override
        public RSSItem[] newArray(int size) {

            return new RSSItem[size];
        }
    };

    public RSSItem(String title, String description, String date, String link, String guid, String image) {
        super(title, description, date, link);

        this.guid = guid;
        this.image = image;
    }

    public RSSItem() {
        this(null, null, null, null, null, null);

    }

    public RSSItem(RSSItem RSSItem) {
        this(RSSItem.title, RSSItem.description, RSSItem.date, RSSItem.link, RSSItem.guid, RSSItem.image);

    }

    public RSSItem(Parcel in) {

        title = in.readString();
        description = in.readString();
        date = in.readString();
        link = in.readString();
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
        dest.writeString(guid);
        dest.writeString(image);
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
