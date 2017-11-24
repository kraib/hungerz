package hungerz.com.hungerz;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by SO-KOOL on 1/17/2017.
 */

public class FireBaseInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(!FirebaseApp.getApps(this).isEmpty()){

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }



    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


}