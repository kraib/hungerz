
package hungerz.com.hungerz.gps;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import hungerz.com.hungerz.LoginActivity;
import hungerz.com.hungerz.R;
import hungerz.com.hungerz.gps.GPSTracker;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GPSTracker.Loc, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    GPSTracker gpsTracker ;
    DrawerLayout drawer;
    private Context context = this;
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gpsTracker = new GPSTracker(getBaseContext(),this);

        //todo adding Navigation menu here!!!...
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.START)) {
                    drawer.closeDrawer(Gravity.START);
                } else {
                    drawer.openDrawer(Gravity.START);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showDialog(){
        try {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.search_dialog, null);
            // this is set the view from XML inside AlertDialog
            alert.setView(view);
            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);

            final AlertDialog dialog = alert.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);


        }catch (Exception e){
            e.printStackTrace();
        }

        if (mMap != null){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    showDialog();
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showDialog( );
            }
        });
    }

    @Override
    public void newLocation(Location newLocation) {
        Toast.makeText(getBaseContext(),newLocation.getLatitude()+"",Toast.LENGTH_LONG).show();
    }

    @Override
    public void mess(String pro) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item != null && item.getItemId() == android.R.id.home) {
            if (drawer.isDrawerOpen(Gravity.START)) {
                drawer.closeDrawer(Gravity.START);
            }
            else {
                drawer.openDrawer(Gravity.START);
            }
        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
         if (id == R.id.nav_add) {
             startActivity(new Intent(context, LoginActivity.class));
        } else if (id == R.id.nav_search) {

        }  else if (id == R.id.nav_exit) {
             //todo exit the application here..!!
             finish();
        }
        //try {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        // }catch (Exception)
        return true;
    }
}
