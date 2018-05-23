package mx.com.iddrive.iddrive;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONException;

import org.apache.commons.lang3.RandomStringUtils;

import java.net.URISyntaxException;

public class login_iddrive extends AppCompatActivity {

    Button btn_enviar;
    EditText ed_idprimario;
    EditText ed_idsecundario;
    EditText ed_pass;

    String idP="";
    String idS="";
    String idPwd="";

    private Socket mSocket;

    SharedPreferences prefs;
    String registros="clv_reg_01";

    String socketWebid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_iddrive);
        installIdd();
        connectIdd();
        enviar();
    }

    public void installIdd(){

        prefs = getSharedPreferences(registros,Context.MODE_PRIVATE);
         socketWebid = prefs.getString("socketWebid", "notVal");
        //Log.d("info_log",socketWebid);


        if(socketWebid.contentEquals("notVal")){

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("socketWebid", "iddrive_"+rnds());
            editor.commit();

            socketWebid = prefs.getString("socketWebid", "notVal");
          }




    }


    public void connectIdd(){

        Toast en;
        if(isNetDisponible()){

            if(isOnlineNet()){
                try {
                    mSocket = IO.socket("http://www.gascert.online:1111");
                    mSocket.on(socketWebid, onNewMessage);
                    mSocket.connect();
                } catch (URISyntaxException e) {


                }


            }else{
                en= Toast.makeText(getApplicationContext(),"Servidor no disponible", Toast.LENGTH_SHORT);
                en.show();
            }
        }else{
            en= Toast.makeText(getApplicationContext(),"Red no disponible", Toast.LENGTH_SHORT);
            en.show();

        }



    }

    public void enviar(){


        btn_enviar=(Button)findViewById(R.id.idEnviar);
        ed_idprimario=(EditText) findViewById(R.id.idPrincipal);
        ed_idsecundario=(EditText) findViewById(R.id.idSecundario);
        ed_pass=(EditText) findViewById(R.id.idPass);

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                JSONObject json=new JSONObject();


                if(ed_idprimario.length()!=0&&ed_idsecundario.length()!=0&&ed_pass.length()!=0){

                    try {
                        json.put("appSocket",socketWebid);
                        json.put("idPrimario",ed_idprimario.getText());
                        json.put("idSecundario",ed_idsecundario.getText());
                        json.put("idPassword",ed_pass.getText());

                        mSocket.emit("loginAndroid", json);




                       // Log.d("info_log",json.toString());

                    }catch (JSONException e){}

                }else{
                    Toast enviando2 = Toast.makeText(getApplicationContext(),"Campo Vacio", Toast.LENGTH_SHORT);
                    enviando2.show();
                }

            }
        });


    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint
                ("MissingPermission") NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.gascert.online");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    String rnds(){
        String generatedString = RandomStringUtils.randomAlphanumeric(16);
        return generatedString;

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           login_iddrive.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        if(data.get("response").equals("registro_full")){

                            JSONObject datos=data.getJSONObject("data");

                            prefs = getSharedPreferences(registros,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("nombre", (String) datos.get("nombre"));
                            editor.putString("idt", (String) datos.get("idt"));
                            editor.putString("puesto", (String) datos.get("puesto"));
                            editor.putString("domicilio", (String) datos.get("domicilio"));
                            editor.putString("telefono", (String) datos.get("telefono"));
                            editor.putString("email", (String) datos.get("email"));
                            editor.putBoolean("install", true);
                            editor.commit();

                            Intent intent = new Intent(login_iddrive.this, nav_iddrive.class);
                            startActivity(intent);
                            finish();


                        }


                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };


}
