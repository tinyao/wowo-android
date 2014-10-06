package com.wowo.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.wowo.R;
import com.wowo.adapter.GridViewPagerAdapter;
import com.wowo.api.WowoApi;

import org.goodev.helpviewpager.CirclePageIndicator;
import org.goodev.helpviewpager.PageIndicator;

import java.util.Random;

public class CreateWowoActivity extends Activity implements GridViewPagerAdapter.OnTemplateSelectedListener {

    private ImageView imgBg;
    private EditText titleView;
    private TextView meTv, askTv;
    private ImageView imgOpt;
    private CheckBox colorOpt;
    private TypedArray templateList;
    private View templatePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_ama);

        imgBg = (ImageView) findViewById(R.id.ama_image);
        titleView = (EditText) findViewById(R.id.ama_content_title);
        meTv = (TextView) findViewById(R.id.ama_content_me);
        askTv = (TextView) findViewById(R.id.ama_content_ask);
        imgOpt = (ImageView) findViewById(R.id.image_opt);
        colorOpt = (CheckBox) findViewById(R.id.color_opt);
        templatePanel = findViewById(R.id.template_panel);

        templateList = getResources().obtainTypedArray(R.array.templates_l);

        titleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    pubishItem.setEnabled(false);
                } else {
                    pubishItem.setEnabled(true);
                }
            }
        });

        colorOpt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                templatePanel.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });

        imgOpt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        loadTemplates();
    }

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onResume() {
//        randomTemplate();
        super.onResume();
    }

    private void randomTemplate() {
        OnTemplateSeleted(new Random().nextInt(29));
    }

    private void loadTemplates() {
//        GridViewPagerAdapter mAdapter = new GridViewPagerAdapter(this, getFragmentManager(), this);
//
//        ViewPager mPager = (ViewPager) findViewById(R.id.template_pager);
//        mPager.setOffscreenPageLimit(4);
//        mPager.setAdapter(mAdapter);
//
//        PageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.template_indicator);
//        mIndicator.setViewPager(mPager);
    }


    MenuItem pubishItem;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        pubishItem.setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_ama, menu);
        pubishItem = menu.findItem(R.id.action_publish);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_publish) {
            createAMA("我" + titleView.getText().toString() + "，你们有什么想问的");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAMA(String title) {

//        WowoApi.publishAma(title, currentIndex, "cover.jpg", 0, 0, new SaveCallback() {
//
//            @Override
//            public void done(AVException e) {
//                if (e == null) {
//                    finish();
//                } else {
//                    Toast.makeText(CreateAmaActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        });

    }

    private int currentIndex = 0;

    @Override
    public synchronized void OnTemplateSeleted(int index) {

        if (index == 0) {
            imgOpt.setImageResource(R.drawable.add_photo_light_selector);
            colorOpt.setButtonDrawable(R.drawable.add_colorbg_light_selector);
            titleView.setTextColor(getResources().getColor(R.color.text_dark));
            titleView.setHintTextColor(getResources().getColor(R.color.text_dark_hint));
            meTv.setTextColor(getResources().getColor(R.color.text_dark));
            askTv.setTextColor(getResources().getColor(R.color.text_dark));
        } else {
            if (currentIndex == 0) {
                imgOpt.setImageResource(R.drawable.add_photo_selector);
                colorOpt.setButtonDrawable(R.drawable.add_colorbg_selector);
                titleView.setTextColor(getResources().getColor(R.color.text_light));
                titleView.setHintTextColor(getResources().getColor(R.color.text_light_hint));
                meTv.setTextColor(getResources().getColor(R.color.text_light));
                askTv.setTextColor(getResources().getColor(R.color.text_light));
            }
        }

        Drawable mBg = templateList.getDrawable(index);
        imgBg.setImageDrawable(mBg);

        currentIndex = index;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("DEBUG", "Uri: " + uri.toString());
                imgBg.setImageURI(uri);
//                String path = getPath(uri);
//
//                byte[] decodedString = Base64.decode(person_object.getPhoto(),Base64.NO_WRAP);
//                InputStream inputStream  = new ByteArrayInputStream(decodedString);
//                Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
//                user_image.setImageBitmap(bitmap);
//
//                Bitmap bm = BitmapFactory.decodeFile(path);
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

//    public static class AskTextAdapter extends PagerAdapter {
//
//        public AskTextAdapter(Context mContext) {
//            mContext.getResources().getStringArray()
//        }
//
//        @Override
//        public int getCount() {
//            return imgids.length;
//        }
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0==arg1;
//        }
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            //super.destroyItem(container, position, object);
//            container.removeView(imgs.get(position));
//        }
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            // TODO Auto-generated method stub
//            container.addView(imgs.get(position));
//            return imgs.get(position);
//        }
//    }

}