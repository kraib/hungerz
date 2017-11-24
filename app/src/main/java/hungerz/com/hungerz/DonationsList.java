package hungerz.com.hungerz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.util.ArrayList;
import java.util.HashMap;

import hungerz.com.hungerz.models.FoodInfoModel;

public class DonationsList extends AppCompatActivity {

    protected RecyclerView recyclerViewDonations;
    private DatabaseReference userReference;

    ArrayList<FoodInfoModel> donationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_donations_list);
        initView();
        donationsList =new ArrayList<>();

        recyclerViewDonations.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("users").child(FirebaseAuth.getInstance().getUid());
        userReference.keepSynced(true);

        userReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    for (DataSnapshot event: dataSnapshot.getChildren()){
                        FoodInfoModel model = event.getValue(FoodInfoModel.class);

                        donationsList.add(model);


                    }

                    SlimAdapter slimAdapter = SlimAdapter.create()
                            .register(R.layout.donations_item, new SlimInjector<FoodInfoModel>() {
                                @Override
                                public void onInject(FoodInfoModel data, IViewInjector injector) {
                                    TextView food = (TextView) injector.findViewById(R.id.food_type);
                                    TextView time_limit = (TextView) injector.findViewById(R.id.time_limit);

                                    food.setText("Food: "+data.getFoodType());
                                    time_limit.setText("Time Limit: "+data.getTimeLimit());
                                }
                            }).attachTo(recyclerViewDonations);

                    slimAdapter.updateData(donationsList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        recyclerViewDonations = (RecyclerView) findViewById(R.id.recyclerViewDonations);
    }
}
