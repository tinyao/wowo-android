package com.wowo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.location.Location;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.wowo.model.Wowo;
import com.wowo.model.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinyao on 9/15/14.
 */
public class WoApplication extends Application {

    private static final String APP_ID = "6ejjb5kzc8hn1xunm9nw5cgjugjoqvvhp0dv11pz8y7grchk";
    private static final String APP_KEY = "yenqgluojksez7fw0tu05zribd1xcmgja8q4wewu3t3bbcnd";

    private static List<Comment> likedComments;

    private static SharedPreferences sharedPreferences;

    private static Context sContext;

    private static Location mLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        AVObject.registerSubclass(Wowo.class);
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
        AVObject.registerSubclass(Comment.class);
        templatesLarge = getResources().obtainTypedArray(R.array.templates_l);
        colorBGs = getResources().obtainTypedArray(R.array.colors_l);

    }

    public static Context getContext() {
        return sContext;
    }

    private static List<Wowo> wowoArray;

    public static List<Wowo> getAmaList() {
        if(wowoArray ==null) {
            wowoArray = new ArrayList<Wowo>();
        }
        return wowoArray;
    }

    public static void setAmaList(List<Wowo> array) {
        wowoArray = array;
    }

    private static TypedArray templatesLarge, colorBGs;

    public static TypedArray getTemplates() {
        if (templatesLarge==null) {
            Log.d("DEBUG", "templates null");
        }
        return templatesLarge;
    }

    public static TypedArray getColors() {
        if (colorBGs==null) {
            Log.d("DEBUG", "templates null");
        }
        return colorBGs;
    }

//    public static SharedPreferences getSharedPref() {
//
//    }

    public static String likedAmaIds;

    public static void like(String objId, boolean isLike) {
        sharedPreferences.edit().putString("ObjectIds", likedAmaIds + "," + objId).commit();
    }

    public static SharedPreferences getPrefs() {
        if (sharedPreferences == null) {
            sharedPreferences = getContext().getSharedPreferences("wowo_pref", MODE_PRIVATE);
        }
        return sharedPreferences;
    }

}
