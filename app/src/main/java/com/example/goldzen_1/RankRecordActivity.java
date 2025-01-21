package com.example.goldzen_1;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RankRecordActivity extends AppCompatActivity {
    private String TAG = "RankRecordActivity";
    private TextView txt_rankRecord_name;
    private String name;
    private ListView lv_rankRecord_list;
    private RankDetailListAdapter adapter;
    private List<Rank> list;
    private static final String DataBaseName = "RankDataBaseIt_2.db";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "Rank";
    private static SQLiteDatabase db;
    private RankSqlDataBaseHelper rankSqlDataBaseHelper;
    private String selectPlayRound;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_record);
        initView();
    }

    private void initView(){

        selectPlayRound = getIntent().getStringExtra("playRound");
        name = getIntent().getStringExtra("name");

        rankSqlDataBaseHelper = new RankSqlDataBaseHelper(getApplicationContext(),DataBaseName,
                null,DataBaseVersion,DataBaseTable);
        db = rankSqlDataBaseHelper.getWritableDatabase();
        list = new ArrayList<>();
        readRoundData(Integer.valueOf(selectPlayRound));

        txt_rankRecord_name = (TextView) findViewById(R.id.txt_rankRecord_name);
        txt_rankRecord_name.setText("玩家 :"+name);

        lv_rankRecord_list = (ListView) findViewById(R.id.lv_rankRecord_list);


        /*for (int i=0 ; i<10;i++){
            Rank rank = new Rank("Jack","1","10","103",5,"PASS");
            list.add(rank);
        }*/
        adapter = new RankDetailListAdapter(getApplicationContext(),list);
        lv_rankRecord_list.setAdapter(adapter);
        mediaPlayer = MediaPlayer.create(RankRecordActivity.this, R.raw.back1);
    }

    private void readRoundData(int _playRound){
        Log.d(TAG, "readEveryPlayRound: ");
        //getWritableDatabase().query(TABLE_NAME_PERSON,null,VALUE_ID+"=?",new String[]{"1"},null,null,null);
        String player,pass_fail_status;
        int playRound,round,playTime,second;
        int totalSecond = 0;
        player = "";
        playRound = 0;
        round = 0;
        String VALUE_playRound = "playRound";
        String nowRound = ""+_playRound;

        Cursor c = db.query(DataBaseTable,null,VALUE_playRound+"=?",new String[]{nowRound},null,null,null);
        Log.d(TAG, "onClick: c.getCount() = "+c.getCount());
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            Log.d(TAG, "readEveryPlayRound: --------------------");
            playRound = c.getInt(1);
            player = c.getString(2);
            round = c.getInt(3);
            playTime = c.getInt(4);
            second = c.getInt(5);
            pass_fail_status = c.getString(6);
            Log.d(TAG, "onClick: playRound = "+playRound);
            Log.d(TAG, "onClick: player = "+player);
            Log.d(TAG, "onClick: round = "+round);
            Log.d(TAG, "onClick: playTime = "+playTime);
            Log.d(TAG, "onClick: second = "+second);
            Log.d(TAG, "onClick: pass_fail_status = "+pass_fail_status);
            c.moveToNext();
            Log.d(TAG, "onClick: readEveryPlayRound -------------");
            Rank rank = new Rank(""+playRound,player,""+round,""+second,
                    "",playTime,pass_fail_status);
            list.add(rank);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }


}