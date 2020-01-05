package com.mosl.module.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PhoneToolbar extends LinearLayout implements View.OnClickListener {

    private Context context;
    private ImageView leftImage;
    private TextView leftText;
    private TextView centerText;
    private TextView rightText;
    private ImageView rightImage;

    public interface OnPhoneToolBarClickListener{
        void leftImageClick();
        void leftTextClick();
        void centerTextClick();
        void rightTextClick();
        void rightImageClick();
    }

    private OnPhoneToolBarClickListener phoneToolBarClickListener;

    public void setOnPhoneToolBarClickListener(OnPhoneToolBarClickListener listener){
        phoneToolBarClickListener = listener;
    }

    public PhoneToolbar(Context context) {
        super(context);
        initView(context);
    }

    public PhoneToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PhoneToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.context = context;
        View mView = LayoutInflater.from(context).inflate(R.layout.phone_toolbar_layout,null);
        if (mView != null){
            leftImage = mView.findViewById(R.id.left_image);
            leftText = mView.findViewById(R.id.left_text);
            centerText = mView.findViewById(R.id.center_text);
            rightImage = mView.findViewById(R.id.right_image);
            rightText = mView.findViewById(R.id.right_text);

            leftImage.setImageResource(R.drawable.phone_toobar_back_selector);

            leftImage.setOnClickListener(this);
            leftText.setOnClickListener(this);
            centerText.setOnClickListener(this);
            rightImage.setOnClickListener(this);
            rightText.setOnClickListener(this);
        }
        addView(mView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
    }

    //获取左边图片
    public ImageView getLeftImage(){
        return leftImage;
    }

    public TextView getLeftText(){
        return leftText;
    }

    public TextView getCenterText(){
        return centerText;
    }

    public TextView getRightText(){
        return rightText;
    }

    //获取右边图片
    public ImageView getRightImage(){
        return rightImage;
    }

    public void setPaddingTop(int top){
        this.setPadding(0,top,0,15);
    }

    @Override
    public void onClick(View v) {
        if (phoneToolBarClickListener != null) {
            if (v == leftImage) {
                phoneToolBarClickListener.leftImageClick();
            }else if (v == leftText){
                phoneToolBarClickListener.leftTextClick();
            }else if (v == centerText){
                phoneToolBarClickListener.centerTextClick();
            }else if (v == rightText){
                phoneToolBarClickListener.rightTextClick();
            }else if (v == rightImage){
                phoneToolBarClickListener.rightImageClick();
            }
        }
    }
}
