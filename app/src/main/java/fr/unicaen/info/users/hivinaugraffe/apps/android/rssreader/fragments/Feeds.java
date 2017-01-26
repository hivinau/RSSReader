package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments;

import android.os.*;
import android.net.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.support.annotation.*;
import android.support.v7.widget.helper.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.controllers.*;

public class Feeds extends Fragment implements RssRecyclerAdapter.OnCardClickListener {

    private RecyclerView recyclerView = null;
    private RssRecyclerAdapter adapter = null;
    private RelativeLayout welcomeLayout = null;

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case HandlerConstant.ITEM_AVAILABLE:

                    if(msg.obj != null && msg.obj instanceof RSSItem) {

                        handleItem((RSSItem) msg.obj);
                    }
                    break;
                case HandlerConstant.ITEM_AVAILABLE_AT_POSITION:

                    if(msg.obj != null && msg.obj instanceof Bundle) {

                        Bundle bundle = (Bundle) msg.obj;

                        RSSItem item = bundle.getParcelable(BundleConstant.ITEM);
                        int position = bundle.getInt(BundleConstant.POSITION);

                        if(item != null) {

                            handleItemAtPosition(item, position);
                        }
                    }
                    break;
                case HandlerConstant.DATABASE_DROPPED:

                    adapter.clear();
                    break;
                default:
                    break;
            }
        }
    };

    private ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            RSSItem item = adapter.getItemAt(position);

            Bundle bundle = new Bundle();
            bundle.putParcelable(BundleConstant.ITEM, item);
            bundle.putInt(BundleConstant.POSITION, position);

            IntentHelper.sendToActivity(getActivity(), MainActivity.class, Action.DATABASE_REQUESTED_TO_REMOVE_ITEM, bundle);

            adapter.remove(item, false);
            adapter.notifyItemRemoved(position);

            welcomeLayout.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    };

    private RecyclerView.AdapterDataObserver itemsObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();

            welcomeLayout.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);

            welcomeLayout.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);

            welcomeLayout.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);

            welcomeLayout.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);

            welcomeLayout.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);

            welcomeLayout.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.feeds, container, false);

        welcomeLayout = (RelativeLayout) root.findViewById(R.id.welcome_relative_layout);
        recyclerView = (RecyclerView) root.findViewById(R.id.feeds_recycler_view);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new RssRecyclerAdapter();

        ((MainActivity) getActivity()).setHandler(handler);
    }

    @Override
    public void onStart() {
        super.onStart();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);

        IntentHelper.sentToService(getActivity(), DatabaseService.class, Action.DATABASE_REQUESTED_TO_PULL_ITEM, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.registerAdapterDataObserver(itemsObserver);
        adapter.addOnCardClickListener(this);
        recyclerView.setAdapter(adapter);

        welcomeLayout.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
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

        RSSItem item = adapter.getItemAt(position);

        String link = item.getLink();

        if(link != null) {

            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            startActivity(intent);
        }
    }

    private void handleItem(final RSSItem item) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                adapter.add(item);
                welcomeLayout.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });

    }

    private void handleItemAtPosition(final RSSItem item, final int position) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                adapter.add(position, item, false);
                adapter.notifyItemInserted(position);
                recyclerView.scrollToPosition(position);
            }
        });
    }
}
