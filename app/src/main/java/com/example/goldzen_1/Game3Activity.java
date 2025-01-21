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

import java.util.HashMap;

public class Game3Activity extends AppCompatActivity {

    private String TAG = "Game3Activity";
    private TextView txt_game3_issue,txt_game3_timeCunt;
    private ImageView img_game3_issue,img_game3_next;
    private ImageView img_game3_ansA,img_game3_ansB,img_game3_ansC,img_game3_ansD;
    private CardView cv_game3_ansA,cv_game3_ansB,cv_game3_ansC,cv_game3_ansD;
    private ImageView img_game3_ansAItem,img_game3_ansBItem,img_game3_ansCItem,img_game3_ansDItem;
    private boolean flag_color_a,flag_color_b,flag_color_c,flag_color_d;
    private int[][] issueAndAnswer = new int[2][5];
    private int[][] AnswerImg = new int[2][4];
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
        setContentView(R.layout.activity_game3);
        initView();
    }

    private void initView(){
        count = 20;
        flag_color_a = false;
        flag_color_b = false;
        flag_color_c = false;
        flag_color_d = false;
        mediaPlayer = MediaPlayer.create(Game3Activity.this, R.raw.back3);
        initSoundPool();

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
        issueNumName = 3;

        loopIssue();
        aHandler = new Handler();
        txt_game3_issue = (TextView) findViewById(R.id.txt_game3_issue);
        txt_game3_timeCunt = (TextView) findViewById(R.id.txt_game3_timeCunt);
        img_game3_next = (ImageView) findViewById(R.id.img_game3_next);
        img_game3_issue = (ImageView) findViewById(R.id.img_game3_issue);
        img_game3_ansA = (ImageView) findViewById(R.id.img_game3_ansA);
        img_game3_ansB = (ImageView) findViewById(R.id.img_game3_ansB);
        img_game3_ansC = (ImageView) findViewById(R.id.img_game3_ansC);
        img_game3_ansD = (ImageView) findViewById(R.id.img_game3_ansD);
        cv_game3_ansA = (CardView) findViewById(R.id.cv_game3_ansA);
        cv_game3_ansB = (CardView) findViewById(R.id.cv_game34567_ansB);
        cv_game3_ansC = (CardView) findViewById(R.id.cv_game3_ansC);
        cv_game3_ansD = (CardView) findViewById(R.id.cv_game3_ansD);
        img_game3_ansAItem = (ImageView) findViewById(R.id.img_game3_ansAItem);
        img_game3_ansBItem = (ImageView) findViewById(R.id.img_game3_ansBItem);
        img_game3_ansCItem = (ImageView) findViewById(R.id.img_game3_ansCItem);
        img_game3_ansDItem = (ImageView) findViewById(R.id.img_game3_ansDItem);
        cv_game3_ansA.setOnClickListener(onClick);
        cv_game3_ansB.setOnClickListener(onClick);
        cv_game3_ansC.setOnClickListener(onClick);
        cv_game3_ansD.setOnClickListener(onClick);
        img_game3_next.setOnClickListener(onClick);
        selectIssue();
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
        if (aHandler != null) {
            aHandler.removeCallbacks(runnable);
        }
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
            playSound(1, 0);
            if (view.getId() == R.id.cv_game3_ansA){
                Log.d(TAG, "onClick: cv_game34567_ansA");
                img_game3_issue.setVisibility(View.VISIBLE);
                if (flag_color_a){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game3_ansA.setCardBackgroundColor(colorStateList);
                    flag_color_a = false;
                    img_game3_ansA.setVisibility(View.GONE);
                }else {
                    resetColor();
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game3_ansA.setCardBackgroundColor(colorStateList);
                    flag_color_a = true;
                    flag_color_b = false;
                    flag_color_c = false;
                    flag_color_d = false;
                    img_game3_ansA.setVisibility(View.VISIBLE);
                    img_game3_issue.setImageResource(AnswerImg[0][0]);
                    selectAnsOnlyOne();
                }
            }else if (view.getId() == R.id.cv_game34567_ansB){
                Log.d(TAG, "onClick: cv_game34567_ansB");
                img_game3_issue.setVisibility(View.VISIBLE);
                if (flag_color_b){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game3_ansB.setCardBackgroundColor(colorStateList);
                    flag_color_b = false;
                    img_game3_ansB.setVisibility(View.GONE);
                }else {
                    resetColor();
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game3_ansB.setCardBackgroundColor(colorStateList);
                    flag_color_b = true;
                    flag_color_a = false;
                    flag_color_c = false;
                    flag_color_d = false;
                    img_game3_ansB.setVisibility(View.VISIBLE);
                    img_game3_issue.setImageResource(AnswerImg[0][1]);
                    selectAnsOnlyOne();
                }
            }else if (view.getId() == R.id.cv_game3_ansC){
                Log.d(TAG, "onClick: cv_game34567_ansC");
                img_game3_issue.setVisibility(View.VISIBLE);
                if (flag_color_c){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game3_ansC.setCardBackgroundColor(colorStateList);
                    flag_color_c = false;
                    img_game3_ansC.setVisibility(View.GONE);
                }else {
                    resetColor();
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game3_ansC.setCardBackgroundColor(colorStateList);
                    flag_color_c = true;
                    flag_color_a = false;
                    flag_color_b = false;
                    flag_color_d = false;
                    img_game3_ansC.setVisibility(View.VISIBLE);
                    img_game3_issue.setImageResource(AnswerImg[0][2]);
                    selectAnsOnlyOne();
                }
            }else if (view.getId() == R.id.cv_game3_ansD){
                Log.d(TAG, "onClick: cv_game34567_ansD");
                img_game3_issue.setVisibility(View.VISIBLE);
                if (flag_color_d){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game3_ansD.setCardBackgroundColor(colorStateList);
                    flag_color_d = false;
                    img_game3_ansD.setVisibility(View.GONE);
                }else {
                    resetColor();
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game3_ansD.setCardBackgroundColor(colorStateList);
                    flag_color_d = true;
                    flag_color_a = false;
                    flag_color_b = false;
                    flag_color_c = false;
                    img_game3_ansD.setVisibility(View.VISIBLE);
                    img_game3_issue.setImageResource(AnswerImg[0][3]);
                    selectAnsOnlyOne();
                }
            }else if (view.getId() == R.id.img_game3_next){
                aHandler.removeCallbacks(runnable);
                if (checkIssue()){
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

    private void resetColor(){
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
        cv_game3_ansA.setCardBackgroundColor(colorStateList);
        cv_game3_ansB.setCardBackgroundColor(colorStateList);
        cv_game3_ansC.setCardBackgroundColor(colorStateList);
        cv_game3_ansD.setCardBackgroundColor(colorStateList);
    }

    private void resetAll(){
        count = 20;
        if (aHandler != null) {
            aHandler.removeCallbacks(runnable);
        }
        aHandler.post(runnable);
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
        cv_game3_ansA.setCardBackgroundColor(colorStateList);
        cv_game3_ansB.setCardBackgroundColor(colorStateList);
        cv_game3_ansC.setCardBackgroundColor(colorStateList);
        cv_game3_ansD.setCardBackgroundColor(colorStateList);

        img_game3_ansA.setVisibility(View.GONE);
        img_game3_ansB.setVisibility(View.GONE);
        img_game3_ansC.setVisibility(View.GONE);
        img_game3_ansD.setVisibility(View.GONE);

        img_game3_issue.setVisibility(View.INVISIBLE);

        spendTime = 20;
        issueNumName = 3;
        playTime = readPlayTime();
        Log.d(TAG, "initView: playTime = "+playTime);
    }

    private void selectAnsOnlyOne(){
        if(flag_color_a){
            img_game3_ansB.setVisibility(View.GONE);
            img_game3_ansC.setVisibility(View.GONE);
            img_game3_ansD.setVisibility(View.GONE);
        }else if(flag_color_b){
            img_game3_ansA.setVisibility(View.GONE);
            img_game3_ansC.setVisibility(View.GONE);
            img_game3_ansD.setVisibility(View.GONE);
        }else if(flag_color_c){
            img_game3_ansA.setVisibility(View.GONE);
            img_game3_ansB.setVisibility(View.GONE);
            img_game3_ansD.setVisibility(View.GONE);
        }else if(flag_color_d){
            img_game3_ansB.setVisibility(View.GONE);
            img_game3_ansC.setVisibility(View.GONE);
            img_game3_ansA.setVisibility(View.GONE);
        }
    }

    private void loopIssue(){
        issueAndAnswer[0][0] = R.string.issue_3;
        AnswerImg[0][0] = R.drawable.ans3_a;
        AnswerImg[0][1] = R.drawable.ans3_b;
        AnswerImg[0][2] = R.drawable.ans3_c;
        AnswerImg[0][3] = R.drawable.ans3_d;
    }

    private void selectIssue(){
        txt_game3_issue.setText(R.string.issue_3);
    }

    final Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            if (count > 0) {
                txt_game3_timeCunt.setText(Integer.toString(count));
                count--;
                aHandler.postDelayed(runnable, 1000);
            }else{
                txt_game3_timeCunt.setText("0");
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

    @Override
    protected void onStart() {
        super.onStart();
        resetAll();
    }

    private void showPassAlertDialog(){
        playSound(3, 0);//Pass
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game3Activity.this);
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
                editor.putInt("playRound", 3);
                editor.apply();
                dialog.cancel();
                finish();
            }
        });
    }

    private void showFailAlertDialog(){
        playSound(2, 0);//fail
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game3Activity.this);
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

    private boolean checkIssue(){
        if(flag_color_d){
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