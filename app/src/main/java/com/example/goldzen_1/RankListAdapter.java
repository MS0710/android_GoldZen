package com.example.goldzen_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RankListAdapter extends BaseAdapter {
    private List<Rank> list;
    private Context context;
    private LayoutInflater listlayoutInflater;

    public RankListAdapter(Context _context,List<Rank> _lisd){
        listlayoutInflater = LayoutInflater.from(_context);
        this.context = _context;
        this.list = _lisd;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int userPlayTime = getPrefs.getInt("userPlayTime",0);
        int NO = i+1;
        convertView = listlayoutInflater.inflate(R.layout.rank_record_list,null);
        LinearLayout layout_bg = (LinearLayout) convertView.findViewById(R.id.layout_bg);
        TextView txt_rankList_No = (TextView) convertView.findViewById(R.id.txt_rankList_No);
        TextView txt_rankList_name = (TextView) convertView.findViewById(R.id.txt_rankList_name);
        TextView txt_rankList_totalSecond = (TextView) convertView.findViewById(R.id.txt_rankList_totalSecond);
        if(userPlayTime == Integer.valueOf(list.get(i).getPlayRound())){
            layout_bg.setBackgroundResource(R.drawable.textview_green_bg);
            txt_rankList_totalSecond.setBackgroundResource(R.drawable.textview_green_bg);
        }
        txt_rankList_No.setText(""+NO+" | ");
        txt_rankList_name.setText(list.get(i).getName());
        txt_rankList_totalSecond.setText(list.get(i).getTotalSecond());
        return convertView;
    }
}
