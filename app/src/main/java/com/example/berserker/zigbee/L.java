package com.example.berserker.zigbee;

import android.util.Log;

public class L {

    private static final String TAG = "Imooc_okhttp";
    private static boolean debug = true;

    public static void e(String msg){
        if(debug)
            Log.e(TAG,msg);
    }
}
