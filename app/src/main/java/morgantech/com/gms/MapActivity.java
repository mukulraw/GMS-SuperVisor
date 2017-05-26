package morgantech.com.gms;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import morgantech.com.gms.Utils.Prefs;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat, lng;
    List<Address> addresses;
    TextView location_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        location_text_view = (TextView) findViewById(R.id.location_text_view);

        Intent in2 = getIntent();
        lng = Double.parseDouble(in2.getStringExtra("lng"));
        lat = Double.parseDouble(in2.getStringExtra("lat"));

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception e) {

        }

        Prefs prefs = new Prefs();
        prefs.setPreferencesString(MapActivity.this, "login", "App");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera


        if (addresses != null && addresses.size() > 0) {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            location_text_view.setText(address + "," + "city" + "," + "\n" + state + "," + country);
            LatLng sydney = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng)).zoom(18).build();
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));


        } else {
            LatLng sydney = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions().position(sydney)).setTitle(String.valueOf(lat) + "," + String.valueOf(lng));
            // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng)).zoom(18).build();
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        mMap.clear();
        finish();
    }
}
