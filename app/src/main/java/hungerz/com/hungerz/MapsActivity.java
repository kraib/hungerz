package hungerz.com.hungerz;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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

    protected FloatingActionButton search;
    private EditText userName;
    protected Button submit;
    private GoogleMap mMap;
    GPSTracker gpsTracker;
    DatabaseReference databaseReference;
    Location currentLocation = null;

    List<HashMap<String, String>> markersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        markersList = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gpsTracker = new GPSTracker(getBaseContext(), this);
        gpsTracker.getLocation();
        initView();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
         databaseReference = database.getReference("users");

        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        HashMap<String, String> mark = new HashMap<>();
                        mark.put("lat",data.child("lat").getValue().toString());
                        mark.put("long",data.child("long").getValue().toString());
//                        Toast.makeText(getBaseContext(), data.getValue().toString(), Toast.LENGTH_SHORT).show();
                        markersList.add(mark);
                    }


                    for(Map<String,String> mm : markersList){
//                        Double lat = mm.get("lat");
//                        Double lng = mm.get("long");
//                        addMyMakers(lat,lng);
                    }

                    ShowNearbyPlaces(markersList);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void addMyMakers(Double lat, Double lng){

        LatLng newMarker = new LatLng(lat, lng);
        Toast.makeText(getBaseContext(), newMarker.toString(), Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions().position(newMarker).title("Marker works"));


//        mMap.addMarker(markerOptions);

    }


    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        mMap.clear();
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("long"));
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            Toast.makeText(getBaseContext(), markerOptions.toString(), Toast.LENGTH_SHORT).show();
            //move map camera
//            gooMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            gooMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void newLocation(Location newLocation) {
        currentLocation = newLocation;
        Toast.makeText(getBaseContext(), newLocation.getLatitude() + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void mess(String pro) {

    }

    private void initView() {
        search = (FloatingActionButton) findViewById(R.id.search);
        search.setOnClickListener(MapsActivity.this);
        userName = (EditText) findViewById(R.id.userName);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(MapsActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search) {

            if (currentLocation != null) {
                Toast.makeText(getBaseContext(), currentLocation.getLatitude() + "", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "null", Toast.LENGTH_LONG).show();
            }

        } else if (view.getId() == R.id.submit) {
            String user = userName.getText().toString();

            if(!TextUtils.isEmpty(user)){

                Map<String,String> location = new HashMap<>();
                location.put("lat",currentLocation.getLatitude()+"");
                location.put("long",currentLocation.getLongitude()+"");

                databaseReference.child(user).setValue(location);
            }

        }
    }

}
