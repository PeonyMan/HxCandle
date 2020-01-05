package com.mosl.module.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mosl.module.widget.R;

public class InputEditDialog extends Dialog implements View.OnClickListener {

    private TextView title;
    private EditText message;
    private ImageView delBtn;
    private TextView cancel;
    private TextView confirm;

    public interface OnClickListener{
        void cancel();
        void confirm(String txt);
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener listener){
        onClickListener = listener;
    }

    public InputEditDialog(@NonNull Context context) {
        this(context, R.style.dialog_style);
    }

    public InputEditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    private void initView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.input_dialog_layout,null);
        if (view != null){
            title = view.findViewById(R.id.confirm_dialog_title);
            message = view.findViewById(R.id.change_edit);
            delBtn = view.findViewById(R.id.change_edit_del);
            cancel = view.findViewById(R.id.confirm_dialog_cancel);
            confirm = view.findViewById(R.id.confirm_dialog_confirm);

            initListener();
        }
        setContentView(view);
    }

    private void initListener(){
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (delBtn != null){
                    delBtn.setImageResource(R.mipmap.del_edit_can_del);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        message.setKeyListener(new DigitsKeyListener()
        {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
            }
            @Override
            protected char[] getAcceptedChars() {
                char[] ac = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_".toCharArray();
                return ac;
            }
        });

        delBtn.setOnClickListener(this);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == delBtn){
            if (message != null){
                message.setText("");
            }
            delBtn.setImageResource(R.mipmap.del_edit_not_del);
        } else if (v == cancel) {
            if (onClickListener != null){
                onClickListener.cancel();
            }
        } else if (v == confirm) {
            if (onClickListener != null){
                onClickListener.confirm(message.getText().toString());
            }
        }
    }

    public TextView getTitle() {
        return title;
    }

    public EditText getMessage() {
        return message;
    }

    public ImageView getDelBtn() {
        return delBtn;
    }

    public TextView getCancel() {
        return cancel;
    }

    public TextView getConfirm() {
        return confirm;
    }
}
