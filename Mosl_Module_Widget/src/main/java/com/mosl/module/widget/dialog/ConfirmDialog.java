package com.mosl.module.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mosl.module.widget.R;

public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private TextView title;
    private TextView message;
    private TextView cancel;
    private TextView confirm;

    public interface OnClickListener{
        void cancel();
        void confirm();
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener listener){
        onClickListener = listener;
    }

    public ConfirmDialog(@NonNull Context context) {
        this(context, R.style.dialog_style);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    private void initView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.confirm_dialog_layout,null);
        if (view != null){
            title = view.findViewById(R.id.confirm_dialog_title);
            message = view.findViewById(R.id.confirm_dialog_message);
            cancel = view.findViewById(R.id.confirm_dialog_cancel);
            confirm = view.findViewById(R.id.confirm_dialog_confirm);

            cancel.setOnClickListener(this);
            confirm.setOnClickListener(this);
        }
        setContentView(view);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getMessage() {
        return message;
    }

    public TextView getCancel() {
        return cancel;
    }

    public TextView getConfirm() {
        return confirm;
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            if (v == cancel) {
                onClickListener.cancel();
            }else if (v == confirm){
                onClickListener.confirm();
            }
        }
    }
}
