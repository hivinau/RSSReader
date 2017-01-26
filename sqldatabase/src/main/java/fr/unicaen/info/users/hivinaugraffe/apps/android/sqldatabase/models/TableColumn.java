package fr.unicaen.info.users.hivinaugraffe.apps.android.sqldatabase.models;

public class TableColumn {

    private final String name;
    private final String type;

    /**
     * Constructor : create a new instance of table column
     * @param name name of column
     * @param type type of column
     */
    public TableColumn(String name, String type) {

        this.name = name;
        this.type = type;
    }

    /**
     * Get name of colum
     * @return {@link String} name of colum
     */
    public String getName() {

        return name;
    }

    /**
     * Get type of colum
     * @return {@link String} type of colum
     */
    public String getType() {

        return type;
    }

}
