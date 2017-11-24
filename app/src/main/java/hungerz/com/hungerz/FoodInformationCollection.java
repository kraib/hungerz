package hungerz.com.hungerz;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import hungerz.com.hungerz.gps.GPSTracker;
import hungerz.com.hungerz.models.FoodInfoModel;

public class FoodInformationCollection extends AppCompatActivity implements View.OnClickListener, GPSTracker.Loc {

    protected Button submit;
    private TextView name;
    private TextView latitudeView;
    private TextView longitudeView;
    private EditText foodType;
    private EditText noOfPeople;
    private EditText timeLimit;
    private EditText wish;
    FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    Location currentLocation = null;
    DatabaseReference userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_food_information_collection);
        initView();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("users");
        userReference.keepSynced(true);

         userData = userReference.child(firebaseAuth.getUid());

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
        foodType = (EditText) findViewById(R.id.food_type);
        noOfPeople = (EditText) findViewById(R.id.no_of_people);
        timeLimit = (EditText) findViewById(R.id.time_limit);
        wish = (EditText) findViewById(R.id.wish);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit) {

            String foodTypeString = foodType.getText().toString();
          String  noOfPeopleString = noOfPeople.getText().toString();
          String timeLimitString = timeLimit.getText().toString();
          String wishString = wish.getText().toString();
           if(!TextUtils.isEmpty(foodTypeString) && !TextUtils.isEmpty(noOfPeopleString) && !TextUtils.isEmpty(timeLimitString)
                   && !TextUtils.isEmpty(wishString)){
               FoodInfoModel foodInfoModel = new FoodInfoModel();
               foodInfoModel.setFoodType(foodTypeString);
               foodInfoModel.setNumberOfPeople(noOfPeopleString);
               foodInfoModel.setTimeLimit(timeLimitString);
               foodInfoModel.setWish(wishString);
               foodInfoModel.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
               foodInfoModel.setLatitude(currentLocation.getLatitude() + "");
               foodInfoModel.setLongitude(currentLocation.getLongitude() + "");
              DatabaseReference ref = userData.child("events").push();
                      ref.setValue(foodInfoModel, new DatabaseReference.CompletionListener() {
                   @Override
                   public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                       if(databaseError==null){
                           Intent intent = new Intent(getBaseContext(), MapsWithSideNav.class);
                           startActivity(intent);
                       }else {
                           Toast.makeText(FoodInformationCollection.this, "Failed", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

           }else{
               Toast.makeText(this, "All fields Required", Toast.LENGTH_SHORT).show();
           }

//
//            Map<String, String> location = new HashMap<>();
//            location.put("lat", currentLocation.getLatitude() + "");
//            location.put("long", currentLocation.getLongitude() + "");
//
//            userReference.child(firebaseAuth.getUid()).child("location").setValue(location);


        }
    }

    @Override
    public void newLocation(Location newLocation) {
        if (newLocation != null) {
            currentLocation = newLocation;
            latitudeView.setText(currentLocation.getLatitude() + "");
            longitudeView.setText(currentLocation.getLongitude() + "");
        }

    }

    @Override
    public void mess(String pro) {

    }
}
