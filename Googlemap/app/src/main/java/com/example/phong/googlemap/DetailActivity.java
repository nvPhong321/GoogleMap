package com.example.phong.googlemap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.phong.googlemap.adapter.ReviewAdapter;
import com.example.phong.googlemap.json.ParseJson;
import com.example.phong.googlemap.model.PlaceDetails;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static com.example.phong.googlemap.MapActivity.mMenu;
import static com.example.phong.googlemap.MapActivity.myLocation;

/**
 * Created by phong on 12/22/2017.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private final String KEY_API_PLACES = "AIzaSyBFvDELwXmjPhs759Sey7vpdMfJRwEbhhg";
    private final String BASIC_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private final String TXT_KEY = "&key=";
    private final String LANGUAGE = "&language=vn";

    private final String BASiC_URL_PHOTO = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";

    private String placeID = null;
    private String url = null;

    private LinearLayout lnHeader;
    private TextView txtAddress;
    private ImageView imgRating1,imgRating2,imgRating3,imgRating4,imgRating5 , imvOpenNow;

    private ListView lvReviews;

    TextView txtPhone, txtWeb, txtRating;

    AppBarLayout Appbar;
    CollapsingToolbarLayout CoolToolbar;
    Toolbar toolbar;

    boolean ExpandedActionBar = true;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        try{
            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getBundleExtra("Data");
                if (bundle != null) {
                    placeID = bundle.getString("PlaceID");
                    Log.d(TAG,"PlaceId " + placeID);
                    buidURL();
                }
            }
        }catch (NullPointerException e){

        }

        CoolToolbar = (CollapsingToolbarLayout) findViewById(R.id.collaps);
        Appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //btnBack = (Button) findViewById(R.id.btn_back_details);
        lnHeader = (LinearLayout) findViewById(R.id.ln_photo_details);
        txtAddress = (TextView) findViewById(R.id.txt_address);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtWeb = (TextView) findViewById(R.id.txt_web);
        txtRating = (TextView) findViewById(R.id.txt_rating);

        imgRating1 = (ImageView) findViewById(R.id.imv_rating_review1);
        imgRating2 = (ImageView) findViewById(R.id.imv_rating_review2);
        imgRating3 = (ImageView) findViewById(R.id.imv_rating_review3);
        imgRating4 = (ImageView) findViewById(R.id.imv_rating_review4);
        imgRating5 = (ImageView) findViewById(R.id.imv_rating_review5);


        imvOpenNow =(ImageView)findViewById(R.id.ic_open_now);

        lvReviews = (ListView) findViewById(R.id.lvReviews);

        if (url != null) {
            ParseTask placeDtails = new ParseTask();
            placeDtails.execute(url);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                myLocation.show();
                mMenu.show();
            }
        });
    }


    private void buidURL() {
        url = BASIC_URL + placeID + LANGUAGE + TXT_KEY + KEY_API_PLACES;
    }

    //ParseTask
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class ParseTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... url) {
            String data = null;
            try{
                data = DownLoadUrl(url[0]);

            }catch (Exception e){
                Log.d("Background Task", e.getMessage());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            ParseDetail parseDetail = new ParseDetail();
            parseDetail.execute(s);
        }
    }

    //parse places detail
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class ParseDetail extends AsyncTask<String,Integer,PlaceDetails>{

        @Override
        protected PlaceDetails doInBackground(String... strings) {
            PlaceDetails data = null;
            try {
                ParseJson parseDetail = new ParseJson();
                data = parseDetail.detailsParse(strings[0]);
            }catch (Exception e){

            }
            return data;
        }

        @Override
        protected void onPostExecute(PlaceDetails placeDetails) {
            buidPhoto(placeDetails);
            updateUI(placeDetails);
        }
    }

    //download url
    private String DownLoadUrl(String strUrl) throws Exception{
        String data = "";
        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;
        try{
            URL Url = new URL(strUrl);

            // creating http connection
            urlConnection = (HttpsURLConnection) Url.openConnection();

            //connection to url
            urlConnection.connect();

            //reading data from url
            inputStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        }catch (Exception e){


        }finally {

            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //update UI detail
    private void updateUI(final PlaceDetails placeDetails){

        Log.d("name: ", placeDetails.getName());
        Log.d("address: ", placeDetails.getAddress());
        Log.d("phone: ", placeDetails.getPhoneNumber());
        Log.d("website: ", placeDetails.getWebsite());

        CoolToolbar.setTitle(placeDetails.getName());
        Appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    CoolToolbar.setTitle("Information");
                    isShow = true;
                } else if(isShow) {
                    CoolToolbar.setTitle(placeDetails.getName());
                    isShow = false;
                }
            }
        });

        txtAddress.setText(placeDetails.getAddress());
        txtPhone.setText(placeDetails.getPhoneNumber());
        txtWeb.setText(placeDetails.getWebsite());
        if(placeDetails.getRating() == -1){
            txtRating.setText("Empty");
        }else {
            txtRating.setText(String.valueOf(placeDetails.getRating()));
        }

        Log.d(TAG,"Review " + placeDetails.getReview());
        //list review
        if(placeDetails.getReview() != null){
            ReviewAdapter rv = new ReviewAdapter(this,R.layout.list_review_details_item,placeDetails.getReview());
            lvReviews.setAdapter(rv);
            rv.notifyDataSetChanged();
        }

        //open now
        String openNow;
        openNow = placeDetails.getOpenNow();
        if(openNow.equals("true")){
            imvOpenNow.setImageResource(R.drawable.opened);
        }else{
            if(openNow.equals("false")){
                imvOpenNow.setImageResource(R.drawable.closed);
            }else{
                imvOpenNow.setImageResource(R.drawable.no_data_open_now);
            }
        }

        //rating
        double rating = placeDetails.getRating();
        if (rating == -1 || rating == 0) {
            imgRating1.setImageResource(R.drawable.ic_star_empty);
            imgRating2.setImageResource(R.drawable.ic_star_empty);
            imgRating3.setImageResource(R.drawable.ic_star_empty);
            imgRating4.setImageResource(R.drawable.ic_star_empty);
            imgRating5.setImageResource(R.drawable.ic_star_empty);
        } else {
            if (rating == 1) {
                imgRating1.setImageResource(R.drawable.ic_star);
                imgRating2.setImageResource(R.drawable.ic_star_empty);
                imgRating3.setImageResource(R.drawable.ic_star_empty);
                imgRating4.setImageResource(R.drawable.ic_star_empty);
                imgRating5.setImageResource(R.drawable.ic_star_empty);
            } else {
                if (rating > 1 && rating < 2) {
                    imgRating1.setImageResource(R.drawable.ic_star);
                    imgRating2.setImageResource(R.drawable.ic_star_half);
                    imgRating3.setImageResource(R.drawable.ic_star_empty);
                    imgRating4.setImageResource(R.drawable.ic_star_empty);
                    imgRating5.setImageResource(R.drawable.ic_star_empty);
                } else {
                    if (rating == 2) {
                        imgRating1.setImageResource(R.drawable.ic_star);
                        imgRating2.setImageResource(R.drawable.ic_star);
                        imgRating3.setImageResource(R.drawable.ic_star_empty);
                        imgRating4.setImageResource(R.drawable.ic_star_empty);
                        imgRating5.setImageResource(R.drawable.ic_star_empty);
                    } else {
                        if (rating > 2 && rating < 3) {
                            imgRating1.setImageResource(R.drawable.ic_star);
                            imgRating2.setImageResource(R.drawable.ic_star);
                            imgRating3.setImageResource(R.drawable.ic_star_half);
                            imgRating4.setImageResource(R.drawable.ic_star_empty);
                            imgRating5.setImageResource(R.drawable.ic_star_empty);
                        } else {
                            if (rating == 3) {
                                imgRating1.setImageResource(R.drawable.ic_star);
                                imgRating2.setImageResource(R.drawable.ic_star);
                                imgRating3.setImageResource(R.drawable.ic_star);
                                imgRating4.setImageResource(R.drawable.ic_star_empty);
                                imgRating5.setImageResource(R.drawable.ic_star_empty);
                            } else {
                                if (rating > 3 && rating < 4) {
                                    imgRating1.setImageResource(R.drawable.ic_star);
                                    imgRating2.setImageResource(R.drawable.ic_star);
                                    imgRating3.setImageResource(R.drawable.ic_star);
                                    imgRating4.setImageResource(R.drawable.ic_star_half);
                                    imgRating5.setImageResource(R.drawable.ic_star_empty);
                                } else {
                                    if (rating == 4) {
                                        imgRating1.setImageResource(R.drawable.ic_star);
                                        imgRating2.setImageResource(R.drawable.ic_star);
                                        imgRating3.setImageResource(R.drawable.ic_star);
                                        imgRating4.setImageResource(R.drawable.ic_star);
                                        imgRating5.setImageResource(R.drawable.ic_star_empty);
                                    } else {
                                        if (rating > 4 && rating < 5) {
                                            imgRating1.setImageResource(R.drawable.ic_star);
                                            imgRating2.setImageResource(R.drawable.ic_star);
                                            imgRating3.setImageResource(R.drawable.ic_star);
                                            imgRating4.setImageResource(R.drawable.ic_star);
                                            imgRating5.setImageResource(R.drawable.ic_star_half);
                                        } else {
                                            if (rating == 5) {
                                                imgRating1.setImageResource(R.drawable.ic_star);
                                                imgRating2.setImageResource(R.drawable.ic_star);
                                                imgRating3.setImageResource(R.drawable.ic_star);
                                                imgRating4.setImageResource(R.drawable.ic_star);
                                                imgRating5.setImageResource(R.drawable.ic_star);
                                            } else {
                                                if (rating > 0 && rating < 1) {
                                                    imgRating1.setImageResource(R.drawable.ic_star_half);
                                                    imgRating2.setImageResource(R.drawable.ic_star_empty);
                                                    imgRating3.setImageResource(R.drawable.ic_star_empty);
                                                    imgRating4.setImageResource(R.drawable.ic_star_empty);
                                                    imgRating5.setImageResource(R.drawable.ic_star_empty);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //download Image and Add to bitmap
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class DownloadImage extends AsyncTask<String,Integer,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... imgUrl) {
            Bitmap imgBitmap = null;
            try{

                URL url = new URL(imgUrl[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();

                //read file
                InputStream inputStream = new BufferedInputStream(url.openStream(),8192);
                imgBitmap = BitmapFactory.decodeStream(inputStream);

            }catch (Exception e){

            }
            return imgBitmap;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            updatePhoto(bitmap);

        }
    }

    //buid url to get place photo
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void buidPhoto(PlaceDetails place) {
        ArrayList<String> photos = place.getPhoto();
        if (photos != null) {
            String urlPhoto = BASiC_URL_PHOTO + photos.get(0).trim() + TXT_KEY + KEY_API_PLACES;
            Log.d("test", urlPhoto);
            DownloadImage downPhoto = new DownloadImage();
            downPhoto.execute(urlPhoto);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void updatePhoto(Bitmap placePhoto) {

        if (placePhoto != null) {
            BitmapDrawable photo = new BitmapDrawable(placePhoto);
            lnHeader.setBackground(photo);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myLocation.show();
        mMenu.show();
    }
}
