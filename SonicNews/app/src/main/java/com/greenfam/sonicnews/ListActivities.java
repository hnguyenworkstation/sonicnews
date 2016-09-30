package com.greenfam.sonicnews;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 9/29/16.
 */

public class ListActivities {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
