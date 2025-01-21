package com.example.goldzen_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RankDetailListAdapter extends BaseAdapter {
    private List<Rank> list;
    private Context context;
    private LayoutInflater listlayoutInflater;

    public RankDetailListAdapter(Context _context,List<Rank> _list){
        listlayoutInflater = LayoutInflater.from(_context);
        //this.context = _context;
        this.list = _list;
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
        convertView = listlayoutInflater.inflate(R.layout.rank_detail_list,null);
        TextView txt_rankDetail_round = (TextView) convertView.findViewById(R.id.txt_rankDetail_round);
        TextView txt_rankDetail_playTime = (TextView) convertView.findViewById(R.id.txt_rankDetail_playTime);
        TextView txt_rankDetail_second = (TextView) convertView.findViewById(R.id.txt_rankDetail_second);
        TextView txt_rankDetail_result = (TextView) convertView.findViewById(R.id.txt_rankDetail_result);

        txt_rankDetail_round.setText(list.get(i).getRound());
        txt_rankDetail_playTime.setText("第"+list.get(i).getPlayTime()+"次");
        txt_rankDetail_second.setText(list.get(i).getSecond());
        txt_rankDetail_result.setText(list.get(i).getResult());

        return convertView;
    }
}
