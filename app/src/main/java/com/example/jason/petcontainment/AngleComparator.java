package com.example.jason.petcontainment;

import java.util.Comparator;

/**
 * Created by jason on 3/17/2016.
 */
public class AngleComparator implements Comparator<GpsPoint> {
    @Override
    public int compare(GpsPoint a, GpsPoint b){
        return a.angle_to_center < b.angle_to_center ? -1 :
                a.angle_to_center == b.angle_to_center ? 0 : 1;
    }
}
