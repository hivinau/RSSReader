package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.activities;

import android.os.*;
import android.view.*;
import android.content.*;
import android.preference.*;
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
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.R;
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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.action, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean offline = preferences.getBoolean("user_offline", false);

        if(offline) {

            String value = preferences.getString("user_update_mode", "0");
            menu.findItem(R.id.refresh).setVisible(value.equals("1"));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:

                Intent intent = new Intent();

                Bundle bundle = new Bundle();
                bundle.putBoolean(BundleConstant.FORCE_REQUEST, true);

                intent.setAction(Action.PULL_CHANNELS);
                intent.putExtras(bundle);

                sendBroadcast(intent);
                return true;
        }

        return toggle.onOptionsItemSelected(item);
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

        Intent rssIntent = new Intent(MainActivity.this, RssRequestService.class);
        bindService(rssIntent, rssConnection, Context.BIND_AUTO_CREATE);

        Intent databaseIntent = new Intent(MainActivity.this, DatabaseService.class);
        bindService(databaseIntent, databaseConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(FragmentHelper.findFragmentByTag(this, Feeds.class.getName()) == null) {

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
            case Action.THROW_ERROR:

                bundle = intent.getExtras();

                if(bundle != null) {

                    showError(bundle.getString(BundleConstant.ERROR_OCCURED, getString(R.string.feed_add_failed)));
                }
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

                if(FragmentHelper.findFragmentByTag(this, UserPreferences.class.getName()) != null) {

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

                            Intent intent = new Intent();

                            Bundle bundle = new Bundle();
                            bundle.putString(BundleConstant.URL, (String) msg.obj);

                            intent.setAction(Action.HTTP_REQUEST_WITH_URL);
                            intent.putExtras(bundle);

                            sendBroadcast(intent);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        dialog.show(transaction, ChannelDialog.class.getName());
    }

    public void askToDeletePreference(final String url) {

        AlertDialog dialog = AlertDialog.newInstance(getString(R.string.channel_delete, url));
        dialog.setHandler(new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case HandlerConstant.DIALOG_BUTTON_CLICKED:

                        Intent intent = new Intent();

                        Bundle bundle = new Bundle();
                        bundle.putString(BundleConstant.URL, url);

                        intent.setAction(Action.DELETE_FEED);
                        intent.putExtras(bundle);

                        sendBroadcast(intent);
                        break;
                    default:
                        break;
                }
            }
        });

        dialog.show(getSupportFragmentManager(), AlertDialog.class.getName());
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

}
