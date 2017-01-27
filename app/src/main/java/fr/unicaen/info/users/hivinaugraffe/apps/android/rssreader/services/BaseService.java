package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services;

import java.util.*;
import android.os.*;
import android.app.*;
import android.content.*;
import java.util.concurrent.*;
import android.support.annotation.*;
import fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.globals.*;

public class BaseService extends Service {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_THREADS = 8;

    protected ExecutorService executor;
    protected List<Future<?>> threads;
    protected IBinder binder = null;
    protected Context context = null;
    protected IntentFilter filter = null;

    @Override
    public void onCreate() {
        super.onCreate();

        executor= new ThreadPoolExecutor(BaseService.CORE_POOL_SIZE,
                BaseService.MAXIMUM_THREADS,
                1000, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        threads = new ArrayList<>();

        filter = new IntentFilter();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (Iterator<Future<?>> iterator = threads.iterator(); iterator.hasNext();) {

            Future<?> thread = iterator.next();

            if(!thread.isCancelled()) {

                thread.cancel(true);
            }

            iterator.remove();
        }

        executor.shutdownNow();
    }

    public void setContext(Context context) {

        this.context = context;
    }

    protected void sendError(String error) {

        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putString(BundleConstant.ERROR, error);

        intent.setAction(Action.THROW_ERROR);
        intent.putExtras(bundle);

        sendBroadcast(intent);
    }
}
