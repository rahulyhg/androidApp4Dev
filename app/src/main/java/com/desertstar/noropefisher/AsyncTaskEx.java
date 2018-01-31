package com.desertstar.noropefisher;

import android.os.AsyncTask;

/**
 * Created by DEV-1 on 1/22/2018.
 */

public class AsyncTaskEx extends AsyncTask<Void, Void, Void> {

    /** The system calls this to perform work in a worker thread and
     * delivers it the parameters given to AsyncTask.execute() */
    @Override
    protected Void doInBackground(Void... arg0) {
        //StartTimer();//call your method here it will run in background
        return null;
    }

    /** The system calls this to perform work in the UI thread and delivers
     * the result from doInBackground() */
    @Override
    protected void onPostExecute(Void result) {
        //Write some code you want to execute on UI after doInBackground() completes
        return ;
    }

    @Override
    protected void onPreExecute() {
        //Write some code you want to execute on UI before doInBackground() starts
        return ;
    }
}

/*
    // Write this class inside your Activity and call where you want execute your method
    new AsyncTaskEx().execute();
 */