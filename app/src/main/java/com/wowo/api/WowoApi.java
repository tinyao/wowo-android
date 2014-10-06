package com.wowo.api;

import android.os.Environment;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.wowo.WoApplication;
import com.wowo.model.Category;
import com.wowo.model.Wowo;
import com.wowo.model.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by tinyao on 9/18/14.
 */
public class WowoApi {

    /**
     * 获取最新的 wowo
     *
     * @param mCallback
     */
    public static void latestWowo(Category mCategory, FindCallback<Wowo> mCallback, boolean fromCache) {

        AVQuery<Wowo> query = new AVQuery<Wowo>("Wowo");
        if (mCategory.ordinal() != 0)
            query.whereEqualTo("category", mCategory.ordinal());
        query.orderByDescending("createdAt");
        query.setLimit(20);
        query.findInBackground(mCallback);

//        AVQuery<Wowo> query = AVQuery.getQuery("Wowo");
////        if (fromCache) {
////            query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
////        } else {
////            query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
////        }
//        query.orderByDescending("createdAt");
////        if (endAma != null) {
////            query.whereGreaterThan("objectId", endAma.getObjectId());
////        }
//        query.setLimit(20);
//        query.findInBackground(mCallback);
    }

    public static void logIn() {

    }

    public static void register() {

    }


    /**
     * 获取下一页Wowo
     *
     * @param startWowo
     * @param mCallback
     */
    public static void nextWowo(Wowo startWowo, Category mCategory, FindCallback<Wowo> mCallback) {
        AVQuery<Wowo> query = AVQuery.getQuery("Wowo");
        if (mCategory.ordinal() != 0)
            query.whereEqualTo("category", mCategory.ordinal());
        query.orderByDescending("createdAt");
        query.whereLessThan("objectId", startWowo.getObjectId());
        query.setLimit(20);
        query.findInBackground(mCallback);
    }

    public static void publishAma(String text, String imagePath,
                                  double latitude, double longtitue,
                                  final SaveCallback mSaveCallback) {
        final Wowo wowo = new Wowo();
        AVUser currentUser = AVUser.getCurrentUser();
        wowo.setAuthor(currentUser);
        wowo.setTitle(text);

        AVGeoPoint point = new AVGeoPoint(latitude, longtitue);
        wowo.setLocation(point);

        if (imagePath == null) {
            wowo.saveInBackground(mSaveCallback);
        } else {

            try {
                final AVFile imageFile = AVFile.withAbsoluteLocalPath("test.jpg",
                        Environment.getExternalStorageDirectory() + "/test.jpg");
                imageFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            wowo.put("image", imageFile);
                            wowo.saveInBackground(mSaveCallback);
                        } else {
                            mSaveCallback.done(new AVException(-1000, "图片上传失败"));
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void comments(AVObject wowo, FindCallback<Comment> callback) {
        AVQuery<Comment> query = new AVQuery<Comment>("QA");
        query.whereEqualTo("wowo", wowo);
        query.orderByAscending("createdAt");
        query.setLimit(30);
        query.findInBackground(callback);
    }

    public static void questions(AVObject wowo, FindCallback<Comment> callback) {
        AVQuery<Comment> query = new AVQuery<Comment>("QA");
        query.whereEqualTo("wowo", wowo);
        query.whereNotEqualTo("isAsk", false);
        query.orderByDescending("vote");
        query.setLimit(20);
        query.findInBackground(callback);
    }

    public static void answers(AVObject wowo, List<Comment> questions, FindCallback<Comment> callback) {
        AVQuery<Comment> query = new AVQuery<Comment>("QA");
        query.whereEqualTo("wowo", wowo);
        query.whereEqualTo("isAsk", false);
        query.whereContainedIn("superQA", questions);
        query.orderByDescending("createdAt");
        query.setLimit(100);
        query.findInBackground(callback);
    }

    /**
     * @param body
     * @param wowo
     * @param mSaveCallback
     * @return
     */
    public static Comment comment(String body, final AVObject wowo,
                                  String woAuthorId,
                                  String quote,
                                  final SaveCallback mSaveCallback) {
        final Comment comm = new Comment();
        comm.setBody(body);
        // 评论者
        comm.setAuthor(AVUser.getCurrentUser());
        // 评论的Wowo
        comm.setWowo(wowo);
        comm.setLZ(woAuthorId.equals(AVUser.getCurrentUser().getObjectId()));
        if (quote != null) comm.setQuote(quote);

        // 查询判断用户是否已经评论过post，如果是获取已经随机的头像
        AVQuery<Comment> query = new AVQuery<Comment>("QA");
        query.whereEqualTo("wowo", wowo);
        query.whereEqualTo("author", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, AVException e) {
                if (e == null && comments != null && comments.size() >= 1) {
                    comm.setAvatar(comments.get(0).getAvatar());
                } else {
                    // 随机设置头像
                    String url = "http://mimi.wumii.cn/resources/avatar/" + new Random().nextInt(1106) + ".png";
                    comm.setAvatar(url);
                }

                // 保存
                comm.setFetchWhenSave(true);
                comm.saveInBackground(mSaveCallback);
            }
        });

        return comm;
    }

    public static void likeComment(final Comment comment, final boolean isLike, SaveCallback mSaveCallback) {
        AVUser mUser = AVUser.getCurrentUser();
        AVRelation<Comment> relation = mUser.getRelation("comment_like");

        if (isLike) {
            relation.add(comment);
        } else {
            relation.remove(comment);
        }

        comment.like(isLike);

        mUser.setFetchWhenSave(true);
        mUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {

            }
        });

        comment.setFetchWhenSave(true);
        comment.saveInBackground();
    }

    public static void likeWowo(final Wowo wowo, final boolean isLike, SaveCallback mSaveCallback) {
        AVUser mUser = AVUser.getCurrentUser();
        AVRelation<Wowo> relation = mUser.getRelation("wowo_liked");

        if (isLike) {
            relation.add(wowo);
        } else {
            relation.remove(wowo);
        }

        wowo.like(isLike);

        mUser.setFetchWhenSave(true);
        mUser.saveInBackground();

        wowo.setFetchWhenSave(true);
        wowo.saveInBackground();
    }

    public static void likedComments(FindCallback<Comment> mCallback) {
        AVUser user = AVUser.getCurrentUser();
        AVRelation<Comment> relation = user.getRelation("comment_like");
        relation.getQuery()
                .orderByDescending("createdAt")
                .setLimit(200)
                .findInBackground(mCallback);
    }

    public static void likedWowos(FindCallback<Wowo> mCallback) {
        AVUser user = AVUser.getCurrentUser();
        AVRelation<Wowo> relation = user.getRelation("wowo_liked");
        relation.getQuery()
                .orderByDescending("createdAt")
                .setLimit(200)
                .findInBackground(mCallback);
    }



//    public static void likeComment(FindCallback<Wowo> callback) {
//        AVUser currentUser = AVUser.getCurrentUser();
//        AVRelation<Wowo> relation = currentUser.getRelation("likes");
//        relation.getQuery().findInBackground(callback);
//    }



}
