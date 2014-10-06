package com.wowo.api;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;

import java.util.List;

/**
 * Created by tinyao on 10/4/14.
 */
public abstract class MFindCallback<T extends com.avos.avoscloud.AVObject> extends FindCallback {

    public MFindCallback() {

    }

    @Override
    public void done(List list, AVException e) {

    }
}
