package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import java.util.*;
import android.os.*;
import android.net.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.support.annotation.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.saxreader.rss.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.controllers.*;

public class Items extends Fragment implements RssRecyclerAdapter.OnCardClickListener {

    private RecyclerView recyclerView = null;
    private RssRecyclerAdapter adapter = null;
    private TextView noItemsTextView = null;

    private RecyclerView.AdapterDataObserver itemsObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();

            noItemsTextView.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);

            noItemsTextView.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);

            noItemsTextView.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);

            noItemsTextView.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);

            noItemsTextView.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.items, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noItemsTextView = (TextView) view.findViewById(R.id.no_items_textview);
        recyclerView = (RecyclerView) view.findViewById(R.id.items_recycler_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new RssRecyclerAdapter();

        Bundle bundle = getArguments();

        if(bundle != null) {

            List<Item> items = bundle.getParcelableArrayList(BundleConstant.ITEMS);

            if(items != null) {

                for(Item item: items) {

                    adapter.add(item);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.registerAdapterDataObserver(itemsObserver);
        adapter.addOnCardClickListener(this);
        recyclerView.setAdapter(adapter);

        noItemsTextView.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        adapter.unregisterAdapterDataObserver(itemsObserver);
        adapter.removeOnCardClickListener(this);
        recyclerView.setAdapter(null);
    }

    @Override
    public void onStop() {
        super.onStop();

        recyclerView.setLayoutManager(null);
    }

    @Override
    public void onClick(View view, int position) {

        Item item = adapter.getItemAt(position);

        String link = item.getLink();

        if(link != null) {

            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            startActivity(intent);
        }
    }
}
