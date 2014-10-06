package com.wowo.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.wowo.WoApplication;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tinyao on 9/16/14.
 */
public class User extends AVUser{

    public void getUserName() {
        this.getString("username");
    }

    public void setEmail(String email) {
        this.put("email", email);
    }

    public String getEmail() {
        return this.getString("email");
    }

    /**
     *
     * @param username
     * @param password
     * @param loginCallback
     */
    public static void login(String username, String password, LogInCallback loginCallback) {
        AVUser.logInInBackground(username, password, loginCallback, User.class);
    }


    public static User getCurrentUser() {
        return User.getCurrentUser(User.class);
    }

//    public static void like(Ama ama, boolean isLike) {
//        AVUser currentUser = AVUser.getCurrentUser();
//        AVRelation<AVObject> relation = currentUser.getRelation("likes");
//        if (isLike)
//            relation.add(ama);
//        else
//            relation.remove(ama);
//        currentUser.saveInBackground();
//
//        WoApplication.like(ama.getObjectId(), isLike);
//    }

//    public static void like(Ama ama, SaveCallback mSaveCallback) {
//        AVUser currentUser = AVUser.getCurrentUser();
//        AVRelation<AVObject> relation = currentUser.getRelation("likes");
//        relation.add(ama);
//        currentUser.saveInBackground(mSaveCallback);
//    }

//    public static void dislike(Ama ama) {
//        AVUser currentUser = AVUser.getCurrentUser();
//        AVRelation<AVObject> relation = currentUser.getRelation("likes");
//        relation.remove(ama);
//        currentUser.saveInBackground();
//    }

//    public static boolean isLike(Ama ama) {
//        return WoApplication.likedAmaIds.contains(ama.getObjectId());
//    }



}
