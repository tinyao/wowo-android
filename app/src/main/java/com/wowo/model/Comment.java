package com.wowo.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.wowo.WoApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tinyao on 9/16/14.
 */
@AVClassName("QA")
public class Comment extends AVObject {

    public void setBody(String body) {
        this.put("body", body);
    }

    public String getBody() {
        return this.getString("body");
    }

    public String getWowoObjectId() {
        try {
            return this.getAVObject("Wowo", Wowo.class).getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getVote() {
        return this.getInt("vote");
    }

    public void like(boolean isLike) {
        if(isLike)
            this.increment("vote");
        else
            this.increment("vote", -1);

        cachedCommLike(isLike);
    }

    public boolean isAsk() {
        return this.getBoolean("isAsk");
    }

    public void setAsk(boolean isAsk) {
        this.put("isAsk", isAsk);
    }

    public void setWowo(AVObject wowo) {
        this.put("wowo", wowo);
    }

    public String getAvatar() {
        return this.getString("avatar");
    }

    public void setAvatar(String url) {
        this.put("avatar", url);
    }

    public User getAuthor() {
        return this.getAVUser("author", User.class);
    }

    public void setAuthor(AVUser mUser) {
        this.put("author", mUser);
    }

    public String getSuperQA() {
        try {
            return this.getAVObject("superQA", Comment.class).getObjectId();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isLZ() {
        return this.getBoolean("isLZ");
    }

    public void setLZ(boolean isLZ) {
        this.put("isLZ", isLZ);
    }

    public String getQuote() {
        return this.getString("quote");
    }

    public void setQuote(String quote) {
        this.put("quote", quote);
    }

//    public boolean isLike() {
//        // 从该用户的喜欢列表中，判断是否喜欢了这个
//
//        return false;
//    }

    private static Set<String> getCachedLikedComments() {
        Set<String> likedCommSet = WoApplication.getPrefs().getStringSet("liked_comments", null);
        return likedCommSet;
    }

    public static void updateCachedLikedComments(List<Comment> likedComm) {
        Set<String> likedCommIds = new HashSet<String>();
        for (Comment item : likedComm) {
            likedCommIds.add(item.getObjectId());
        }
        WoApplication.getPrefs().edit().putStringSet("liked_comments", likedCommIds).commit();
    }

    public void cachedCommLike(boolean isLike) {
        Set<String> likedCommSet = getCachedLikedComments();
        if(isLike) {
            likedCommSet.add(this.getObjectId());
        } else {
            if(likedCommSet.contains(this.getObjectId()))
                likedCommSet.remove(this.getObjectId());
        }
        WoApplication.getPrefs().edit().putStringSet("liked_comments", likedCommSet).commit();
    }

    public boolean isCurrentUserLike() {
        Set<String> likedCommSet = getCachedLikedComments();
        return likedCommSet.contains(this.getObjectId());
    }

}
