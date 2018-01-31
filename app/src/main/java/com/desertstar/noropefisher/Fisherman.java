package com.desertstar.noropefisher;

import java.util.ArrayList;

/**
 * Created by Iker Redondo on 1/31/2018.
 */

public class Fisherman {

    public  String id;

    public ArrayList<Deployment> deployments ;

    public Fisherman(String id) {
        this.id = id;
    }

    public ArrayList<Deployment> getDeployments() {
        return deployments;
    }

    public void setDeployments(ArrayList<Deployment> deployments) {
        this.deployments = deployments;
    }

    public void addDeploymet(Deployment myDeployment){
        this.deployments.add(myDeployment);
    }

}
