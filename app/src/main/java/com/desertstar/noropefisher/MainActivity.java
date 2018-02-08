package com.desertstar.noropefisher;
/**
 * Created by Iker Redondo on 1/17/2018.
 */
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static com.desertstar.noropefisher.Constants.FIRST_COLUMN;
import static com.desertstar.noropefisher.Constants.SECOND_COLUMN;
import static com.desertstar.noropefisher.Constants.THIRD_COLUMN;


public class MainActivity extends AppCompatActivity {

    //Attributes
    static final int REQUEST_LOCATION = 1;
    static  final int MAX_DISTANCE_RANGE =700003 ;
    LocationManager locationManager;


    //Firebase db Reference
    private DatabaseReference dref;


    //Android Layout for multicolumn item list
    private ListView listView;


    private ArrayList<HashMap<String, String>> list2;
    ListViewAdapter adapterGlobal;
    String fisherName = "Fisher1";
    String gearN = "1";
    String g = "1";
    String la = "32.515";
    String lo = "121.1516";
    String da = "";
    String  ex = "1";
    String vi ="2";
    Deployment clickedDeploymentData;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences("com.desertstar.noropefisher", Context.MODE_PRIVATE);

        //String elUUID = Installation.id(this);

        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);


        //List View with the table
        listView = findViewById(R.id.database_list_view);

        //List with the different HashMap (1st-ID, 2nd-Serial# and 3rd-Distance)
        list2=new ArrayList<>();

        //Custom Adapter with list2 as parameter
        final ListViewAdapter adapter2=new ListViewAdapter(this, list2);

        //TextView for AlertDialog's Title
        final TextView title = new TextView(this);
        final TextView title2 = new TextView(this);

        //Setting up the adapter into the ListView
        listView.setAdapter(adapter2);

        try {
            //BEGINNING OF ONCLICK EVENT LISTENER
            //Listener for the ListView (To Pop-Up the Alert Dialog with the clicked Deployment's info)
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                {

                    //Toast.makeText(MainActivity.this, Integer.toString(pos)+" Clicked", Toast.LENGTH_SHORT).show();
                    title.setText("Deployment Information:");
                    title.setGravity(Gravity.CENTER);
                    title.setTextSize(25);
                    title.setTypeface(null, Typeface.BOLD);

                    title.setBackgroundColor(Color.parseColor("#3E50B4"));
                    title.setTextColor(Color.WHITE);

                    title2.setText("Deployment Information:");
                    title2.setGravity(Gravity.CENTER);
                    title2.setTextSize(25);
                    title2.setTypeface(null, Typeface.BOLD);

                    title2.setBackgroundColor(Color.parseColor("#3E50B4"));
                    title2.setTextColor(Color.WHITE);


                    //GETTING THE CLICKED DEPLOYMENT BY RETRIVING IT FROM THE GLOBAL ADAPTER SPECIFYING ITS LOCATION IN THE LISTVIEW WITH 'position' VAR.
                    final Object theDeployment = adapterGlobal.getItem(position);
                    HashMap<String,String> a = (HashMap<String,String>) theDeployment;
                    fisherName = a.get("First");
                    gearN = a.get("Second");


                    //GETTING REFERENCE TO FISHERMAN SPECIFIC DEPLOYMENT
                    dref = FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"-"+gearN);
                    // Read from the database
                    dref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            clickedDeploymentData = dataSnapshot.getValue(Deployment.class);

                            if (clickedDeploymentData != null) {
                                g = clickedDeploymentData.getGearNumber();
                                la = String.valueOf(clickedDeploymentData.getLatitude()) ;
                                lo = String.valueOf(clickedDeploymentData.getLongitude());
                                da = clickedDeploymentData.getDeploymentDate().toString();
                                ex = String.valueOf(clickedDeploymentData.getExpirationTime());
                                vi = String.valueOf(clickedDeploymentData.getVisibilityRange());

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("sdf", "Failed to read value.", error.toException());
                        }
                    });
                    //START OF MAIN DIALOG
                    AlertDialog dialog5 = new AlertDialog.Builder(MainActivity.this).setCustomTitle(title2).setMessage(""+
                            "Gear  #" + gearN + "\n" +
                            "from fisher "+fisherName +"\n" +
                            "\n\nRelease Deployment?"
                    ).setPositiveButton("Details", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AlertDialog dialog78 = new AlertDialog.Builder(MainActivity.this).setCustomTitle(title).setMessage("" + //.setTitle("Deployment Information: \n")
                                    "Fisher: "+fisherName +"\n" +
                                    "Gear Number: " + g + "\n" +
                                    "Latitude: "+ la  +" \n" +
                                    "Longitude: "+ lo  +"\n" +
                                    "Deployed on:\n"+ da  +" \n" +
                                    "Expires in: "+ ex  +" days \n" +
                                    "Visibility: "+ vi+" NM"
                            ).setPositiveButton("Release", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    release();
//                                    final String phoneUUID = getPhoneUuid();
//
//                                    dref = FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"-"+g+"/uuid");
//
//                                    ValueEventListener eventListener = new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            Object algo = dataSnapshot.getValue();
//                                            String s = (String)algo;
//
//                                            if(s != null && !s.equals(phoneUUID) ) {
//                                                AlertDialog dialog45 = new AlertDialog.Builder(MainActivity.this).setMessage("This gear is not yours").setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int id) {
//                                                    }
//                                                }).show();
//                                                setDialog(dialog45,25,0,0,0);
////                                                TextView textView = (TextView) dialog45.findViewById(android.R.id.message);
////                                                textView.setTextSize(25);
//                                            }else{
//                                                FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"-"+g).removeValue();
//                                                AlertDialog dialog2 = new AlertDialog.Builder(MainActivity.this).setMessage("Trap Released \nDo you want directions to it?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int id) {
//                                                        startDirections(la,lo);
//
//                                                    }
//                                                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                                    @Override
//                                                    public void onDismiss(DialogInterface dialogInterface) {
//                                                        //finish();
//                                                    }
//                                                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                        //finish();
//                                                    }
//                                                }).show();
//                                                setDialog(dialog2,25,25,25,0);
//
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {}
//                                    };
//                                    dref.addListenerForSingleValueEvent(eventListener);
//                                    //finish();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //nothing
                                }
                            }).setNeutralButton("Map", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startDirections(la,lo);

                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    //To avoid ERROR: The specified child already has a parent. You must call removeView() on the child's parent first.
                                    if(title.getParent()!=null)
                                        ((ViewGroup)title.getParent()).removeView(title);
                                }
                            }).show();
                            setDialog(dialog78,25,20,20,20);

                        }
                    }).setNegativeButton("Release", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            release();
//                            final String phoneUUID = getPhoneUuid();
//                            dref = FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"-"+g+"/uuid");
//
//
//                            ValueEventListener eventListener = new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    Object algo = dataSnapshot.getValue();
//                                    String s = (String)algo;
//
//                                    if(s !=null && !s.equals(phoneUUID) ) {
//                                        AlertDialog dialog45 = new AlertDialog.Builder(MainActivity.this).setMessage("This gear is not yours").setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                            }
//                                        }).show();
//                                        setDialog(dialog45,25,0,0,0);
////
//                                    }else{
//                                        FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"-"+g).removeValue();
//                                        AlertDialog dialog2 = new AlertDialog.Builder(MainActivity.this).setMessage("Trap Released \nDo you want directions to it?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                                startDirections(la,lo);
//
//                                            }
//                                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                            @Override
//                                            public void onDismiss(DialogInterface dialogInterface) {
//                                                //finish();
//                                            }
//                                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                //finish();
//                                            }
//                                        }).show();
//                                        setDialog(dialog2,25,20,20,0);
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {}
//                            };
//                            dref.addListenerForSingleValueEvent(eventListener);

                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //To avoid ERROR: The specified child already has a parent. You must call removeView() on the child's parent first.
                            if(title2.getParent()!=null)
                                ((ViewGroup)title2.getParent()).removeView(title2);
                        }
                    }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing
                        }
                    }).show();
                    setDialog(dialog5,30,18,18,18);
                    //END OF MAIN DIALOG

                }//END OF OnItemClick() METHOD
            });
        }catch (Exception e){
            Log.d("e", "Quiiiii");
        }
        //END OF new OnItemClickListener() EVENT LISTENER



        //GETTING REFERENCE TO FIREBASE DATABASE WITH ALL THE DEPLOYMENTS.
        dref = FirebaseDatabase.getInstance().getReference("deployments");
        //ADDING listener to the DB reference, add child event listener <-IMPORTANT to notice what kind of event.
        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                poblateListView(dataSnapshot, 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                poblateListView(dataSnapshot,2);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                poblateListView(dataSnapshot,3);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sortList(HashMap<String,String>[] listToSort){
        Arrays.sort(listToSort, new Comparator<HashMap<String,String>>() {
            public int compare(HashMap<String,String> o1, HashMap<String,String> o2) {
                String t1 = o1.get("Third");
                String t2 = o2.get("Third");
                String s1 =t1.substring(0, t1.length());// - 3);
                String s2 =t2.substring(0, t2.length());// - 3);
                double n1 = Double.parseDouble(s1);
                double n2 = Double.parseDouble(s2);
                return (n1 > n2) ? 1 : -1;
            }
        });
    }
    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public void startDirections(String la, String lo){
        Intent intent = new Intent( Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+la+","+lo+"&travelmode=driving&dir_action=navigate&travelmode"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK&Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    //Method for START DEPLOYMENT button. It leads to DeployActivity
    public void goToDeployment(View view) {
        Intent intent = new Intent(this, DeployActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    //Get phone's geolocation
    double[] getLocation(){
        double result[] = {0,0};
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                result[0] = latti;
                result [1]= longi;

                ((EditText)findViewById(R.id.etLocationLat)).setText("Latitude: " + latti);
                ((EditText)findViewById(R.id.etLocationLong)).setText("Longitude: " + longi);
            } else {
                ((EditText)findViewById(R.id.etLocationLat)).setText("Unable to find correct location.");
                ((EditText)findViewById(R.id.etLocationLong)).setText("Unable to find correct location. ");
            }

        }
        return result;
    }
    //Internal method for getting location.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }

    //Code to execute asynchronous tasks in the background.
//    private class AsyncTaskEx extends AsyncTask<Void, Void, Void> {
//        /** The system calls this to perform work in a worker thread and
//         * delivers it the parameters given to AsyncTask.execute() */
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            //StartTimer();//call your method here it will run in background
//            return null;
//        }
//
//        /** The system calls this to perform work in the UI thread and delivers
//         * the result from doInBackground() */
//        @Override
//        protected void onPostExecute(Void result) {
//            //Write some code you want to execute on UI after doInBackground() completes
//            return ;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            //Write some code you want to execute on UI before doInBackground() starts
//            return ;
//        }
//    }

    public void poblateListView(DataSnapshot dataSnapshot, int addedChangedRemoved){
        Deployment d = dataSnapshot.getValue(Deployment.class);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        double location [] = getLocation();
        double lat2 = location[0];
        double long2 =  location[1];
        Date daysAddedDate =new Date();  //OJO

        if(d != null){
            daysAddedDate = addDays(d.getDeploymentDate(),d.getExpirationTime());
        }


        //IF dayDif is 1 the trap has NOT expired yet. Meaning today's date (the argument) is less than the daysAddedDate.
        int dayDif = daysAddedDate.compareTo(new Date());
        boolean isExpired = dayDif <= 0;
        DecimalFormat df2 = new DecimalFormat(".##");
        DistanceCalculator calculator = new DistanceCalculator();
        double dist = 0;//OJO
        String fisherUUID = "";//OJO
        double visibilityRange=0.0;
        String elID = "";
        String gearNumber = "";

        if(d != null){
            dist = calculator.distance(d.getLatitude(),lat2,d.getLongitude(),long2,0,0);
            fisherUUID = d.getUuid();
            visibilityRange = d.getVisibilityRange();
            elID = d.getID();
            gearNumber = d.getGearNumber();
        }

        String phoneUUID = getPhoneUuid();

        dist = dist/1000;
        if(dist < MAX_DISTANCE_RANGE  && (  (fisherUUID.equals(phoneUUID) ) || ((dist *0.53996 <= visibilityRange)&&!isExpired) )){
            HashMap<String,String> temp=new HashMap<>();
            temp.put(FIRST_COLUMN, elID);
            temp.put(SECOND_COLUMN, gearNumber);


            String res2 =df2.format(dist*0.53996);
            temp.put(THIRD_COLUMN, res2 );

            if (addedChangedRemoved == 2){
                for(HashMap<String, String> a : list2){
                    if(a.get("Second").equals(gearNumber)){
                        list2.remove(a);
                        break;
                    }
                }
                list2.add(temp);
            }else if (addedChangedRemoved == 3){
                list2.remove(temp);
            }else if(addedChangedRemoved == 1){
                list2.add(temp);
            }

            HashMap<String,String>[] harr =list2.toArray(new HashMap[list2.size()]);
            sortList(harr);
            ArrayList<HashMap<String, String>> orderedlist2 = new ArrayList<>();

            for(int i =0;i<list2.size();i++){
                orderedlist2.add(harr[i]);
            }

            list2 = orderedlist2;//new ArrayList(Arrays.asList(harr));
            final ListViewAdapter adapter3 = new ListViewAdapter(MainActivity.this, list2);
//                  listView.setAdapter(adapter2);
//                  adapter2.notifyDataSetChanged();
            listView.setAdapter(adapter3);
            adapterGlobal = (ListViewAdapter) listView.getAdapter();
            adapter3.notifyDataSetChanged();
        }
    }
//NOTES:
    //    //trying to dynamically refresh ListView. Not got it yet.
//    public void refreshListView(View view){
//        //https://stackoverflow.com/questions/5320358/update-listview-dynamically-with-adapter
//        adapterGlobal=new ListViewAdapter(this, list2);
//        listView.setAdapter(adapterGlobal);  //CHEEEEEEEEEEECKKK
//        adapterGlobal.notifyDataSetChanged();
//    }


/*
https://stackoverflow.com/questions/10733682/make-a-specific-code-run-in-background
http://www.businessinsider.com/how-facebook-finds-exceptional-employees-2016-2/#facebook-looks-for-talent-and-cultural-fit-3
    // Write this class inside your Activity and call where you want execute your method
    new AsyncTaskEx().execute();
 */

    public void setDialog(AlertDialog dialog78, int messageSize, int button1Size,int button2Size,int button3Size ){

        TextView textView =  dialog78.findViewById(android.R.id.message);
        if(textView != null){
            textView.setTextSize(messageSize);
        }

        TextView textViewButton =  dialog78.findViewById(android.R.id.button1);
        if(textViewButton != null){
            textViewButton.setTextSize(button1Size);
        }
        TextView textViewButton2 =  dialog78.findViewById(android.R.id.button2);
        if(textViewButton2 != null){
            textViewButton2.setTextSize(button2Size);
        }

        TextView textViewButton3 =  dialog78.findViewById(android.R.id.button3);
        if(textViewButton3 != null){
            textViewButton3.setTextSize(button3Size);
        }
    }

    public String getPhoneUuid(){
        File installation;
        String phoneUUID;
        try {
            installation = new File(MainActivity.this.getFilesDir(), "INSTALLATION");
            phoneUUID =  Installation.readInstallationFile(installation);
            return phoneUUID;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public void release(){
        final String phoneUUID = getPhoneUuid();

        dref = FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"-"+g+"/uuid");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object algo = dataSnapshot.getValue();
                String s = (String)algo;

                if(s != null && !s.equals(phoneUUID) ) {
                    AlertDialog dialog45 = new AlertDialog.Builder(MainActivity.this).setMessage("This gear is not yours").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).show();
                    setDialog(dialog45,25,25,0,0);
//                                                TextView textView = (TextView) dialog45.findViewById(android.R.id.message);
//                                                textView.setTextSize(25);
                }else{
                    FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"-"+g).removeValue();
                    AlertDialog dialog2 = new AlertDialog.Builder(MainActivity.this).setMessage("Trap Released \nDo you want directions to it?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startDirections(la,lo);

                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //finish();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //finish();
                        }
                    }).show();
                    setDialog(dialog2,25,25,25,0);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        dref.addListenerForSingleValueEvent(eventListener);
        //finish();
    }
}