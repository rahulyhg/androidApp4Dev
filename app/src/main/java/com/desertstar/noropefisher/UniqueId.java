package com.desertstar.noropefisher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.util.UUID;

/**
 * Created by Iker Redondo on 2/13/2018.
 */

public class UniqueId {

    private static String sID = null;
    private static final String SHARED_PREF_KEY = "rf";
    private static final String ID_KEY = "id";
//    private static  boolean firstTime=true;

    public synchronized static String id(Context context) {
        if (sID == null) {
            SharedPreferences pref = context.getSharedPreferences(
                    SHARED_PREF_KEY, 0);

            sID = pref.getString(ID_KEY, "");

            if (sID == "") {
                sID = generateAndStoreUserId(pref);
//                firstTime = false;
            }
        }
        return sID;
    }

//    public synchronized  static boolean isFirstTime(){
//        return firstTime;
//    }

    private synchronized static String generateAndStoreUserId(SharedPreferences pref) {
        String id = UUID.randomUUID().toString();
        byte [] input = id.getBytes();
        Base64.encodeToString(input, Base64.NO_PADDING);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ID_KEY, id);
        editor.commit();
        return id;
    }
    public static String getID(){
        return sID;
    }

}