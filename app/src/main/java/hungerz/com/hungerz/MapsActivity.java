package hungerz.com.hungerz;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hungerz.com.hungerz.gps.GPSTracker;
import hungerz.com.hungerz.models.FoodInfoModel;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GPSTracker.Loc, View.OnClickListener {

    private GoogleMap mMap;
    GPSTracker gpsTracker;
    Location currentLocation = null;

    List<FoodInfoModel> markersList;
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
            type.setText("Giving out"+foodInfoModel.getFoodType());
            time.setText("Stop time"+foodInfoModel.getTimeLimit());

            alert.setCancelable(true);

            final AlertDialog dialog = alert.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);



        }catch (Exception e){
            e.printStackTrace();
        }

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
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView()));
            mMap.addMarker(markerOptions).setTag(listData);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }
    }
    private Bitmap getMarkerBitmapFromView() {

        Bitmap returnedBitmap = null;
        try {
            View customMarkerView = ((LayoutInflater) (this).getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);

            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
        }catch (NullPointerException e){
        }

        return returnedBitmap;
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

                            Toast.makeText(getBaseContext(), model.getFoodType(), Toast.LENGTH_SHORT).show();
                            HashMap<String, String> mark = new HashMap<>();
                            mark.put("lat", model.getLatitude());
                            mark.put("long", model.getLongitude());
                            markersList.add(model);
                        }
                    }
                    addMyMakers(markersList);

                    Toast.makeText(getBaseContext(), markersList.size()+"", Toast.LENGTH_SHORT).show();
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
