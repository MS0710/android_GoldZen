package com.example.goldzen_1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class Game12789Activity extends AppCompatActivity {

    private String TAG = "Game12789Activity";
    private TextView txt_game_issue,img_game_selectContent,txt_game_timeCunt;
    private Button btn_game_ansA,btn_game_ansB,btn_game_ansC,btn_game_ansD;
    private boolean flag_color_a,flag_color_b,flag_color_c,flag_color_d;
    private ImageView img_game_next,img_game_issue,img_game_selectAns;
    private ImageView img_game_ansA,img_game_ansB,img_game_ansC,img_game_ansD;
    private int issueNum;
    private int issueNumName;
    private int[][] issueAndAnswer = new int[10][5];
    private int[][] AnswerImg = new int[10][4];
    int count = 20;
    private Handler aHandler;
    private static final String DataBaseName = "RankDataBaseIt_2.db";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "Rank";
    private static SQLiteDatabase db;
    private RankSqlDataBaseHelper rankSqlDataBaseHelper;
    private int spendTime;
    private String name;
    private int playTime,beforeIssue;
    private MediaPlayer mediaPlayer;
    private SoundPool sp;//声明SoundPool的引用
    private HashMap<Integer, Integer> hm;//声明HashMap来存放声音文件
    private int currStaeamId;//当前正播放的streamId
    private int userPlayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game12789);
        initView();
    }

    private void initView(){
        beforeIssue = 0;
        playTime = 1;
        spendTime = 20;
        count = 20;
        issueNum = 0;
        flag_color_a = false;
        flag_color_b = false;
        flag_color_c = false;
        flag_color_d = false;
        loopIssue();

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        name = getPrefs.getString("name", "");
        userPlayTime = getPrefs.getInt("userPlayTime",0);

        rankSqlDataBaseHelper = new RankSqlDataBaseHelper(getApplicationContext(),DataBaseName,
                null,DataBaseVersion,DataBaseTable);
        db = rankSqlDataBaseHelper.getWritableDatabase();

        playTime = readPlayTime();
        Log.d(TAG, "initView: playTime = "+playTime);

        mediaPlayer = MediaPlayer.create(Game12789Activity.this, R.raw.back2);
        initSoundPool();

        txt_game_issue = (TextView) findViewById(R.id.txt_game_issue);
        txt_game_timeCunt = (TextView) findViewById(R.id.txt_game_timeCunt);
        btn_game_ansA = (Button) findViewById(R.id.btn_game_ansA);
        btn_game_ansB = (Button) findViewById(R.id.btn_game_ansB);
        btn_game_ansC = (Button) findViewById(R.id.btn_game_ansC);
        btn_game_ansD = (Button) findViewById(R.id.btn_game_ansD);
        img_game_ansA = (ImageView) findViewById(R.id.img_game_ansA);
        img_game_ansB = (ImageView) findViewById(R.id.img_game_ansB);
        img_game_ansC = (ImageView) findViewById(R.id.img_game_ansC);
        img_game_ansD = (ImageView) findViewById(R.id.img_game_ansD);
        img_game_next = (ImageView) findViewById(R.id.img_game_next);
        img_game_issue = (ImageView) findViewById(R.id.img_game_issue);
        img_game_selectAns = (ImageView) findViewById(R.id.img_game_selectAns);
        img_game_selectContent = (TextView) findViewById(R.id.img_game_selectContent);
        btn_game_ansA.setOnClickListener(onClick);
        btn_game_ansB.setOnClickListener(onClick);
        btn_game_ansC.setOnClickListener(onClick);
        btn_game_ansD.setOnClickListener(onClick);
        img_game_next.setOnClickListener(onClick);
        aHandler = new Handler();
        //selectIssue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: return");
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        int playRound = getPrefs.getInt("playRound", 0);
        Log.d(TAG, "onResume: playRound = "+playRound);

        issueNum = playRound;
        //issueNum = 8;
        selectIssue();
        resetAll();
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
            Log.d(TAG, "onClick: issueNum = "+issueNum);
            if(issueNum>10){
                Log.d(TAG, "onClick: issueNum>10");
                issueNum = 0;
            }
            playSound(1, 0);
            Log.d(TAG, "onClick: issueNum = "+issueNum);
            if(view.getId() == R.id.btn_game_ansA){
                img_game_selectAns.setVisibility(View.VISIBLE);
                img_game_issue.setVisibility(View.VISIBLE);
                if (flag_color_a){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game_ansA.setBackgroundTintList(colorStateList);
                    flag_color_a = false;
                    img_game_ansA.setVisibility(View.GONE);
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game_ansA.setBackgroundTintList(colorStateList);
                    flag_color_a = true;
                    img_game_ansA.setVisibility(View.VISIBLE);
                    img_game_issue.setImageResource(AnswerImg[issueNum][0]);
                    img_game_selectAns.setImageResource(R.drawable.green_a);
                    img_game_selectContent.setText(issueAndAnswer[issueNum][1]);
                }
            }else if(view.getId() == R.id.btn_game_ansB){
                img_game_selectAns.setVisibility(View.VISIBLE);
                img_game_issue.setVisibility(View.VISIBLE);
                if (flag_color_b){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game_ansB.setBackgroundTintList(colorStateList);
                    flag_color_b = false;
                    img_game_ansB.setVisibility(View.GONE);
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game_ansB.setBackgroundTintList(colorStateList);
                    flag_color_b = true;
                    img_game_ansB.setVisibility(View.VISIBLE);
                    img_game_issue.setImageResource(AnswerImg[issueNum][1]);
                    img_game_selectAns.setImageResource(R.drawable.green_b);
                    img_game_selectContent.setText(issueAndAnswer[issueNum][2]);
                }
            }else if(view.getId() == R.id.btn_game_ansC){
                img_game_selectAns.setVisibility(View.VISIBLE);
                img_game_issue.setVisibility(View.VISIBLE);
                if (flag_color_c){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game_ansC.setBackgroundTintList(colorStateList);
                    flag_color_c = false;
                    img_game_ansC.setVisibility(View.GONE);
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game_ansC.setBackgroundTintList(colorStateList);
                    flag_color_c = true;
                    img_game_ansC.setVisibility(View.VISIBLE);
                    img_game_issue.setImageResource(AnswerImg[issueNum][2]);
                    img_game_selectAns.setImageResource(R.drawable.green_c);
                    img_game_selectContent.setText(issueAndAnswer[issueNum][3]);
                }
            }else if(view.getId() == R.id.btn_game_ansD){
                img_game_selectAns.setVisibility(View.VISIBLE);
                img_game_issue.setVisibility(View.VISIBLE);
                if (flag_color_d){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game_ansD.setBackgroundTintList(colorStateList);
                    flag_color_d = false;
                    img_game_ansD.setVisibility(View.GONE);
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game_ansD.setBackgroundTintList(colorStateList);
                    flag_color_d = true;
                    img_game_ansD.setVisibility(View.VISIBLE);
                    img_game_issue.setImageResource(AnswerImg[issueNum][3]);
                    img_game_selectAns.setImageResource(R.drawable.green_d);
                    img_game_selectContent.setText(issueAndAnswer[issueNum][4]);
                }
            }
            else if(view.getId() == R.id.img_game_next){
                if (correctIssue()){
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
                /*Intent intent = new Intent(GameActivity.this,Game34567Activity.class);
                startActivity(intent);*/
                /*Intent intent = new Intent(GameActivity.this,Game5Activity.class);
                startActivity(intent);*/
                /*Intent intent = new Intent(Game12789Activity.this,Game3Activity.class);
                startActivity(intent);*/
                /*Intent intent = new Intent(Game12789Activity.this,Game4Activity.class);
                startActivity(intent);*/
                /*Intent intent = new Intent(Game12789Activity.this,Game6Activity.class);
                startActivity(intent);*/
                /*Intent intent = new Intent(Game12789Activity.this,Game5Activity.class);
                startActivity(intent);*/
                /*Intent intent = new Intent(Game12789Activity.this,Game10Activity.class);
                startActivity(intent);*/
            }
        }
    };

    private void showPassAlertDialog(){
        playSound(3, 0);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game12789Activity.this);
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
                issueNum++;
                Log.d(TAG, "onClick: showPassAlertDialog issueNum = "+issueNum);
                if(issueNum>10){
                    issueNum = 0;
                }
                selectIssue();
                resetAll();
                dialog.cancel();
            }
        });
    }

    private void showFailAlertDialog(){
        playSound(2, 0);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game12789Activity.this);
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
                if(issueNum>10){
                    issueNum = 0;
                }
                selectIssue();
                resetAll();
                dialog.cancel();
            }
        });
    }

    private boolean correctIssue(){
        if(issueNumName == 1){
            if (!flag_color_a){
                return false;
            }
            if (!flag_color_b){
                return false;
            }
            if (!flag_color_c){
                return false;
            }
            if (flag_color_d){
                return false;
            }
            return true;
        }else if(issueNumName == 2){
            if (!flag_color_a){
                return false;
            }
            if (!flag_color_b){
                return false;
            }
            if (!flag_color_c){
                return false;
            }
            if (flag_color_d){
                return false;
            }
            return true;
        }else if(issueNumName == 7){
            if (!flag_color_a){
                return false;
            }
            if (!flag_color_b){
                return false;
            }
            if (!flag_color_c){
                return false;
            }
            if (flag_color_d){
                return false;
            }
            return true;
        }else if(issueNumName == 8){
            if (!flag_color_a){
                return false;
            }
            if (!flag_color_b){
                return false;
            }
            if (!flag_color_c){
                return false;
            }
            if (flag_color_d){
                return false;
            }
            return true;
        }else if(issueNumName == 9){
            if (flag_color_a){
                return false;
            }
            if (flag_color_b){
                return false;
            }
            if (flag_color_d){
                return false;
            }
            if (flag_color_c){
                return true;
            }
            return true;
        }
        return false;
    }

    private void loopIssue(){
        issueAndAnswer[0][0] = R.string.issue_1;
        issueAndAnswer[0][1] = R.string.answer_1_a;
        issueAndAnswer[0][2] = R.string.answer_1_b;
        issueAndAnswer[0][3] = R.string.answer_1_c;
        issueAndAnswer[0][4] = R.string.answer_1_d;
        AnswerImg[0][0] = R.drawable.ans1_a;
        AnswerImg[0][1] = R.drawable.ans1_b;
        AnswerImg[0][2] = R.drawable.ans1_c;
        AnswerImg[0][3] = R.drawable.ans1_d;

        issueAndAnswer[1][0] = R.string.issue_2;
        issueAndAnswer[1][1] = R.string.answer_2_a;
        issueAndAnswer[1][2] = R.string.answer_2_b;
        issueAndAnswer[1][3] = R.string.answer_2_c;
        issueAndAnswer[1][4] = R.string.answer_2_d;
        AnswerImg[1][0] = R.drawable.ans2_a;
        AnswerImg[1][1] = R.drawable.ans2_b;
        AnswerImg[1][2] = R.drawable.ans2_c;
        AnswerImg[1][3] = R.drawable.ans2_d;

        issueAndAnswer[6][0] = R.string.issue_7;
        issueAndAnswer[6][1] = R.string.answer_7_a;
        issueAndAnswer[6][2] = R.string.answer_7_b;
        issueAndAnswer[6][3] = R.string.answer_7_c;
        issueAndAnswer[6][4] = R.string.answer_7_d;
        AnswerImg[6][0] = R.drawable.ans7_a;
        AnswerImg[6][1] = R.drawable.ans7_b;
        AnswerImg[6][2] = R.drawable.ans7_c;
        AnswerImg[6][3] = R.drawable.ans7_d;

        /*issueAndAnswer[7][0] = R.string.issue_8;
        issueAndAnswer[7][1] = R.string.answer_8_a;
        issueAndAnswer[7][2] = R.string.answer_8_b;
        issueAndAnswer[7][3] = R.string.answer_8_c;
        issueAndAnswer[7][4] = R.string.answer_8_d;
        AnswerImg[7][0] = R.drawable.ans8_a;
        AnswerImg[7][1] = R.drawable.ans8_b;
        AnswerImg[7][2] = R.drawable.ans8_c;
        AnswerImg[7][3] = R.drawable.ans8_d;*/

        issueAndAnswer[8][0] = R.string.issue_9;
        issueAndAnswer[8][1] = R.string.answer_9_a;
        issueAndAnswer[8][2] = R.string.answer_9_b;
        issueAndAnswer[8][3] = R.string.answer_9_c;
        issueAndAnswer[8][4] = R.string.answer_9_d;
        AnswerImg[8][0] = R.drawable.ans9_a;
        AnswerImg[8][1] = R.drawable.ans9_b;
        AnswerImg[8][2] = R.drawable.ans9_c;
        AnswerImg[8][3] = R.drawable.ans9_d;

    }

    private void resetAll(){
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
        btn_game_ansA.setBackgroundTintList(colorStateList);
        btn_game_ansB.setBackgroundTintList(colorStateList);
        btn_game_ansC.setBackgroundTintList(colorStateList);
        btn_game_ansD.setBackgroundTintList(colorStateList);

        img_game_ansA.setVisibility(View.GONE);
        img_game_ansB.setVisibility(View.GONE);
        img_game_ansC.setVisibility(View.GONE);
        img_game_ansD.setVisibility(View.GONE);
        img_game_selectAns.setVisibility(View.INVISIBLE);
        img_game_issue.setVisibility(View.INVISIBLE);
        img_game_selectContent.setText("");
        if(issueNum>10){
            issueNum = 0;
        }
        flag_color_a = false;
        flag_color_b = false;
        flag_color_c = false;
        flag_color_d = false;
        count = 20;
        spendTime = 20;
        if (aHandler != null) {
            aHandler.removeCallbacks(runnable);
        }
        aHandler.post(runnable);
        playTime = readPlayTime();
        Log.d(TAG, "initView: playTime = "+playTime);
    }

    private void selectIssue(){
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = getPrefs.edit();
        if (issueNum == 0){
            txt_game_issue.setText(R.string.issue_1);
            btn_game_ansA.setText(R.string.answer_1_a);
            btn_game_ansB.setText(R.string.answer_1_b);
            btn_game_ansC.setText(R.string.answer_1_c);
            btn_game_ansD.setText(R.string.answer_1_d);
            issueNumName = 1;
            editor.putInt("playRound", 0);
            editor.apply();
        } else if (issueNum == 1){
            txt_game_issue.setText(R.string.issue_2);
            btn_game_ansA.setText(R.string.answer_2_a);
            btn_game_ansB.setText(R.string.answer_2_b);
            btn_game_ansC.setText(R.string.answer_2_c);
            btn_game_ansD.setText(R.string.answer_2_d);
            issueNumName = 2;
            editor.putInt("playRound", 1);
            editor.apply();
        }else if (issueNum == 2){
            editor.putInt("playRound", 2);
            editor.apply();
            Intent intent = new Intent(Game12789Activity.this,Game3Activity.class);
            startActivity(intent);
        }else if (issueNum == 3){
            editor.putInt("playRound", 3);
            editor.apply();
            Intent intent = new Intent(Game12789Activity.this,Game4Activity.class);
            startActivity(intent);
        }else if (issueNum == 4){
            editor.putInt("playRound", 4);
            editor.apply();
            Intent intent = new Intent(Game12789Activity.this,Game5Activity.class);
            startActivity(intent);
        }else if (issueNum == 5){
            editor.putInt("playRound", 5);
            editor.apply();
            Intent intent = new Intent(Game12789Activity.this,Game6Activity.class);
            startActivity(intent);
        }else if (issueNum == 6){
            /*txt_game_issue.setText(R.string.issue_7);
            btn_game_ansA.setText(R.string.answer_7_a);
            btn_game_ansB.setText(R.string.answer_7_b);
            btn_game_ansC.setText(R.string.answer_7_c);
            btn_game_ansD.setText(R.string.answer_7_d);
            issueNumName = 7;
            editor.putInt("playRound", 6);
            editor.apply();*/
            //////////
            editor.putInt("playRound", 6);
            editor.apply();
            Intent intent = new Intent(Game12789Activity.this,Game7Activity.class);
            startActivity(intent);
        }else if (issueNum == 7){
            editor.putInt("playRound", 7);
            editor.apply();
            Intent intent = new Intent(Game12789Activity.this,Game8Activity.class);
            startActivity(intent);
        }else if (issueNum == 8){
            txt_game_issue.setText(R.string.issue_9);
            btn_game_ansA.setText(R.string.answer_9_a);
            btn_game_ansB.setText(R.string.answer_9_b);
            btn_game_ansC.setText(R.string.answer_9_c);
            btn_game_ansD.setText(R.string.answer_9_d);
            issueNumName = 9;
            editor.putInt("playRound", 8);
            editor.apply();
        }else if (issueNum == 9){
            editor.putInt("playRound", 9);
            editor.apply();
            Intent intent = new Intent(Game12789Activity.this,Game10Activity.class);
            startActivity(intent);
        }else if (issueNum == 10){
            Toast.makeText(getApplicationContext(),"遊戲結束",Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    final Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            if (count > 0) {
                txt_game_timeCunt.setText(Integer.toString(count));
                count--;
                aHandler.postDelayed(runnable, 1000);
            }else{
                txt_game_timeCunt.setText("0");
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
        //resetAll();
        Log.d(TAG, "onStart: ");
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