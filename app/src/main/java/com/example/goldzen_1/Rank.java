package com.example.goldzen_1;

public class Rank {
    private String playRound;
    private String name;
    private String round;
    private String second;
    private String totalSecond;
    private int playTime;
    private String result;

    public Rank(String _playRound,String _name,String _round,String _second,String _totalSecond,int _playTime,String _result){
        this.playRound = _playRound;
        this.name = _name;
        this.round = _round;
        this.second = _second;
        this.totalSecond = _totalSecond;
        this.playTime = _playTime;
        this.result = _result;
    }
    public String getPlayRound() {
        return playRound;
    }
    public String getName() {
        return name;
    }

    public String getRound() {
        return round;
    }

    public String getSecond() {
        return second;
    }

    public String getTotalSecond() {
        return totalSecond;
    }

    public int getPlayTime() {
        return playTime;
    }

    public String getResult() {
        return result;
    }
}
