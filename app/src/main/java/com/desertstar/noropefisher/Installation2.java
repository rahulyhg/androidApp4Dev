package com.desertstar.noropefisher;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by Iker Redondo on 1/24/2018.
 */

public class Installation2 {
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String id(Context context) {
        Log.d("In Installation", "Entered in public synchronized static String id");

        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            Log.d("E","EEEEEEEEEEEEEEEEEEEEEEEE");
        }
        return sID;
    }

    public static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
//        f.readFully(bytes);
//        f.close();
//        Log.d("In Installation", "Entered in readInstallationFile");
//        return new String(bytes);
        return "SUUUP";
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        Log.d("In Installation", "Entered in writeInstallationFile");
        out.close();
    }
}