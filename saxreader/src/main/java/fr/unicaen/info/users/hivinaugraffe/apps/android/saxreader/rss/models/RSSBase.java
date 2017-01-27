package fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models;

public class RSSBase {

    protected int id;
    protected String title = null;
    protected String description = null;
    protected String date = null;
    protected String link = null;

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

        if(obj instanceof RSSBase) {

            RSSBase base = (RSSBase) obj;

            if(title != null) {

                same = title.equals(base.title);
            }

            if(description != null) {

                same = same && description.equals(base.description);
            }

            if(date != null) {

                same = same && date.equals(base.date);
            }

            if(link != null) {

                same = same && link.equals(base.link);
            }
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

        if(link != null) {

            link = link.trim();
        }

        this.link = link;
    }
}
