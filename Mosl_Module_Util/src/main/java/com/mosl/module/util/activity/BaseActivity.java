package com.mosl.module.util.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mosl.module.util.R;
import com.mosl.module.util.comment.StatusBarUtil;
import com.mosl.module.widget.PhoneToolbar;
import com.mosl.module.widget.dialog.LoadingDialog;


public abstract class BaseActivity extends AppCompatActivity {

    public PhoneToolbar phoneToolbar;
    public LinearLayout baseLayout;
    public LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
        StatusBarUtil.setStatusBarTranslucent(this, false);
        setContentView(R.layout.activity_base);
        baseLayout = findViewById(R.id.base_layout);
        phoneToolbar = findViewById(R.id.phone_toolbar);
        //初始化布局
        View view = View.inflate(this, initLayout(), null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        baseLayout.addView(view, layoutParams);
//        ButterKnife.bind(this);
        initPhoneBar();
        initView();
        iniTitleBar();
        initData(savedInstanceState);
        initListener();
    }

    private void initPhoneBar(){
        phoneToolbar.setOnPhoneToolBarClickListener(new PhoneToolbar.OnPhoneToolBarClickListener() {
            @Override
            public void leftImageClick() {
                finish();
            }

            @Override
            public void leftTextClick() {

            }

            @Override
            public void centerTextClick() {

            }

            @Override
            public void rightTextClick() {

            }

            @Override
            public void rightImageClick() {

            }
        });
    }

    //加载布局
    protected abstract int initLayout();

    //初始化控件
    protected abstract void initView();

    //初始化标题栏
    protected abstract void iniTitleBar();

    //初始化数据
    protected abstract void initData(Bundle savedInstanceState);

    //初始化监听
    protected abstract void initListener();

    protected void showLoadingDialog(){
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        if (!loadingDialog.isShowing() && !this.isFinishing()){
            //如果页面没有被关闭则往下执行
            loadingDialog.show();
        }
    }

    protected void hideLoadingDialog(){
        if (loadingDialog != null && loadingDialog.isShowing() && !this.isFinishing()){
            loadingDialog.dismiss();
        }
    }
}
