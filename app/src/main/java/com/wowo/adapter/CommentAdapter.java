package com.wowo.adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.squareup.picasso.Picasso;
import com.wowo.R;
import com.wowo.api.WowoApi;
import com.wowo.model.Comment;
import com.wowo.utils.TimeUtils;
import com.wowo.view.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by tinyao on 9/19/14.
 */
public class CommentAdapter extends BaseAdapter {

    private List<Comment> comments;
    private Context mContext;
    private LayoutInflater mInflater;

    SparseBooleanArray mCheckStates;
    OnReplyClickListener replyClickListener;

    public CommentAdapter(Context context, List<Comment> comments, OnReplyClickListener listener) {
        this.mContext = context;
        this.comments = comments;
        mInflater = LayoutInflater.from(context);
        mCheckStates = new SparseBooleanArray(comments.size());
        replyClickListener = listener;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int i) {
        return comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.comment_item, viewGroup, false);
            holder.avatarV = (CircleImageView) convertView.findViewById(R.id.comment_avatar);
            holder.commentText = (TextView) convertView.findViewById(R.id.comment_text);
            holder.commentInfo = (TextView) convertView.findViewById(R.id.comment_info);
            holder.commentQuote = (TextView) convertView.findViewById(R.id.comment_quote);
            holder.likeButton = (CheckBox) convertView.findViewById(R.id.comment_like);
            holder.replyButton = (ImageButton) convertView.findViewById(R.id.comment_reply);
//            holder.likeView = (ImageView) convertView.findViewById(R.id.comment_like);
//            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.sending);
//            holder.imgFailed = (ImageView) convertView.findViewById(R.id.failed);

            convertView.setTag(holder);
            convertView.setTag(R.id.comment_like, holder.likeButton);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.likeButton.setTag(position); // This line is important.

        final Comment item = comments.get(position);

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WowoApi.likeComment(comments.get(position), holder.likeButton.isChecked(), null);
                holder.likeButton.setText("" + item.getVote());
            }
        });

        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyClickListener.onReplyClick(item);
            }
        });

        Picasso.with(mContext)
                .load(item.getAvatar())
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(holder.avatarV);

        holder.likeButton.setChecked(item.isCurrentUserLike());
        holder.likeButton.setText(""+item.getVote());

        if (item.getQuote() == null || item.getQuote().equals("")) {
            holder.commentQuote.setVisibility(View.GONE);
        } else {
            holder.commentQuote.setVisibility(View.VISIBLE);
            holder.commentQuote.setText(item.getQuote());
        }

        if (item.isLZ()) {
            holder.commentText.setTextColor(mContext.getResources().getColor(R.color.action_bar_color));
        } else {
            holder.commentText.setTextColor(mContext.getResources().getColor(R.color.text_dark));
        }

        holder.commentText.setText(item.getBody());

        Log.d("DEBUG", "comment createdAt: " + item.getCreatedAt());
        if (item.getCreatedAt() != null) {
            holder.commentInfo.setText(TimeUtils.getTimeAgo(item.getCreatedAt()));
        } else {
            holder.commentInfo.setText("刚刚");
        }

        if(holder.commentInfo.getText().equals("")) {
            holder.commentInfo.setText("刚刚");
        }

//        if (item.isSending()) {
//            holder.progressBar.setVisibility(View.VISIBLE);
//        } else {
//            holder.progressBar.setVisibility(View.GONE);
//        }

        return convertView;
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);

    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));

    }

    private class ViewHolder {
        CircleImageView avatarV;
        TextView commentText;
        TextView commentInfo;
        TextView commentQuote;
        CheckBox likeButton;
        ImageButton replyButton;
//        ImageView likeView;
//        ProgressBar progressBar;
//        ImageView imgFailed;
    }

    public interface OnReplyClickListener {
        public void onReplyClick(Comment item);
    }

}
