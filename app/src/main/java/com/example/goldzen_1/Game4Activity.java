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

public class Game4Activity extends AppCompatActivity {
    private String TAG = "Game4Activity";
    private TextView txt_game4_timeCunt;
    private ImageView img_game4_issue,img_game4_next;
    private CardView cv_game4_A,cv_game4_B,cv_game4_C,cv_game4_D,cv_game4_E,cv_game4_F;
    private CardView cv_game4_ansA,cv_game4_ansB,cv_game4_ansC,cv_game4_ansD,cv_game4_ansE,
            cv_game4_ansF;
    private boolean isPass = false;
    private boolean[] flag_ans = new boolean[6];
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
        setContentView(R.layout.activity_game4);
        initView();
    }

    private void initView(){
        isPass = false;
        for (int i=0; i<6; i++){
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
        issueNumName = 4;

        mediaPlayer = MediaPlayer.create(Game4Activity.this, R.raw.back3);
        initSoundPool();
        aHandler = new Handler();

        txt_game4_timeCunt = (TextView) findViewById(R.id.txt_game4_timeCunt);
        img_game4_issue = (ImageView) findViewById(R.id.img_game4_issue);
        img_game4_next = (ImageView) findViewById(R.id.img_game4_next);
        cv_game4_ansA = (CardView) findViewById(R.id.cv_game4_ansA);
        cv_game4_ansB = (CardView) findViewById(R.id.cv_game4_ansB);
        cv_game4_ansC = (CardView) findViewById(R.id.cv_game4_ansC);
        cv_game4_ansD = (CardView) findViewById(R.id.cv_game4_ansD);
        cv_game4_ansE = (CardView) findViewById(R.id.cv_game4_ansE);
        cv_game4_ansF = (CardView) findViewById(R.id.cv_game4_ansF);

        cv_game4_A = (CardView) findViewById(R.id.cv_game4_A);
        cv_game4_B = (CardView) findViewById(R.id.cv_game4_B);
        cv_game4_C = (CardView) findViewById(R.id.cv_game4_C);
        cv_game4_D = (CardView) findViewById(R.id.cv_game4_D);
        cv_game4_E = (CardView) findViewById(R.id.cv_game4_E);
        cv_game4_F = (CardView) findViewById(R.id.cv_game4_F);

        cv_game4_ansA.setOnClickListener(onClick);
        cv_game4_ansB.setOnClickListener(onClick);
        cv_game4_ansC.setOnClickListener(onClick);
        cv_game4_ansD.setOnClickListener(onClick);
        cv_game4_ansE.setOnClickListener(onClick);
        cv_game4_ansF.setOnClickListener(onClick);
        img_game4_next.setOnClickListener(onClick);

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
            int item = 0;
            ColorStateList colorStateList;
            playSound(1, 0);
            if (view.getId() == R.id.cv_game4_ansA){
                item = 0;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game4_ansA.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = false;
                    dressUp(item);
                    cv_game4_A.setVisibility(View.GONE);
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game4_ansA.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = true;
                    dressUp(item);
                    cv_game4_A.setVisibility(View.VISIBLE);
                }
            }else if (view.getId() == R.id.cv_game4_ansB){
                item = 1;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game4_ansB.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = false;
                    dressUp(item);
                    cv_game4_B.setVisibility(View.GONE);
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game4_ansB.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = true;
                    dressUp(item);
                    cv_game4_B.setVisibility(View.VISIBLE);
                }
            }else if (view.getId() == R.id.cv_game4_ansC){
                item = 2;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game4_ansC.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = false;
                    dressUp(item);
                    cv_game4_C.setVisibility(View.GONE);
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game4_ansC.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = true;
                    dressUp(item);
                    cv_game4_C.setVisibility(View.VISIBLE);
                }
            }else if (view.getId() == R.id.cv_game4_ansD){
                item = 3;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game4_ansD.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = false;
                    dressUp(item);
                    cv_game4_D.setVisibility(View.GONE);
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game4_ansD.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = true;
                    dressUp(item);
                    cv_game4_D.setVisibility(View.VISIBLE);
                }
            }else if (view.getId() == R.id.cv_game4_ansE){
                item = 4;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game4_ansE.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = false;
                    dressUp(item);
                    cv_game4_E.setVisibility(View.GONE);
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game4_ansE.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = true;
                    dressUp(item);
                    cv_game4_E.setVisibility(View.VISIBLE);
                }
            }else if (view.getId() == R.id.cv_game4_ansF){
                item = 5;
                if (flag_ans[item]){
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
                    cv_game4_ansF.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = false;
                    dressUp(item);
                    cv_game4_F.setVisibility(View.GONE);
                }else {
                    colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    cv_game4_ansF.setCardBackgroundColor(colorStateList);
                    flag_ans[item] = true;
                    dressUp(item);
                    cv_game4_F.setVisibility(View.VISIBLE);
                }
            }else if (view.getId() == R.id.img_game4_next){
                if (isPass){
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

    final Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            if (count > 0) {
                txt_game4_timeCunt.setText(Integer.toString(count));
                count--;
                aHandler.postDelayed(runnable, 1000);
            }else{
                txt_game4_timeCunt.setText("0");
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game4Activity.this);
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
                editor.putInt("playRound", 4);
                editor.apply();
                dialog.cancel();
                finish();
            }
        });
    }

    private void showFailAlertDialog(){
        playSound(2, 0);//fail
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game4Activity.this);
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
        count = 20;
        if (aHandler != null) {
            aHandler.removeCallbacks(runnable);
        }
        aHandler.post(runnable);

        isPass = false;
        for (int i=0; i<6; i++){
            flag_ans[i] = false;
        }
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#FBFDEF"));
        cv_game4_ansA.setCardBackgroundColor(colorStateList);
        cv_game4_ansB.setCardBackgroundColor(colorStateList);
        cv_game4_ansC.setCardBackgroundColor(colorStateList);
        cv_game4_ansD.setCardBackgroundColor(colorStateList);
        cv_game4_ansE.setCardBackgroundColor(colorStateList);
        cv_game4_ansF.setCardBackgroundColor(colorStateList);

        cv_game4_A.setVisibility(View.GONE);
        cv_game4_B.setVisibility(View.GONE);
        cv_game4_C.setVisibility(View.GONE);
        cv_game4_D.setVisibility(View.GONE);
        cv_game4_E.setVisibility(View.GONE);
        cv_game4_F.setVisibility(View.GONE);

        spendTime = 20;
        issueNumName = 4;
        playTime = readPlayTime();
        Log.d(TAG, "initView: playTime = "+playTime);
    }

    //0 a-v
    //1 b-sm
    //2 c-c
    //3 d-m
    //4 e-f
    //5 f-g
    private void dressUp(int item){
        isPass = false;
        for (int i=0 ; i<6; i++ ){
            Log.d(TAG, "dressUp: flag_ans["+i+"] = "+flag_ans[i]);
        }
        Log.d(TAG, "dressUp: -------------");

        if (flag_ans[0] && !flag_ans[1] && !flag_ans[2] && !flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 0");
            img_game4_issue.setImageResource(R.drawable.ans4_v);
        }else if (!flag_ans[0] && flag_ans[1] && !flag_ans[2] && !flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 1");
            img_game4_issue.setImageResource(R.drawable.ans4_sm);
        } else if (!flag_ans[0] && !flag_ans[1] && flag_ans[2] && !flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 2");
            img_game4_issue.setImageResource(R.drawable.ans4_c);
        } else if (!flag_ans[0] && !flag_ans[1] && !flag_ans[2] && flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 3");
            img_game4_issue.setImageResource(R.drawable.ans4_m);
        } else if (!flag_ans[0] && !flag_ans[1] && !flag_ans[2] && !flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 4");
            img_game4_issue.setImageResource(R.drawable.ans4_f);
        } else if (!flag_ans[0] && !flag_ans[1] && !flag_ans[2] && !flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 5");
            img_game4_issue.setImageResource(R.drawable.ans4_g);
        }
        else if (flag_ans[0] && flag_ans[1] && !flag_ans[2] && !flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 6");
            img_game4_issue.setImageResource(R.drawable.ans4_v_sm);
        }
        else if (flag_ans[0] && !flag_ans[1] && flag_ans[2] && !flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 7");
            img_game4_issue.setImageResource(R.drawable.ans4_v_c);
        }
        else if (flag_ans[0] && !flag_ans[1] && !flag_ans[2] && flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 8");
            img_game4_issue.setImageResource(R.drawable.ans4_v_m);
        }
        else if (flag_ans[0] && !flag_ans[1] && !flag_ans[2] && !flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 9");
            img_game4_issue.setImageResource(R.drawable.ans4_v_f);
        }
        else if (flag_ans[0] && !flag_ans[1] && !flag_ans[2] && !flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 10");
            img_game4_issue.setImageResource(R.drawable.ans4_v_g);
        }
        ///////////////////
        else if (!flag_ans[0] && flag_ans[1] && flag_ans[2] && !flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 11");
            img_game4_issue.setImageResource(R.drawable.ans4_sm_c);
        }
        else if (!flag_ans[0] && flag_ans[1] && !flag_ans[2] && !flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 12");
            img_game4_issue.setImageResource(R.drawable.ans4_sm_f);
        }
        else if (!flag_ans[0] && flag_ans[1] && !flag_ans[2] && !flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 13");
            img_game4_issue.setImageResource(R.drawable.ans4_sm_g);
        }
        ////////////////////
        else if (!flag_ans[0] && !flag_ans[1] && flag_ans[2] && flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 14");
            img_game4_issue.setImageResource(R.drawable.ans4_m_c);
        }
        else if (!flag_ans[0] && !flag_ans[1] && flag_ans[2] && !flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 15");
            img_game4_issue.setImageResource(R.drawable.ans4_c_f);
        }
        else if (!flag_ans[0] && !flag_ans[1] && flag_ans[2] && !flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 16");
            //img_game4_issue.setImageResource(R.drawable.ans4_c_g);---------------
        }
        ////////////////////
        else if (!flag_ans[0] && !flag_ans[1] && !flag_ans[2] && flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 17");
            img_game4_issue.setImageResource(R.drawable.ans4_m_g);
        }
        ////////////////////
        else if (flag_ans[0] && flag_ans[1] && flag_ans[2] && !flag_ans[3] && !flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 18");
            img_game4_issue.setImageResource(R.drawable.ans4_v_sm_c);
        }
        else if (flag_ans[0] && flag_ans[1] && !flag_ans[2] && !flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 19");
            //img_game4_issue.setImageResource(R.drawable.ans4_v_sm_g);
        }
        else if (flag_ans[0] && !flag_ans[1] && flag_ans[2] && !flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 20");
            img_game4_issue.setImageResource(R.drawable.ans4_v_c_f);
        }
        else if (flag_ans[0] && !flag_ans[1] && flag_ans[2] && flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 21");
            img_game4_issue.setImageResource(R.drawable.ans4_v_m_c);
        }
        else if (flag_ans[0] && !flag_ans[1] && !flag_ans[2] && flag_ans[3] && flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 22");
            img_game4_issue.setImageResource(R.drawable.ans4_v_m_g);
        }
        ////////////////////
        else if (!flag_ans[0] && flag_ans[1] && flag_ans[2] && !flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 23");
            img_game4_issue.setImageResource(R.drawable.ans4_sm_c_g);
        }
        else if (!flag_ans[0] && flag_ans[1] && flag_ans[2] && !flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 24");
            img_game4_issue.setImageResource(R.drawable.ans4_sm_c_f);
        }
        else if (!flag_ans[0] && flag_ans[1] && !flag_ans[2] && !flag_ans[3] && flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 25");
            img_game4_issue.setImageResource(R.drawable.ans4_sm_g_f);
        }
        ////////////////////
        else if (!flag_ans[0] && !flag_ans[1] && !flag_ans[2] && flag_ans[3] && flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 26");
            img_game4_issue.setImageResource(R.drawable.ans4_m_g_f);
        }
        else if (!flag_ans[0] && !flag_ans[1] && flag_ans[2] && flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 27");
            img_game4_issue.setImageResource(R.drawable.ans4_m_c_f);
        }
        else if (!flag_ans[0] && !flag_ans[1] && flag_ans[2] && flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 28");
            img_game4_issue.setImageResource(R.drawable.ans4_m_c_g);
        }
        ////////////////////
        else if (flag_ans[0] && flag_ans[1] && flag_ans[2] && !flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 29");
            img_game4_issue.setImageResource(R.drawable.ans4_v_sm_c_f);
        }
        else if (flag_ans[0] && !flag_ans[1] && flag_ans[2] && flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 30");
            img_game4_issue.setImageResource(R.drawable.ans4_v_m_c_g);
        }
        else if (flag_ans[0] && !flag_ans[1] && flag_ans[2] && flag_ans[3] && flag_ans[4] && !flag_ans[5]){
            Log.d(TAG, "dressUp: 31");
            img_game4_issue.setImageResource(R.drawable.ans4_v_m_c_f);
        }
        ////////////////////
        else if (!flag_ans[0] && flag_ans[1] && flag_ans[2] && !flag_ans[3] && flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 32");
            img_game4_issue.setImageResource(R.drawable.ans4_sm_c_g_f);
        }
        else if (!flag_ans[0] && !flag_ans[1] && flag_ans[2] && flag_ans[3] && flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 33");
            img_game4_issue.setImageResource(R.drawable.ans4_m_c_g_f);
        }
        else if (flag_ans[0] && !flag_ans[1] && flag_ans[2] && flag_ans[3] && flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 34");
            img_game4_issue.setImageResource(R.drawable.ans4_v_m_c_g_f);
        }
        else if (flag_ans[0] && flag_ans[1] && !flag_ans[2] && !flag_ans[3] && flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 35");
            img_game4_issue.setImageResource(R.drawable.ans4_v_sm_g_f);
        }
        else if (flag_ans[0] && flag_ans[1] && flag_ans[2] && !flag_ans[3] && !flag_ans[4] && flag_ans[5]){
            Log.d(TAG, "dressUp: 36");
            img_game4_issue.setImageResource(R.drawable.ans4_v_sm_c_g_correct);
            isPass = true;
        }
        ////////////////////
        else {
            Log.d(TAG, "dressUp: 00 ori");
            img_game4_issue.setImageResource(R.drawable.ans4_ori);
        }
        Log.d(TAG, "dressUp: -------------");
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