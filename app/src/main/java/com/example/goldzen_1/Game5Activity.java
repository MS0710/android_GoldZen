package com.example.goldzen_1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import pl.droidsonroids.gif.GifDrawable;

public class Game5Activity extends AppCompatActivity {
    private String TAG = "Game5Activity";
    private ImageView img_game5_issue,img_game5_next;
    private CardView cv_game5_ansA,cv_game5_ansB,cv_game5_ansC;
    private boolean flag_ansA,flag_ansB,flag_ansC;
    private ImageView img_game5_ansA,img_game5_ansB,img_game5_ansC;
    private TextView txt_game5_timeCunt;
    int count = 20;
    private Handler aHandler;
    private MediaPlayer mediaPlayer;
    private SoundPool sp;//声明SoundPool的引用
    private HashMap<Integer, Integer> hm;//声明HashMap来存放声音文件
    private int currStaeamId;//当前正播放的streamId
    private static final String DataBaseName = "RankDataBaseIt_2.db";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "Rank";
    private static SQLiteDatabase db;
    private RankSqlDataBaseHelper rankSqlDataBaseHelper;
    private String name;
    private int userPlayTime,playTime,spendTime,issueNumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game5);
        initView();
    }

    private void initView(){
        flag_ansA = false;
        flag_ansB = false;
        flag_ansC = false;
        aHandler = new Handler();

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        name = getPrefs.getString("name", "");
        userPlayTime = getPrefs.getInt("userPlayTime",0);

        rankSqlDataBaseHelper = new RankSqlDataBaseHelper(getApplicationContext(),DataBaseName,
                null,DataBaseVersion,DataBaseTable);
        db = rankSqlDataBaseHelper.getWritableDatabase();

        playTime = readPlayTime();
        Log.d(TAG, "initView: playTime = "+playTime);
        spendTime = 20;
        issueNumName = 5;

        mediaPlayer = MediaPlayer.create(Game5Activity.this, R.raw.back3);
        initSoundPool();

        img_game5_issue = (ImageView) findViewById(R.id.img_game5_issue);
        img_game5_next = (ImageView) findViewById(R.id.img_game5_next);
        txt_game5_timeCunt = (TextView)findViewById(R.id.txt_game5_timeCunt);

        cv_game5_ansA = (CardView) findViewById(R.id.cv_game5_ansA);
        cv_game5_ansB = (CardView) findViewById(R.id.cv_game5_ansB);
        cv_game5_ansC = (CardView) findViewById(R.id.cv_game5_ansC);

        img_game5_ansA = (ImageView) findViewById(R.id.img_game5_ansA);
        img_game5_ansB = (ImageView) findViewById(R.id.img_game5_ansB);
        img_game5_ansC = (ImageView) findViewById(R.id.img_game5_ansC);

        cv_game5_ansA.setOnClickListener(onClick);
        cv_game5_ansB.setOnClickListener(onClick);
        cv_game5_ansC.setOnClickListener(onClick);
        img_game5_next.setOnClickListener(onClick);

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
            ColorStateList colorStateList;
            playSound(1, 0);
            if (view.getId() == R.id.cv_game5_ansA){
                if (flag_ansA){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game5_ansA.setCardBackgroundColor(colorStateList);
                    img_game5_ansA.setVisibility(View.GONE);
                    flag_ansA = false;
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game5_ansA.setCardBackgroundColor(colorStateList);
                    img_game5_ansA.setVisibility(View.VISIBLE);
                    try {
                        GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.ans5_a_correct);
                        img_game5_issue.setImageDrawable(gifDrawable);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    flag_ansA = true;
                    flag_ansB = false;
                    flag_ansC = false;
                    selectOneAns();
                }
            }else if (view.getId() == R.id.cv_game5_ansB){
                if (flag_ansB){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game5_ansB.setCardBackgroundColor(colorStateList);
                    img_game5_ansB.setVisibility(View.GONE);
                    flag_ansB = false;
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game5_ansB.setCardBackgroundColor(colorStateList);
                    img_game5_ansB.setVisibility(View.VISIBLE);
                    try {
                        GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.ans5_b);
                        img_game5_issue.setImageDrawable(gifDrawable);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    flag_ansA = false;
                    flag_ansB = true;
                    flag_ansC = false;
                    selectOneAns();
                }
            }else if (view.getId() == R.id.cv_game5_ansC){
                if (flag_ansC){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game5_ansC.setCardBackgroundColor(colorStateList);
                    img_game5_ansC.setVisibility(View.GONE);
                    flag_ansC = false;
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game5_ansC.setCardBackgroundColor(colorStateList);
                    img_game5_ansC.setVisibility(View.VISIBLE);
                    try {
                        GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.ans5_c);
                        img_game5_issue.setImageDrawable(gifDrawable);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    flag_ansA = false;
                    flag_ansB = false;
                    flag_ansC = true;
                    selectOneAns();
                }
            }else if(view.getId() == R.id.img_game5_next){
                aHandler.removeCallbacks(runnable);
                if(checkIssue()){
                    if (aHandler != null) {
                        aHandler.removeCallbacks(runnable);
                    }
                    spendTime = 20 - count -1;
                    Log.d(TAG, "onClick: count = "+count);
                    Log.d(TAG, "onClick: spendTime = "+spendTime);
                    saveResult("Pass");
                    showPassAlertDialog();
                }else {
                    if (aHandler != null) {
                        aHandler.removeCallbacks(runnable);
                    }
                    spendTime = 20 - count -1;
                    Log.d(TAG, "onClick: count = "+count);
                    Log.d(TAG, "onClick: spendTime = "+spendTime);
                    saveResult("Fail");
                    showFailAlertDialog();
                }

            }
        }
    };

    private void selectOneAns(){
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
        if(flag_ansA){
            img_game5_ansA.setVisibility(View.VISIBLE);
            img_game5_ansB.setVisibility(View.GONE);
            img_game5_ansC.setVisibility(View.GONE);
            cv_game5_ansB.setCardBackgroundColor(colorStateList);
            cv_game5_ansC.setCardBackgroundColor(colorStateList);
        }else if(flag_ansB){
            img_game5_ansA.setVisibility(View.GONE);
            img_game5_ansB.setVisibility(View.VISIBLE);
            img_game5_ansC.setVisibility(View.GONE);
            cv_game5_ansA.setCardBackgroundColor(colorStateList);
            cv_game5_ansC.setCardBackgroundColor(colorStateList);
        } else if(flag_ansC){
            img_game5_ansA.setVisibility(View.GONE);
            img_game5_ansB.setVisibility(View.GONE);
            img_game5_ansC.setVisibility(View.VISIBLE);
            cv_game5_ansA.setCardBackgroundColor(colorStateList);
            cv_game5_ansB.setCardBackgroundColor(colorStateList);
        }
    }

    final Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            if (count > 0) {
                txt_game5_timeCunt.setText(Integer.toString(count));
                count--;
                aHandler.postDelayed(runnable, 1000);
            }else{
                txt_game5_timeCunt.setText("0");
                spendTime = 20;
                Log.d(TAG, "spendTime = "+spendTime);
                saveResult("Fail");
                showFailAlertDialog();
            }
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void showPassAlertDialog(){
        playSound(3, 0);//Pass
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game5Activity.this);
        View v = getLayoutInflater().inflate(R.layout.pass_dialog_layout,null);
        alertDialog.setView(v);
        ImageView imgPASS = v.findViewById(R.id.img_pass);
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        final Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        dialog.show();
        imgPASS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = getPrefs.edit();
                editor.putInt("playRound", 5);
                editor.apply();
                dialog.cancel();
                finish();
            }
        });
    }

    private void showFailAlertDialog(){
        playSound(2, 0);//Fail
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game5Activity.this);
        View v = getLayoutInflater().inflate(R.layout.fail_dialog_layout,null);
        alertDialog.setView(v);
        ImageView imgPASS = v.findViewById(R.id.img_fail);
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        final Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        dialog.show();
        imgPASS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAll();
                dialog.cancel();
            }
        });
    }

    private void resetAll(){
        flag_ansA = false;
        flag_ansB = false;
        flag_ansC = false;
        count = 20;
        if (aHandler != null) {
            aHandler.removeCallbacks(runnable);
        }
        aHandler.post(runnable);
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
        cv_game5_ansA.setCardBackgroundColor(colorStateList);
        cv_game5_ansB.setCardBackgroundColor(colorStateList);
        cv_game5_ansC.setCardBackgroundColor(colorStateList);

        img_game5_ansA.setVisibility(View.GONE);
        img_game5_ansB.setVisibility(View.GONE);
        img_game5_ansC.setVisibility(View.GONE);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.ans5_start);
            img_game5_issue.setImageDrawable(gifDrawable);
        }catch (Exception e){
            e.printStackTrace();
        }

        spendTime = 20;
        issueNumName = 5;
        playTime = readPlayTime();
        Log.d(TAG, "initView: playTime = "+playTime);
    }

    private boolean checkIssue(){
        if (flag_ansA && !flag_ansB  && !flag_ansC){
            return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
        resetAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (aHandler != null) {
            aHandler.removeCallbacks(runnable);
        }
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //resetAll();
    }

    private void saveResult(String result){
        long id;
        if(spendTime>20){
            spendTime = 20;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("playRound",userPlayTime);
        contentValues.put("player",name);
        contentValues.put("round",issueNumName);
        contentValues.put("playTime",playTime);
        contentValues.put("second",spendTime);
        contentValues.put("pass_fail_status",result);
        id = db.insert(DataBaseTable,null,contentValues);
        Log.d(TAG, "savePassResult: save OK");
    }

    private int readPlayTime(){
        //getWritableDatabase().query(TABLE_NAME_PERSON,null,VALUE_NAME+"=?"+" and "+VALUE_AGE+">?",new String[]{"张三","23"},null,null,null);
        String player,pass_fail_status;
        int playRound_1,round,playTime,second;
        int tempPlayTime,maxPlayTime;
        tempPlayTime = 0;
        maxPlayTime = 0;
        String VALUE_playRound = "playRound";
        String VALUE_round = "round";
        String searchPlayRound = ""+userPlayTime;
        String searchRound = ""+issueNumName;

        Cursor c = db.query(DataBaseTable,null,VALUE_playRound+"=?"+" and "+VALUE_round+"=?",
                new String[]{searchPlayRound,searchRound},null,null,null);

        Log.d(TAG, "onClick: c.getCount() = "+c.getCount());
        c.moveToFirst();
        if(c.getCount()>0){
            for(int i=0;i<c.getCount();i++){
                playRound_1 = c.getInt(1);
                player = c.getString(2);
                round = c.getInt(3);
                playTime = c.getInt(4);
                second = c.getInt(5);
                pass_fail_status = c.getString(6);
                Log.d(TAG, "onClick: playRound = "+playRound_1);
                Log.d(TAG, "onClick: player = "+player);
                Log.d(TAG, "onClick: round = "+round);
                Log.d(TAG, "onClick: playTime = "+playTime);
                Log.d(TAG, "onClick: second = "+second);
                Log.d(TAG, "onClick: pass_fail_status = "+pass_fail_status);
                c.moveToNext();
                Log.d(TAG, "onClick: readRank -------------");
                tempPlayTime = playTime;
                if (maxPlayTime < tempPlayTime){
                    maxPlayTime = tempPlayTime;
                }
            }
            return maxPlayTime+1;
        }else {
            return 1;
        }
    }
}