package com.wowo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.squareup.picasso.Picasso;
import com.wowo.R;
import com.wowo.WoApplication;
import com.wowo.adapter.CommentAdapter;
import com.wowo.api.WowoApi;
import com.wowo.model.Wowo;
import com.wowo.model.Comment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tinyao on 9/19/14.
 */
public class WowoDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    ListView commentsListV;
    HashMap<String, Object> woMap;

    private EditText bodyEdt;
    private ImageButton sendBtn;

    private CommentAdapter adapter;
    private List<Comment> commentArray = new ArrayList<Comment>(), questionArray, anwserArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("");

        woMap = (HashMap<String, Object>) getIntent().getSerializableExtra("wowo");

        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground((Integer) woMap.get("color"))
                .headerLayout(R.layout.header)
                .gradient(woMap.get("photoUrl") != null)
                .headerOverlayLayout(R.layout.card_wowo_detail)
                .contentLayout(R.layout.activity_wowo_discuss);
        setContentView(helper.createView(this));
        helper.initActionBar(this);

        ImageView imageV = (ImageView) findViewById(R.id.image_header);

        TextView titleV = (TextView) findViewById(R.id.wowo_content_title);
        View photoMask = findViewById(R.id.wowo_photo_mask);
        TextView metaV = (TextView) findViewById(R.id.wowo_meta);
        CheckBox likeCheckBox = (CheckBox) findViewById(R.id.wowo_like);

        titleV.setText("" + woMap.get("text"));
        metaV.setText(woMap.get("category") + ", " + woMap.get("time"));
        likeCheckBox.setText("" + woMap.get("score"));

        if (woMap.get("photoUrl") == null || woMap.get("photoUrl").equals("")) {
            imageV.setBackgroundColor(getResources().getColor((Integer) woMap.get("color")));
            photoMask.setVisibility(View.INVISIBLE);
        } else {
            Picasso.with(this)
                    .load((String) (woMap.get("photoUrl")))
                    .placeholder(R.drawable.default_bg)
                    .error(R.drawable.default_bg)
                    .into(imageV);
            photoMask.setVisibility(View.VISIBLE);
        }

        commentsListV = (ListView) findViewById(android.R.id.list);

        commentsListV.setAdapter(adapter = new CommentAdapter(WowoDetailActivity.this, commentArray));

        View bodyView = getLayoutInflater().inflate(R.layout.wowo_body, null, false);
        commentsListV.addHeaderView(bodyView);

        registerForContextMenu(commentsListV);

        commentsListV.setOnItemClickListener(this);

//        WowoApi.questions(Wowo.createWithoutData("Wowo", (String) woMap.get("objectId")), new FindCallback<Comment>() {
//            @Override
//            public void done(final List<Comment> qus, AVException e) {
//                if (e == null) {
//                    Log.d("DEBUG", "Questions: " + qus.size());
//                    questionArray = qus;
//                    WowoApi.answers(Wowo.createWithoutData("Wowo", (String) woMap.get("objectId")), qus,
//                            new FindCallback<Comment>() {
//                                @Override
//                                public void done(List<Comment> ans, AVException e) {
//                                    if (e == null) {
//                                        Log.d("DEBUG", "Answers: " + ans.size());
//                                        anwserArray = ans;
//                                        // sort the comments
//                                        for (Comment qq : questionArray) {
//                                            commentArray.add(qq);
//                                            for (Comment aa : anwserArray) {
//                                                if (aa.getSuperQA().equals(qq.getObjectId())) {
//                                                    commentArray.add(aa);
//                                                }
//                                            }
//                                        }
//
//                                        adapter = new CommentAdapter(WowoDetailActivity.this, commentArray);
//                                        commentsListV.setAdapter(adapter);
//
//                                    } else {
//                                        Log.d("DEBUG", e.toString());
//                                    }
//                                }
//                            });
//
//                } else {
//                    Log.d("DEBUG", e.toString());
//                }
//            }
//        });


        WowoApi.comments(Wowo.createWithoutData("Wowo", (String) woMap.get("objectId")),
                new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> comments, AVException e) {
                        if (e == null) {
                            commentArray = comments;
                            adapter = new CommentAdapter(WowoDetailActivity.this, comments);
                            commentsListV.setAdapter(adapter);
                            Log.d("DEBUG", "Comments: " + comments.size());
                        } else {
                            Log.d("DEBUG", e.toString());
                        }
                    }
                });

        bodyEdt = (EditText) findViewById(R.id.edit_box);
        sendBtn = (ImageButton) findViewById(R.id.send);
        sendBtn.setEnabled(false);

        likeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        bodyEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    sendBtn.setEnabled(false);
                    bodyEdt.setHint("");
                } else {
                    sendBtn.setEnabled(true);
                }
            }
        });

    }

    private String replyQuote = null;

    public void clickOnSend(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bodyEdt.getWindowToken(), 0);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.show();

        Log.d("DEBUG", "wowoID: " + woMap.get("objectId") + "-authorId: " + woMap.get("author"));

        Comment item = WowoApi.comment(bodyEdt.getText().toString(), Wowo.createWithoutData("Wowo",
                (String) woMap.get("objectId")), (String) woMap.get("author"), replyQuote,
                new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    commentsListV.smoothScrollToPosition(commentArray.size()+1);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(WowoDetailActivity.this, "评论：" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                pd.cancel();
            }
        });

        commentArray.add(item);
    }

    private Handler mHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    commentsListV.smoothScrollToPosition(commentArray.size()+1);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wowo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_share:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comment_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.comment_popmenu_share:
                commentShare();
                return true;
            case R.id.comment_popmenu_like:
                likeComment();
                return true;
            case R.id.comment_popmenu_reply:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Log.d("DEBUG", "removing item pos=" + (info.position-2));
                Toast.makeText(getApplicationContext(), "position: " + (info.position-2) + "  " + adapter.getItem((info.position-2)).getBody(), Toast.LENGTH_SHORT).show();
                Comment comm = adapter.getItem(info.position-2);
                bodyEdt.setHint("回复: " + comm.getBody());
                replyQuote = comm.getBody();
                bodyEdt.requestFocus();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("DEBUG", "Click");
        adapterView.showContextMenuForChild(view);
    }

    private void likeComment() {

    }

    private void commentShare() {
    }

}
