package com.desertstar.noropefisher;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by Iker Redondo on 2/13/2018.
 */

public class UniqueId {

    private static String sID = null;
    private static final String SHARED_PREF_KEY = "rf";
    private static final String ID_KEY = "id";

    public synchronized static String id(Context context) {
        if (sID == null) {
            SharedPreferences pref = context.getSharedPreferences(
                    SHARED_PREF_KEY, 0);

            sID = pref.getString(ID_KEY, "");

            if (sID == "") {
                sID = generateAndStoreUserId(pref);
            }

        }
        return sID;
    }

    private synchronized static String generateAndStoreUserId(SharedPreferences pref) {
        String id = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ID_KEY, id);
        editor.commit();
        return id;
    }

}