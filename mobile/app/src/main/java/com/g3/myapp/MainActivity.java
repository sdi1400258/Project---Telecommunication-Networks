package com.g3.myapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.g3.myapp.internetoptions.ApplicationOptions;
import com.g3.myapp.markeroptions.MqttMarker;
import com.g3.myapp.mqtt.MqttController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// AIzaSyBa2kabAVayhDrrvVK4phcheUXrfQbDvuQ

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText duration;
    private Button buttonStart;
    private Button buttonStop;
    private BackgroundJob job;

    private GoogleMap mMap;

    private LatLng focusPoint = new LatLng(37.96775,  23.770075);
    private float firstFocus = 15;

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void resetMap() {
        mMap.clear();
    }
    class OpenSettings implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    class StartSending implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Start sending ...", Toast.LENGTH_SHORT).show();

            try {
                resetMap();

                ApplicationOptions.setTIME(Integer.parseInt(duration.getText().toString()));

                job = new BackgroundJob();
                job.execute();
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "**** invalid duration ***** ", Toast.LENGTH_SHORT).show();
            }


        }
    }

    class StopSending implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Stop sending ...", Toast.LENGTH_SHORT).show();
            if (job != null) {
                job.cancel(true);
            }
        }
    }

    class BackgroundJob extends AsyncTask<Void, MqttMarker, Void> implements MqttCallback {
        private int sampleCounter = 0;


        protected void onPreExecute() {
            super.onPreExecute();

            buttonStart.setEnabled(false);
            buttonStop.setEnabled(true);
            duration.setEnabled(false);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            int role = ApplicationOptions.getRole();

            MqttController ctr = new MqttController(role);

            ctr.connect();


            if (role == 26) {
                ctr.subscribeToEdgeServer(1, BackgroundJob.this);
                ctr.publishToEdge("new_database", 1);
            } else {
                ctr.subscribeToEdgeServer(2, BackgroundJob.this);
                ctr.publishToEdge("new_database", 2);
            }


            try {
                int i = 0;

                BufferedReader reader;


                final InputStream file = getAssets().open(ApplicationOptions.getFile());
                reader = new BufferedReader(new InputStreamReader(file));
                String line = reader.readLine();


                while (line != null && !this.isCancelled()) {


                    if (role == 26) {
                        ctr.publishToEdge(line, 1);
                    } else {
                        ctr.publishToEdge(line, 2);
                    }


                    Thread.sleep(1000);

                    if (++i == ApplicationOptions.getTIME()) {
                        break;
                    }

                    line = reader.readLine();
                }

                if (role == 26) {
                    ctr.publishToEdge("end_transmission", 1);
                } else {
                    ctr.publishToEdge("end_transmission", 2);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

                if (role == 26) {
                    ctr.publishToEdge("end_transmission", 1);
                } else {
                    ctr.publishToEdge("end_transmission", 2);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ctr.disconnect();
            }
            return null;
        }


        protected void onProgressUpdate(MqttMarker... array) {

            for (MqttMarker m : array) {
                super.onProgressUpdate(array);

                sampleCounter++;

                LatLng real = new LatLng(m.real_lat, m.real_lon);
                LatLng pred = new LatLng(m.pred_lat, m.pred_lon);

                String real_title = "(" + sampleCounter + ")" + "R:" + m.real_rssi + ", T:" + m.real_throughput;
                String pred_title = "(" + sampleCounter + ")" + "R:" + m.pred_rssi + ", T:" + m.pred_throughput;

                float hueBlue = BitmapDescriptorFactory.HUE_BLUE;
                float hueRed = BitmapDescriptorFactory.HUE_RED;

                mMap.addMarker(new MarkerOptions().position(real).title(real_title).icon(BitmapDescriptorFactory.defaultMarker(hueRed)));

                mMap.addMarker(new MarkerOptions().position(pred).title(pred_title).icon(BitmapDescriptorFactory.defaultMarker(hueBlue)));
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            buttonStart.setEnabled(true);
            buttonStop.setEnabled(false);
            duration.setEnabled(true);

            job = null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            buttonStart.setEnabled(true);
            buttonStop.setEnabled(false);
            duration.setEnabled(true);

            job = null;
        }

        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String s= new String(message.getPayload());
            Log.i("messageArrived", s);

            String [] fields = s.split(",");

            MqttMarker mm = new MqttMarker(
                    Double.parseDouble(fields[0]),
                    Double.parseDouble(fields[1]),
                    Double.parseDouble(fields[4]),
                    Double.parseDouble(fields[5]),
                    Double.parseDouble(fields[2]),
                    Double.parseDouble(fields[3]),
                    Double.parseDouble(fields[6]),
                    Double.parseDouble(fields[7])
            );

            this.publishProgress(mm);

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        MqttController ctr = new MqttController();
//
//        ctr.connect();
//        ctr.publishToEdge1("hello");
//

        Button button = findViewById(R.id.idButtonTest);

        button.setOnClickListener(new OpenSettings());

        buttonStart = findViewById(R.id.buttonStart);

        buttonStart.setOnClickListener(new StartSending());

        buttonStop = findViewById(R.id.buttonStop);

        buttonStop.setOnClickListener(new StopSending());


        duration = findViewById(R.id.duration);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String[] options = ApplicationOptions.getOptions();

        duration.setText(options[2]);
    }

    @Override
    protected void onStop() {
        super.onStop();

        String[] options = ApplicationOptions.getOptions();

        options[2] = duration.getText().toString();

        ApplicationOptions.setOptions(options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            confirmExit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    boolean doubleBackToExitPressedOnce = false;


    public void confirmExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(focusPoint, firstFocus));
    }

}


