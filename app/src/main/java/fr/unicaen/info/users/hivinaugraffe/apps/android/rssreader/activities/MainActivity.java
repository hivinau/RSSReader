package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities;

import java.util.*;

import android.annotation.SuppressLint;
import android.os.*;
import android.view.*;
import android.content.*;
import android.content.res.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.support.v4.content.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.models.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.helpers.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.fragments.AlertDialog;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private static final int FEEDS_FRAGMENT = 0;
    private static final int USER_PREFERENCES_FRAGMENT = 1;

    private DrawerLayout drawerLayout = null;
    private Toolbar toolbar = null;
    private ActionBarDrawerToggle toggle = null;
    private NavigationView navigationView = null;
    private CoordinatorLayout coordinatorLayout = null;
    private Message message = null;
    private boolean rssServiceConnectionEstablished = false;
    private boolean databaseServiceConnectionEstablished = false;
    private boolean isDialog = false;

    private final ServiceConnection rssConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            RssRequestService.RssBinder binder = (RssRequestService.RssBinder) service;

            RssRequestService requestService = binder.getRequestService();
            requestService.setContext(MainActivity.this);

            rssServiceConnectionEstablished = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            rssServiceConnectionEstablished = false;
        }
    };

    private final ServiceConnection databaseConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            DatabaseService.DatabaseBinder binder = (DatabaseService.DatabaseBinder) service;

            DatabaseService requestService = binder.getDatabaseService();
            requestService.setContext(MainActivity.this);

            databaseServiceConnectionEstablished = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            databaseServiceConnectionEstablished = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.action_bar));
        navigationView.setItemIconTintList(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);

        Intent rssIntent= new Intent(MainActivity.this, RssRequestService.class);
        bindService(rssIntent, rssConnection, Context.BIND_AUTO_CREATE);

        Intent databaseIntent= new Intent(MainActivity.this, DatabaseService.class);
        bindService(databaseIntent, databaseConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(FragmentHelper.fragmentsCount(this) == 0) {

            FragmentHelper.addFragment(this, Feeds.class.getName(), R.id.fragments_container, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        drawerLayout.removeDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(null);

        if(rssServiceConnectionEstablished) {

            unbindService(rssConnection);
        }

        if(databaseServiceConnectionEstablished) {

            unbindService(databaseConnection);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();

        final Bundle bundle;

        switch (action) {
            case Action.THROW_CHANNEL:

                bundle = intent.getExtras();

                if(bundle != null) {

                    RSSChannel channel = bundle.getParcelable(BundleConstant.CHANNEL);

                    if (channel != null) {

                        String title = channel.getTitle();
                        String link = channel.getLink();

                        if(title != null && link != null) {

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

                            String titlesKey = getString(R.string.channels_title);
                            String linksKey = getString(R.string.channels_summary);

                            Set<String> titles = new HashSet<>(preferences.getStringSet(titlesKey, new HashSet<String>()));
                            Set<String> links = new HashSet<>(preferences.getStringSet(linksKey, new HashSet<String>()));

                            SharedPreferences.Editor editor = preferences.edit();

                            titles.add(title);
                            links.add(link);

                            editor.putStringSet(titlesKey, titles);
                            editor.putStringSet(linksKey, links);

                            editor.apply();
                        }
                    }
                }

                break;
            case Action.THROW_ITEM:

                bundle = intent.getExtras();

                if(bundle != null) {

                    RSSItem item = bundle.getParcelable(BundleConstant.ITEM);

                    if (item != null) {

                        handle(HandlerConstant.ITEM_AVAILABLE, item);

                        String source = bundle.getString(BundleConstant.ITEM_SOURCE);

                        if(source != null && source.equals(RssRequestService.class.getName())) {

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                            boolean offline = preferences.getBoolean("user_offline", false);

                            if(offline) {

                                bundle.putParcelable(BundleConstant.ITEM_TO_PUSH_ON_DATABASE, item);

                                IntentHelper.sentToService(this, DatabaseService.class, Action.DATABASE_REQUESTED_TO_PUSH_ITEM, bundle);
                            }
                        }
                    }
                }

                break;
            case Action.THROW_ERROR:

                bundle = intent.getExtras();

                if(bundle != null) {

                    showError(bundle.getString(BundleConstant.ERROR_OCCURED, getString(R.string.feed_add_failed)));
                }
                break;
            case Action.DATABASE_REQUESTED_TO_REMOVE_ITEM:

                bundle = intent.getExtras();

                if(bundle != null) {

                    RSSItem item = bundle.getParcelable(BundleConstant.ITEM);
                    int position = bundle.getInt(BundleConstant.POSITION);

                    if(item != null) {

                        removeItem(item, position);
                    }
                }
                break;
            case Action.THROW_DATABASE_DROPPED_STATE:

                handle(HandlerConstant.DATABASE_DROPPED, null);
                break;
            case Action.DATABASE_HANDLE_EVENT:

                bundle = intent.getExtras();

                if(bundle != null) {

                    boolean state = bundle.getBoolean(BundleConstant.DATABASE_ACTION_SUCCEED, false);

                    if(!state) {

                        showError(bundle.getString(BundleConstant.DATABASE_ERROR_OCCURED, getString(R.string.database_error_occured)));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if(!FragmentHelper.popBackStack(this)) {

            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        boolean selected = true;

        switch (item.getItemId()) {
            case R.id.feeds_menu_item:

                FragmentHelper.popToFragment(this, MainActivity.FEEDS_FRAGMENT);

                isDialog = false;
                break;
            case R.id.settings_menu_item:

                if(FragmentHelper.fragmentExist(this, MainActivity.USER_PREFERENCES_FRAGMENT)) {

                    FragmentHelper.popToFragment(this, MainActivity.USER_PREFERENCES_FRAGMENT);

                } else {

                    FragmentHelper.addFragment(this, UserPreferences.class.getName(), R.id.fragments_container, null);
                }

                isDialog = false;
                break;
            default:

                selected = false;
                break;
        }

        drawerLayout.closeDrawers();

        return selected;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {

        if(!isDialog) {

            final int lastFragment = FragmentHelper.fragmentsCount(this) - 1;

            switch (lastFragment) {
                case MainActivity.FEEDS_FRAGMENT:

                    setTitle(getString(R.string.feeds_title));
                    break;
                case MainActivity.USER_PREFERENCES_FRAGMENT:

                    setTitle(getString(R.string.user_preferences_title));
                    break;
                default:
                    break;
            }
        }
    }

    public void addChannel(View view) {

        FragmentManager manager = getSupportFragmentManager();

        Fragment fragment = manager.findFragmentByTag(ChannelDialog.class.getName());

        FragmentTransaction transaction = manager.beginTransaction();

        if (fragment != null) {

            transaction.remove(fragment);
        }

        transaction.addToBackStack(null);

        isDialog = true;

        final ChannelDialog dialog = ChannelDialog.getInstance();
        dialog.setHandler(new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case HandlerConstant.DIALOG_BUTTON_CLICKED:

                        if(msg.obj != null && msg.obj instanceof String) {

                            Bundle bundle = new Bundle();
                            bundle.putString(BundleConstant.URL, (String) msg.obj);

                            IntentHelper.sentToService(MainActivity.this, RssRequestService.class, Action.HTTP_REQUEST_WITH_URL, bundle);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        dialog.show(transaction, ChannelDialog.class.getName());
    }

    public void askToDeletePreference(final String key) {

        AlertDialog dialog = AlertDialog.newInstance(getString(R.string.channel_delete, key));
        dialog.setHandler(new Handler(Looper.getMainLooper()) {

            @SuppressLint("CommitPrefEdits")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case HandlerConstant.DIALOG_BUTTON_CLICKED:

                        handle(HandlerConstant.CHANNEL_REMOVED, key);
                        break;
                    default:
                        break;
                }
            }
        });

        dialog.show(getSupportFragmentManager(), AlertDialog.class.getName());
    }

    public void setHandler(Handler handler) {

        if(handler != null) {

            message = handler.obtainMessage();
        }
    }

    private void handle(int action, Object object) {

        if(message != null) {

            if(object != null) {

                message.obj = object;
            }

            message.what = action;

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

    private void showError(final String error) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Snackbar snackbar = Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void removeItem(RSSItem item, int position) {

        final Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.undo_message), Snackbar.LENGTH_LONG);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(BundleConstant.ITEM, item);
        bundle.putInt(BundleConstant.POSITION, position);

        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);

                IntentHelper.sentToService(MainActivity.this, DatabaseService.class, Action.DATABASE_REQUESTED_TO_REMOVE_ITEM, bundle);
            }
        });

        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                handle(HandlerConstant.ITEM_AVAILABLE_AT_POSITION, bundle);
            }
        });

        snackbar.show();
    }
}
