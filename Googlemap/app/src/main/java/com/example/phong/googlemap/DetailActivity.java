package com.example.phong.googlemap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.phong.googlemap.adapter.BannerAdapter;
import com.example.phong.googlemap.adapter.ReviewAdapter;
import com.example.phong.googlemap.json.ParseJson;
import com.example.phong.googlemap.model.PlaceDetails;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

import static com.example.phong.googlemap.MapActivity.mMenu;
import static com.example.phong.googlemap.MapActivity.myLocation;

/**
 * Created by phong on 12/22/2017.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private final String KEY_API_PLACES = "AIzaSyBFvDELwXmjPhs759Sey7vpdMfJRwEbhhg";
    private final String BASIC_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private final String BASiC_URL_PHOTO = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    private final String TXT_KEY = "&key=";
    private final String LANGUAGE = "&language=vn";

    private String placeID = null;
    private String url = null;

    private TextView txtAddress;
    private ImageView imgRating1, imgRating2, imgRating3, imgRating4, imgRating5, imvOpenNow,imgDefault;

    private ListView lvReviews;

    TextView txtPhone, txtWeb, txtRating;

    AppBarLayout Appbar;
    CollapsingToolbarLayout CoolToolbar;
    Toolbar toolbar;
    private static int currentPage = 0;
    CircleIndicator indicator;
    ViewPager mViewPager;

    RequestQueue requestQueue;
    ArrayList<String> pictures;


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        requestQueue = Volley.newRequestQueue(this);
        pictures = new ArrayList<>();

        try {
            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getBundleExtra("Data");
                if (bundle != null) {
                    placeID = bundle.getString("PlaceID");
                    buidURL();
                }
            }
        } catch (NullPointerException e) {

        }

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        CoolToolbar = (CollapsingToolbarLayout) findViewById(R.id.collaps);
        Appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //btnBack = (Button) findViewById(R.id.btn_back_details);
        txtAddress = (TextView) findViewById(R.id.txt_address);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtWeb = (TextView) findViewById(R.id.txt_web);
        txtRating = (TextView) findViewById(R.id.txt_rating);
        imgDefault = (ImageView) findViewById(R.id.imgDefault);

        imgRating1 = (ImageView) findViewById(R.id.imv_rating_review1);
        imgRating2 = (ImageView) findViewById(R.id.imv_rating_review2);
        imgRating3 = (ImageView) findViewById(R.id.imv_rating_review3);
        imgRating4 = (ImageView) findViewById(R.id.imv_rating_review4);
        imgRating5 = (ImageView) findViewById(R.id.imv_rating_review5);


        imvOpenNow = (ImageView) findViewById(R.id.ic_open_now);

        lvReviews = (ListView) findViewById(R.id.lvReviews);

        if (url != null) {
            parseDetail(url);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                myLocation.show();
                mMenu.show();
            }
        });
    }

    private void buidURL() {
        url = BASIC_URL + placeID + LANGUAGE + TXT_KEY + KEY_API_PLACES;
        Log.d(TAG,"Url " + url);
    }

    private void parseDetail(String url) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                PlaceDetails place = new PlaceDetails();
                ParseJson parseJson = new ParseJson();
                parseJson.detailsParse(response,place);
                updateUI(place);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"error" + error);
            }
        });

        requestQueue.add(objectRequest);
    }


    //update UI detail
    private void updateUI(final PlaceDetails placeDetails) {

        if(placeDetails.getPhoto() == null){
            mViewPager.setEnabled(false);
            mViewPager.setVisibility(View.GONE);
            imgDefault.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.GONE);
        }else {

            for (int i = 0; i < placeDetails.getPhoto().size(); i++) {
                String urlPhoto = BASiC_URL_PHOTO + placeDetails.getPhoto().get(i).trim() + TXT_KEY + KEY_API_PLACES;
                pictures.add(urlPhoto);
                Log.d(TAG, "Picture " + placeDetails.getPhoto().get(i));
                mViewPager.setEnabled(true);
                mViewPager.setVisibility(View.VISIBLE);
                imgDefault.setVisibility(View.GONE);
                indicator.setVisibility(View.VISIBLE);
                mViewPager.setAdapter(new BannerAdapter(DetailActivity.this, pictures));
                indicator.setViewPager(mViewPager);
            }
        }

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == pictures.size()) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);

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
                } else if (isShow) {
                    CoolToolbar.setTitle(placeDetails.getName());
                    isShow = false;
                }
            }
        });



        txtAddress.setText(placeDetails.getAddress());
        txtPhone.setText(placeDetails.getPhoneNumber());
        txtWeb.setText(placeDetails.getWebsite());
        if (placeDetails.getRating() == -1) {
            txtRating.setText("Empty");
        } else {
            txtRating.setText(String.valueOf(placeDetails.getRating()));
        }

        //list review
        if (placeDetails.getReview() != null) {
            ReviewAdapter rv = new ReviewAdapter(this, R.layout.list_review_details_item, placeDetails.getReview());
            lvReviews.setAdapter(rv);
            rv.notifyDataSetChanged();
        }

        //open now
        String openNow;
        openNow = placeDetails.getOpenNow();
        if (openNow.equals("true")) {
            imvOpenNow.setImageResource(R.drawable.opened);
        } else {
            if (openNow.equals("false")) {
                imvOpenNow.setImageResource(R.drawable.closed);
            } else {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myLocation.show();
        mMenu.show();
    }
}
