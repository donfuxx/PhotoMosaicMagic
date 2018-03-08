package com.appham.photomosaicmagic;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Thomas Fuchs-Martin <t.fuchsmartin@gmail.com>
 */

public abstract class HttpUtils {

    private static final int READ_TIMEOUT = 3000;
    private static final int CONNECT_TIMEOUT = 1500;

    @NonNull
    public static URL getServerUrl(@NonNull String server,
                                   int width, int height,
                                   @NonNull String color) throws MalformedURLException {
        return new URL(server + width + "/" + height + "/" + color);
    }

    @WorkerThread
    @NonNull
    public static HttpURLConnection getHttpConnection(@NonNull URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

        return connection;
    }

}
