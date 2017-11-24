package hungerz.com.hungerz.gps;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by sokool on 8/10/2017.
 */

public class GPSTracker extends Service implements LocationListener {

    private final Context context;
    boolean isGPSEnabled =false;
    boolean isNetworkEnabled =false;
    boolean canGetLocation = false;

    Location location;
    protected LocationManager locationManager;
    Loc loc;

    public GPSTracker(Context context, Loc loc)
    {
        this.loc = loc;
        this.context=context;


    }


    //Create a GetLocation Method //
    public Location getLocation(){
        try{

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            isNetworkEnabled=locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){



                if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )){
                    showDialog(context);
                    loc.mess("off");
                }
                // if lcoation is not found from GPS than it will found from network //
                if(location==null){
                    if(isGPSEnabled){
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,10,this);
                            if(locationManager!=null){
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if(location != null){
                                    loc.newLocation(location);
                                    return location;
                                }

                            }
                    }


                    if(isNetworkEnabled){

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,10,this);
                        if(locationManager!=null){
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if(location != null){
                                loc.newLocation(location);
                                return location;
                            }
                        }

                    }
                }
                else{
                    return location;
                }

            }

        }catch(Exception ex){

        }
        return  location;
    }

    public void onLocationChanged(Location locationd){
        if(locationd != null){

            location =locationd;
            loc.newLocation(location);
        }

    }

    public void onStatusChanged(String Provider, int status, Bundle extras){

    }
    public void onProviderEnabled(String Provider){
        loc.mess("on");
        loc.newLocation(getLocation());

    }
    public void onProviderDisabled(String Provider){
        loc.mess("off");

    }
    public IBinder onBind(Intent arg0){
        return null;
    }

    interface Loc{
        public void newLocation(Location newLocation);
        public void mess(String pro);
    }


    public void remove(){
        if(locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    public void showDialog(final Context ntxt){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("Location not enabled");
        dialog.setPositiveButton("turn on", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ntxt.startActivity(myIntent);
                //get gps
            }
        });
        dialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        dialog.setCancelable(false);

        dialog.show();
    }
}