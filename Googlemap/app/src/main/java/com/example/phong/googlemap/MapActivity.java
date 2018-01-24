package com.example.phong.googlemap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phong.googlemap.adapter.PlaceAutocompleteAdapter;
import com.example.phong.googlemap.json.ParseJson;
import com.example.phong.googlemap.model.Directions;
import com.example.phong.googlemap.model.NearByPlaces;
import com.example.phong.googlemap.model.StepsDirections;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static com.example.phong.googlemap.R.id.map;

/**
 * Created by Nguyen Van Phong on 12/15/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLoadedCallback,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 18f;

    private LatLng locateMe; // location of user
    private LatLng locatePin; //location of pin picker

    //search
    private static final String BASIC_URL_TEXT_SEARCH = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    private static final String TOKEN_AND = "&";
    private static final String TXT_KEY = "key=";
    private static final String KEY_API_PLACES = "AIzaSyBFvDELwXmjPhs759Sey7vpdMfJRwEbhhg";
    private final String BASIC_PARSE_URL_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private final String TOKEN_COMMA = ",";
    private final String TXT_TYPE = "types=";
    private final String TXT_RADIUS = "radius=";
    private final String SENSOR = "sensor=false";
    private final String DEFAULT_TYPES_SEARCH = "cafe|food";

    //directions
    private final String BASIC_URL_DIRECTIONS = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private final String TXT_DESTINATION = "&destination=place_id:";
    private String language = "en";
    private final String KEY_DIRECTIONS = "AIzaSyBFvDELwXmjPhs759Sey7vpdMfJRwEbhhg";


    public static FloatingActionButton myLocation, mMenu, atm, hospital, SuperMarket, cafe, gas, restaurant;
    private TextView  tvAtm, tvHospital, tvSuperMarket, tvCafe, tvGas, tvRestaurant;
    private AutoCompleteTextView edtsearch;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Animation hideRight, showRight, fabMoveUpRotate, plusOpenRotate, plusCloseRotate, fabMoveDown, textMoveUp, textMoveOut;
    private Button btnDirect, btnDetail;
    private ImageView imgClear;

    //flag
    private Boolean mLocationPermissionsGranted = false;
    private boolean flagMenuOpen = false;
    private boolean flagOpenBtnRight = true;
    boolean doubleBackToExitPressedOnce = false;

    //array list
    private ArrayList<NearByPlaces> listPlaces;

    //model
    NearByPlaces nearByPlace;

    //String
    private String placeIDMarkerClicked;
    private String urlNearBy;

    private String lngSearch;
    private String radiusSearch;
    private String typeSearch;
    private String latSearch;
    private float carmeraZoom;


    private PlaceAutocompleteAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168), new LatLng(71, 136));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        listPlaces = new ArrayList<>();
        nearByPlace = new NearByPlaces();

        myLocation = (FloatingActionButton) findViewById(R.id.btnMyLocation);
        edtsearch = (AutoCompleteTextView) findViewById(R.id.edtSearch);
        btnDirect = (Button) findViewById(R.id.btn_direct);
        btnDetail = (Button) findViewById(R.id.btn_detail);
        imgClear = (ImageView) findViewById(R.id.btnClear);
        showRight = AnimationUtils.loadAnimation(this, R.anim.btn_right_action);
        hideRight = AnimationUtils.loadAnimation(this, R.anim.btn_right_action_close);

        fabMoveUpRotate = AnimationUtils.loadAnimation(this, R.anim.fab_move_up_rotate);
        plusOpenRotate = AnimationUtils.loadAnimation(this, R.anim.fab_plus_open_rotate);
        plusCloseRotate = AnimationUtils.loadAnimation(this, R.anim.fab_plus_close_rotate);
        fabMoveDown = AnimationUtils.loadAnimation(this, R.anim.fab_move_down);
        textMoveUp = AnimationUtils.loadAnimation(this, R.anim.text_move_up);
        textMoveOut = AnimationUtils.loadAnimation(this, R.anim.text_move_out);

        mMenu = (FloatingActionButton) findViewById(R.id.btnMenu);
        atm = (FloatingActionButton) findViewById(R.id.btnATM);
        hospital = (FloatingActionButton) findViewById(R.id.btnHospital);
        SuperMarket = (FloatingActionButton) findViewById(R.id.btnSupermarket);
        gas = (FloatingActionButton) findViewById(R.id.btnGasStation);
        restaurant = (FloatingActionButton) findViewById(R.id.btnRestaurant);
        cafe = (FloatingActionButton) findViewById(R.id.btnCafe);

        tvAtm = (TextView) findViewById(R.id.tv_ATM);
        tvCafe = (TextView) findViewById(R.id.tv_cafe);
        tvGas = (TextView) findViewById(R.id.tv_gas_station);
        tvSuperMarket = (TextView) findViewById(R.id.tv_supermarket);
        tvHospital = (TextView) findViewById(R.id.tv_hospital);
        tvRestaurant = (TextView) findViewById(R.id.tv_restaurant);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getLocationPermission();
        search();
        clickButton();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getMyLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }

            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMapLoadedCallback(this);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
        }
    }

    private void clickButton() {
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideButton();
                Intent intent = new Intent(MapActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("PlaceID", placeIDMarkerClicked);
                intent.putExtra("Data", bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButton();
                String url = buidURLDirections();
                DirectionsTask directionsTask = new DirectionsTask();
                directionsTask.execute(url);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(locateMe)      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(50)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flagMenuOpen) {
                    showChildMenu();
                } else {
                    hideChildMenu();
                }
                hideButton();
            }
        });

        gas.setOnClickListener(this);
        hospital.setOnClickListener(this);
        restaurant.setOnClickListener(this);
        SuperMarket.setOnClickListener(this);
        atm.setOnClickListener(this);
        cafe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        CameraPosition.Builder carmeraBuider = new CameraPosition.Builder();
        carmeraBuider.target(locateMe);
        carmeraBuider.bearing(90);                // Sets the orientation of the camera to east
        carmeraBuider.tilt(15);

        switch (v.getId()) {

            case R.id.btnATM:
                // do your code
                typeSearch = "atm";
                radiusSearch = "5000";
                carmeraBuider.zoom(16);
                carmeraZoom = 16;
                Log.d("click", "atm");
                break;
            case R.id.btnCafe:
                // do your code
                typeSearch = "cafe";
                radiusSearch = "5000";
                carmeraBuider.zoom(16);
                carmeraZoom = 16;
                Log.d("click", "cafe");
                break;

            case R.id.btnGasStation:
                // do your code
                typeSearch = "gas_station";
                radiusSearch = "5000";
                carmeraBuider.zoom(16);
                carmeraZoom = 16;
                Log.d("click", "gas");
                break;
            case R.id.btnSupermarket:
                // do your code
                typeSearch = "supermarket";
                radiusSearch = "5000";
                carmeraBuider.zoom(16);
                carmeraZoom = 16;
                Log.d("click", "supermarket");
                break;
            case R.id.btnHospital:
                // do your code
                typeSearch = "hospital";
                radiusSearch = "5000";
                carmeraBuider.zoom(16);
                carmeraZoom = 16;
                Log.d("click", "hospital");
                break;
            case R.id.btnRestaurant:
                // do your code
                typeSearch = "restaurant";
                radiusSearch = "5000";
                carmeraBuider.zoom(16);
                carmeraZoom = 16;
                Log.d("click", "restaurant");
                break;
            default:
                break;
        }
        CameraPosition cameraPosition = carmeraBuider.build();  //buid position for camera map
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        buidURLSearch();
        Log.d("test", urlNearBy);
        ParseNearPlaces places = new ParseNearPlaces();
        mMap.clear();
        places.execute(urlNearBy);
        hideChildMenu();

    }

    private void showButton() {
        btnDetail.startAnimation(showRight);
        btnDirect.startAnimation(showRight);
        btnDetail.setClickable(true);
        btnDirect.setClickable(true);
        btnDetail.setVisibility(View.VISIBLE);
        btnDirect.setVisibility(View.VISIBLE);
        flagOpenBtnRight = true;
    }

    private void hideButton() {
        if (flagOpenBtnRight) {
            btnDetail.startAnimation(hideRight);
            btnDirect.startAnimation(hideRight);
            btnDetail.setClickable(false);
            btnDirect.setClickable(false);
            btnDetail.setVisibility(View.INVISIBLE);
            btnDirect.setVisibility(View.INVISIBLE);
            flagOpenBtnRight = false;
        }
    }

    private void showChildMenu() {
        if (!flagMenuOpen) {
            atm.show();
            hospital.show();
            restaurant.show();
            SuperMarket.show();
            gas.show();
            cafe.show();

            mMenu.startAnimation(plusOpenRotate);
            atm.startAnimation(fabMoveUpRotate);
            hospital.startAnimation(fabMoveUpRotate);
            restaurant.startAnimation(fabMoveUpRotate);
            SuperMarket.startAnimation(fabMoveUpRotate);
            gas.startAnimation(fabMoveUpRotate);
            cafe.startAnimation(fabMoveUpRotate);

            atm.setClickable(true);
            SuperMarket.setClickable(true);
            hospital.setClickable(true);
            restaurant.setClickable(true);
            gas.setClickable(true);
            cafe.setClickable(true);

            tvRestaurant.startAnimation(textMoveUp);
            tvHospital.startAnimation(textMoveUp);
            tvSuperMarket.startAnimation(textMoveUp);
            tvGas.startAnimation(textMoveUp);
            tvCafe.startAnimation(textMoveUp);
            tvAtm.startAnimation(textMoveUp);

            tvCafe.setVisibility(View.VISIBLE);
            tvAtm.setVisibility(View.VISIBLE);
            tvGas.setVisibility(View.VISIBLE);
            tvSuperMarket.setVisibility(View.VISIBLE);
            tvHospital.setVisibility(View.VISIBLE);
            tvRestaurant.setVisibility(View.VISIBLE);
            flagMenuOpen = true;
        }

    }

    private void hideChildMenu() {

        if (flagMenuOpen) {
            tvRestaurant.startAnimation(textMoveOut);
            tvHospital.startAnimation(textMoveOut);
            tvSuperMarket.startAnimation(textMoveOut);
            tvGas.startAnimation(textMoveOut);
            tvCafe.startAnimation(textMoveOut);
            tvAtm.startAnimation(textMoveOut);

            tvCafe.setVisibility(View.INVISIBLE);
            tvAtm.setVisibility(View.INVISIBLE);
            tvGas.setVisibility(View.INVISIBLE);
            tvSuperMarket.setVisibility(View.INVISIBLE);
            tvHospital.setVisibility(View.INVISIBLE);
            tvRestaurant.setVisibility(View.INVISIBLE);

            mMenu.startAnimation(plusCloseRotate);
            atm.hide();
            hospital.hide();
            restaurant.hide();
            SuperMarket.hide();
            gas.hide();
            cafe.hide();

            atm.setClickable(false);
            SuperMarket.setClickable(false);
            hospital.setClickable(false);
            restaurant.setClickable(false);
            gas.setClickable(false);
            cafe.setClickable(false);
            flagMenuOpen = false;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(18f)
                .bearing(90)
                .tilt(40)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        hideButton();
        myLocation.show();
        mMenu.show();
        hideChildMenu();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marker.getPosition())
                .zoom(19)
                .bearing(20)
                .tilt(80)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        marker.showInfoWindow();

        if (!listPlaces.isEmpty() && marker.getSnippet() != null) {
            showButton();
            hideChildMenu();
            mMenu.hide();
            myLocation.hide();
            String makerAddress = marker.getSnippet();
            for (int i = 0; i < listPlaces.size(); i++) {
                NearByPlaces place = listPlaces.get(i);
                Log.d("list place" , String.valueOf(listPlaces.get(i)));
                String placeAddress = place.getAddress();
                if (placeAddress.equals(makerAddress)) {
                    placeIDMarkerClicked = place.getPlaceID();
                    Log.d("place id ",placeIDMarkerClicked);
                }
            }
        }

        return true;
    }

    @Override
    public void onMapLoaded() {
        getDevidelocation();
        buidURLSearch();
        ParseNearPlaces placesTask = new ParseNearPlaces();
        placesTask.execute(urlNearBy);
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    private void getMyLocation() {
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDevidelocation();
            }
        });
    }

    private void search() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);

        edtsearch.setAdapter(mAdapter);

        edtsearch.setOnItemClickListener(mAutoComplete);

        edtsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
                    mMap.clear();
                    searchLocate();
                    hideKeyBoard();
                    hideChildMenu();
                }
                return false;
            }
        });

        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = edtsearch.getText().toString();
                if (text.equals("") || text.length() < 1) {
                    imgClear.setVisibility(View.INVISIBLE);
                    imgClear.setClickable(false);
                } else if (text.equals(1) || text.length() > 1) {

                    imgClear.setVisibility(View.VISIBLE);
                    imgClear.setClickable(true);
                    imgClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            edtsearch.setText("");
                            mMap.clear();
                            getDevidelocation();
                            hideKeyBoard();
                            hideChildMenu();
                        }
                    });
                }
            }
        });
    }

    private AdapterView.OnItemClickListener mAutoComplete = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            mMap.clear();
            hideChildMenu();
            hideKeyBoard();
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String id = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,id);
            placeResult.setResultCallback(mResultCallBack);
        }
    };

    private ResultCallback<PlaceBuffer> mResultCallBack = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {

            if(!places.getStatus().isSuccess()){
                places.release();
                return;
            }

            final Place place = places.get(0);

            try{

                nearByPlace.setName(place.getName().toString());
                nearByPlace.setAddress(place.getAddress().toString());
                nearByPlace.setLatitude(place.getViewport().getCenter().latitude);
                nearByPlace.setLongitude(place.getViewport().getCenter().longitude);
                nearByPlace.setPlaceID(place.getId());

            }catch (NullPointerException e){

            }

            listPlaces.add(nearByPlace);

            LatLng locateSearch = new LatLng(nearByPlace.getLatitude(), nearByPlace.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(locateSearch)
                    .zoom(16)
                    .bearing(90)
                    .tilt(40)
                    .build();

            MarkerOptions markers = new MarkerOptions();
            markers.title(nearByPlace.getName());
            markers.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_mark));
            markers.snippet(nearByPlace.getAddress());
            markers.position(new LatLng(nearByPlace.getLatitude(), nearByPlace.getLongitude()));
            mMap.addMarker(markers);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    };

    private void hideKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchLocate() {
        String textSearch = edtsearch.getText().toString();
        String url = buidURLTextSearch(textSearch);
        ParsePlaces places = new ParsePlaces();
        places.execute(url);
    }

    private void buidURLSearch() {
        urlNearBy = BASIC_PARSE_URL_NEARBY + latSearch + TOKEN_COMMA + lngSearch + TOKEN_AND
                + TXT_RADIUS + radiusSearch + TOKEN_AND +
                TXT_TYPE + typeSearch + TOKEN_AND +
                SENSOR + TOKEN_AND +
                TXT_KEY + KEY_API_PLACES;
    }

    private String buidURLTextSearch(String textS) {
        String url = BASIC_URL_TEXT_SEARCH + textS + TOKEN_AND + TXT_KEY + KEY_API_PLACES;
        return url;
    }

    //get devide location
    private void getDevidelocation() {
        Log.d(TAG, "get devide location: getting devide the current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        mMap.clear();
                        hideButton();
                        hideChildMenu();
                        edtsearch.setText("");
                        Location location = (Location) task.getResult();
                        locateMe = new LatLng(location.getLatitude(),
                                location.getLongitude());
                        lngSearch = String.valueOf(locateMe.longitude);
                        latSearch = String.valueOf(locateMe.latitude);
                        moveCamera(locateMe, DEFAULT_ZOOM, "You");
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "get devide location: SecurityException " + e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .bearing(90)
                .tilt(40)
                .build();
        MarkerOptions markers = new MarkerOptions();
        markers.title(title);
        markers.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_map_marker));
        markers.position(latLng);
        mMap.addMarker(markers);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void searchmoveCamera(NearByPlaces nearByPlaces) {
        LatLng locateSearch = new LatLng(nearByPlaces.getLatitude(), nearByPlaces.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(locateSearch)
                .zoom(16)
                .bearing(90)
                .tilt(40)
                .build();

        MarkerOptions markers = new MarkerOptions();
        markers.title(nearByPlaces.getName());
        markers.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_mark));
        markers.snippet(nearByPlaces.getAddress());
        markers.position(new LatLng(nearByPlaces.getLatitude(), nearByPlaces.getLongitude()));
        mMap.addMarker(markers);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // update places
    private void updatePlacesNB(NearByPlaces nearByPlaces) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(nearByPlaces.getLatitude(), nearByPlaces.getLongitude()));
        markerOptions.title(nearByPlaces.getName());
        markerOptions.snippet(nearByPlaces.getAddress());

        Log.d("updatePlacesNB","places ID" + String.valueOf(nearByPlaces.getPlaceID()));
        Log.d("updatePlacesNB","Lat" + String.valueOf(nearByPlaces.getLatitude()));
        Log.d("updatePlacesNB","Long" + String.valueOf(nearByPlaces.getLongitude()));
        Log.d("updatePlacesNB","address" + String.valueOf(nearByPlaces.getAddress()));
        Log.d("updatePlacesNB","name" + String.valueOf(nearByPlaces.getName()));
        Log.d("updatePlacesNB","types" + String.valueOf(nearByPlaces.getTypes()));
        switch (typeSearch) {
            case "cafe":
                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_cafe_map));
                mMap.addMarker(markerOptions);
                break;
            case "restaurant":
                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_restaurant_map));
                mMap.addMarker(markerOptions);
                break;
            case "gas_station":
                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_gas_map));
                mMap.addMarker(markerOptions);
                break;
            case "supermarket":
                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_cart_map));
                mMap.addMarker(markerOptions);
                break;
            case "atm":
                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_dollar_map));
                mMap.addMarker(markerOptions);
                break;
            case "hospital":
                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_hospital_map));
                mMap.addMarker(markerOptions);
                break;
        }
        LatLng locateSearch = new LatLng(nearByPlaces.getLatitude(), nearByPlaces.getLongitude());
        Log.d("test", String.valueOf(locateSearch));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(locateSearch)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(15)                   // Sets the tilt of the camera to 15 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //parseJson NearByPlaces
    private class ParseTask extends AsyncTask<JSONObject, Integer, NearByPlaces> {
        NearByPlaces data = null;

        @Override
        protected NearByPlaces doInBackground(JSONObject... jsonObjects) {
            try {
                ParseJson parseJson = new ParseJson();
                data = parseJson.jsonNearByPlaces(jsonObjects[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.getMessage());
            }
            return data;
        }

        @Override
        protected void onPostExecute(NearByPlaces nearByPlaces) {
            listPlaces.add(nearByPlaces);
            searchmoveCamera(nearByPlaces);
        }
    }

    private class ParseTasks extends AsyncTask<JSONObject, Integer, NearByPlaces> {
        NearByPlaces data = null;

        @Override
        protected NearByPlaces doInBackground(JSONObject... jsonObjects) {
            try {
                ParseJson parseJson = new ParseJson();
                data = parseJson.JSONParseNearBy(jsonObjects[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.getMessage());
            }
            return data;
        }

        @Override
        protected void onPostExecute(NearByPlaces nearByPlaces) {
            listPlaces.add(nearByPlaces);
            Log.d("list place" , String.valueOf(listPlaces.size()));
            updatePlacesNB(nearByPlaces);
        }
    }

    private class ParsePlaces extends AsyncTask<String, Integer, String> {

        String data = null;

        @Override
        protected String doInBackground(String... url) {

            try {
                data = DownLoadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.getMessage());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            listPlaces.clear();
            JSONObject rootObject = null;
            try {
                rootObject = new JSONObject(result);
                JSONArray resultArr = rootObject.getJSONArray("results");
                for (int i = 0; i < resultArr.length(); i++) {
                    JSONObject jsonPlaces = resultArr.getJSONObject(i);
                    ParseTask parseTask = new ParseTask();
                    parseTask.execute(jsonPlaces);
                }
            } catch (JSONException e) {

            }
        }
    }

    private class ParseNearPlaces extends AsyncTask<String, Integer, String> {

        String data = null;

        @Override
        protected String doInBackground(String... url) {

            try {
                data = DownLoadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.getMessage());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            listPlaces.clear();
            JSONObject rootObject = null;
            try {
                rootObject = new JSONObject(result);
                JSONArray resultArr = rootObject.getJSONArray("results");
                for (int i = 0; i < resultArr.length(); i++) {
                    JSONObject jsonPlaces = resultArr.getJSONObject(i);
                    ParseTasks parseTasks = new ParseTasks();
                    parseTasks.execute(jsonPlaces);
                }
            } catch (JSONException e) {

            }
        }
    }

    private String DownLoadUrl(String Url) throws IOException {
        String data = "";
        InputStream ipStream = null;
        HttpsURLConnection urlConnect = null;
        try {

            URL url = new URL(Url);

            // create http connect to communicate with url
            urlConnect = (HttpsURLConnection) url.openConnection();

            //connecting to url
            urlConnect.connect();

            // reading data from url
            ipStream = urlConnect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(ipStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("url", e.toString());
        } finally {
            ipStream.close();
            urlConnect.disconnect();
        }
        return data;
    }

    //build url direction

    private String buidURLDirections() {

        String url = BASIC_URL_DIRECTIONS + locateMe.latitude +
                "," + locateMe.longitude + TXT_DESTINATION + placeIDMarkerClicked + TOKEN_AND
                + "language=" + language + TOKEN_AND + TXT_KEY + KEY_DIRECTIONS;
        return url;
    }

    // direction

    //draw directions
    private void drawDirections(Directions directions) {
        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<StepsDirections> steps = null;
        steps = directions.getSteps();
        for (StepsDirections step : steps) {

            polylineOptions.addAll(step.getPolyPoint());
            // Log.d("test",step.getInstructions());
        }


        polylineOptions.width(11);
        polylineOptions.color(getResources().getColor(R.color.colorAccent));

        if (polylineOptions != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(locateMe).title("you").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_map_marker)));
            mMap.addMarker(new MarkerOptions().position(directions.getEndLocation()).title(directions.getEndAddress()).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_mark)));
            mMenu.show();
            myLocation.show();
            mMap.addPolyline(polylineOptions);

        }

    }

    private class DirectionsTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            try {
                data = DownLoadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String directions) {
            parseDirectionsTask directionsTask = new parseDirectionsTask();
            directionsTask.execute(directions);
        }
    }

    private class parseDirectionsTask extends AsyncTask<String, Integer, Directions> {
        @Override
        protected Directions doInBackground(String... strings) {
            Directions directions = null;

            ParseJson direct = new ParseJson();
            directions = direct.parseDirections(strings[0]);
            return directions;
        }

        @Override
        protected void onPostExecute(Directions directions) {
            drawDirections(directions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        getDevidelocation();
        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
