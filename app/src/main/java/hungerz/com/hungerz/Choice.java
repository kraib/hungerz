package hungerz.com.hungerz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Choice extends AppCompatActivity implements View.OnClickListener {

    protected Button search;
    protected Button donate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_choice);
        initView();



    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search) {
            Intent intent =new Intent(getBaseContext(),MapsActivity.class);
            startActivity(intent);

        } else if (view.getId() == R.id.donate) {

            Intent intent =new Intent(getBaseContext(),LoginActivity.class);
            startActivity(intent);

        }
    }

    private void initView() {
        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(Choice.this);
        donate = (Button) findViewById(R.id.donate);
        donate.setOnClickListener(Choice.this);
    }
}
