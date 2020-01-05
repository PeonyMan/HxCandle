package com.mosl.module.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.mosl.module.widget.R;

public class LoadingDialog extends Dialog {

    private ImageView loadingView;
    private TextView loadingText;

    public LoadingDialog(@NonNull Context context) {
        this(context,R.style.dialog_style);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    private void initView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog_layout,null);
        if (view != null){
            loadingView = view.findViewById(R.id.loading_image);
            loadingText = view.findViewById(R.id.loading_text);

            Glide.with(context).load(R.mipmap.loading).into(loadingView);
        }
        setContentView(view);
    }
}
