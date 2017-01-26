package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import android.os.*;
import android.app.*;
import android.content.*;
import android.support.annotation.*;
import android.support.v4.app.DialogFragment;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;

public class AlertDialog extends DialogFragment {

    private static final String TITLE = "title";

    private Message message = null;

    public static AlertDialog newInstance(String title) {

        AlertDialog frag = new AlertDialog();

        Bundle args = new Bundle();

        args.putString(AlertDialog.TITLE, title);

        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString(AlertDialog.TITLE);

        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        send();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })

                .create();
    }

    public void setHandler(Handler handler) {

        if(handler != null) {

            message = handler.obtainMessage();
        }
    }

    private void send() {

        if(message != null) {

            message.what = HandlerConstant.DIALOG_BUTTON_CLICKED;

            Handler handler = message.getTarget();

            if(handler != null) {

                Looper looper = handler.getLooper();

                if(looper != null) {

                    if(looper == Looper.getMainLooper()) {

                        handler.handleMessage(message);

                    } else {

                        message.sendToTarget();
                    }
                }
            }
        }
    }
}
