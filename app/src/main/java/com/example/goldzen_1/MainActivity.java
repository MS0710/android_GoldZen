package com.example.goldzen_1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private ImageButton btn_start,btn_rank;

    private static final String DataBaseName = "RankDataBaseIt_2.db";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "Rank";
    private static SQLiteDatabase db;
    private RankSqlDataBaseHelper rankSqlDataBaseHelper;
    private SharedPreferences getPrefs;
    private SharedPreferences.Editor editor;
    private String name;
    private RankListAdapter adapter;
    private List<Rank> list;
    private String[][] playInfo = new String[100][5];
    private int totalCunt;
    private MediaPlayer mediaPlayer;
    private SoundPool sp;//声明SoundPool的引用
    private HashMap<Integer, Integer> hm;//声明HashMap来存放声音文件
    private int currStaeamId;//当前正播放的streamId
    private int nowMaxPlayRound;
    private String[][] playRound_name_Time = new String[100][3];
    private int BubbleCunt;
    ///////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        totalCunt = 0;
        name = "";
        nowMaxPlayRound = 0;
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        getPrefs.getString("name", "");
        getPrefs.getInt("playRound", 0);

        rankSqlDataBaseHelper = new RankSqlDataBaseHelper(getApplicationContext(),DataBaseName,
                null,DataBaseVersion,DataBaseTable);
        db = rankSqlDataBaseHelper.getWritableDatabase();

        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.back1);
        initSoundPool();

        list = new ArrayList<>();

        btn_rank = (ImageButton) findViewById(R.id.btn_rank);
        btn_start = (ImageButton) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(onClick);
        btn_rank.setOnClickListener(onClick);
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

    private void playSound(int sound, int loop) {//获取AudioManager引用
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //获取当前音量
        float streamVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //获取系统最大音量
        float streamVolumeMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //计算得到播放音量
        float volume = streamVolumeCurrent / streamVolumeMax;
        //调用SoundPool的play方法来播放声音文件
        currStaeamId = sp.play(hm.get(sound), volume, volume, 1, loop, 1.0f);
    }

    private void initSoundPool() {//初始化声音池
        sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);//创建SoundPool对象
        hm = new HashMap<Integer, Integer>();//创建HashMap对象
        //加载声音文件，并且设置为1号声音放入hm中
        hm.put(1, sp.load(this, R.raw.click, 1));
        hm.put(2, sp.load(this, R.raw.error, 1));
        hm.put(3, sp.load(this, R.raw.correct, 1));
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_start){
                Log.d(TAG, "onClick: btn_start");
                playSound(1, 0);
                shoeNameAlertDialog();
            }else if (view.getId() == R.id.btn_rank){
                Log.d(TAG, "onClick: btn_rank");
                playSound(1, 0);
                //readRank();
                //showRankAlertDialog();
                Log.d(TAG, "onClick: PlayRound = "+readPlayRound());
                nowMaxPlayRound = readPlayRound();
                BubbleCunt = 0;
                for (int i=1 ; i<nowMaxPlayRound;i++){
                    readEveryPlayRound(i);
                }
                BubbleSort();
                showRank();
                showRankAlertDialog();
            }
        }
    };

    private void shoeNameAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        View v = getLayoutInflater().inflate(R.layout.name_dialog_layout,null);
        alertDialog.setView(v);

        EditText edit_nameDialog_name = v.findViewById(R.id.edit_nameDialog_name);
        CardView cv_nameDialog_ok = v.findViewById(R.id.cv_nameDialog_ok);
        AlertDialog dialog = alertDialog.create();
        //dialog.setCanceledOnTouchOutside(false);
        final Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        dialog.show();
        cv_nameDialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btn_nameDialog_ok");
                if (edit_nameDialog_name.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"請輸入名稱",Toast.LENGTH_SHORT).show();
                }else {
                    name = edit_nameDialog_name.getText().toString();
                    Intent intent = new Intent(MainActivity.this,Game12789Activity.class);
                    editor = getPrefs.edit();
                    editor.putString("name", name);
                    //editor.putInt("playRound", readPlayRound());
                    editor.putInt("playRound", 0);
                    editor.putInt("userPlayTime", readPlayRound());
                    editor.apply();
                    startActivity(intent);
                    dialog.cancel();
                }
            }
        });
    }

    private void showRankAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        View v = getLayoutInflater().inflate(R.layout.rank_dialog_layout,null);
        alertDialog.setView(v);
        ListView lv_rankDialog_list = v.findViewById(R.id.lv_rankDialog_list);

        /*for (int i=0 ; i<10;i++){
            Rank rank = new Rank("Jack","1","10","103",5);
            list.add(rank);
        }*/
        adapter = new RankListAdapter(getApplicationContext(),list);
        lv_rankDialog_list.setAdapter(adapter);

        AlertDialog dialog = alertDialog.create();
        //dialog.setCanceledOnTouchOutside(false);
        final Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        dialog.show();
        lv_rankDialog_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RankRecordActivity.class);
                intent.putExtra("name",list.get(position).getName());
                intent.putExtra("playRound",list.get(position).getPlayRound());
                startActivity(intent);
                dialog.cancel();
                return false;
            }
        });
    }

    private int readPlayRound(){

        String player,pass_fail_status;
        int playRound,round,playTime,second;
        int tempRound,maxRound;
        tempRound = 0;
        maxRound = 0;
        Cursor c = db.rawQuery("SELECT * FROM " + DataBaseTable,null);
        Log.d(TAG, "onClick: c.getCount() = "+c.getCount());
        c.moveToFirst();
        totalCunt = c.getCount();
        if(c.getCount()>0){
            for(int i=0;i<c.getCount();i++){
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
                Log.d(TAG, "onClick: readRank -------------");
                tempRound = playRound;
                if (maxRound < tempRound){
                    maxRound = tempRound;
                }
            }
            return maxRound+1;
        }else {
            return 1;
        }
    }

    private void readEveryPlayRound(int _playRound){
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
        totalCunt = c.getCount();
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
            totalSecond = totalSecond + second;
            //Log.d(TAG, "readEveryPlayRound: totalSecond = "+totalSecond);
        }
        if (round == 10){
            Log.d(TAG, "readEveryPlayRound:round == 10 ; _playRound ="+_playRound);
            playRound_name_Time[BubbleCunt][0] = String.valueOf(playRound);
            playRound_name_Time[BubbleCunt][1] = player;
            playRound_name_Time[BubbleCunt][2] = String.valueOf(totalSecond);
            BubbleCunt++;
            Log.d(TAG, "readEveryPlayRound: playRound_name_Time[_playRound][0] = "+playRound_name_Time[_playRound][0]);
            Log.d(TAG, "readEveryPlayRound: playRound_name_Time[_playRound][1] = "+playRound_name_Time[_playRound][1]);
            Log.d(TAG, "readEveryPlayRound: playRound_name_Time[_playRound][2] = "+playRound_name_Time[_playRound][2]);
        }
    }

    private void BubbleSort(){
        String temp_playRound;
        String temp_player;
        String temp_totalSecond;
        for(int j = 0; j < BubbleCunt -1; j++) {
            for(int i = 0; i < BubbleCunt - 1 - j; i++) {
                if(Integer.parseInt(playRound_name_Time[i][2]) > Integer.parseInt(playRound_name_Time[i+1][2])) {
                    temp_playRound = playRound_name_Time[i][0];
                    temp_player = playRound_name_Time[i][1];
                    temp_totalSecond = playRound_name_Time[i][2];

                    playRound_name_Time[i][0] = playRound_name_Time[i+1][0];
                    playRound_name_Time[i][1] = playRound_name_Time[i+1][1];
                    playRound_name_Time[i][2] = playRound_name_Time[i+1][2];

                    playRound_name_Time[i+1][0] = temp_playRound;
                    playRound_name_Time[i+1][1] = temp_player;
                    playRound_name_Time[i+1][2] = temp_totalSecond;
                }
            }
        }
    }

    private void showRank(){
        list.clear();
        for (int i=0; i<BubbleCunt; i++){
            Rank rank = new Rank(playRound_name_Time[i][0],playRound_name_Time[i][1],"","",playRound_name_Time[i][2],0,"");
            list.add(rank);
        }
    }

}