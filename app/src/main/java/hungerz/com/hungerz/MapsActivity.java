package hungerz.com.hungerz;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GPSTracker.Loc, View.OnClickListener {

    private GoogleMap mMap;
    GPSTracker gpsTracker;
    Location currentLocation = null;

    List<HashMap<String, String>> markersList;
    private DatabaseReference userReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        markersList = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("users");
        userReference.keepSynced(true);





    }


    // Adds markers to the mMap
    private void addMyMakers(List<HashMap<String, String>> markersListData) {
        mMap.clear();
        for (HashMap<String, String> listData : markersListData) {
            MarkerOptions markerOptions = new MarkerOptions();
            double lat = Double.parseDouble(listData.get("lat"));
            double lng = Double.parseDouble(listData.get("long"));
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        gpsTracker = new GPSTracker(getBaseContext(), this);
        currentLocation=  gpsTracker.getLocation();

        if (currentLocation!=null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }

        if (!markersList.isEmpty()){
            addMyMakers(markersList);
        }

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> mark = new HashMap<>();
                        mark.put("lat", data.child("location").child("lat").getValue().toString());
                        mark.put("long", data.child("location").child("long").getValue().toString());
                        markersList.add(mark);
                    }
                    addMyMakers(markersList);

                    Toast.makeText(MapsActivity.this, markersList.size()+"", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void newLocation(Location newLocation) {
        currentLocation = newLocation;
        if (currentLocation!=null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
//        Toast.makeText(getBaseContext(), newLocation.getLatitude() + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void mess(String pro) {

    }

    // Initialising all the views


    @Override
    public void onClick(View view) {


    }

}
