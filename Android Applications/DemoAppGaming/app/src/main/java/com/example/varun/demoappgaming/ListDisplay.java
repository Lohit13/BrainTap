//package com.example.varun.demoappclassroom;
package com.example.varun.demoappgaming;

/**
 * Created by Varun on 23-11-2017.
 */

import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListDisplay extends AppCompatActivity {
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
    private MediaPlayer mp = new MediaPlayer();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_display);

        // Find the ListView resource.
        mainListView = (ListView) findViewById(R.id.mainListView);

        ArrayList<String> recordings = new ArrayList<String>();

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, recordings);
        String path = Environment.getExternalStorageDirectory().toString() + "/Voice Recorder2";
        File directory = new File(path);
        File[] files = directory.listFiles();
        int i = 0;
        if(files.length >0) {
            for (i = 0; i < files.length; i++) {
                listAdapter.add(Integer.toString(i + 1) + ") " + files[i].getName());
            }
        }
//        listAdapter.add("Missed Conversation 1");
//        listAdapter.add("Missed Conversation 2");
//        listAdapter.add("Missed Conversation 3");

        mainListView.setAdapter(listAdapter);

        ListView lv = (ListView) findViewById(R.id.mainListView);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                String fileName = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Voice Recorder2/"+(String)arg0.getItemAtPosition(position);
                Log.v("my_file_name",  fileName);
                if(mp.isPlaying()){
                    mp.stop();
                    mp.reset();
//                    mp=null;
                }
                mp.reset();
                if(mp== null){
                    mp = new MediaPlayer();
                }
                try {
                    mp.setDataSource(fileName);//Write your location here
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
