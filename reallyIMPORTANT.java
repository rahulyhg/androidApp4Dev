package com.desertstar.noropefisher;
/**
 * Created by Iker Redondo on 1/17/2018.
 */
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;

import static com.desertstar.noropefisher.Constants.FIRST_COLUMN;
import static com.desertstar.noropefisher.Constants.SECOND_COLUMN;
import static com.desertstar.noropefisher.Constants.THIRD_COLUMN;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    //Attributes
    public static final String EXTRA_MESSAGE = "com.desertstar.noropefisher.MESSAGE";  //DELETE
    static final int REQUEST_LOCATION = 1;
    static  final int MAX_DISTANCE_RANGE =700003 ;
    LocationManager locationManager;

    //Firebase storage reference (not real time database)
    private StorageReference mStorageRef;

    //Firebase db Reference
    private DatabaseReference dref;


    //Android Layout for multicolumn item list
    private ListView listView;

    //Array List
    //private ArrayAdapter<String> adapter;

    ArrayList<String> list=new ArrayList<>(); //DELETE

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
    ArrayList<Deployment> depList;
    Deployment clickedDeploymentData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String elUUID = Installation.id(this);


        //Log.d("EEEEL UUUUUUUUIIIDDDDDD", elUUID);

        //TextView textView = findViewById(R.id.textViewMIO);


//        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("uuid: " + elUUID).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                //finish();
//            }
//        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                //finish();
//            }
//        }).show();



        //textView.setText(elUUID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //List View with the table
        listView = (ListView)findViewById(R.id.database_list_view);
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list); //DELETE

        //List with the different HashMap (1st-ID, 2nd-Serial# and 3rd-Distance)
        list2=new ArrayList<HashMap<String,String>>();

        //Custom Adapter with list2 as parameter
        final ListViewAdapter adapter2=new ListViewAdapter(this, list2);
        ListViewAdapter adapter3;
        //TextView for AlertDialog's Title
        final TextView title = new TextView(this);
        final TextView title2 = new TextView(this);

        //Setting up the adapter into the ListView
        listView.setAdapter(adapter2);



        //Listener for the ListView (To Pop-Up the Alert Dialog with the clicked Deployment's info)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                //int pos=position+1;
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

                //dialog.setCustomTitle(title);

                //GETTING THE CLICKED DEPLOYMENT BY RETRIVING IT FROM THE GLOBAL ADAPTER SPECIFYING ITS LOCATION IN THE LISTVIEW WITH 'position' VAR.
                final Object theDeployment = adapterGlobal.getItem(position);
                HashMap<String,String> a = ((HashMap<String,String>) theDeployment);
                fisherName = a.get("First");
                gearN = a.get("Second");

                ///*DELETE
                Class sd = theDeployment.getClass();
                String typename = sd.getName();
                Log.d("Typename", typename);
                //*/

                //VAMOS CON OTRO!! O PARAAA!!

                dref = FirebaseDatabase.getInstance().getReference("deployments/"+fisherName);
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

                //g = clickedDeploymentData.getGearNumber();
//                double la = clickedDeploymentData.getLatitude() ;
//                double lo = clickedDeploymentData.getLongitude();
//                Date da = clickedDeploymentData.getDeploymentDate();
//                int  ex = clickedDeploymentData.getExpirationTime();
//                int vi = clickedDeploymentData.getVisibilityRange();


                DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference();

                String avcbbvg = mDatabase.child("deployments").child(fisherName).getKey();
                Log.d("yokeseeeeee", avcbbvg);


                Log.d("SACADO DE LA LISTAAAAAA", String.valueOf(position));
//                g = depList.get(position).getGearNumber();
//                la = String.valueOf(depList.get(position).getLatitude()) ;
//                lo = String.valueOf(depList.get(position).getLongitude());
//                da = depList.get(position).getDeploymentDate().toString();
//                ex = String.valueOf(depList.get(position).getExpirationTime());
//                vi = String.valueOf(depList.get(position).getVisibilityRange());
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
                                File installation;
                                final String phoneUUID;
                                try {
                                    installation = new File(MainActivity.this.getFilesDir(), "INSTALLATION");
                                    phoneUUID =  Installation.readInstallationFile(installation);
                                }catch (Exception e){
                                    throw new RuntimeException(e);
                                }



                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                Log.d("el fisher: ", fisherName);
                                Log.d("uuid del telefono: ", phoneUUID);

                                String ruta = "deployments/"+fisherName+"/uuid/"+phoneUUID ;

                                dref = FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"/uuid");

                                //DatabaseReference userNameRef = rootRef.child("deployments").child(fisherName).child(phoneUUID);
                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Object algo = dataSnapshot.getValue();
                                        String s = (String)algo;
                                        //Log.d("s: ", s);

                                        if( !s.equals(phoneUUID) ) {
                                            AlertDialog dialog45 = new AlertDialog.Builder(MainActivity.this).setMessage("This gear is not yours").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            }).show();
                                            TextView textView = (TextView) dialog45.findViewById(android.R.id.message);
                                            textView.setTextSize(25);
                                        }else{
                                            FirebaseDatabase.getInstance().getReference("deployments/"+fisherName).removeValue();
                                            AlertDialog dialog2 = new AlertDialog.Builder(MainActivity.this).setMessage("Trap Released \nDo you want directions to it?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    String a = la;
                                                    String b = lo;
                                                    Intent intent = new Intent( Intent.ACTION_VIEW,
                                                            Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+la+","+lo+"&travelmode=driving&dir_action=navigate&travelmode"));
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK&Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                                    startActivity(intent);
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
                                            TextView textView = (TextView) dialog2.findViewById(android.R.id.message);
                                            textView.setTextSize(25);

                                            TextView textViewButton = (TextView) dialog2.findViewById(android.R.id.button1);
                                            textViewButton.setTextSize(25);
                                            TextView textViewButton2 = (TextView) dialog2.findViewById(android.R.id.button2);
                                            textViewButton2.setTextSize(25);                                }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                };
                                dref.addListenerForSingleValueEvent(eventListener);
                                //finish();

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //nothing
                            }
                        }).setNeutralButton("Map", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String a = la;
                                String b = lo;
                                Intent intent = new Intent( Intent.ACTION_VIEW,
                                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+la+","+lo+"&travelmode=driving&dir_action=navigate&travelmode"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK&Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intent);
                            }
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                //To avoid ERROR: The specified child already has a parent. You must call removeView() on the child's parent first.
                                if(title.getParent()!=null)
                                    ((ViewGroup)title.getParent()).removeView(title);
                            }
                        }).show();

                        TextView textView = (TextView) dialog78.findViewById(android.R.id.message);
                        textView.setTextSize(25);
                        TextView textViewButton = (TextView) dialog78.findViewById(android.R.id.button1);
                        textViewButton.setTextSize(20);
                        TextView textViewButton2 = (TextView) dialog78.findViewById(android.R.id.button2);
                        textViewButton2.setTextSize(20);
                        TextView textViewButton3 = (TextView) dialog78.findViewById(android.R.id.button3);
                        textViewButton3.setTextSize(20);

                    }
                }).setNegativeButton("Release", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference();


                        File installation;
                        final String phoneUUID;
                        try {
                            installation = new File(MainActivity.this.getFilesDir(), "INSTALLATION");
                            phoneUUID =  Installation.readInstallationFile(installation);
                        }catch (Exception e){
                            throw new RuntimeException(e);
                        }



                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        Log.d("el fisher: ", fisherName);
                        Log.d("uuid del telefono: ", phoneUUID);

                        String ruta = "deployments/"+fisherName+"/uuid/"+phoneUUID ;

                        dref = FirebaseDatabase.getInstance().getReference("deployments/"+fisherName+"/uuid");

                        //DatabaseReference userNameRef = rootRef.child("deployments").child(fisherName).child(phoneUUID);
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Object algo = dataSnapshot.getValue();
                                String s = (String)algo;
                                //Log.d("s: ", s);

                                if( !s.equals(phoneUUID) ) {
                                    AlertDialog dialog45 = new AlertDialog.Builder(MainActivity.this).setMessage("This gear is not yours").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    }).show();
                                    TextView textView = (TextView) dialog45.findViewById(android.R.id.message);
                                    textView.setTextSize(25);
                                }else{
                                    FirebaseDatabase.getInstance().getReference("deployments/"+fisherName).removeValue();
                                    AlertDialog dialog2 = new AlertDialog.Builder(MainActivity.this).setMessage("Trap Released \nDo you want directions to it?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String a = la;
                                            String b = lo;
                                            Intent intent = new Intent( Intent.ACTION_VIEW,
                                                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+la+","+lo+"&travelmode=driving&dir_action=navigate&travelmode"));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK&Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                            startActivity(intent);                            }
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
                                    TextView textView = (TextView) dialog2.findViewById(android.R.id.message);
                                    textView.setTextSize(25);

                                    TextView textViewButton = (TextView) dialog2.findViewById(android.R.id.button1);
                                    textViewButton.setTextSize(25);
                                    TextView textViewButton2 = (TextView) dialog2.findViewById(android.R.id.button2);
                                    textViewButton2.setTextSize(25);                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        };
                        dref.addListenerForSingleValueEvent(eventListener);









                        Log.d("FISHERNAME: ", fisherName);
                        Log.d("id telefono: ", phoneUUID);
                        //Log.d("es su TRAP?", a);




                        //finish();

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
                TextView textView = (TextView) dialog5.findViewById(android.R.id.message);
                textView.setTextSize(30);

                TextView textViewButton = (TextView) dialog5.findViewById(android.R.id.button1);
                textViewButton.setTextSize(18);
                TextView textViewButton2 = (TextView) dialog5.findViewById(android.R.id.button2);
                textViewButton2.setTextSize(18);
                TextView textViewButton3 = (TextView) dialog5.findViewById(android.R.id.button3);
                textViewButton3.setTextSize(18);

                Object a2 = adapterGlobal.getItem(position);
                //Log.d("OBJETOOOOOOO", a2.toString());

            }
        });
        //SystemClock.sleep(1000);   SLEEPO

        //Getting reference to Firebase Database with all the deployments.
        dref = FirebaseDatabase.getInstance().getReference("deployments");
        //Adding listener to the DB reference, add child event listener <-IMPORTANT to notice what kind of event.
        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Deployment d = dataSnapshot.getValue(Deployment.class);
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                double location [] = getLocation();
                double lat2 = location[0];
                double long2 =  location[1];
                //Log.d("LAFFFFFFAA", d.getDeploymentDate().toString());
                Date daysAddedDate = addDays(d.getDeploymentDate(),d.getExpirationTime());
                //IF dayDif is 1 the trap has NOT expired yet. Meaning today's date (the argument) is less than the daysAddedDate.
                int dayDif = daysAddedDate.compareTo(new Date());
                String isExpiredSt;
                boolean isExpired;
                if(dayDif <0){
                    isExpiredSt = "TRAP EXPIRED~~~";
                    isExpired=true;
                }else if (dayDif ==0){
                    isExpiredSt = "TRAP EXPIRES TODAY";
                    isExpired=false;
                }else{
                    isExpiredSt = "TRAP OK";
                    isExpired=false;
                }
                //Log.d("LA DIFFFFFF", isExpiredSt);

                DecimalFormat df2 = new DecimalFormat(".##");
                DistanceCalculator calculator = new DistanceCalculator();

                double dist = calculator.distance(d.getLatitude(),lat2,d.getLongitude(),long2,0,0);

                String fisherUUID = d.getUuid();

                File installation;
                String phoneUUID;
                try {
                    installation = new File(MainActivity.this.getFilesDir(), "INSTALLATION");
                    phoneUUID =  Installation.readInstallationFile(installation);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }


                dist = dist/1000;
                if(dist < MAX_DISTANCE_RANGE  && (  (fisherUUID.equals(phoneUUID) ) || ((dist *0.53996 <= d.getVisibilityRange())&&!isExpired) )){
                    HashMap<String,String> temp=new HashMap<String, String>();
                    temp.put(FIRST_COLUMN, d.getID());
                    temp.put(SECOND_COLUMN, d.getGearNumber());


                    String res2 =df2.format(dist*0.53996);
                    temp.put(THIRD_COLUMN, res2 +" nmi");
//                  if(list2.size() <=11)
//
                    list2.add(temp);
                    Log.d("AAAAAAABBBBBBBBBBBBBBB", list2.toString());


                    HashMap<String,String>[] harr =list2.toArray(new HashMap[list2.size()]);
                    sortList(harr);
                    ArrayList<HashMap<String, String>> orderedlist2 = new ArrayList<HashMap<String, String>>();

                    for(int i =0;i<list2.size();i++){
                        Log.d("KKKKKKKKKK", harr[i].get("First"));
                        orderedlist2.add(harr[i]);

                    }
                    Log.d("AAAAAAABBBBBBBBBBBBBBB", orderedlist2.toString());

                    list2 = orderedlist2;//new ArrayList(Arrays.asList(harr));

//                    ArrayList<HashMap<String, String>> displayedList = new ArrayList<HashMap<String, String>>();
//                    for (int i=0; i<12; i++) {
//                        //Log.d("PPPPPPPPPPPPP", o.toString());
//                        displayedList.add(list2.get(i));
//                    }
////                    String res =df2.format(dist/1000);
////                    list.add(res);
//                    list2 = displayedList;
                      final ListViewAdapter adapter3 = new ListViewAdapter(MainActivity.this, list2);
//                    listView.setAdapter(adapter2);
//                    adapter2.notifyDataSetChanged();
                    listView.setAdapter(adapter3);
                    adapterGlobal = (ListViewAdapter) listView.getAdapter();
                    adapter3.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Deployment d = dataSnapshot.getValue(Deployment.class);
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                double location [] = getLocation();
                double lat2 = location[0];
                double long2 =  location[1];
                //Log.d("LAFFFFFFAA", d.getDeploymentDate().toString());
                Date daysAddedDate = addDays(d.getDeploymentDate(),d.getExpirationTime());
                //IF dayDif is 1 the trap has NOT expired yet. Meaning today's date (the argument) is less than the daysAddedDate.
                int dayDif = daysAddedDate.compareTo(new Date());
                String isExpiredSt;
                boolean isExpired;
                if(dayDif <0){
                    isExpiredSt = "TRAP EXPIRED~~~";
                    isExpired=true;
                }else if (dayDif ==0){
                    isExpiredSt = "TRAP EXPIRES now";
                    isExpired=true;
                }else{
                    isExpiredSt = "TRAP OK";
                    isExpired=false;
                }
                Log.d("LA DIFFFFFF", isExpiredSt);

                DecimalFormat df2 = new DecimalFormat(".##");
                DistanceCalculator calculator = new DistanceCalculator();

                double dist = calculator.distance(d.getLatitude(),lat2,d.getLongitude(),long2,0,0);

                String fisherUUID = d.getUuid();

                File installation;
                String phoneUUID;
                try {
                    installation = new File(MainActivity.this.getFilesDir(), "INSTALLATION");
                    phoneUUID =  Installation.readInstallationFile(installation);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }


                dist = dist/1000;
                if(dist < MAX_DISTANCE_RANGE  && (  (fisherUUID.equals(phoneUUID) ) || ((dist *0.53996 <= d.getVisibilityRange())&&!isExpired) )){
                    HashMap<String,String> temp=new HashMap<String, String>();
                    temp.put(FIRST_COLUMN, d.getID());
                    temp.put(SECOND_COLUMN, d.getGearNumber());


                    String res2 =df2.format(dist*0.53996);
                    temp.put(THIRD_COLUMN, res2 +" nmi");
                    for(HashMap<String, String> a : list2){
                        Log.d("compa fisher: "+d.getID(), "con: "+a.get("First"));

                        if(a.get("First").equals(d.getID())){
                            list2.remove(a);
                            break;
                        }
                    }
                    if(list2.size() <=11)
                        list2.add(temp);
                    Log.d("AAAAAAABBBBBBBBBBBBBBB", list2.toString());


                    HashMap<String,String>[] harr =list2.toArray(new HashMap[list2.size()]);
                    sortList(harr);
                    ArrayList<HashMap<String, String>> orderedlist2 = new ArrayList<HashMap<String, String>>();

                    for(int i =0;i<list2.size();i++){
                        Log.d("KKKKKKKKKK", harr[i].get("First"));
                        orderedlist2.add(harr[i]);

                    }
                    Log.d("AAAAAAABBBBBBBBBBBBBBB", orderedlist2.toString());

                    list2 = orderedlist2;//new ArrayList(Arrays.asList(harr));

                    for (HashMap<String, String> o : list2) {
                        Log.d("PPPPPPPPPPPPP", o.toString());

                    }
//                    String res =df2.format(dist/1000);
//                    list.add(res);

                    final ListViewAdapter adapter3 = new ListViewAdapter(MainActivity.this, list2);
//                    listView.setAdapter(adapter2);
//                    adapter2.notifyDataSetChanged();
                    listView.setAdapter(adapter3);
                    adapterGlobal = (ListViewAdapter) listView.getAdapter();
                    adapter3.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
/*                String key = dataSnapshot.getKey();

                ///////////////////////////FIX/////////////////////////////////////

                for (int i = 0; i < list2.size(); i++) {
                    // Find the item to remove and then remove it by index
                    HashMap a = list2.get(i);
                    Object b=a.get(key);
                    if (b!=null) {
                        list2.remove(i);
                        break;
                    }
                }
                ////////////////////////////////////////////////////////////////////////

//                list.remove(dataSnapshot.getValue(Deployment.class));
//                adapter.notifyDataSetChanged();
//
//                list2.remove(dataSnapshot.getValue(Deployment.class));
                //listView.setAdapter(adapter2);  //CHEEEEEEEEEEECKKK
                adapter2.notifyDataSetChanged();*/



//                final ListViewAdapter adapter4= new ListViewAdapter(MainActivity.this,  new ArrayList<HashMap<String,String>>());
////                    listView.setAdapter(adapter2);
////                    adapter2.notifyDataSetChanged();
//                listView.setAdapter(adapter4);
//                //adapterGlobal = (ListViewAdapter) listView.getAdapter();
//                adapter4.notifyDataSetChanged();
//                SystemClock.sleep(5000);
                Deployment d = dataSnapshot.getValue(Deployment.class);
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                double location [] = getLocation();
                double lat2 = location[0];
                double long2 =  location[1];
                //Log.d("LAFFFFFFAA", d.getDeploymentDate().toString());
                Date daysAddedDate = addDays(d.getDeploymentDate(),d.getExpirationTime());
                //IF dayDif is 1 the trap has NOT expired yet. Meaning today's date (the argument) is less than the daysAddedDate.
                int dayDif = daysAddedDate.compareTo(new Date());
                String isExpiredSt;
                boolean isExpired;
                if(dayDif <=0){
                    isExpiredSt = "TRAP EXPIRED~~~";
                    isExpired=true;
                }else if (dayDif ==0){
                    isExpiredSt = "TRAP EXPIRES TODAY";
                    isExpired=false;
                }else{
                    isExpiredSt = "TRAP OK";
                    isExpired=false;
                }
                Log.d("LA DIFFFFFF", isExpiredSt);

                DecimalFormat df2 = new DecimalFormat(".##");
                DistanceCalculator calculator = new DistanceCalculator();

                double dist = calculator.distance(d.getLatitude(),lat2,d.getLongitude(),long2,0,0);

                String fisherUUID = d.getUuid();

                File installation;
                String phoneUUID;
                try {
                    installation = new File(MainActivity.this.getFilesDir(), "INSTALLATION");
                    phoneUUID =  Installation.readInstallationFile(installation);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }


                dist = dist/1000;
                if(dist < MAX_DISTANCE_RANGE  && (  (fisherUUID.equals(phoneUUID) ) || ((dist *0.53996 <= d.getVisibilityRange())&&!isExpired) )){
                    HashMap<String,String> temp=new HashMap<String, String>();
                    temp.put(FIRST_COLUMN, d.getID());
                    temp.put(SECOND_COLUMN, d.getGearNumber());


                    String res2 =df2.format(dist*0.53996);
                    temp.put(THIRD_COLUMN, res2 +" nmi");
                    list2.remove(temp);
                    Log.d("AAAAAAABBBBBBBBBBBBBBB", list2.toString());


                    HashMap<String,String>[] harr =list2.toArray(new HashMap[list2.size()]);
                    sortList(harr);
                    ArrayList<HashMap<String, String>> orderedlist2 = new ArrayList<HashMap<String, String>>();

                    for(int i =0;i<list2.size();i++){
                        Log.d("KKKKKKKKKK", harr[i].get("First"));
                        orderedlist2.add(harr[i]);

                    }
                    Log.d("AAAAAAABBBBBBBBBBBBBBB", orderedlist2.toString());

                    list2 = orderedlist2;//new ArrayList(Arrays.asList(harr));

                    for (HashMap<String, String> o : list2) {
                        Log.d("PPPPPPPPPPPPP", o.toString());

                    }
//                    String res =df2.format(dist/1000);
//                    list.add(res);

                    final ListViewAdapter adapter3 = new ListViewAdapter(MainActivity.this, list2);
//                    listView.setAdapter(adapter2);
//                    adapter2.notifyDataSetChanged();
                    listView.setAdapter(adapter3);
                    adapterGlobal = (ListViewAdapter) listView.getAdapter();
                    adapter3.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       // adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        //listView = (ListView) findViewById(R.id.database_list_view);
        //listView.setAdapter(adapter);
       // String value = dataSnapshot.getValue(String.class);

/*
        // Read from the database
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("tsg", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tag", "Failed to read value.", error.toException());
            }
        });
        */

//        SystemClock.sleep(2000);
//        Intent intent = getIntent();
//        finish();
//        startActivity(intent);
    }

    public void sortList(HashMap<String,String>[] listToSort){
        Arrays.sort(listToSort, new Comparator<HashMap<String,String>>() {
            public int compare(HashMap<String,String> o1, HashMap<String,String> o2) {
                String t1 = o1.get("Third");
                String t2 = o2.get("Third");
                String s1 =t1.substring(0, t1.length() - 3);
                String s2 =t2.substring(0, t2.length() - 3);
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
    public void testingFirebaseStorage(){
        Uri file = Uri.fromFile(new File("rivers1.jpg"));
        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

//    //trying to dynamically refresh ListView. Not got it yet.
//    public void refreshListView(View view){
//        //https://stackoverflow.com/questions/5320358/update-listview-dynamically-with-adapter
//        adapterGlobal=new ListViewAdapter(this, list2);
//        listView.setAdapter(adapterGlobal);  //CHEEEEEEEEEEECKKK
//        adapterGlobal.notifyDataSetChanged();
//    }

    public void startDirections(View view){
        Intent intent = new Intent( Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=Monterey&travelmode=driving&dir_action=navigate&travelmode"));
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

//    public static double distance(double lat1, double lat2, double lon1,
//                                  double lon2, double el1, double el2) {
//
//        final int R = 6371; // Radius of the earth
//
//        double latDistance = Math.toRadians(lat2 - lat1);
//        double lonDistance = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = R * c * 1000; // convert to meters
//
//        double height = el1 - el2;
//
//        distance = Math.pow(distance, 2) + Math.pow(height, 2);
//
//        return Math.sqrt(distance);
//    }

    //Code to execute asynchronous tasks in the background.
    private class AsyncTaskEx extends AsyncTask<Void, Void, Void> {
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
https://stackoverflow.com/questions/10733682/make-a-specific-code-run-in-background
http://www.businessinsider.com/how-facebook-finds-exceptional-employees-2016-2/#facebook-looks-for-talent-and-cultural-fit-3
    // Write this class inside your Activity and call where you want execute your method
    new AsyncTaskEx().execute();
 */
}
