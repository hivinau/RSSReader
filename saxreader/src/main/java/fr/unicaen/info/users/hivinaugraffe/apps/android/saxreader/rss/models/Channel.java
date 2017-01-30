package fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models;

import java.util.*;
import android.os.*;

public class Channel extends RSSBase implements Parcelable {

    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "pubDate";

    private final Set<Item> items;

    public static final Parcelable.Creator<Channel> CREATOR = new Parcelable.Creator<Channel>() {

        @Override
        public Channel createFromParcel(Parcel in) {

            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {

            return new Channel[size];
        }
    };

    public Channel(String title, String description, String date, String link) {
        super(title, description, date, link);

        items = new HashSet<>();
    }

    public Channel() {
        this(null, null, null, null);

    }

    public Channel(Channel channel) {
        this(channel.title, channel.description, channel.date, channel.link);

    }

    public Channel(Parcel in) {
        this(in.readString(), in.readString(), in.readString(), in.readString());

        List<Item> items = new ArrayList<>();

        in.readTypedList(items, Item.CREATOR);

        for(Item item: items) {

            this.items.add(item);
        }

    }

    public void addItem(Item item) {

        items.add(item);
    }

    public void removeItem(Item item) {

        items.remove(item);
    }

    public ArrayList<Item> getItems() {

        ArrayList<Item> items = new ArrayList<>();

        for(Item item: this.items) {

            items.add(item);
        }

        return items;
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

        boolean same = false;

        try {

            semaphore.acquire();

            if(obj instanceof Channel) {

                Channel channel = (Channel) obj;

                if(title != null) {

                    same = title.equalsIgnoreCase(channel.title);
                }

                if(description != null) {

                    same = same && description.equalsIgnoreCase(channel.description);
                }

                if(date != null) {

                    same = same && date.equalsIgnoreCase(channel.date);
                }

                if(link != null) {

                    same = same && link.equalsIgnoreCase(channel.link);
                }

                same = same && items.size() == channel.getItems().size();
            }

        } catch (Exception ignored) { }
        finally {

            semaphore.release();
        }

        return same;
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

        List<Item> items = new ArrayList<>();

        for(Item item: this.items) {

            items.add(item);
        }

        dest.writeTypedList(items);
    }

}
