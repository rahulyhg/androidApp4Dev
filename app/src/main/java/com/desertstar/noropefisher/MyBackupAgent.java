package com.desertstar.noropefisher;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Created by Iker Redondo on 2/14/2018.
 */

public class MyBackupAgent extends BackupAgentHelper {

    // The name of the SharedPreferences file
    static final String PREFS = "rf";

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "id";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}