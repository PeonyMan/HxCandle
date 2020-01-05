package com.mosl.hxcandle.popuwindow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mosl.hxcandle.R;
import com.mosl.module.util.comment.Util;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/20.
 */

public class TimingPopuWindow extends PopupWindow {

    private Context mContext;
    private LayoutInflater mInflater;
    private TextView centerTips;
    private WheelView mWheelView;
    private TextView mCancel;
    private TextView mConfirm;
    private OnTimeSelectListener mListener;
    private int mTime = -1;
    private int dp;

    public void setOnTimeSelectListener(OnTimeSelectListener listener){
        mListener = listener;
    }

    public interface OnTimeSelectListener{
        void onConfirm(int hour);
        void onCancel();
    }

    public TimingPopuWindow(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        dp = Util.px2dp(mContext,1);
        initView();
        initListener();
    }

    public void setInitPosition(int position){
        mTime = position;
        if (mTime != -1 && mWheelView != null){
            mWheelView.setSelection(mTime);
        }
    }

    private void initView(){
        View view = mInflater.inflate(R.layout.timming_popuwindow,null);
        if (view != null){
            centerTips = view.findViewById(R.id.center_tips);
            mCancel = view.findViewById(R.id.purifier_timing_cancel);
            mConfirm = view.findViewById(R.id.purifier_timing_confirm);
            mWheelView = view.findViewById(R.id.purifier_timing_wheel);
            mWheelView.setWheelSize(3);
            mWheelView.setLoop(true);
            mWheelView.setSkin(WheelView.Skin.Holo);
            mWheelView.setExtraText("h", Color.parseColor("#0288ce"), 30*dp, 70*dp);
            mWheelView.setWheelAdapter(new com.wx.wheelview.adapter.ArrayWheelAdapter(mContext));
            mWheelView.setWheelData(getOptionsItems());
            //mWheelView.setWheelSize();
            setContentView(view);
        }
    }

    private void initListener(){
        if (mWheelView != null){
            mWheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    mTime = position;
                }
            });
        }

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.onCancel();
                }
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.onConfirm(mTime);
                }
            }
        });
    }

    private List<String> getOptionsItems(){
        List<String> options = new ArrayList<>();
        for (int i = 0;i<24;i++){
            options.add(String.valueOf(i));
        }
        return options;
    }

    public TextView getCenterTips() {
        return centerTips;
    }

    public TextView getCancel() {
        return mCancel;
    }

    public TextView getConfirm() {
        return mConfirm;
    }
}
