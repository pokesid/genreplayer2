package com.example.saidu.genreplayer;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saidu.genreplayer.adapter.SimpleRecyclerAdapter;

import java.util.ArrayList;

import static com.example.saidu.genreplayer.playbgmusic.mDatabaseHelper;

public class MainActivity extends AppCompatActivity {
 //   DatabaseHelper mDatabaseHelper;
    public static final int MY_PERMISSION_REQUEST=1;
    static SharedPreferences mPrefs;
    static SharedPreferences.Editor editor;
    String yorn;
    public static TextView textsongname;
   public static ArrayList<String> arrayList,arrayList2;
    ListView listView;
    ArrayAdapter<String> adapter;
RelativeLayout llay;
public  static Button btnporp;
    RecyclerView recyclerView;
    static Context ctxx;
    EditText searchbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    llay=(RelativeLayout)findViewById(R.id.llay);
        btnporp=(Button)findViewById(R.id.btnporp);
        mDatabaseHelper=new DatabaseHelper(this);
        mPrefs = getSharedPreferences("IDvalue", 0);
        textsongname=(TextView)findViewById(R.id.textsongname);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        ctxx=MainActivity.this;
        searchbox = (EditText) findViewById(R.id.searchbox);
        searchbox.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                runAnimation(recyclerView,0);
            }
        });

    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
        }
    }
    else
    {

        dostuff();
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        runAnimation(recyclerView,0);


    }


        checkplayornot();


    }

    public void checkplayornot()
    {
        boolean is_running= isMyServiceRunning(playbgmusic.class);
        if(is_running==true) {
            playbgmusic.getsongnamesinger2();
            if (playbgmusic.player.isPlaying()) {
                btnporp.setBackgroundResource(R.drawable.play);

            } else {
                btnporp.setBackgroundResource(R.drawable.pause);
            }
        }
        else
        {
            btnporp.setBackgroundResource(R.drawable.pause);
        }
    }


    public void getmusic()
    {
        ContentResolver contentResolver=getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor=contentResolver.query(songUri,null,null,null,null);

        if(songCursor !=null && songCursor.moveToFirst())
        {
            int songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songPath2=songCursor.getColumnIndex(MediaStore.MediaColumns.DATA);
          //  Toast.makeText(getApplicationContext(),songpath2,Toast.LENGTH_SHORT).show();

            do{
try {
    String currentTitle = songCursor.getString(songTitle);
    String currentArtist = songCursor.getString(songArtist);
    //String Path=songCursor.getString(songPath);
    String Path2 = songCursor.getString(songPath2);
   /* boolean insertdata = mDatabaseHelper.adddata(currentTitle, Path2, currentArtist);
    if (insertdata) {
        Toast.makeText(getApplicationContext(), "vetrii", Toast.LENGTH_SHORT).show();
        Log.d("HG", "success");
    } else {
        Log.d("HG", "fail");
    }*/
    arrayList.add(currentTitle);
    arrayList2.add(currentTitle + "---" + currentArtist + "---" + Path2);
}
catch (Exception e)
    {

    }
            }while(songCursor.moveToNext());


        }

    }
    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        if(type==0)
            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);

       // Toast.makeText(getApplicationContext(),"gg",Toast.LENGTH_SHORT).show();
        ArrayList<String> temp_arraylist=new ArrayList<>();

        if(searchbox.getText().toString().length()>0)
        {

           String search= searchbox.getText().toString();

           for(String each_value:arrayList2)
           {

               String each_part[]=each_value.split("---");
               if(each_part[0].toLowerCase().contains(search.toLowerCase()))
               {
                   Log.d(search,each_part[0]);
                   temp_arraylist.add(each_value);
               }

           }




        }
        else
        {
            temp_arraylist=arrayList2;
        }

        SimpleRecyclerAdapter simpleRecyclerAdapter = new SimpleRecyclerAdapter(temp_arraylist,MainActivity.this);
        recyclerView.setAdapter(simpleRecyclerAdapter);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }


    public void dostuff()
    {
        listView=(ListView)findViewById(R.id.listView);
        arrayList=new ArrayList<>();
        arrayList2=new ArrayList<>();
        getmusic();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, musicplayer
                        .class);

                Object item = adapterView.getItemAtPosition(i);
                String myitem = item.toString();
                Cursor data2=mDatabaseHelper.getyorn(myitem);
                while(data2.moveToNext())
                {
                    yorn=data2.getString(0);
                }


               // Toast.makeText(getApplicationContext(),yorn,Toast.LENGTH_SHORT).show();

                for(String data:arrayList2){
                   if(data.contains(myitem))
                   {
                       String s1[]=data.split("---");
                       String s3=s1[2];


                      // Toast.makeText(getApplicationContext(),s3,Toast.LENGTH_SHORT).show();
                       editor = mPrefs.edit();

                       editor.putString("path", s3);
                       editor.commit();
                   //    Toast.makeText(getApplicationContext(),s1[2],Toast.LENGTH_SHORT).show();
                       intent.putExtra("path",s3);
                       boolean abc= isMyServiceRunning(playbgmusic.class);
                       if(abc==true)
                       { Intent intent2 = new Intent(MainActivity.this, playbgmusic.class);
                          // Toast.makeText(getApplicationContext(),"running",Toast.LENGTH_SHORT).show();
                           playbgmusic.player.stop();
                           playbgmusic.threadinga.interrupt();
                          stopService(intent2);
                         musicplayer.getInstance().finish();


                       }
                       else
                       {
                        //   Toast.makeText(getApplicationContext(),"not running",Toast.LENGTH_SHORT).show();
                       }

                      startActivity(intent);
                   }
                }


            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST:{

                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this,"Permission Granted!",Toast.LENGTH_SHORT).show();
                            dostuff();
                        }
                } else {
                    Toast.makeText(this,"No Permission Granted!",Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;

            }

        }
    }



    private static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctxx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void gotoplayer(View view) {


        if(isTaskRoot())
        {

        }
        else {
            onBackPressed();
            boolean abc = isMyServiceRunning(playbgmusic.class);
            if (abc == true) {
                if (playbgmusic.player.isPlaying()) {


                } else {
                    musicplayer.songstate = "paused";
                    musicplayer.porp.setBackgroundResource(R.drawable.pause);
                }
            } else {

            }
        }
    }

    public void playorpausesong(View view) {
        try {
            boolean abc = isMyServiceRunning(playbgmusic.class);
            if (abc == true) {
                if (playbgmusic.player.isPlaying()) {

                    musicplayer.songstate = "paused";
                    musicplayer.porp.setBackgroundResource(R.drawable.pause);
                    playbgmusic.notificationplay(MainActivity.this);


                    playbgmusic.player.pause();
                    btnporp.setBackgroundResource(R.drawable.pause);


                } else {
                    playbgmusic.player.start();

                    musicplayer.songstate = "playing";
                    musicplayer.porp.setBackgroundResource(R.drawable.play);
                    playbgmusic.notificationpause(MainActivity.this);

                    btnporp.setBackgroundResource(R.drawable.play);
                }
            } else {

            }
        } catch (Exception e) {

        }

    }

    public void playnextsong2(View view) {
        boolean abc= isMyServiceRunning(playbgmusic.class);
        if(abc==true) {

            playbgmusic.player.stop();
            musicplayer.songstate="playing";
            musicplayer.porp.setBackgroundResource(R.drawable.play);
            playbgmusic.playnextsong();
        }




    }

    public void playprevioussong2(View view) {

      /*  AlertDialog.Builder mBuilder=new AlertDialog.Builder(MainActivity.this);

        View mView= getLayoutInflater().inflate(R.layout.prioritydialog,null);
        mBuilder.setView(mView);
        final AlertDialog dialog=mBuilder.create();

        dialog.show();*/

        boolean abc= isMyServiceRunning(playbgmusic.class);
        if(abc==true) {

            playbgmusic.player.stop();
            musicplayer.songstate="playing";
            musicplayer.porp.setBackgroundResource(R.drawable.play);
            playbgmusic.playprevioussong();
        }


    }

    public static void gotosong(String songnamedaw)
    {


        Intent intent = new Intent(ctxx, musicplayer.class);


        String myitem = songnamedaw;
        Cursor data2=mDatabaseHelper.getyorn(myitem);


        for(String data:arrayList2){
            if(data.contains(myitem))
            {
                String s1[]=data.split("---");
                String s3=s1[2];


                // Toast.makeText(getApplicationContext(),s3,Toast.LENGTH_SHORT).show();
                editor = mPrefs.edit();

                editor.putString("path", s3);
                editor.commit();
                //    Toast.makeText(getApplicationContext(),s1[2],Toast.LENGTH_SHORT).show();
                intent.putExtra("path",s3);
                boolean abc= isMyServiceRunning(playbgmusic.class);
                if(abc==true)
                { Intent intent2 = new Intent(ctxx, playbgmusic.class);
                    // Toast.makeText(getApplicationContext(),"running",Toast.LENGTH_SHORT).show();
                    playbgmusic.player.stop();
                    playbgmusic.threadinga.interrupt();
                    ctxx.stopService(intent2);
                    musicplayer.getInstance().finish();


                }
                else
                {
                    //   Toast.makeText(getApplicationContext(),"not running",Toast.LENGTH_SHORT).show();
                }

                ctxx.startActivity(intent);

                    ((Activity) ctxx).overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);


            }
        }







    }


    public void showsearch(View view) {

        searchbox.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchbox, InputMethodManager.SHOW_IMPLICIT);
    }
}
