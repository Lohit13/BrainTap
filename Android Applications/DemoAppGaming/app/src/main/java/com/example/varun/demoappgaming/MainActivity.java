//package com.example.varun.demoappclassroom;
package com.example.varun.demoappgaming;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.toString;
public class MainActivity extends AppCompatActivity {
    public int count = 1;
    Handler m_handler;
    Runnable m_handlerTask ;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
    }
    public void showRecordings(View view) {
        Intent intent = new Intent(this, ListDisplay.class);
        startActivity(intent);
    }

    public void startService(View view) {
        Intent intent = new Intent(this, AudioService.class);
        startService(intent);
        final long[] delayTimeinMS = {500};

        queue = Volley.newRequestQueue(this);
        m_handler = new Handler();
        m_handlerTask = new Runnable()
        {
            @Override
            public void run() {
                // do something
//                Toast.makeText(getApplicationContext(), "tada", Toast.LENGTH_LONG).show();
                Log.d("h0,", "hello1");
                String url ="http://192.168.1.92:8080/final";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v("reponse", "a" + response + "a");
                                if(parseInt(response) == 0 ) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextView status = (TextView)findViewById(R.id.status);
                                            status.setText("Relaxed");
                                            ImageView imageView = (ImageView)findViewById(R.id.imageView2);
                                            imageView.setVisibility(View.GONE);
                                            ImageView imageView2 = (ImageView)findViewById(R.id.imageView3);
                                            imageView2.setVisibility(View.GONE);
                                            ImageView imageView3 = (ImageView)findViewById(R.id.imageView4);
                                            imageView3.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    Log.v("Where?", "getting response 0");
                                }
                                else{
                                    Log.v("Where?", "getting response something else");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextView status = (TextView)findViewById(R.id.status);
                                            status.setText("Concentrating");
                                            ImageView imageView = (ImageView)findViewById(R.id.imageView4);
                                            imageView.setVisibility(View.GONE);
                                            ImageView imageView2 = (ImageView)findViewById(R.id.imageView2);
                                            imageView2.setVisibility(View.GONE);
                                            ImageView imageView3 = (ImageView)findViewById(R.id.imageView3);
                                            imageView3.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    delayTimeinMS[0] = 500;
                                }
                                Log.d("h0,", "got response " + response);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("h0,", "that didn't work");
                    }
                });

                queue.add(stringRequest);
                Log.d("h0,", "helllo2");
                m_handler.postDelayed(m_handlerTask, 500);
                // repeat some task every 1 second
            }
        };
        m_handlerTask.run();
    }
    public void stopService(View view) {
//        stopService(new Intent(getBaseContext(), MyService.class));
        stopService(new Intent(getBaseContext(), AudioService.class));
        String url = "http://192.168.1.92:8080/stop_session";
        final RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("h0,", "got response " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("h0,", "that didn't work");
            }
        });
        queue.add(stringRequest);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ImageView imageView = (ImageView)findViewById(R.id.imageView4);
        imageView.setVisibility(View.GONE);
        ImageView imageView2 = (ImageView)findViewById(R.id.imageView3);
        imageView2.setVisibility(View.GONE);
        ImageView imageView3 = (ImageView)findViewById(R.id.imageView2);
        imageView3.setVisibility(View.VISIBLE);


//        stopService(new Intent(getBaseContext(), BackgroundVideoRecorder.class));

    }
    public void claibrate(final View view){
//        EditText editText = (EditText)findViewById(R.id.ipAndPort);
        final Button callibrateButton = (Button)findViewById(R.id.calibrate);
        callibrateButton.setEnabled(false);
        callibrateButton.setVisibility(View.GONE);
        TextView relax = (TextView)findViewById(R.id.relax);
        relax.setText("Relax your mind and body!");
        final TextView concentrate = (TextView)findViewById(R.id.concentrate);
        concentrate.setText("Calibrating....");
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ImageView imageView = (ImageView)findViewById(R.id.imageView2);
        imageView.setVisibility(View.GONE);
        ImageView imageView2 = (ImageView)findViewById(R.id.imageView3);
        imageView2.setVisibility(View.GONE);
        ImageView imageView3 = (ImageView)findViewById(R.id.imageView4);
        imageView3.setVisibility(View.VISIBLE);

        Thread t = new Thread(){
            @Override
            public void run(){
                try {
                    String url= "http://192.168.1.92:8080/calibration_start";
                    final RequestQueue queue;
                    queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("h0,", "got response " + response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("h0,", "that didn't work");
                        }
                    });
                    queue.add(stringRequest);
                    for(int i=0;i<60;i++){
                        callibrateFunction();
                        sleep(1000);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView relax = (TextView)findViewById(R.id.relax);
                            relax.setText("");
                            TextView concentrate = (TextView)findViewById(R.id.concentrate);
                            concentrate.setText("Working....");
                            Button callibrateButton = (Button)findViewById(R.id.calibrate);
                            callibrateButton.setEnabled(true);
                            callibrateButton.setVisibility(View.VISIBLE);
                            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.VISIBLE);
                            ImageView imageView = (ImageView)findViewById(R.id.imageView4);
                            imageView.setVisibility(View.GONE);
                            ImageView imageView2 = (ImageView)findViewById(R.id.imageView3);
                            imageView2.setVisibility(View.GONE);
                            ImageView imageView3 = (ImageView)findViewById(R.id.imageView2);
                            imageView3.setVisibility(View.VISIBLE);

                        }
                    });
//                } catch (InterruptedException ex) {
//                    Log.i("error","thread");
//                }
//            }
//        };
//        t.start();
        final int[] readyCallibration = {0};
//
//        t = new Thread(){
//            @Override
//            public void run(){
//                try {
                    while(readyCallibration[0]==0){
                        url = "http://192.168.1.92:8080/ready";
                        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        readyCallibration[0] = parseInt(response);
                                        Log.d("h1,", "got response " + response);
                                        TextView relax = (TextView)findViewById(R.id.relax);
                                        relax.setText("Successfully Calibrated!");
                                        TextView concentrate = (TextView)findViewById(R.id.concentrate);
                                        concentrate.setText("");
                                        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("h0,", "that didn't work");
                            }
                        });
                        queue.add(stringRequest2);
                        sleep(1000);
                    }
                    if(readyCallibration[0]==1){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            startService(view);
                            readyCallibration[0]=0;
                        }
                        });
                    }
                } catch (InterruptedException ex) {
                    Log.i("error","thread");
                }
            }
        };
        t.start();

    }

    public void callibrateFunction(){
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(count*100/30);
        Log.v("progress bar ", String.valueOf(count*100/30));
        Log.v("progress bar ", String.valueOf(progressBar.getProgress()));
        count +=1;
        if(count == 30) {
            Log.v("1", "a");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView relax = (TextView)findViewById(R.id.relax);
                    relax.setText("Try to concentrate on something specific!");
                    TextView concentrate = (TextView)findViewById(R.id.concentrate);
                    concentrate.setText("Calibrating....");
                    ImageView imageView = (ImageView)findViewById(R.id.imageView4);
                    imageView.setVisibility(View.GONE);
                    ImageView imageView2 = (ImageView)findViewById(R.id.imageView2);
                    imageView2.setVisibility(View.GONE);
                    ImageView imageView3 = (ImageView)findViewById(R.id.imageView3);
                    imageView3.setVisibility(View.VISIBLE);


                }
            });
            Log.v("1", "b");
            count = 1;
        }
    }

}
