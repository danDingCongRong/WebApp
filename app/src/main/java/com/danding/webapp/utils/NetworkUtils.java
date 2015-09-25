package com.danding.webapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by baidu on 15/9/25.
 */
public class NetworkUtils {

    private Context mContext;

    private static NetworkUtils mInstance;

    private NetworkUtils(Context context) {
        mContext = context;
    }

    public static NetworkUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (NetworkUtils.class) {
                if (mInstance == null) {
                    mInstance = new NetworkUtils(context);
                }
            }
        }

        return mInstance;
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (networkInfos != null) {
                for (int i = 0; i < networkInfos.length; ++i) {
                    NetworkInfo networkInfo = networkInfos[i];
                    if (networkInfo != null && networkInfo.isConnected()) {
                        return true;
                    }
                }
            }

        }

        return false;
    }
}
