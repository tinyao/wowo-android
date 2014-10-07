package com.wowo.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.wowo.R;
import com.wowo.WoApplication;
import com.wowo.adapter.GridViewPagerAdapter;
import com.wowo.api.WowoApi;
import com.wowo.model.Category;

import org.goodev.helpviewpager.CirclePageIndicator;
import org.goodev.helpviewpager.PageIndicator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Random;

public class CreateWowoActivity extends BaseActivity implements
        GridViewPagerAdapter.OnTemplateSelectedListener, View.OnClickListener{

    private final static int REQUEST_CATEGORY = 100;
    private final static int REQUEST_SCOPE = 101;
    private static final int PICK_IMAGE = 102;
    private static final int CROP_IMAGE = 103;

    private EditText titleView;
    private View categoryView, visScopeView;
    private EditText bodyEdtV;
    private ImageView photoView;
    private View coverPanel, maskView;
    private TypedArray templateList;
    private TextView categoryTv, titleAskTv;
    private CheckBox neabyOnlyCheck;
    private ImageView imageOpt, colorOpt;

    private Category mCategory = Category.others;

    private Bitmap mBitmap;
    private int mColorId;

    private LocationManager locationManager;
    private Location mLocation;
    private final static String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.card_wowo_create);
        getActionBar().setDisplayShowTitleEnabled(false);

        photoView = (ImageView) findViewById(R.id.wowo_create_photo);
        maskView = findViewById(R.id.wowo_photo_mask);
        titleView = (EditText) findViewById(R.id.wowo_create_title);
        titleAskTv = (TextView) findViewById(R.id.wowo_create_title_end);
        bodyEdtV = (EditText) findViewById(R.id.wowo_create_body_edt);
        coverPanel = findViewById(R.id.wowo_photo_lay);
        categoryView = findViewById(R.id.wowo_create_option_category);
        visScopeView = findViewById(R.id.wowo_create_option_visibility);
        categoryTv = (TextView)findViewById(R.id.wowo_create_category_txt);
        neabyOnlyCheck = (CheckBox) findViewById(R.id.wowo_create_nearby_only_check);
        imageOpt = (ImageView) findViewById(R.id.wowo_image_opt);
        colorOpt = (ImageView) findViewById(R.id.wowo_color_opt);

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

        coverPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "cover panel click");
            }
        });

        templateList = getResources().obtainTypedArray(R.array.colors_l);

        mColorId = new Random().nextInt(12);
        photoView.setImageResource(WoApplication.getColors()
                .getResourceId(mColorId, R.color.action_bar_color));

        categoryView.setOnClickListener(this);
        visScopeView.setOnClickListener(this);
        imageOpt.setOnClickListener(this);
        colorOpt.setOnClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocation = locationManager.getLastKnownLocation(LOCATION_PROVIDER);
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);

        loadTemplates();
    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            mLocation = location;
            // Remove the listener you previously added
            locationManager.removeUpdates(locationListener);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };




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
            createWowo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ProgressDialog pd;

    private void createWowo() {
        String title = titleView.getText() + "\n" + titleAskTv.getText();
        String body = bodyEdtV.getText().toString();
        int mCategoryId = mCategory.ordinal();

        pd = new ProgressDialog(this);
        pd.setMessage("发布中...");
        pd.show();
        WowoApi.publishWowo(title, body, mBitmap, mColorId,
                mCategoryId, neabyOnlyCheck.isChecked(),
                mLocation, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    pd.cancel();
                    finish();
                } else {
                    Toast.makeText(CreateWowoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private int currentIndex = 0;

    @Override
    public synchronized void OnTemplateSeleted(int index) {

//        if (index == 0) {
//            imgOpt.setImageResource(R.drawable.add_photo_light_selector);
//            colorOpt.setButtonDrawable(R.drawable.add_colorbg_light_selector);
//            titleView.setTextColor(getResources().getColor(R.color.text_dark));
//            titleView.setHintTextColor(getResources().getColor(R.color.text_dark_hint));
//            meTv.setTextColor(getResources().getColor(R.color.text_dark));
//            askTv.setTextColor(getResources().getColor(R.color.text_dark));
//        } else {
//            if (currentIndex == 0) {
//                imgOpt.setImageResource(R.drawable.add_photo_selector);
//                colorOpt.setButtonDrawable(R.drawable.add_colorbg_selector);
//                titleView.setTextColor(getResources().getColor(R.color.text_light));
//                titleView.setHintTextColor(getResources().getColor(R.color.text_light_hint));
//                meTv.setTextColor(getResources().getColor(R.color.text_light));
//                askTv.setTextColor(getResources().getColor(R.color.text_light));
//            }
//        }

//        Drawable mBg = templateList.getDrawable(index);
//        imgBg.setImageDrawable(mBg);

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
                cropPhoto(uri);
//                photoView.setImageURI(uri);
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

        if (requestCode == CROP_IMAGE && resultCode == RESULT_OK && resultData!=null) {
            mBitmap = BitmapFactory.decodeFile(getCorpCacheFile().getAbsolutePath());
            photoView.setImageBitmap(mBitmap);
            maskView.setVisibility(View.VISIBLE);
        }


        if (requestCode == REQUEST_CATEGORY && resultCode == RESULT_OK) {
            mCategory = Category.values()[resultData.getIntExtra("category_id", Category.others.ordinal()-2) + 2];
            categoryTv.setText(mCategory.getDisplayName());
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



    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.wowo_create_option_category:
                startActivityForResult(new Intent(this, CategoryActivity.class), REQUEST_CATEGORY);
                break;
            case R.id.wowo_color_opt:
                mColorId = new Random().nextInt(12);
                photoView.setImageResource(WoApplication.getColors()
                        .getResourceId(mColorId, R.color.action_bar_color));
                maskView.setVisibility(View.GONE);
                break;
            case R.id.wowo_image_opt:
//                Intent intent = new Intent("com.android.camera.action.CROP");
////                intent.setDataAndType(uri, "image/*");
//                intent.setDataAndType(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                intent.putExtra("crop", "true");
//                intent.putExtra("aspectX", 3);
//                intent.putExtra("aspectY", 2);
////        intent.putExtra("outputX", 96);
////        intent.putExtra("outputY", 96);
////                intent.putExtra("noFaceDetection", true);
////                intent.putExtra("return-data", true);
//                startActivityForResult(intent, CROP_IMAGE);
                Intent intent = new Intent(Intent.ACTION_PICK, null);

                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
                startActivityForResult(intent, PICK_IMAGE);
                break;
            case R.id.wowo_create_option_visibility:
                neabyOnlyCheck.setChecked(!neabyOnlyCheck.isChecked());
                break;
        }

    }

    private boolean cropPhoto(Uri uri) {
        Intent cropIntent = new Intent(
                "com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 3);
        cropIntent.putExtra("aspectY", 2);
        cropIntent.putExtra("outputX", 640);
        cropIntent.putExtra("outputY", 427);
        cropIntent.putExtra("return-data", false);
        cropIntent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        Uri cropUri = Uri.fromFile(getCorpCacheFile());
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        if (!isIntentAvailable(cropIntent)) {
            return false;
        } else {
            try {
                startActivityForResult(cropIntent, CROP_IMAGE);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private static final String AVATAR_CORP_CACHE_NAME = "/avatar_corp_cache";
    private static final String AVATAR_CAMARA_CACHE_NAME = "/avatar_camara_cache";

    protected File getCorpCacheFile() {
        String corp_cache_path = null;
        if (isSDCardAvailable()) {
            corp_cache_path = Environment.getExternalStorageDirectory()
                    + AVATAR_CORP_CACHE_NAME;
        } else {
            corp_cache_path = getCacheDir().getAbsolutePath()
                    + AVATAR_CORP_CACHE_NAME;
        }
        Log.d("gaoyihang.debug", corp_cache_path);
        return new File(corp_cache_path);
    }

    private boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * check if intent is available
     *
     * @param intent
     * @return
     */
    protected boolean isIntentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list = packageManager
                .queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
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
