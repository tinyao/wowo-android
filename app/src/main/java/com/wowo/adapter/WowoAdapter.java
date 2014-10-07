package com.wowo.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wowo.R;
import com.wowo.api.WowoApi;
import com.wowo.model.Wowo;
import com.wowo.utils.TimeUtils;
import com.wowo.view.RoundedImageView;

import java.util.List;

/**
 * Created by tinyao on 9/16/14.
 */
public class WowoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context mContext;
    List<Wowo> wowos;
    private TypedArray templates;

    public WowoAdapter(Context context, List<Wowo> wowosList) {
        mInflater = LayoutInflater.from(context);
        templates = context.getResources().obtainTypedArray(R.array.templates_l);
        this.mContext = context;
        wowos = wowosList;
    }

    @Override
    public int getCount() {
        return wowos != null ? wowos.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        if (wowos != null) {
            return wowos.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final Wowo post = wowos.get(position);
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.card_wowo_base, viewGroup, false);
            holder.imgView = (RoundedImageView) convertView.findViewById(R.id.wowo_photo);
            holder.titleView = (TextView) convertView.findViewById(R.id.wowo_content_title);
            holder.metaTextV = (TextView) convertView.findViewById(R.id.wowo_meta);
            holder.likeButton = (CheckBox) convertView.findViewById(R.id.wowo_like);
            holder.photoMask = convertView.findViewById(R.id.wowo_photo_mask);

            convertView.setTag(holder);
            convertView.setTag(R.id.wowo_like, holder.likeButton);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.likeButton.setTag(position); // This line is important.


        holder.titleView.setText(post.getTitle());
        holder.metaTextV.setText(post.getCategoryName() + ", " + TimeUtils.getTimeAgo(post.getCreatedAt()));

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WowoApi.likeWowo(post, holder.likeButton.isChecked(), null);
                holder.likeButton.setText("" + post.getVote());
            }
        });

        holder.likeButton.setChecked(post.isCurrentUserLike());
        holder.likeButton.setText("" + post.getVote());

        Log.d("DEBUG", "photo:" + post.getPhotoUrl());

        if (post.getPhotoUrl() != null && !post.getPhotoUrl().equals("")) {
            Picasso.with(mContext)
                    .load(post.getPhotoUrl())
                    .placeholder(R.drawable.card_gradient_bg)
                    .error(R.drawable.card_gradient_bg)
                    .into(holder.imgView);
            holder.photoMask.setVisibility(View.VISIBLE);
        } else {
            holder.imgView.setImageResource(post.getColor());
            holder.photoMask.setVisibility(View.INVISIBLE);
        }


//        AVRelation<AVUser> relation = post.getRelation("likes");
//        relation.getQuery().whereEqualTo("objectId", AVUser.getCurrentUser().getObjectId()).findInBackground(new FindCallback<AVUser>() {
//            @Override
//            public void done(List<AVUser> avUsers, AVException e) {
//                if(avUsers!=null && avUsers.size()!=0)
//                    holder.likeView.setChecked(true);
//                else
//                    holder.likeView.setChecked(false);
//            }
//        });

//        holder.likeView.setChecked(User.isLike(post));

//        if(!post.likeChecked) {
//            AVRelation<Ama> relation = AVUser.getCurrentUser().getRelation("likes");
//
//            relation.getQuery().whereEqualTo("objectId", post.getObjectId())
//                    .setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK)
//                    .findInBackground(new FindCallback<Ama>() {
//                        @Override
//                        public void done(List<Ama> amas, AVException e) {
//                            boolean liked = (amas != null && amas.size() != 0);
//                            holder.likeView.setChecked(liked);
////                            post.setLiked(liked);
////                            post.likeChecked = true;
//                        }
//                    });
//        } else {
//            holder.likeView.setChecked(post.isLiked());
//        }

//        holder.likeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
////                User.like(post, check);
//
////                post.likeChecked = false;
//            }
//        });

        return convertView;
    }

    public class ViewHolder {
        RoundedImageView imgView;
        RoundedImageView colorView;
        TextView titleView;
        CheckBox likeButton;
        TextView metaTextV;
        View photoMask;
    }

    // Display anchored popup menu based on view selected
    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(mContext, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.wowo_more_menu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_share:
                        Toast.makeText(mContext, "Keyword!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_subscribe:
                        Toast.makeText(mContext, "Popularity!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_remove:
                        return true;
                    case R.id.menu_report_block:
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

}
