package hungerz.com.hungerz;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FoodInformationCollection extends AppCompatActivity implements View.OnClickListener, GPSTracker.Loc {

    protected Button submit;
    protected TextView name;
    protected TextView latitudeView;
    protected TextView longitudeView;
    FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    Location currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_food_information_collection);
        initView();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("users");
        userReference.keepSynced(true);

        DatabaseReference userData = userReference.child(firebaseAuth.getUid());

        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameData = dataSnapshot.child("name").getValue().toString();
                name.setText(nameData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GPSTracker gpsTracker = new GPSTracker(this, this);
        gpsTracker.getLocation();
    }

    private void initView() {
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(FoodInformationCollection.this);
        name = (TextView) findViewById(R.id.name);
        latitudeView = (TextView) findViewById(R.id.latitude_view);
        longitudeView = (TextView) findViewById(R.id.longitude_view);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit) {


            Map<String,String> location = new HashMap<>();
            location.put("lat",currentLocation.getLatitude()+"");
            location.put("long",currentLocation.getLongitude()+"");

            userReference.child(firebaseAuth.getUid()).child("location").setValue(location);
            Intent intent = new Intent(getBaseContext(), MapsWithSideNav.class);
            startActivity(intent);

        }
    }

    @Override
    public void newLocation(Location newLocation) {
        if(newLocation!=null){
            currentLocation = newLocation;
            latitudeView.setText(currentLocation.getLatitude()+"");
            longitudeView.setText(currentLocation.getLongitude()+"");
        }

    }

    @Override
    public void mess(String pro) {

    }
}
