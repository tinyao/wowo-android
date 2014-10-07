package com.wowo.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.wowo.R;
import com.wowo.WoApplication;
import com.wowo.utils.TimeUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tinyao on 9/16/14.
 */
@AVClassName("Wowo")
public class Wowo extends AVObject{

    // author, title, body, category, color, photo, location, vote, createdAt

    public void setTitle(String title) {
        this.put("title", title);
    }

    public String getTitle() {
        return this.getString("title");
    }

    public void setBody(String title) {
        this.put("body", title);
    }

    public String getBody() {
        return this.getString("body");
    }

    public void setAuthor(AVUser mUser) {
        this.put("author", mUser);
    }

    public User getAuthor() {
        return this.getAVUser("author", User.class);
    }

    public void setCategory(int catId) {
        this.put("category", catId);
    }

    public int getCategory() {
        return this.getInt("category");
    }

    public String getCategoryName() {
        if(getCategory() <= Category.values().length)
            return Category.values()[getCategory()].getDisplayName();
        else
            return Category.others.getDisplayName();
    }

    public void setColorId(int colorId) {
        this.put("color", colorId);
    }

    public int getColorId() {
        return this.getInt("color");
    }

    public int getVoteScore() {
        return this.getInt("vote");
    }

    public boolean isNearbyOnly() {
        return this.getBoolean("nearbyOnly");
    }

    public void setNearybyOnly(boolean is) {
        this.put("nearbyOnly", is);
    }

    /**
     *
     * @return
     */
    public int getColor() {
        return WoApplication.getColors().getResourceId(getColorId(), 0);
    }

    public void setPhoto(AVFile imageFile) {
        this.put("photo", imageFile);
    }

    public String getPhotoUrl() {
        AVFile file = this.getAVFile("photo");
        if (file != null){
            return file.getUrl();
        } else {
            return null;
        }
    }

    public void setLocation() {

    }

    public AVGeoPoint getLocation() {
        return this.getAVGeoPoint("location");
    }

    public void setLocation(AVGeoPoint geoPoint) {
        this.put("location", geoPoint);
    }


    public void like(boolean isLike) {
        if(isLike)
            this.increment("vote");
        else
            this.increment("vote", -1);

        cachedWowoLike(isLike);
    }

    public int getVote() {
        return this.getInt("vote");
    }

    private static Set<String> getCachedLikedWowos() {
        Set<String> likedWowoSet = WoApplication.getPrefs().getStringSet("liked_wowos", null);
        return likedWowoSet;
    }

    public static void updateCachedLikedWowos(List<Wowo> likedWowos) {
        Set<String> likedWowoIds = new HashSet<String>();
        for (Wowo item : likedWowos) {
            likedWowoIds.add(item.getObjectId());
        }
        WoApplication.getPrefs().edit().putStringSet("liked_wowos", likedWowoIds).commit();
    }

    public void cachedWowoLike(boolean isLike) {
        Set<String> likedWowoSet = getCachedLikedWowos();
        if(isLike) {
            likedWowoSet.add(this.getObjectId());
        } else {
            if(likedWowoSet.contains(this.getObjectId()))
                likedWowoSet.remove(this.getObjectId());
        }
        WoApplication.getPrefs().edit().putStringSet("liked_comments", likedWowoSet).commit();
    }

    public boolean isCurrentUserLike() {
        Set<String> likedWowoSet = getCachedLikedWowos();
        return likedWowoSet.contains(this.getObjectId());
    }


    @Override
    public String toString() {
        return super.toString();
    }

    public static void refreshForLatest(int startId, FindCallback<Wowo> mFindCallback) {
        AVQuery<Wowo> query = new AVQuery<Wowo>("Ama");
        query.whereGreaterThan("amaId", startId);
        query.orderByDescending("createdAt");
        query.findInBackground(mFindCallback);
    }

    public HashMap toMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("objectId", this.getObjectId());
        map.put("text", this.getTitle());
        map.put("body", this.getBody());
        map.put("color", this.getPhotoUrl()!=null ? R.color.action_bar_color : getColor());
        map.put("photoUrl", this.getPhotoUrl());
        map.put("author", this.getAuthor().getObjectId());
        map.put("category", this.getCategoryName());
        map.put("time", TimeUtils.getTimeAgo(this.getCreatedAt()));
        map.put("score", this.getVoteScore());
        map.put("isLiked", this.isCurrentUserLike());
        return map;
    }

}
