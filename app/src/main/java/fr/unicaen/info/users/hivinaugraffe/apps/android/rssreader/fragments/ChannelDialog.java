package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import android.os.*;
import android.view.*;
import android.widget.*;
import android.support.v4.app.*;
import android.support.annotation.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;

public class ChannelDialog extends DialogFragment {

    private Message message = null;
    private EditText feedEditText = null;
    private Button cancelButton = null;
    private Button addButton = null;

    private Button.OnClickListener cancelOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            send();

            dismiss();
        }
    };

    private Button.OnClickListener startOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if(feedEditText.getText().length() > 0) {

                if(message != null) {

                    message.obj = feedEditText.getText().toString();
                    send();

                    dismiss();
                }
            }
        }
    };

    public static ChannelDialog getInstance() {

        return new ChannelDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.feed_dialog, container, false);

        feedEditText = (EditText) root.findViewById(R.id.feed_edittext);
        cancelButton = (Button) root.findViewById(R.id.feed_cancel_button);
        addButton = (Button) root.findViewById(R.id.feed_add_button);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setTitle(getString(R.string.feed_dialog_title));
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        //debug:
        feedEditText.setText("http://www.lemonde.fr/videos/rss_full.xml");

    }

    @Override
    public void onResume() {
        super.onResume();

        cancelButton.setOnClickListener(cancelOnClickListener);
        addButton.setOnClickListener(startOnClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelButton.setOnClickListener(null);
        addButton.setOnClickListener(null);
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
