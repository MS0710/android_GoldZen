package com.example.goldzen_1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game6Activity extends AppCompatActivity {

    private String TAG = "Game6Activity";
    private ImageView img_game4_issue,img_game6_next;
    private Button btn_game6_ansA,btn_game6_ansB,btn_game6_ansC,btn_game6_ansD,btn_game6_ansE,
            btn_game6_ansF,btn_game6_ansG,btn_game6_ansH,btn_game6_ansI;
    private TextView txt_game6_timeCunt;
    private String ansResult;
    private RecyclerView recycler_game6_ans;
    private LinearLayoutManager linearLayoutManager;
    private GameListAdapter adapter;
    private List<Game> mGameList;
    //int count = 20;
    int count = 60;
    private Handler aHandler;
    private boolean flag_ansA,flag_ansB,flag_ansC,flag_ansD,flag_ansE,flag_ansF,flag_ansG,
            flag_ansH,flag_ansI;
    private int position;
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
        setContentView(R.layout.activity_game6);
        initView();
    }

    private void initView(){
        position = 0;
        flag_ansA = false;
        flag_ansB = false;
        flag_ansC = false;
        flag_ansD = false;
        flag_ansE = false;
        flag_ansF = false;
        flag_ansG = false;
        flag_ansH = false;
        flag_ansI = false;
        aHandler = new Handler();
        ansResult = "";

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
        issueNumName = 6;

        mediaPlayer = MediaPlayer.create(Game6Activity.this, R.raw.back3);
        initSoundPool();

        mGameList = new ArrayList<>();
        btn_game6_ansA = (Button) findViewById(R.id.btn_game6_ansA);
        btn_game6_ansB = (Button) findViewById(R.id.btn_game6_ansB);
        btn_game6_ansC = (Button) findViewById(R.id.btn_game6_ansC);
        btn_game6_ansD = (Button) findViewById(R.id.btn_game6_ansD);
        btn_game6_ansE = (Button) findViewById(R.id.btn_game6_ansE);
        btn_game6_ansF = (Button) findViewById(R.id.btn_game6_ansF);
        btn_game6_ansG = (Button) findViewById(R.id.btn_game6_ansG);
        btn_game6_ansH = (Button) findViewById(R.id.btn_game6_ansH);
        btn_game6_ansI = (Button) findViewById(R.id.btn_game6_ansI);
        img_game4_issue = (ImageView)findViewById(R.id.img_game4_issue);
        img_game6_next = (ImageView)findViewById(R.id.img_game6_next);
        txt_game6_timeCunt = (TextView)findViewById(R.id.txt_game6_timeCunt);

        recycler_game6_ans = (RecyclerView) findViewById(R.id.recycler_game6_ans);

        /*for (int i=0 ;i<3 ; i++ ){
            Game game = new Game("A");
            mGameList.add(game);
        }*/

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recycler_game6_ans.setLayoutManager(linearLayoutManager);
        adapter = new GameListAdapter(mGameList,getApplicationContext());
        recycler_game6_ans.setAdapter(adapter);


        btn_game6_ansA.setOnClickListener(onClick);
        btn_game6_ansB.setOnClickListener(onClick);
        btn_game6_ansC.setOnClickListener(onClick);
        btn_game6_ansD.setOnClickListener(onClick);
        btn_game6_ansE.setOnClickListener(onClick);
        btn_game6_ansF.setOnClickListener(onClick);
        btn_game6_ansG.setOnClickListener(onClick);
        btn_game6_ansH.setOnClickListener(onClick);
        btn_game6_ansI.setOnClickListener(onClick);
        img_game6_next.setOnClickListener(onClick);
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
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onClick(View view) {
            int ansNum = -1;
            img_game4_issue.setVisibility(View.VISIBLE);
            playSound(1, 0);
            if (view.getId() == R.id.btn_game6_ansA){
                ansNum = 3;
                if(flag_ansA){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansA.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansA = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansA.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_3a);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("A");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansA = true;
                }
            }else if (view.getId() == R.id.btn_game6_ansB){
                ansNum = 8;
                if(flag_ansB){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansB.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansB = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansB.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_8b);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("B");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansB = true;
                }
            }else if (view.getId() == R.id.btn_game6_ansC){
                ansNum = 1;
                if(flag_ansC){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansC.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansC = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansC.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_1c);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("C");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansC = true;
                }
            }else if (view.getId() == R.id.btn_game6_ansD){
                ansNum = 7;//6
                if(flag_ansD){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansD.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansD = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansD.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_6d);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("D");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansD = true;
                }
            }else if (view.getId() == R.id.btn_game6_ansE){
                ansNum = 5;
                if(flag_ansE){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansE.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansE = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansE.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_5e);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("E");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansE = true;
                }
            }else if (view.getId() == R.id.btn_game6_ansF){
                ansNum = 4;
                if(flag_ansF){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansF.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansF = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansF.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_4f);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("F");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansF = true;
                }
            }else if (view.getId() == R.id.btn_game6_ansG){
                ansNum = 9;
                if(flag_ansG){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansG.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansG = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansG.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_9g);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("G");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansG = true;
                }
            }else if (view.getId() == R.id.btn_game6_ansH){
                ansNum = 2;
                if(flag_ansH){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansH.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansH = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansH.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_2h);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("H");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansH = true;
                }
            }else if (view.getId() == R.id.btn_game6_ansI){
                ansNum = 6;//7
                if(flag_ansI){
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
                    btn_game6_ansI.setBackgroundTintList(colorStateList);
                    ansResult = ansResult.replace(Integer.toString(ansNum), "");
                    if ((position-1)<0){
                        position = 0;
                    }
                    mGameList.remove(position-1);
                    adapter.notifyItemRemoved(position-1);
                    adapter.notifyItemChanged(position-1,mGameList.size());
                    position--;
                    flag_ansI = false;
                }else {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#F5BACA"));
                    btn_game6_ansI.setBackgroundTintList(colorStateList);
                    img_game4_issue.setImageResource(R.drawable.ans6_7i);
                    ansResult = ansResult+ansNum;
                    Game game = new Game("I");
                    mGameList.add(game);
                    adapter.notifyDataSetChanged();
                    position++;
                    flag_ansI = true;
                }
            }else if (view.getId() == R.id.img_game6_next){
                if (checkIssue()){
                    if (aHandler != null) {
                        aHandler.removeCallbacks(runnable);
                    }
                    spendTime = 60 - count -1;
                    Log.d(TAG, "onClick: count = "+count);
                    Log.d(TAG, "onClick: spendTime = "+spendTime);
                    saveResult("Pass");
                    showPassAlertDialog();
                }else {
                    if (aHandler != null) {
                        aHandler.removeCallbacks(runnable);
                    }
                    spendTime = 60 - count -1;
                    Log.d(TAG, "onClick: count = "+count);
                    Log.d(TAG, "onClick: spendTime = "+spendTime);
                    saveResult("Fail");
                    showFailAlertDialog();
                }
            }
        }
    };

    private boolean checkIssue(){
        Log.d(TAG, "checkIssue: ansResult = "+ansResult);
        if (ansResult.equals("123456789")){
            //chafedibg before
            //chafeidbg
            ansResult = "";
            return true;
        }
        return false;
    }

    private void resetAll(){
        flag_ansA = false;
        flag_ansB = false;
        flag_ansC = false;
        flag_ansD = false;
        flag_ansE = false;
        flag_ansF = false;
        flag_ansG = false;
        flag_ansH = false;
        flag_ansI = false;
        img_game4_issue.setVisibility(View.INVISIBLE);
        ansResult = "";
        count = 60;
        if (aHandler != null) {
            aHandler.removeCallbacks(runnable);
        }
        aHandler.post(runnable);
        position = 0;
        mGameList.clear();
        adapter.notifyDataSetChanged();
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#8F9872"));
        btn_game6_ansA.setBackgroundTintList(colorStateList);
        btn_game6_ansB.setBackgroundTintList(colorStateList);
        btn_game6_ansC.setBackgroundTintList(colorStateList);
        btn_game6_ansD.setBackgroundTintList(colorStateList);
        btn_game6_ansE.setBackgroundTintList(colorStateList);
        btn_game6_ansF.setBackgroundTintList(colorStateList);
        btn_game6_ansG.setBackgroundTintList(colorStateList);
        btn_game6_ansH.setBackgroundTintList(colorStateList);
        btn_game6_ansI.setBackgroundTintList(colorStateList);

        spendTime = 20;
        issueNumName = 6;
        playTime = readPlayTime();
        Log.d(TAG, "initView: playTime = "+playTime);
    }

    private void showPassAlertDialog(){
        playSound(3, 0);//Pass
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game6Activity.this);
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
                editor.putInt("playRound", 6);
                editor.apply();
                dialog.cancel();
                finish();
            }
        });
    }

    private void showFailAlertDialog(){
        playSound(2, 0);//Fail
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Game6Activity.this);
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
                txt_game6_timeCunt.setText(Integer.toString(count));
                count--;
                aHandler.postDelayed(runnable, 1000);
            }else{
                Log.d(TAG, "run: time up");
                txt_game6_timeCunt.setText("0");
                spendTime = 60;
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