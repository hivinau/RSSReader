package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.controllers;

import java.util.*;
import android.view.*;
import android.widget.*;
import android.support.v7.widget.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;

public class RssRecyclerAdapter extends RecyclerView.Adapter<RssRecyclerAdapter.ViewHolder> {

    private final List<Item> items;
    private final Set<OnCardClickListener> cardClickListeners;

    public RssRecyclerAdapter() {

        items = new ArrayList<>();
        cardClickListeners = new HashSet<>();
    }

    public void add(Item item) {

        add(items.size(), item, true);
    }

    public void add(int position, Item item, boolean notify) {

        if(!items.contains(item)) {

            items.add(position, item);

            if(notify) {

                notifyDataSetChanged();
            }
        }
    }

    @SuppressWarnings({"unused"})
    public void remove(Item item) {

        remove(item, true);
    }

    public void remove(Item item, boolean notify) {

        items.remove(item);

        if(notify) {

            notifyDataSetChanged();
        }
    }

    @SuppressWarnings({"unused"})
    public void clear() {

        items.clear();
        notifyDataSetChanged();
    }

    public Item getItemAt(int position) {

        Item item = null;

        Iterator<Item> iterator = items.iterator();

        int index = 0;
        while (iterator.hasNext()) {

            Item a = iterator.next();

            if(index == position) {

                item = a;
                break;
            }

            index++;
        }

        return item;
    }

    public void addOnCardClickListener(OnCardClickListener listener) {

        cardClickListeners.add(listener);
    }

    public void removeOnCardClickListener(OnCardClickListener listener) {

        cardClickListeners.remove(listener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View root = inflater.inflate(R.layout.rss, parent, false);

        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        final ViewHolder viewHolder = holder;

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                for(OnCardClickListener listener: cardClickListeners) {

                    listener.onClick(v, viewHolder.getAdapterPosition());
                }
            }
        });

        Item[] items = this.items.toArray(new Item[this.items.size()]);

        if(position >= 0 && position < items.length) {

            Item item = items[position];

            if(item != null) {

                String title = item.getTitle();
                String date = item.getDate();
                String description = item.getDescription();

                if(title != null && date != null && description != null) {

                    viewHolder.titleTextView.setText(title);
                    viewHolder.dateTextView.setText(date);
                    viewHolder.descriptionTextView.setText(description);
                }
            }

        }
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTextView;
        private final TextView dateTextView;
        private final TextView descriptionTextView;

        ViewHolder(View root) {
            super(root);

            titleTextView = (TextView) root.findViewById(R.id.rss_title_textview);
            dateTextView = (TextView) root.findViewById(R.id.rss_date_textview);
            descriptionTextView = (TextView) root.findViewById(R.id.rss_description_textview);
        }
    }

    public interface OnCardClickListener {

        void onClick(View view, int position);
    }
}
