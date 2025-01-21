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

public class Game7Activity extends AppCompatActivity {
    private String TAG = "Game7Activity";
    private TextView txt_game7_timeCunt;
    private ImageView img_game7_issue,img_game7_ansA,img_game7_ansB,img_game7_ansC,img_game7_ansD,img_game7_next;
    private CardView cv_game7_ansA,cv_game7_ansB,cv_game7_ansC,cv_game7_ansD;
    private boolean[] flag_ans = new boolean[4];
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
        setContentView(R.layout.activity_game7);
        initView();
    }

    private void initView(){
        for (int i=0; i<4; i++){
            flag_ans[i] = false;
        }
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
        issueNumName = 7;

        aHandler = new Handler();
        mediaPlayer = MediaPlayer.create(Game7Activity.this, R.raw.back3);
        initSoundPool();

        txt_game7_timeCunt = (TextView) findViewById(R.id.txt_game7_timeCunt);
        img_game7_issue = (ImageView) findViewById(R.id.img_game7_issue);
        img_game7_ansA = (ImageView) findViewById(R.id.img_game7_ansA);
        img_game7_ansB = (ImageView) findViewById(R.id.img_game7_ansB);
        img_game7_ansC = (ImageView) findViewById(R.id.img_game7_ansC);
        img_game7_ansD = (ImageView) findViewById(R.id.img_game7_ansD);
        img_game7_next = (ImageView) findViewById(R.id.img_game7_next);

        cv_game7_ansA = (CardView) findViewById(R.id.cv_game7_ansA);
        cv_game7_ansB = (CardView) findViewById(R.id.cv_game7_ansB);
        cv_game7_ansC = (CardView) findViewById(R.id.cv_game7_ansC);
        cv_game7_ansD = (CardView) findViewById(R.id.cv_game7_ansD);
        cv_game7_ansA.setOnClickListener(onClick);
        cv_game7_ansB.setOnClickListener(onClick);
        cv_game7_ansC.setOnClickListener(onClick);
        cv_game7_ansD.setOnClickListener(onClick);
        img_game7_next.setOnClickListener(onClick);

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
        public void onClick(View v) {
            int item;
            ColorStateList colorStateList;
            playSound(1, 0);
            if(v.getId() == R.id.cv_game7_ansA){
                Log.d(TAG, "onClick: cv_game7_ansA");
                item = 0;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game7_ansA.setCardBackgroundColor(colorStateList);
                    img_game7_ansA.setVisibility(View.GONE);
                    flag_ans[item] = false;
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game7_ansA.setCardBackgroundColor(colorStateList);
                    img_game7_ansA.setVisibility(View.VISIBLE);
                    flag_ans[item] = true;
                    img_game7_issue.setImageResource(R.drawable.ans7_a);
                }
            }else if(v.getId() == R.id.cv_game7_ansB){
                Log.d(TAG, "onClick: cv_game7_ansB");
                item = 1;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game7_ansB.setCardBackgroundColor(colorStateList);
                    img_game7_ansB.setVisibility(View.GONE);
                    flag_ans[item] = false;
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game7_ansB.setCardBackgroundColor(colorStateList);
                    img_game7_ansB.setVisibility(View.VISIBLE);
                    flag_ans[item] = true;
                    img_game7_issue.setImageResource(R.drawable.ans7_b);
                }
            }else if(v.getId() == R.id.cv_game7_ansC){
                Log.d(TAG, "onClick: cv_game7_ansC");
                item = 2;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game7_ansC.setCardBackgroundColor(colorStateList);
                    img_game7_ansC.setVisibility(View.GONE);
                    flag_ans[item] = false;
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game7_ansC.setCardBackgroundColor(colorStateList);
                    img_game7_ansC.setVisibility(View.VISIBLE);
                    flag_ans[item] = true;
                    img_game7_issue.setImageResource(R.drawable.ans7_c);
                }
            }else if(v.getId() == R.id.cv_game7_ansD){
                Log.d(TAG, "onClick: cv_game7_ansD");
                item = 3;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game7_ansD.setCardBackgroundColor(colorStateList);
                    img_game7_ansD.setVisibility(View.GONE);
                    flag_ans[item] = false;
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game7_ansD.setCardBackgroundColor(colorStateList);
                    img_game7_ansD.setVisibility(View.VISIBLE);
                    flag_ans[item] = true;
                    img_game7_issue.setImageResource(R.drawable.ans7_d);
                }
            }else if(v.getId() == R.id.img_game7_next){
                Log.d(TAG, "onClick: img_game7_next");
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

    private void showPassAlertDialog(){
        playSound(3, 0);//Pass
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game7Activity.this);
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
                editor.putInt("playRound", 7);
                editor.apply();
                dialog.cancel();
                finish();
            }
        });
    }

    private void showFailAlertDialog(){
        playSound(2, 0);//Fail
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game7Activity.this);
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

    final Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            if (count > 0) {
                txt_game7_timeCunt.setText(Integer.toString(count));
                count--;
                aHandler.postDelayed(runnable, 1000);
            }else{
                Log.d(TAG, "run: time up");
                txt_game7_timeCunt.setText("0");
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

    private void resetAll(){
        count = 20;
        if (aHandler != null) {
            aHandler.removeCallbacks(runnable);
        }
        aHandler.post(runnable);
        for (int i=0; i<4; i++){
            flag_ans[i] = false;
        }
        img_game7_issue.setImageResource(R.drawable.ans7_ori_people);
        img_game7_ansA.setVisibility(View.GONE);
        img_game7_ansB.setVisibility(View.GONE);
        img_game7_ansC.setVisibility(View.GONE);
        img_game7_ansD.setVisibility(View.GONE);
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
        cv_game7_ansA.setCardBackgroundColor(colorStateList);
        cv_game7_ansB.setCardBackgroundColor(colorStateList);
        cv_game7_ansC.setCardBackgroundColor(colorStateList);
        cv_game7_ansD.setCardBackgroundColor(colorStateList);

        spendTime = 20;
        issueNumName = 8;
        playTime = readPlayTime();
        Log.d(TAG, "initView: playTime = "+playTime);
    }

    private boolean checkIssue(){
        if (flag_ans[0] && flag_ans[1] && flag_ans[2] && !flag_ans[3]){
            return true;
        }
        return false;
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