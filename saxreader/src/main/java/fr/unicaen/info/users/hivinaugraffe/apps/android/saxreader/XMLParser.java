package fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader;

import java.io.*;
import javax.xml.parsers.*;

public class XMLParser {

    public static void parse(InputStream stream, SaxHandler.ElementListener listener) {

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();

        if(parserFactory != null) {

            try {

                SAXParser parser = parserFactory.newSAXParser();

                parser.parse(stream, new SaxHandler(listener));

            } catch(Exception exception) {

                exception.printStackTrace();
            }

        }
    }
}
