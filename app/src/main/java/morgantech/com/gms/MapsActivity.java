package morgantech.com.gms;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import morgantech.com.gms.UiHelper.CustomMapFragment;
import morgantech.com.gms.UiHelper.MapWrapperLayout;

public class MapsActivity extends Activity implements MapWrapperLayout.OnDragListener {

    // Google Map
    private GoogleMap googleMap;
    private CustomMapFragment mCustomMapFragment;

    private View mMarkerParentView;
    private ImageView mMarkerImageView;

    private int imageParentWidth = -1;
    private int imageParentHeight = -1;
    private int imageHeight = -1;
    private int centerX = -1;
    private int centerY = -1;
    double lat, lng;
    private String flag;
    int i = 0;

    private TextView mLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Button btn_save = (Button) findViewById(R.id.btn_save);
        initializeUI();

        Intent in2 = getIntent();
        TextView text_view = (TextView) findViewById(R.id.text_view);
        lng = Double.parseDouble(in2.getStringExtra("lng"));
        lat = Double.parseDouble(in2.getStringExtra("lat"));
        flag = in2.getStringExtra("flag");
        Log.e("Lat", String.valueOf(lat) + String.valueOf(lng));
        LatLng centerLatLng = new LatLng(lat, lng);
        updateLocation(centerLatLng);

        if (flag.equals("home")) {
            btn_save.setText("Back");
            text_view.setVisibility(View.GONE);
        } else {
            btn_save.setVisibility(View.VISIBLE);
            text_view.setVisibility(View.VISIBLE);
        }

        // InitializeUI


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                if (flag.equals("pic")) {

                    if (extras != null) {
                        Intent intent = new Intent(MapsActivity.this, ReportIncident.class);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        Log.e("Lat", String.valueOf(lat) + String.valueOf(lng));
                        setResult(1, intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        finish();
                    }

                } else if (flag.equals("home")) {
                    Intent intent = new Intent(MapsActivity.this, Home.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    Log.e("Lat", String.valueOf(lat) + String.valueOf(lng));
                    setResult(1, intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                } else {
                    if (extras != null) {
                        Intent intent = new Intent(MapsActivity.this, Report_Incident_video.class);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        setResult(2, intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        finish();
                    }
                }
            }
        });

    }

    private void initializeUI() {

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationTextView = (TextView) findViewById(R.id.location_text_view);
        mMarkerParentView = findViewById(R.id.marker_view_incl);
        mMarkerImageView = (ImageView) findViewById(R.id.marker_icon_view);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        imageParentWidth = mMarkerParentView.getWidth();
        imageParentHeight = mMarkerParentView.getHeight();
        imageHeight = mMarkerImageView.getHeight();

        centerX = imageParentWidth / 2;
        centerY = (imageParentHeight / 2) + (imageHeight / 2);
    }

    private void initilizeMap() {
        if (googleMap == null) {
            mCustomMapFragment = ((CustomMapFragment) getFragmentManager()
                    .findFragmentById(R.id.map));
            mCustomMapFragment.setOnDragListener(MapsActivity.this);
            googleMap = mCustomMapFragment.getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
        // 10);
        // googleMap.animateCamera(cameraUpdate);
        // locationManager.removeUpdates(this);

       /* LatLng sydney = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(sydney));

        */

        LatLng sydney = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));

        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng)).zoom(18).build();*/
      /*  googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (googleMap != null && googleMap
                    .getProjection() != null) ? googleMap.getProjection()
                    : null;
            //
            if (projection != null) {
                LatLng centerLatLng = projection.fromScreenLocation(new Point(
                        centerX, centerY));
                updateLocation(centerLatLng);

            }
        }
    }

    private void updateLocation(LatLng centerLatLng) {
        if (centerLatLng != null) {
            if (i == 0) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(centerLatLng.latitude, centerLatLng.longitude)).zoom(18).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
                i++;
            }

            lat = centerLatLng.latitude;
            lng = centerLatLng.longitude;
            Geocoder geocoder = new Geocoder(MapsActivity.this,
                    Locale.getDefault());

            List<Address> addresses = new ArrayList<Address>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude,
                        centerLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {

                String addressIndex0 = (addresses.get(0).getAddressLine(0) != null) ? addresses
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addresses.get(0).getAddressLine(1) != null) ? addresses
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addresses.get(0).getAddressLine(2) != null) ? addresses
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addresses.get(0).getAddressLine(3) != null) ? addresses
                        .get(0).getAddressLine(3) : null;

                String completeAddress = addressIndex0 + "," + addressIndex1;

                if (addressIndex2 != null) {
                    completeAddress += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += "," + addressIndex3;
                }
                if (completeAddress != null) {
                    mLocationTextView.setText(completeAddress);
                }
            }
        }
    }
}