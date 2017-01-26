package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models;

public class RSS {

    protected int id;
    protected String title = null;
    protected String description = null;
    protected String date = null;
    protected String link = null;

    public RSS(String title, String description, String date, String link) {

        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
    }

    public RSS() {
        this(null, null, null, null);

    }

    public RSS(RSS rss) {
        this(rss.title, rss.description, rss.date, rss.link);

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

        if(obj instanceof RSS) {

            RSS rss = (RSS) obj;

            same = rss.title.equals(title) && rss.date.equals(date) && rss.description.equals(description);
        }

        return same;
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

        this.link = link;
    }
}
