package com.example.jason.petcontainment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jason on 3/17/2016.
 */
public class GpsPoint {
    public static LatLng center;
    public LatLng location;
    public double angle_to_center;

    //Constructor: can take an input LatLng and set location
    public GpsPoint(LatLng loc_inp){
        location = loc_inp;
    }

    /*
        Returns the center location of a point given an ArrayList of GpsPoints.
        The return is of type LatLng.
     */
    public static LatLng findCenter(ArrayList<GpsPoint> list){
        LatLng center;
        double latsum = 0, lngsum = 0;
        for (int i = 0; i < list.size(); ++i) {
            latsum += list.get(i).location.latitude;
            lngsum += list.get(i).location.longitude;
        }
        center = new LatLng(latsum/list.size(),lngsum/list.size());
        return center;
    }

    /*
       Sorts a given input ArrayList of GpsPoints in counterclockwise order.  The method
       returns an ArrayList in sorted order of same size as input ArrayList.
    */
    public static ArrayList<GpsPoint> sortPoints(ArrayList<GpsPoint> input){
        ArrayList<GpsPoint> sorted = new ArrayList<GpsPoint>();
        //copy contents of input into sorted
        for (int i = 0; i < input.size(); ++i){
            sorted.add(input.get(i));
        }

        LatLng center = GpsPoint.center; //find avg location of all input points

        //compute each point's angle relative to the center point
        for (int i = 0; i < sorted.size(); ++i){
            double lat = sorted.get(i).location.latitude;
            double lng = sorted.get(i).location.longitude;
            sorted.get(i).angle_to_center = Math.atan2(lat-center.latitude, lng-center.longitude);
        }
        Collections.sort(sorted, new AngleComparator());
        //debugging purposes
        System.out.println("Center coordinate: " + GpsPoint.center);
        System.out.println("Sort points based on angle_to_center:");
        for (int i = 0; i < sorted.size(); ++i) {
            System.out.println(sorted.get(i).angle_to_center);
        }
        return sorted;
    }
}
