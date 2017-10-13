package com.power.travel.xixuntravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.model.AreaModel;
import com.power.travel.xixuntravel.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public class AreaTwoAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater inflater;
    Context context;
    int mPosition;

    private List<AreaModel> list = new ArrayList<AreaModel>();

    public AreaTwoAdapter(Context context, List<AreaModel> list) {
        super();
        this.context = context;
        this.list = list;
        LogUtil.e("......", "hahahhah" + list.size());
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.size();
    }

    @Override
    public long getItemId(int i) {
        return mPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_area_two_latout, null, false);

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
