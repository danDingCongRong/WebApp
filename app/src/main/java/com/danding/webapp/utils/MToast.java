package com.danding.webapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by baidu on 15/9/25.
 */
public class MToast {

    private static boolean debug = true;

    public static void show(Context context,String msg){
        if (debug){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        }
    }
}
