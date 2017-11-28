//package com.example.varun.demoappclassroom;
package com.example.varun.demoappgaming;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static java.lang.Integer.parseInt;

/**
 * Created by Varun on 23-10-2017.
 */

public class AudioService extends Service

    implements MediaRecorder.OnInfoListener {

    private MediaRecorder mRecorder;
    //setting maximum file size to be recorded
    private long Audio_MAX_FILE_SIZE = 1000000;//1Mb

    private int[] amplitudes = new int[100];
    private int i = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler m_handler;
    Runnable m_handlerTask ;
    private RequestQueue queue;
    private boolean recording = false;

    private File mOutputFile;

    String url ="http://192.168.1.92:8080/final";
    private Toast mToastToShow;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags,
                              int startId) {
        super.onStartCommand(intent, flags, startId);
//        final Toast mktoast = Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG);
//        mktoast.show();

        String editTextValue = intent.getStringExtra("ipAndPort");
        url = "http://192.168.1.92:8080/final";
//        Toast.makeText(getApplicationContext(),editTextValue , Toast.LENGTH_LONG).show();

        final Toast mktoast = Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG);
//        mktoast.show();

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
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v("reponse", "a" + response + "a");
                                if(parseInt(response) == 0 ) {
                                    Log.v("Where?", "getting response 1");
                                    Log.v("Where?", String.valueOf(recording));

                                    try {
                                        if(recording == false) {
                                            startRecording();
                                            recording = true;
                                            delayTimeinMS[0] = 1000;
                                            mktoast.setText("Recording started");
                                            mktoast.show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    Log.v("Where?", "getting response something else");
                                    try {
                                        if(recording == true) {
                                            stopRecording(true);
                                            recording = false;
                                            mktoast.setText("Recording Stopped");
                                            mktoast.show();
                                        }
                                    }
                                    catch (Exception e){e.printStackTrace();}
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
        return Service.START_STICKY;
    }



    private void startRecording() throws IOException {
        mRecorder = new MediaRecorder();
        mRecorder.setOnInfoListener(this);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setMaxFileSize(Audio_MAX_FILE_SIZE);
        mRecorder.setOutputFormat
                (MediaRecorder.OutputFormat.MPEG_4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            mRecorder.setAudioEncodingBitRate(48000);
        } else {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(64000);
        }
        mRecorder.setAudioSamplingRate(16000);
        mOutputFile = getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());

        mRecorder.prepare();
        mRecorder.start();
        long mStartTime = SystemClock.elapsedRealtime();
    }

    protected void stopRecording(boolean saveFile) {
        mRecorder.stop();
        final Toast mktoast = Toast.makeText(getApplicationContext(), "New Recording Saved", Toast.LENGTH_LONG);
        mktoast.show();
        getOutputFile();

        stopSelf();

    }

    private File getOutputFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat
                ("yyyyMMdd_HHmmssSSS", Locale.ENGLISH);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Voice Recorder2/Missed_Lecture_"+ dateFormat.format(new Date())+ ".m4a");
//        return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Voice Recorder/RECORDING_"+ dateFormat.format(new Date())+ ".m4a");
//        return new File("/Voice Recorder/RECORDING_"+ dateFormat.format(new Date())+ ".m4a");

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        //check whether file size has reached to 1MB to stop recording
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            stopRecording(true);
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        m_handler.removeCallbacks(m_handlerTask);
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
//        try {
//            if(recording == true) {
//                stopRecording(true);
//                recording = false;
//            }
//        }
//        catch (Exception e){e.printStackTrace();};
//    }
}
