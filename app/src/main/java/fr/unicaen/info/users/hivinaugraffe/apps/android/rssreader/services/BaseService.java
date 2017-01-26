package fr.unicaen.info.users.hivinaugraffe.apps.android.rssreader.services;

import java.util.*;
import android.os.*;
import android.app.*;
import android.content.*;
import java.util.concurrent.*;
import android.support.annotation.*;

public class BaseService extends Service {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_THREADS = 8;

    protected ExecutorService executor;
    protected List<Future<?>> threads;
    protected IBinder binder = null;
    protected Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();

        executor= new ThreadPoolExecutor(BaseService.CORE_POOL_SIZE,
                BaseService.MAXIMUM_THREADS,
                1000, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        threads = new ArrayList<>();
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
}