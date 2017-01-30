package fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models;

import java.util.concurrent.Semaphore;

public class RSSBase {

    protected int id;
    protected String title = null;
    protected String description = null;
    protected String date = null;
    protected String link = null;

    protected final Semaphore semaphore = new Semaphore(1);

    public RSSBase(String title, String description, String date, String link) {

        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
    }

    public RSSBase() {
        this(null, null, null, null);

    }

    public RSSBase(RSSBase base) {
        this(base.title, base.description, base.date, base.link);

    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getLink() {

        return link;
    }

    public void setLink(String link) {

        if(link != null) {

            link = link.trim();
        }

        this.link = link;
    }
}
