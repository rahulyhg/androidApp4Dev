package com.desertstar.noropefisher;

/**
 * Created by Iker Redondo on 1/17/2018.
 */
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DeployActivity extends AppCompatActivity {
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private DatabaseReference mDatabase;
    SharedPreferences settings;
    Deployment myDepo;
    boolean everythingOKwithUUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy);

        //Log.d("QUEPASA", String.valueOf(doesContainGsfPackage(this)));

        settings = getSharedPreferences("com.desertstar.noropefisher", Context.MODE_PRIVATE);
        String fisherName = settings.getString("fisherName","");
        String expirationDays = settings.getString("expirationDays","");
        String visibilityRange = settings.getString("visibilityRange","");


        EditText editTextFisherName = (EditText) findViewById(R.id.editTextID);
        editTextFisherName.setText(fisherName);
//        EditText editTextGearNumber = (EditText) findViewById(R.id.editTextSerialNumber);
//        editTextGearNumber.setText("sup");
        EditText editTextExpirationDays = (EditText) findViewById(R.id.editTextExpiration);
        editTextExpirationDays.setText(expirationDays);
        EditText editTextVisibilityRange = (EditText) findViewById(R.id.editTextVisibility);
        editTextVisibilityRange.setText(visibilityRange);
    }

    //Method for DEPLOY button. Before showing a popup confirmation, It calls getLocation method and this phone's geolocation and saves all the data in the DB.
    public void deploy(View view) {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean everythingOK = getLocation();// GET GEOLOCATION and SAVE IT IN THE DATABASE (See below getLocation() method)

        //DialogFragment popUp = new AcknowledgeDialogFragment();


        if (everythingOK) {
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage("Deployment Information Saved").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                }
            }).show();

            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(30);

            TextView textViewButton = (TextView) dialog.findViewById(android.R.id.button1);
            textViewButton.setTextSize(30);
        }
        //popUp.show(getSupportFragmentManager(), "ok msg");
    }

    //It gets the geolocation and saves it in the Firebase Database.
    boolean getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                double latti = location.getLatitude();
                double longi = location.getLongitude();


                //DELETE. JUST A TEST OF getting distance between 2 coordinates.
                //Log.d("AAAAAAA", "latti is: " + latti);
                //Log.d("BBBBBBB", "longi is: " + longi);
//                double lat2 = 36.9;
//                double long2 =  -121.7;
//                double resDistance = distance(latti,lat2,longi,long2,0,0);
                //Log.d("CCCCCCCCC", "Distance is: " + resDistance);
                ////////////////////////////////////////////////////////////////////////


                //SAVING entered values into the Database.
                mDatabase = FirebaseDatabase.getInstance().getReference();
                EditText editTextFisherName = (EditText) findViewById(R.id.editTextID);
                EditText editTextGearNumber = (EditText) findViewById(R.id.editTextSerialNumber);
                final EditText editTextExpiration = (EditText) findViewById(R.id.editTextExpiration);
                final EditText editTextVisibility = (EditText) findViewById(R.id.editTextVisibility);



                    final String theFisherName = editTextFisherName.getText().toString();
                    final String theGearNumber = editTextGearNumber.getText().toString();

                if (!theFisherName.equals("") && !theGearNumber.equals("") && !editTextExpiration.getText().toString().equals("")  && !editTextVisibility.getText().toString().equals("") ) {
                    int theExpiration = Integer.parseInt(editTextExpiration.getText().toString());

                    if( theExpiration == 0 ) {
                        theExpiration = -1;
                    }

                    String visib = editTextVisibility.getText().toString();
                    if(visib.equals(".")){
                        visib = "0";
                    }
                    double theVisibility = Double.parseDouble(visib);
                    String currentDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
                    Date laDate = new Date();
                    //Log.d("DATEEEEEEEEEE", "DATE is: " + currentDate);
                    File installation;
                   final String elUUID;
                    try {
                        installation = new File(DeployActivity.this.getFilesDir(), "INSTALLATION");
                        elUUID =  Installation.readInstallationFile(installation);
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }

                    final Deployment user = new Deployment(theFisherName,elUUID ,theGearNumber,latti,longi, theExpiration,theVisibility,laDate );
                    Random rnd = new Random();
                    int whatever = rnd.nextInt();
                    //String.valueOf(whatever)+"-"+

                    mDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                            myDepo =  dataSnapshot.child("deployments").child(theFisherName+"-"+theGearNumber).getValue(Deployment.class);
                            myDepo =  dataSnapshot.child("deployments").child(theFisherName+"-"+theGearNumber).getValue(Deployment.class);

                            if (myDepo == null){
                                myDepo = user;
                            }

                            if( !myDepo.getUuid().equals(elUUID) ) {
                                AlertDialog dialog45 = new AlertDialog.Builder(DeployActivity.this).setMessage("These name and gear number are taken").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).show();
                                TextView textView = (TextView) dialog45.findViewById(android.R.id.message);
                                textView.setTextSize(25);
                                everythingOKwithUUID = false;

                            }else{
                                mDatabase.child("deployments").child(theFisherName+"-"+theGearNumber).setValue(user);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("fisherName",theFisherName);
                                editor.putString("expirationDays",editTextExpiration.getText().toString());
                                editor.putString("visibilityRange",editTextVisibility.getText().toString());
                                editor.commit();
                                AlertDialog dialog = new AlertDialog.Builder(DeployActivity.this).setMessage("Deployment Information Saved").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        finish();
                                    }
                                }).show();

                                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                                textView.setTextSize(30);

                                TextView textViewButton = (TextView) dialog.findViewById(android.R.id.button1);
                                textViewButton.setTextSize(30);
                                everythingOKwithUUID = true;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this).setMessage("Enter all the fills").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //finish();
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //finish();
                        }
                    }).show();
                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                    textView.setTextSize(25);

                    TextView textViewButton = (TextView) dialog.findViewById(android.R.id.button1);
                    textViewButton.setTextSize(25);
                    everythingOKwithUUID = false;
                    return everythingOKwithUUID;
                }

            } else {
                System.out.println("Unable to find correct location.");
                System.out.println("Unable to find correct location. ");

            }

        }
        return everythingOKwithUUID;
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

    public static boolean doesContainGsfPackage(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);

        for (PackageInfo pi : list) {
            if(pi.packageName.equals("com.google.android.gsf")) return true;  // ACCUWX.GSF_PACKAGE = com.google.android.gsf

        }

        return false;
    }

    //DELETE
//    public static double distance(double lat1, double lat2, double lon1,
//                                  double lon2, double el1, double el2) {
//        final int R = 6371; // Radius of the earth
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
}
