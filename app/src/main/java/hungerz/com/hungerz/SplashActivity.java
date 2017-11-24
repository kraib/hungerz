package hungerz.com.hungerz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if  (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )  {

            ActivityCompat.requestPermissions((Activity) getBaseContext(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

        }else {
            Thread timer = new Thread(){
                public void  run(){
                    try{
                        sleep(500);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }finally {
                        Intent intent =new Intent(getBaseContext(),Choice.class);
                        startActivity(intent);
                    }
                }
            } ;
            timer.start();
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Thread timer = new Thread(){
                        public void  run(){
                            try{
                                sleep(500);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }finally {
                                Intent intent =new Intent(getBaseContext(),Choice.class);
                                startActivity(intent);
                            }
                        }
                    } ;
                    timer.start();
                    
                } else {
                    Toast.makeText(getBaseContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

}
