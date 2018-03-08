package com.appham.photomosaicmagic.async;

import android.os.AsyncTask;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public abstract class PooledTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private static final String TAG = "PooledTask";

    /**
     * Execute the async task in parallel on THREAD_POOL_EXECUTOR
     *
     * @return AsyncTask
     */
    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> executePool(Params... params) {

        if (THREAD_POOL_EXECUTOR instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadPoolExecutor = ((ThreadPoolExecutor) THREAD_POOL_EXECUTOR);

            // increase pool sizes if really needed
            if (threadPoolExecutor.getMaximumPoolSize() <= threadPoolExecutor.getPoolSize()) {
                threadPoolExecutor.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize() + 5);
//                Log.d(TAG, " max pool: " + threadPoolExecutor.getMaximumPoolSize() +
//                        " current pool: " + threadPoolExecutor.getPoolSize() +
//                        " queue size: " + threadPoolExecutor.getQueue().size());
            } else if (threadPoolExecutor.getCorePoolSize() <= threadPoolExecutor.getQueue().size()) {
                threadPoolExecutor.setCorePoolSize(threadPoolExecutor.getCorePoolSize() + 2);
            }

            return executeOnExecutor(threadPoolExecutor, params);
        }
        return executeOnExecutor(THREAD_POOL_EXECUTOR, params);

    }
}
