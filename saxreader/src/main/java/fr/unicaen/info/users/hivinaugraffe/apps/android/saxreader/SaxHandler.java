package fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;

public class SaxHandler extends DefaultHandler {

    private final ElementListener listener;
    private final StringBuilder values;

    private final Channel channel;

    private Item item = null;

    private String node = null;
    private boolean itemParsing = false;

    public SaxHandler(ElementListener listener) {
        super();

        this.listener = listener;
        values = new StringBuilder();

        channel = new Channel();
        item = new Item();
    }

    @Override
    public void startElement(String namespace, String localName, String markName, Attributes attributes) throws SAXException {
        super.startElement(namespace, localName, markName, attributes);

        if(localName.length() > 0) {

            String node = localName.trim();

            if(node.equalsIgnoreCase("channel")) {

                itemParsing = false;

            } else if(node.equalsIgnoreCase("item")) {

                itemParsing = true;

                channel.addItem(item);
                item = new Item();
            }

            this.node = node;
        }

        values.setLength(0);
    }

    @Override
    public void endElement(String namespace, String localName, String markName) throws SAXException {
        super.endElement(namespace, localName, markName);

        if(localName.length() > 0) {

            if(itemParsing) {

                parseItem(this.node, values.toString());
            } else {

                parseChannel(this.node, values.toString());
            }
        }
    }

    @Override
    public void characters(char[] characters, int startIndex, int length) throws SAXException {
        super.characters(characters, startIndex, length);

        this.values.append(characters, startIndex, length);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();

        if(listener != null) {

            listener.onParsingFinished(channel);
        }
    }

    private void parseChannel(String node, String value) {

        if(node.equalsIgnoreCase(Channel.TITLE)) {

            channel.setTitle(value);

        } else if(node.equalsIgnoreCase(Channel.DESCRIPTION)) {

            channel.setDescription(value);

        } else if(node.equalsIgnoreCase(Channel.DATE)) {

            channel.setDate(value);

        } else if(node.equalsIgnoreCase(Channel.LINK)) {

            channel.setLink(value);

        }
    }

    private void parseItem(String node, String value) {

        if(node.equalsIgnoreCase(Item.TITLE)) {

            item.setTitle(value);

        } else if(node.equalsIgnoreCase(Item.DESCRIPTION)) {

            item.setDescription(value);

        } else if(node.equalsIgnoreCase(Item.DATE)) {

            item.setDate(value);

        } else if(node.equalsIgnoreCase(Item.LINK)) {

            item.setLink(value);

        } else if(node.equalsIgnoreCase(Item.GUID)) {

            item.setGuid(value);
        }
    }

    public interface ElementListener {

        void onParsingFinished(final Channel channel);
    }
}
