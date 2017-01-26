package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.views;

import android.view.*;
import android.util.*;
import android.content.*;
import android.support.v7.preference.*;

public class MarqueePreference extends Preference implements View.OnLongClickListener {

    private OnPreferenceLongClickListener longClickListener;

    public MarqueePreference(Context context) {
        super(context);

        layout();
    }

    public MarqueePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        layout();
    }

    public MarqueePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        layout();
    }

    public MarqueePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        layout();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.itemView.setOnLongClickListener(this);

        holder.findViewById(android.R.id.title).setSelected(true);
    }

    public void setOnPreferenceLongClickListener(OnPreferenceLongClickListener listener) {

        longClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {

        boolean consumed = false;

        if(longClickListener != null) {

            consumed = longClickListener.onLongClicked(this, getKey());
        }

        return consumed;
    }

    public interface OnPreferenceLongClickListener {

        boolean onLongClicked(Preference preference, String key);
    }

    private void layout() {

        setLayoutResource(R.layout.preference);
    }
}
