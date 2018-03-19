package com.power.travel.xixuntravel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.power.travel.xixuntravel.R;
import com.power.travel.xixuntravel.activity.FuJinRenActivity;
import com.power.travel.xixuntravel.activity.MasterListActivity;
import com.power.travel.xixuntravel.activity.YueBanActivity;
import com.power.travel.xixuntravel.weight.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerViewPagerAdapter
 * Adapter wrapper.
 *
 * @author frank
 */
public class RecyclerViewPagerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final RecyclerViewPager mViewPager;
    public RecyclerView.Adapter<VH> mAdapter;


    public RecyclerViewPagerAdapter(RecyclerViewPager viewPager, RecyclerView.Adapter<VH> adapter) {
        mAdapter = adapter;
        mViewPager = viewPager;
        setHasStableIds(mAdapter.hasStableIds());
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        mAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
        mAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(VH holder) {
        return mAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        mAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        super.onViewDetachedFromWindow(holder);
        mAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        mAdapter.onBindViewHolder(holder, position);
        final View itemView = holder.itemView;
        ViewGroup.LayoutParams lp;
        if (itemView.getLayoutParams() == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = itemView.getLayoutParams();
            if (mViewPager.getLayoutManager().canScrollHorizontally()) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
        itemView.setLayoutParams(lp);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        mAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    public static class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {

        private final Context mContext;
        private String province_id;
        private final List<Integer> mItems;
        private int mCurrentItemId = 0;
        private int totalSize;//总页数
        private int currentPosition;//当前页数


        class SimpleViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout item_bg;//背景图片
            private ImageView item_one_icon;//第一页图标
            private ImageView item_one_iv;//第一页图片
            private ImageView item_click_iv;//点击跳转图片
            private ImageView item_tag_iv;//下部图片


            private SimpleViewHolder(View view) {
                super(view);
                item_bg = view.findViewById(R.id.item_bg);
                item_one_icon = view.findViewById(R.id.item_one_icon);
                item_one_iv = view.findViewById(R.id.item_one_iv);
                item_click_iv = view.findViewById(R.id.item_click_iv);
                item_tag_iv = view.findViewById(R.id.item_tag_iv);
            }
        }

        public LayoutAdapter(Context context, int itemCount, String province_id) {
            this.province_id = province_id;
            mContext = context;
            mItems = new ArrayList<>(itemCount);
            for (int i = 0; i < itemCount; i++) {
                addItem(i);
            }
        }

        private void addItem(int position) {
            final int id = mCurrentItemId++;
            mItems.add(position, id);
            notifyItemInserted(position);
        }

        private void removeItem(int position) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_sign, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleViewHolder holder, final int position) {

            updateWaitSignTip(holder);//更新页面

            holder.item_click_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            break;
                        case 1:
                            Intent intent = new Intent(mContext, YueBanActivity.class);
                            intent.putExtra("typename", "yueban");
                            intent.putExtra("listtype", 1);
                            intent.putExtra("province_id",province_id);
                            mContext.startActivity(intent);
                            /*Intent intent = new Intent(mContext, YueBanActivity.class);
                            intent.putExtra("typename", "yueban");
                            intent.putExtra("listtype", 2);
                            mContext.startActivity(intent);*/
                            break;
                        case 2:
                            mContext.startActivity(new Intent(mContext, FuJinRenActivity.class));
                            break;
                        case 3:
                            mContext.startActivity(new Intent(mContext, MasterListActivity.class));
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        /**
         * 更新未签提示
         */
        private void updateWaitSignTip(SimpleViewHolder mHolder) {
            currentPosition = mHolder.getAdapterPosition();
            totalSize = getItemCount();
            if (totalSize > 1) {
                if (currentPosition == 0) {
                    mHolder.item_one_icon.setVisibility(View.VISIBLE);
                    mHolder.item_one_iv.setVisibility(View.VISIBLE);
                    mHolder.item_click_iv.setVisibility(View.GONE);
                } else if (currentPosition == 1){
                    mHolder.item_one_icon.setVisibility(View.GONE);
                    mHolder.item_one_iv.setVisibility(View.GONE);
                    mHolder.item_click_iv.setVisibility(View.VISIBLE);
                    mHolder.item_click_iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.a_ylxx));
                    mHolder.item_bg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yueban_two_bg_bg));
                    mHolder.item_tag_iv.setVisibility(View.VISIBLE);
                } else if (currentPosition == 2){
                    mHolder.item_one_icon.setVisibility(View.GONE);
                    mHolder.item_one_iv.setVisibility(View.GONE);
                    mHolder.item_click_iv.setVisibility(View.VISIBLE);
                    mHolder.item_click_iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.a_ylzx));
                    mHolder.item_bg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yueban_three_bg_bg));
                    mHolder.item_tag_iv.setVisibility(View.VISIBLE);
                    mHolder.item_tag_iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.first_three_down));
                } else if (currentPosition == (totalSize - 1)) {//在最后一页，只显示向下滑动
                    mHolder.item_one_icon.setVisibility(View.GONE);
                    mHolder.item_one_iv.setVisibility(View.GONE);
                    mHolder.item_click_iv.setVisibility(View.VISIBLE);
                    mHolder.item_click_iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.a_jyyx));
                    mHolder.item_bg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yueban_four_bg_bg));
                    mHolder.item_tag_iv.setVisibility(View.GONE);
                }
            }
        }
    }
}
