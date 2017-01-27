package fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader;

import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class SaxHandler extends DefaultHandler {

    private final ElementListener listener;
    private final StringBuilder values;

    private int item;
    private final Map<String, String> channelContent;
    private final Map<String, String> itemContent;

    private String node = null;
    private boolean itemParsing = false;

    public SaxHandler(ElementListener listener) {
        super();

        this.listener = listener;
        values = new StringBuilder();

        channelContent = new HashMap<>();
        itemContent = new HashMap<>();

        item = 0;
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

                if(item > 0 && listener != null) {

                    listener.onItemParsed(channelContent, itemContent);
                }

                item++;
                itemContent.clear();
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

                itemContent.put(this.node, values.toString());
            } else {

                channelContent.put(this.node, values.toString());
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

            listener.onChannelParsed(channelContent);
        }
    }

    public interface ElementListener {

        void onChannelParsed(final Map<String, String> channel);
        void onItemParsed(final Map<String, String> channel, final Map<String, String> item);
    }
}
