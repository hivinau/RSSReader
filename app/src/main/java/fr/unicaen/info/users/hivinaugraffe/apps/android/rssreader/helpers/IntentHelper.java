package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers;

import android.os.*;
import android.content.*;

public class IntentHelper {

    public static void sendToActivity(Context context, Class<?> clazz, String action, Bundle bundle) {

        Intent intent = new Intent(context, clazz);

        intent.setAction(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if(bundle != null) {

            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }

    public static void sentToService(Context context, Class<?> clazz, String action, Bundle bundle) {

        Intent intent = new Intent(context, clazz);

        intent.setAction(action);

        if(bundle != null) {

            intent.putExtras(bundle);
        }

        context.startService(intent);
    }
}
