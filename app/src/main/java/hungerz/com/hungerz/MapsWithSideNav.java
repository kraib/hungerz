package hungerz.com.hungerz;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hungerz.com.hungerz.gps.GPSTracker;
import hungerz.com.hungerz.models.FoodInfoModel;

public class MapsWithSideNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GPSTracker.Loc {

    private GoogleMap mMap;
    GPSTracker gpsTracker;
    Location currentLocation = null;

    List<FoodInfoModel> markersList;
    private DatabaseReference userReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_with_side_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        markersList = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("users");
        userReference.keepSynced(true);

    }


    // Adds markers to the mMap
    private void addMyMakers(List<FoodInfoModel> markersListData) {
        mMap.clear();
        for (FoodInfoModel listData : markersListData) {
            MarkerOptions markerOptions = new MarkerOptions();
            double lat = Double.parseDouble(listData.getLatitude());
            double lng = Double.parseDouble(listData.getLongitude());
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(listData.getFoodType());
            mMap.addMarker(markerOptions).setTag(listData);

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


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                FoodInfoModel model = (FoodInfoModel) arg0.getTag();
                showDialog(model);
            }
        });

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        DataSnapshot events = data.child("events");
                        for (DataSnapshot event: events.getChildren()){
                            FoodInfoModel model = event.getValue(FoodInfoModel.class);

                            HashMap<String, String> mark = new HashMap<>();
                            mark.put("lat", model.getLatitude());
                            mark.put("long", model.getLongitude());
                            markersList.add(model);
                        }
                    }
                    addMyMakers(markersList);

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



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps_with_side_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.donations) {
            // Handle the camera action
            Intent intent = new Intent(getBaseContext(),DonationsList.class);
            startActivity(intent);
        } else if (id == R.id.profile) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showDialog(FoodInfoModel foodInfoModel){
        try {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.search_dialog, null);
            // this is set the view from XML inside AlertDialog
            alert.setView(view);
            TextView input_name = (TextView)view.findViewById(R.id.name);
            TextView type = (TextView)view.findViewById(R.id.food_type);
            TextView time = (TextView)view.findViewById(R.id.time_limit);

            input_name.setText(foodInfoModel.getName());
            type.setText("Giving out "+foodInfoModel.getFoodType());
            time.setText("Stop time "+foodInfoModel.getTimeLimit());

            alert.setCancelable(true);

            final AlertDialog dialog = alert.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);



        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
