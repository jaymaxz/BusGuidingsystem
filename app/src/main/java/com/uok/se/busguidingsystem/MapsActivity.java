package com.uok.se.busguidingsystem;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int REQUEST_CHECK_SETTINGS = 1717;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Map<String, Marker> Markers;
    private boolean TrackMe = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Markers = new HashMap<String, Marker>();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Locations");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkPermissions()) {
            getLastLocation();
            setLocationListener();
            createLocationRequest();
        }
        Switch mySwitch = (Switch)findViewById(R.id.switchTrackMe);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    TrackMe = true;
                }
                else
                {
                    TrackMe = false;
                }
            }
        });
    }

    private void setDataChangeListener() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, com.uok.se.busguidingsystem.Location> liveUsers = (Map<String, com.uok.se.busguidingsystem.Location>) dataSnapshot.getValue();
                for(Map.Entry<String, com.uok.se.busguidingsystem.Location> user : liveUsers.entrySet()){
                    Map<String,String> locationDeatails = (Map<String,String>) user.getValue();
                    com.uok.se.busguidingsystem.Location location = new com.uok.se.busguidingsystem.Location();
                    for(String key: locationDeatails.keySet()){
                        location.setEmail(locationDeatails.get("email"));
                        location.setLat(locationDeatails.get("lat"));
                        location.setLng(locationDeatails.get("lng"));
                    }
                    displayLocation(location);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        };
        //dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(postListener);
        dbRef.addValueEventListener(postListener);
    }

    private void displayLocation(com.uok.se.busguidingsystem.Location location) {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context mContext = getApplicationContext();
                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });
        DecimalFormat df = new DecimalFormat("#.###");
        Location oldLocation = new Location("");
        Location newLocation = new Location("");
        Marker currentMarker = Markers.get(location.getEmail());
        if(currentMarker!=null)
        {
            oldLocation.setLongitude(currentMarker.getPosition().longitude);
            oldLocation.setLatitude(currentMarker.getPosition().latitude);
            currentMarker.remove();
        }
        LatLng latLng = new LatLng( Double.parseDouble(location.getLat()), Double.parseDouble(location.getLng()));
        newLocation.setLatitude(Double.parseDouble(location.getLat()));
        newLocation.setLongitude(Double.parseDouble(location.getLng()));
        float speed = oldLocation.distanceTo(newLocation)*(3.6f/4);
        if(location.getEmail()==FirebaseAuth.getInstance().getCurrentUser().getEmail()) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(location.getEmail()).snippet("Speed: "+df.format(speed)+"KmpH"+"\n"+"")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_bus)));
            if(TrackMe){
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));
            }
            Markers.put(location.getEmail(),marker);
        }
        else
        {
            Marker mynMarker = (Marker) Markers.get(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            Location myLocation = new Location("");
            myLocation.setLongitude(mynMarker.getPosition().longitude);
            myLocation.setLatitude(mynMarker.getPosition().latitude);
            Location hisLocation = new Location("");
            hisLocation.setLongitude(latLng.longitude);
            hisLocation.setLatitude(latLng.latitude);
            float distance = hisLocation.distanceTo(myLocation);
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(location.getEmail()).snippet("Distance: "+df.format(distance/1000)+"Km"+"\n"+"Speed: "+df.format(speed)+"KmpH")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
            Markers.put(location.getEmail(),marker);
        }


    }

    private void setLocationListener() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocation((location));
                }
            }

            ;
        };
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    private void updateLocation(Location location) {
        com.uok.se.busguidingsystem.Location locationObj = new com.uok.se.busguidingsystem.Location(
                FirebaseAuth.getInstance().getCurrentUser().getEmail(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())
        );
        dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(locationObj);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        setDataChangeListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                    setLocationListener();
                    createLocationRequest();
                }
            }
            break;
        }
    }

    private void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                StartLocationRequest();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        return;
                    }
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);

    }

    private void StartLocationRequest() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    updateLocation(location);
                }
            };
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                StartLocationRequest();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                return;
            }
        }
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            updateLocation(location);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
