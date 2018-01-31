package com.desertstar.noropefisher;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by Iker Redondo on 1/17/2018.
 */

@IgnoreExtraProperties
public class Deployment {

    public String id;
    public String uuid;



    public String gearNumber;
    public double latitude;
    public double longitude;
    public int expirationTime;
    public double visibilityRange;
    public Date deploymentDate;



    public Deployment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Deployment(String id, String uuid, String gearNumber, double latitude, double longitude, int expirationTime, double visibilityRange, Date deploymentDate) {
        this.id = id;
        this.uuid = uuid;
        this.gearNumber = gearNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expirationTime = expirationTime;
        this.visibilityRange = visibilityRange;
        this.deploymentDate = deploymentDate;
    }

    public String getID(){
        return this.id;
    }


    public String getUuid() {
        return uuid;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setGearNumber(String gearNumber) {
        this.gearNumber = gearNumber;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public int getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }

    public double getVisibilityRange() {
        return visibilityRange;
    }

    public void setVisibilityRange(double visibilityRange) {
        this.visibilityRange = visibilityRange;
    }

    public Date getDeploymentDate() {
        return deploymentDate;
    }

    public void setDeploymentDate(Date deploymentDate) {
        this.deploymentDate = deploymentDate;
    }

    public String getGearNumber(){
        return this.gearNumber;
    }

    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }



}