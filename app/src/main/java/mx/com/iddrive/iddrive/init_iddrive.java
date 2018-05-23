package mx.com.iddrive.iddrive;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class init_iddrive extends AppCompatActivity {

    Handler handler;
    private static final int    REQUEST_ENABLE_BT   = 1;
    final int permiso_loc=2;
    private BluetoothAdapter bAdapter;
    AlertDialog alert = null;
    SharedPreferences prefs;
    String registros="clv_reg_01";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_init_iddrive);




        loc_permiso();




    }




    public void loc_permiso(){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(init_iddrive.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(init_iddrive.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {



            } else {



                ActivityCompat.requestPermissions(init_iddrive.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        permiso_loc);

            }
        }
        else{
           enc_com();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults) {
        switch (requestCode) {



            case permiso_loc: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    enc_com();

                } else {

                    Toast permiso =
                            Toast.makeText(getApplicationContext(),
                                    "Permiso de localizacion requerido", Toast.LENGTH_LONG);
                                  permiso.show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }



    }

    public void enc_com(){
        bAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bAdapter.isEnabled())
        {


            final AlertDialog alert = null;
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                AlertNoGps();
            }else {
                 splash();
            }
        }
        else
        {
            // Lanzamos el Intent que mostrara la interfaz de activacion del
            // Bluetooth. La respuesta de este Intent se manejara en el metodo
            // onActivityResult
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


    }



    public void splash(){
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                prefs = getSharedPreferences(registros,Context.MODE_PRIVATE);
                boolean install = prefs.getBoolean("install", false);

                if(install==true){
                    Intent intent = new Intent(init_iddrive.this, nav_iddrive.class);
                    startActivity(intent);
                    finish();
                }else {

                    Intent intent = new Intent(init_iddrive.this, login_iddrive.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {
            case REQUEST_ENABLE_BT:
            {
                if(resultCode == RESULT_OK)
                {

                    final AlertDialog alert = null;
                    final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        AlertNoGps();
                    }else{

                        splash();
                    }


                }
                else
                {
                    Toast blue =
                            Toast.makeText(getApplicationContext(),
                                    "Bluetooth requerido", Toast.LENGTH_LONG);
                    blue.show();

                }
                break;
            }

            default:
                break;
        }
    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        splash();
                    }
                });
        alert = builder.create();
        alert.show();
    }


}
