package com.power.travel.xixuntravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.CarModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/28.
 * 是否有车
 */

public class CarAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<CarModel> list = new ArrayList<>();
    private LayoutInflater inflater;


    public CarAdapter(Context context, List<CarModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_area_only_layout, null, false);

            holder.title = (TextView) convertView
                    .findViewById(R.id.area_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(list.get(position).getName());

        return convertView;
    }

    final class ViewHolder {
        TextView title;
    }
}
