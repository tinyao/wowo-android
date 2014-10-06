/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wowo.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.plus.PlusOneButton;
import com.wowo.R;
import com.wowo.utils.LPreviewUtils;
import com.wowo.utils.LPreviewUtilsBase;
import com.wowo.utils.UIUtils;
import com.wowo.view.CheckableFrameLayout;
import com.wowo.view.ObservableScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * A fragment that shows detail information for a session, including session title, abstract,
 * time information, speaker photos and bios, etc.
 */
public class SessionDetailFragment extends Fragment implements
        ObservableScrollView.Callbacks {

//    private static final String TAG = makeLogTag(SessionDetailFragment.class);

    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;

    public static final String VIEW_NAME_PHOTO = "photo";

    private Handler mHandler = new Handler();
    private static final int TIME_HINT_UPDATE_INTERVAL = 10000; // 10 sec

//    private TagMetadata mTagMetadata;

    private String mSessionId;
    private Uri mSessionUri;

    private long mSessionStart;
    private long mSessionEnd;
    private String mTitleString;
    private String mHashTag;
    private String mUrl;
    private String mRoomId;
    private String mRoomName;
    private String mTagsString;

    // A comma-separated list of speakers to be passed to Android Wear
    private String mSpeakers;

    private boolean mStarred;
    private boolean mInitStarred;
    private boolean mDismissedWatchLivestreamCard = false;
    private boolean mHasLivestream = false;
    private MenuItem mSocialStreamMenuItem;
    private MenuItem mShareMenuItem;

    private ViewGroup mRootView;
    private View mScrollViewChild;
    private TextView mTitle;
    private TextView mSubtitle;
    private PlusOneButton mPlusOneButton;

    private ObservableScrollView mScrollView;
    private CheckableFrameLayout mAddScheduleButton;

    private TextView mAbstract;
    private View mHeaderBox;
    private View mHeaderContentBox;
    private View mHeaderBackgroundBox;
    private View mHeaderShadow;
    private View mDetailsContainer;

    private boolean mSessionCursor = false;
    private boolean mSpeakersCursor = false;
    private boolean mHasSummaryContent = false;

//    private ImageLoader mSpeakersImageLoader, mNoPlaceholderImageLoader;
    private List<Runnable> mDeferredUiOperations = new ArrayList<Runnable>();

    private StringBuilder mBuffer = new StringBuilder();

    private int mHeaderTopClearance;
    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;
    private int mAddScheduleButtonHeightPixels;

    private boolean mHasPhoto;
    private View mPhotoViewContainer;
    private ImageView mPhotoView;
    boolean mGapFillShown;
    private int mSessionColor;
    private String mLivestreamUrl;

    private static final float GAP_FILL_DISTANCE_MULTIPLIER = 1.5f;

    private Runnable mTimeHintUpdaterRunnable = null;

    private boolean mAlreadyGaveFeedback = false;
    private boolean mIsKeynote = false;

    // this set stores the session IDs for which the user has dismissed the
    // "give feedback" card. This information is kept for the duration of the app's execution
    // so that if they say "No, thanks", we don't show the card again for that session while
    // the app is still executing.
    private static HashSet<String> sDismissedFeedbackCard = new HashSet<String>();

    private TextView mSubmitFeedbackView;
    private float mMaxHeaderElevation;
    private float mFABElevation;

    private int mTagColorDotSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        if (mSessionUri == null) {
//            return;
//        }

        setHasOptionsMenu(true);

        mFABElevation = getResources().getDimensionPixelSize(R.dimen.fab_elevation);
        mMaxHeaderElevation = getResources().getDimensionPixelSize(
                R.dimen.session_detail_max_header_elevation);

        mTagColorDotSize = getResources().getDimensionPixelSize(R.dimen.tag_color_dot_size);

        mHandler = new Handler();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_session_detail, container, false);
        mScrollViewChild = mRootView.findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.VISIBLE);

        mDetailsContainer = mRootView.findViewById(R.id.details_container);
        mHeaderBox = mRootView.findViewById(R.id.header_session);
        mHeaderContentBox = mRootView.findViewById(R.id.header_session_contents);
        mHeaderBackgroundBox = mRootView.findViewById(R.id.header_background);
        mHeaderShadow = mRootView.findViewById(R.id.header_shadow);
        mTitle = (TextView) mRootView.findViewById(R.id.session_title);
        mSubtitle = (TextView) mRootView.findViewById(R.id.session_subtitle);
        mPhotoViewContainer = mRootView.findViewById(R.id.session_photo_container);
        mPhotoView = (ImageView) mRootView.findViewById(R.id.session_photo);

        mAbstract = (TextView) mRootView.findViewById(R.id.session_abstract);

        mAddScheduleButton = (CheckableFrameLayout)
                mRootView.findViewById(R.id.add_schedule_button);
//        mAddScheduleButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean starred = !mStarred;
//                SessionsHelper helper = new SessionsHelper(getActivity());
//                showStarred(starred, true);
//                helper.setSessionStarred(mSessionUri, starred, mTitleString);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    mRootView.announceForAccessibility(starred ?
//                            getString(R.string.session_details_a11y_session_added) :
//                            getString(R.string.session_details_a11y_session_removed));
//                }
//
//                /* [ANALYTICS:EVENT]
//                 * TRIGGER:   Add or remove a session from My Schedule.
//                 * CATEGORY:  'Session'
//                 * ACTION:    'Starred' or 'Unstarred'
//                 * LABEL:     Session title/subtitle.
//                 * [/ANALYTICS]
//                 */
//                AnalyticsManager.sendEvent(
//                        "Session", starred ? "Starred" : "Unstarred", mTitleString, 0L);
//            }
//        });

//        ((BaseActivity) getActivity()).getLPreviewUtils().setViewName(mPhotoView, VIEW_NAME_PHOTO);

        mHasPhoto = true;

        mSessionColor = getResources().getColor(R.color.text_dark);
//
//        LPreviewUtilsBase lpu = LPreviewUtils.getInstance(this.getActivity());
//
//        mHeaderBackgroundBox.setBackgroundResource(R.color.text_dark);

        mHeaderBackgroundBox.setBackgroundColor(mSessionColor);
        mHeaderBackgroundBox.setVisibility(View.VISIBLE);


        setupCustomScrolling(mRootView);

        String photo = "...";
        mHasPhoto=true;
        if (!TextUtils.isEmpty(photo)) {
            mHasPhoto = true;
            recomputePhotoAndScrollingMetrics();
        } else {
            mHasPhoto = false;
            recomputePhotoAndScrollingMetrics();
        }

        mAddScheduleButton.setVisibility(View.VISIBLE);

        mAddScheduleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddScheduleButton.toggle();
            }
        });


        return mRootView;
    }

    private void recomputePhotoAndScrollingMetrics() {
        final int actionBarSize = UIUtils.calculateActionBarSize(getActivity());
        mHeaderTopClearance = actionBarSize - mHeaderContentBox.getPaddingTop();
        mHeaderHeightPixels = mHeaderContentBox.getHeight();

        mPhotoHeightPixels = mHeaderTopClearance;
        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mRootView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

//        lp = mHeaderBackgroundBox.getLayoutParams();
//        if (lp.height != mHeaderHeightPixels) {
//            lp.height = mHeaderHeightPixels;
//            mHeaderBackgroundBox.setLayoutParams(lp);
//        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (mSpeakersImageLoader == null) {
//            mSpeakersImageLoader = new ImageLoader(this.getActivity(), R.drawable.person_image_empty);
//        }
//        if (mNoPlaceholderImageLoader == null) {
//            mNoPlaceholderImageLoader = new ImageLoader(this.getActivity());
//        }
    }

    private void setupCustomScrolling(View rootView) {
        mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mScrollView == null) {
            return;
        }

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mAddScheduleButtonHeightPixels = mAddScheduleButton.getHeight();
            recomputePhotoAndScrollingMetrics();
        }
    };

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        if (getActivity() == null) {
            return;
        }

        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY + mHeaderTopClearance);
        mHeaderBox.setTranslationY(newTop);
        mAddScheduleButton.setTranslationY(newTop + mHeaderHeightPixels
                - mAddScheduleButtonHeightPixels / 2);

        mHeaderBackgroundBox.setPivotY(mHeaderHeightPixels);
        int gapFillDistance = (int) (mHeaderTopClearance * GAP_FILL_DISTANCE_MULTIPLIER);
        boolean showGapFill = !mHasPhoto || (scrollY > (mPhotoHeightPixels - gapFillDistance));
        float desiredHeaderScaleY = showGapFill ?
                ((mHeaderHeightPixels + gapFillDistance + 1) * 1f / mHeaderHeightPixels)
                : 1f;
        if (!mHasPhoto) {
            mHeaderBackgroundBox.setScaleY(desiredHeaderScaleY);
        } else if (mGapFillShown != showGapFill) {
            mHeaderBackgroundBox.animate()
                    .scaleY(desiredHeaderScaleY)
                    .setInterpolator(new DecelerateInterpolator(2f))
                    .setDuration(250)
                    .start();
        }
        mGapFillShown = showGapFill;

        LPreviewUtilsBase lpu = LPreviewUtils.getInstance(this.getActivity());

        mHeaderShadow.setVisibility(View.INVISIBLE);

//        if (mHeaderTopClearance != 0) {
////            Fill the gap between status bar and header bar with color
//            float gapFillProgress = Math.min(Math.max(UIUtils.getProgress(scrollY,
//                    mPhotoHeightPixels - mHeaderTopClearance * 2,
//                    mPhotoHeightPixels - mHeaderTopClearance), 0), 1);
//            lpu.setViewElevation(mHeaderBackgroundBox, gapFillProgress * mMaxHeaderElevation);
//            lpu.setViewElevation(mHeaderContentBox, gapFillProgress * mMaxHeaderElevation + 0.1f);
//            lpu.setViewElevation(mAddScheduleButton, gapFillProgress * mMaxHeaderElevation
//                    + mFABElevation);
//            if (!lpu.hasLPreviewAPIs()) {
//                mHeaderShadow.setAlpha(gapFillProgress);
//            }
//        }

        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void setTextSelectable(TextView tv) {
        if (tv != null && !tv.isTextSelectable()) {
            tv.setTextIsSelectable(true);
        }
    }



    private void tryExecuteDeferredUiOperations() {
        if (mSocialStreamMenuItem != null) {
            for (Runnable r : mDeferredUiOperations) {
                r.run();
            }
            mDeferredUiOperations.clear();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    }

    @Override
    public void onDestroyOptionsMenu() {
    }

}
