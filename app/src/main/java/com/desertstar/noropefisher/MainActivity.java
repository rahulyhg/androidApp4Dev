package com.desertstar.noropefisher;
/**
 * Created by Iker Redondo on 1/17/2018.
 */

import android.Manifest;
import android.app.FragmentTransaction;
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
import android.os.Handler;
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

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Attributes
    static final int REQUEST_LOCATION = 1;
    static final int MAX_DISTANCE_RANGE = 700003;
    LocationManager locationManager;


    //Firebase db Reference
    private DatabaseReference dref;
    LatLng BRISBANE;
    String latLng = "";
    String selectedGearToBeShownOnMap="";


    //Android Layout for multicolumn item list
    private ListView listView;

    public GoogleMap mMap;
    private ArrayList<HashMap<String, String>> list2;
    private ArrayList<HashMap<String, String>> listGLOBAL;
    private ArrayList<LatLng> listOfLocations;
    ListViewAdapter adapterGlobal;
    String fisherName = "Fisher1";
    String gearN = "1";
    String g = "1";
    String la = "32.515";
    String lo = "121.1516";
    String da = "";
    String ex = "1";
    String vi = "2";
    Deployment clickedDeploymentData;
    public static final String EXTRA_MESSAGE = "com.desertstar.noropefisher.MESSAGE";
    SharedPreferences settings;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    boolean showinMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



//        if(UniqueId.getID()==null){
//            AlertDialog dialog = new AlertDialog.Builder(this).setMessage("Firs time opening app").setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//
//                }
//            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialogInterface) {
//
//                }
//            }).show();
//            setDialog(dialog, 25,25,0,0);
//        }

        //Getting settings for saved preferences
        settings = getSharedPreferences("com.desertstar.noropefisher", Context.MODE_PRIVATE);

        //Calling super class' constructor
        super.onCreate(savedInstanceState);

        //Preparing a handler to deal with Uncaught Exceptions
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        //Setting content view with activity_main.xml layout
        setContentView(R.layout.activity_main);

//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        //List View with the table
        listView = findViewById(R.id.database_list_view);

        //List with the different HashMaps (1st-ID, 2nd-Serial# and 3rd-Distance)
        list2 = new ArrayList<>();
        listOfLocations = new ArrayList<>();

        //Custom Adapter with list2 as parameter
        final ListViewAdapter adapter2 = new ListViewAdapter(this, list2);




        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                Log.d("ffffffffff","Entramos en bucle");
                elMethodQueMeVaASalvar();
                handler.postDelayed( this, 60 * 1000 );
            }
        }, 60 * 1000 );

//        final Handler handler1 = new Handler();
//        handler.postDelayed( new Runnable() {
//            @Override
//            public void run() {
//                Log.d("ffffffffff","tic");
//                handler1.postDelayed( this, 1 * 1000 );
//            }
//        }, 1 * 1000 );

        //TextView for AlertDialogs' Titles
        final TextView title = new TextView(this);
        final TextView title2 = new TextView(this);

        //Setting up the adapter into the ListView
        listView.setAdapter(adapter2);

//        listView.postDelayed(new Runnable() {
//            public void run() {
//                Log.d("23", "updateando");
////                elMethodQueMeVaASalvar();
//                finish();
//                startActivity(starterIntent);
//                listView.postDelayed(this, 5000);
//            }
//        }, 5000);

        try {
            //BEGINNING OF ONCLICK EVENT LISTENER
            //Listener for the ListView (To Pop-Up the Alert Dialog with the clicked Deployment's info)
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    //Setting titles' properties
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


                    //GETTING THE CLICKED DEPLOYMENT BY RETRIEVING IT FROM THE GLOBAL ADAPTER SPECIFYING ITS LOCATION IN THE LISTVIEW WITH 'position' VAR.
                    final Object theDeployment = adapterGlobal.getItem(position);
                    HashMap<String, String> a = (HashMap<String, String>) theDeployment;
                    fisherName = a.get("First");
                    gearN = a.get("Second");

                    //GETTING REFERENCE TO FISHERMAN SPECIFIC DEPLOYMENT
                    dref = FirebaseDatabase.getInstance().getReference("deployments/" + fisherName + "-" + gearN);
                    // Read from the database
                    dref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            clickedDeploymentData = dataSnapshot.getValue(Deployment.class);

                            if (clickedDeploymentData != null) {
                                g = clickedDeploymentData.getGearNumber();
                                la = String.valueOf(clickedDeploymentData.getLatitude());
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
                    //Dialog with CANCEL , RELEASE and DETAILS buttons (inside another dialog with MAP, CANCEL and RELEASE buttons)
                    AlertDialog dialog5 = new AlertDialog.Builder(MainActivity.this).setCustomTitle(title2).setMessage("" +
                            "Gear  #" + gearN + "\n" +
                            "from fisher " + fisherName + "\n" +
                            "\n\nRelease Deployment?"
                    ).setPositiveButton("Details", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Dialog with MAP, CANCEL and RELEASE buttons
                            AlertDialog dialog78 = new AlertDialog.Builder(MainActivity.this).setCustomTitle(title).setMessage("" + //.setTitle("Deployment Information: \n")
                                    "Fisher: " + fisherName + "\n" +
                                    "Gear Number: " + g + "\n" +
                                    "Latitude: " + la + " \n" +
                                    "Longitude: " + lo + "\n" +
                                    "Deployed on:\n" + da + " \n" +
                                    "Expires in: " + ex + " days \n" +
                                    "Visibility: " + vi + " NM"
                            ).setPositiveButton("Release", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    releaseDeployment();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //nothing
                                }
                            }).setNeutralButton("Map", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //startDirections(la,lo);
//                                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                                    //EditText editText = (EditText) findViewById(R.id.editText);
//                                    //String message = editText.getText().toString();
//                                    intent.putExtra(EXTRA_MESSAGE, "algo");
//                                    startActivity(intent);
                                    displayMap();
                                    /*
                                    Button p1_button = (Button)findViewById(R.id.buttonDeploy);
                                    p1_button.setText("Back");


                                    GoogleMapOptions options = new GoogleMapOptions();
                                    options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                                            .compassEnabled(false)
                                            .rotateGesturesEnabled(false)
                                            .tiltGesturesEnabled(false);
                                    MapFragment mMapFragment = MapFragment.newInstance(options);
                                    FragmentTransaction fragmentTransaction =
                                            getFragmentManager().beginTransaction();
                                    fragmentTransaction.add(R.id.map, mMapFragment);
                                    fragmentTransaction.commit();

//                                    MapFragment mapFragment = (MapFragment) getFragmentManager()
//                                            .findFragmentById(R.id.map);
                                    mMapFragment.getMapAsync(MainActivity.this);

                                    if (MainActivity.this.mMap != null) {
                                        LatLng sydney = new LatLng(37.339800, -121.879220);

                                        MainActivity.this.mMap.addMarker(new MarkerOptions().position(sydney)
                                                .title("Iker marker"));
                                    } else {
                                        Log.d("null", "es null");
                                    }
                                    selectedGearToBeShownOnMap = g;
                                    latLng = la +", " + lo;
                                    BRISBANE = new LatLng(Double.valueOf(la), Double.valueOf(lo));
                                    showinMap = true;
                                    */

                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    //To avoid ERROR: The specified child already has a parent. You must call removeView() on the child's parent first.
                                    if (title.getParent() != null)
                                        ((ViewGroup) title.getParent()).removeView(title);
                                }
                            }).show();
                            setDialog(dialog78, 25, 20, 20, 20);
                        }
                    }).setNegativeButton("Release", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            releaseDeployment();
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //To avoid ERROR: The specified child already has a parent. You must call removeView() on the child's parent first.
                            if (title2.getParent() != null)
                                ((ViewGroup) title2.getParent()).removeView(title2);
                        }
                    }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //nothing
                        }
                    }).show();
                    setDialog(dialog5, 30, 18, 18, 18);
                    //END OF MAIN DIALOG

                }//END OF OnItemClick() METHOD
            });
        } catch (Exception e) {
            Log.d("error", e.getCause().toString());
        }//END OF new OnItemClickListener() EVENT LISTENER

        settingFirebaseListener();

//        //GETTING REFERENCE TO FIREBASE DATABASE WITH ALL THE DEPLOYMENTS.
//        dref = FirebaseDatabase.getInstance().getReference("deployments");
//        //ADDING listener to the DB reference, add child event listener <-IMPORTANT to notice what kind of event.
//        dref.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                fillListView(dataSnapshot, 1);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                fillListView(dataSnapshot, 2);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                fillListView(dataSnapshot, 3);
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }//END OF onCreate

    //Method to sort an array of HashMaps with String,String Key-Value relationship
    public void sortList(HashMap<String, String>[] listToSort) {
        Arrays.sort(listToSort, new Comparator<HashMap<String, String>>() {
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                String t1 = o1.get("Third");
                String t2 = o2.get("Third");
                String s1 = t1.substring(0, t1.length());// - 3);
                String s2 = t2.substring(0, t2.length());// - 3);
                double n1 = Double.parseDouble(s1);
                double n2 = Double.parseDouble(s2);
                return (n1 > n2) ? 1 : -1;
            }
        });
    }


    public void inflateHashWithNewDit(HashMap<String, String>[] listToSort){
        double location[] = getLocation();
        final double myLat = location[0];
        final double myLongi = location[1];
        DecimalFormat df2 = new DecimalFormat(".##");
        double dist = 0;//OJO
        DistanceCalculator calculator = new DistanceCalculator();

        for (HashMap<String, String> a : listToSort) {
            dist = calculator.distance(Double.valueOf(a.get("Fourth")), myLat, Double.valueOf(a.get("Fifth")), myLongi, 0, 0);
            dist = dist / 1000;
            String res2 = df2.format(dist * 0.53996);
            a.put(THIRD_COLUMN, res2);
        }

    }

    //Method to sort an array of HashMaps with String,String Key-Value relationship
//    public void sortListByLocation(HashMap<String, String>[] listToSort) {
//        double location[] = getLocation();
//        final double lat = location[0];
//        final double longi = location[1];
//
//        Arrays.sort(listToSort, new Comparator<HashMap<String, String>>() {
//            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
//
//                String t1 = o1.get("Fourth");
//                String t2 = o2.get("Fifth");
//
//                String t11 = o1.get("Fourth");
//                String t22 = o2.get("Fifth");
//
//
//                double d1 = Double.valueOf(t1);
//                double d2 = Double.valueOf(t2);
//                double d11 = Double.valueOf(t11);
//                double d22 = Double.valueOf(t22);
//
//                DistanceCalculator calculator = new DistanceCalculator();
//                double dist1 = calculator.distance(d1, lat, d2, longi, 0, 0);
//                double dist2 = calculator.distance(d11, lat, d22, longi, 0, 0);
//
//                return (dist1 > dist2) ? 1 : -1;
//            }
//        });
//    }

    //Method to add days to a given Date
    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    //Method to launch google maps with a given Latitude and Longitude
//    public void startDirections(String la, String lo) {
//        Intent intent = new Intent(Intent.ACTION_VIEW,
//                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + la + "," + lo + "&travelmode=driving&dir_action=navigate&travelmode"));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//        startActivity(intent);
//    }


//    public void goToSomewhere(View view){
//        Intent intent = new Intent(MainActivity.this, principal.class);
//        startActivity(intent);
//    }

    //Method for START DEPLOYMENT button. It leads to DeployActivity
    public void goToDeployment(View view) {
        if(showinMap){
            Intent intent = new Intent(this, MainActivity.class);
            //EditText editText = (EditText) findViewById(R.id.editText);
            //String message = editText.getText().toString();
            //intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }else {

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setMessage("Single or Trawl?")
                    .setPositiveButton("Trawl", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MainActivity.this, TrollActivity.class);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("Single", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, DeployActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
            setDialog(dialog,25,25,25,0);
//            Intent intent = new Intent(this, DeployActivity.class);
//            startActivity(intent);
        }
    }

    //Method to get phone's geolocation
    double[] getLocation() {
        double result[] = {0, 0};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                result[0] = latti;
                result[1] = longi;

                ((EditText) findViewById(R.id.etLocationLat)).setText("Latitude: " + latti);
                ((EditText) findViewById(R.id.etLocationLong)).setText("Longitude: " + longi);
            } else {
                ((EditText) findViewById(R.id.etLocationLat)).setText("Unable to find correct location.");
                ((EditText) findViewById(R.id.etLocationLong)).setText("Unable to find correct location. ");
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

    //Method to fill a list view with a given DataSnapshot and case (added, changed or removed)
    public void fillListView(DataSnapshot dataSnapshot, int addedChangedRemoved) {
        Log.d("23", "ON FILL");
        Deployment d = dataSnapshot.getValue(Deployment.class);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        double location[] = getLocation();
        double lat2 = location[0];
        double long2 = location[1];
        Date daysAddedDate = new Date();  //OJO

        if (d != null) {
            daysAddedDate = addDays(d.getDeploymentDate(), d.getExpirationTime());
        }

        //IF dayDif is 1 the trap has NOT expired yet. Meaning today's date (the argument) is less than the daysAddedDate.
        int dayDif = daysAddedDate.compareTo(new Date());
        boolean isExpired = dayDif <= 0;
        DecimalFormat df2 = new DecimalFormat(".##");
        DistanceCalculator calculator = new DistanceCalculator();
        double dist = 0;//OJO
        String fisherUUID = "";//OJO
        double visibilityRange = 0.0;
        String elID = "";
        String gearNumber = "";

        if (d != null) {
            dist = calculator.distance(d.getLatitude(), lat2, d.getLongitude(), long2, 0, 0);
            fisherUUID = d.getUuid();
            visibilityRange = d.getVisibilityRange();
            elID = d.getID();
            gearNumber = d.getGearNumber();
        }

        String phoneUUID = getPhoneUuid();

        dist = dist / 1000;
        if (dist < MAX_DISTANCE_RANGE && ((fisherUUID.equals(phoneUUID)) || ((dist * 0.53996 <= visibilityRange) && !isExpired))) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(FIRST_COLUMN, elID);
            temp.put(SECOND_COLUMN, gearNumber);

            String res2 = df2.format(dist * 0.53996);
            temp.put(THIRD_COLUMN, res2);
            temp.put("Fourth",String.valueOf(d.latitude));
            temp.put("Fifth",String.valueOf(d.longitude));
            LatLng theLocation = new LatLng(d.getLatitude(), d.getLongitude());

            if (addedChangedRemoved == 2) {
                for (HashMap<String, String> a : list2) {
                    if (a.get("Second").equals(gearNumber)) {
                        list2.remove(a);
                        break;
                    }
                }
                list2.add(temp);
                //listOfLocations
            } else if (addedChangedRemoved == 3) {
                list2.remove(temp);
                listOfLocations.remove(theLocation);
            } else if (addedChangedRemoved == 1) {

                list2.add(temp);
                listOfLocations.add(theLocation);
            }
            HashMap<String, String>[] harr = list2.toArray(new HashMap[list2.size()]);
            sortList(harr);
            ArrayList<HashMap<String, String>> orderedlist2 = new ArrayList<>();

            for (int i = 0; i < list2.size(); i++) {
                orderedlist2.add(harr[i]);
            }

            list2 = orderedlist2;//new ArrayList(Arrays.asList(harr));
            final ListViewAdapter adapter3 = new ListViewAdapter(MainActivity.this, list2);
//                  listView.setAdapter(adapter2);
//                  adapter2.notifyDataSetChanged();
            listView.setAdapter(adapter3);
            adapterGlobal = (ListViewAdapter) listView.getAdapter();
            listGLOBAL = list2;
            adapter3.notifyDataSetChanged();
        }
    }

    //Method to set up a given dialog with a given size for message and buttons 1,2 and 3 if exist
    public void setDialog(AlertDialog dialog78, int messageSize, int button1Size, int button2Size, int button3Size) {
        TextView textView = dialog78.findViewById(android.R.id.message);
        if (textView != null) {
            textView.setTextSize(messageSize);
        }

        TextView textViewButton = dialog78.findViewById(android.R.id.button1);
        if (textViewButton != null) {
            textViewButton.setTextSize(button1Size);
        }
        TextView textViewButton2 = dialog78.findViewById(android.R.id.button2);
        if (textViewButton2 != null) {
            textViewButton2.setTextSize(button2Size);
        }

        TextView textViewButton3 = dialog78.findViewById(android.R.id.button3);
        if (textViewButton3 != null) {
            textViewButton3.setTextSize(button3Size);
        }
    }

    //Method to get phone's UUID
    public String getPhoneUuid() {
        File installation;
        String phoneUUID;
        try {
//            Log.d("IKERdir",MainActivity.this.getFilesDir().toString());
            installation = new File(MainActivity.this.getFilesDir(), "INSTALLATION");
//            Log.d("Existe?", String.valueOf(installation.exists()));
            //File mi = new File()
//            phoneUUID = Installation.readInstallationFile(installation);
            phoneUUID = UniqueId.id(this);
            Log.d("phone id?", phoneUUID);
            return phoneUUID;
//            return "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Method to release a deployment
    public void releaseDeployment() {
        final String phoneUUID = getPhoneUuid();

        dref = FirebaseDatabase.getInstance().getReference("deployments/" + fisherName + "-" + g + "/uuid");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object algo = dataSnapshot.getValue();
                String s = (String) algo;

                if (s != null && !s.equals(phoneUUID)) {
                    AlertDialog dialog45 = new AlertDialog.Builder(MainActivity.this).setMessage("This gear is not yours").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).show();
                    setDialog(dialog45, 25, 20, 0, 0);
                } else {
                    FirebaseDatabase.getInstance().getReference("deployments/" + fisherName + "-" + g).removeValue();
                    AlertDialog dialog2 = new AlertDialog.Builder(MainActivity.this).setMessage("Trap Released \nDo you want to see it on a map?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            displayMap();
                            //startDirections(la, lo);
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
                    setDialog(dialog2, 25, 25, 25, 0);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dref.addListenerForSingleValueEvent(eventListener);
        //finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(36.871806, -122.109441);
//        LatLng sydney2 = new LatLng(36.660881, -121.783714);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("36.871806, -122.109441"));
//        mMap.addMarker(new MarkerOptions().position(sydney2).title("Marker in MB2"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        Marker mBrisbane;
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title(selectedGearToBeShownOnMap)
                .snippet(latLng));

        for(LatLng c : listOfLocations){
            if(!c.equals(BRISBANE))
            mMap.addMarker(new MarkerOptions()
                    .position(c)
                    .title("other dep")
                    .snippet(c.toString()));
        }
        pointToPosition(BRISBANE, mMap);
        double currentLocation []= getLocation();
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(currentLocation[0], currentLocation[1]), BRISBANE)
                .width(5)
                .color(Color.BLUE));
    }

    private void pointToPosition(LatLng position, GoogleMap mGoogleMap) {
        //Build camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(15).build();
        //Zoom in and animate the camera.
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addMarkersToMap() {
        Marker mBrisbane;
        LatLng BRISBANE = new LatLng(37.339800, -121.879220);
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane")
                .snippet("Population: 2,074,200"));

    }

    private void displayMap(){
        Button p1_button = (Button)findViewById(R.id.buttonDeploy);
        p1_button.setText("Back");

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);
        MapFragment mMapFragment = MapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();

//                                    MapFragment mapFragment = (MapFragment) getFragmentManager()
//                                            .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(MainActivity.this);

        if (MainActivity.this.mMap != null) {
            LatLng sydney = new LatLng(37.339800, -121.879220);

            MainActivity.this.mMap.addMarker(new MarkerOptions().position(sydney)
                    .title("Iker marker"));
        } else {
            Log.d("null", "es null");
        }
        selectedGearToBeShownOnMap = g;
        latLng = la +", " + lo;
        BRISBANE = new LatLng(Double.valueOf(la), Double.valueOf(lo));
        showinMap = true;
    }

    public void settingFirebaseListener(){
        //GETTING REFERENCE TO FIREBASE DATABASE WITH ALL THE DEPLOYMENTS.
        dref = FirebaseDatabase.getInstance().getReference("deployments");
        //ADDING listener to the DB reference, add child event listener <-IMPORTANT to notice what kind of event.
        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                fillListView(dataSnapshot, 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fillListView(dataSnapshot, 2);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                fillListView(dataSnapshot, 3);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void elMethodQueMeVaASalvar(){
        Log.d("ven","Y VENGA");
        HashMap<String, String>[] harr = listGLOBAL.toArray(new HashMap[listGLOBAL.size()]);
        inflateHashWithNewDit(harr);
        sortList(harr);
        ArrayList<HashMap<String, String>> orderedlist2 = new ArrayList<>();

        for (int i = 0; i < list2.size(); i++) {
            orderedlist2.add(harr[i]);
        }

        listGLOBAL = orderedlist2;//new ArrayList(Arrays.asList(harr));

        for (HashMap map: listGLOBAL) {
            Log.d("MAPAAAAAAA",map.toString());
        }

        ListViewAdapter adapterro = new ListViewAdapter(this,listGLOBAL);
        listView.setAdapter(adapterro);
        adapterro.notifyDataSetChanged();

        list2 = listGLOBAL;
        Log.d("pr","PRINTEADO");
    }
}