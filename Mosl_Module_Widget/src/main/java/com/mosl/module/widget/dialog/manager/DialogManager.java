package com.mosl.module.widget.dialog.manager;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.mosl.module.widget.R;

public class DialogManager {

    public static void initBottomDialog(Activity activity, Dialog dialog){
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        lp.y = 10;
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
        //WindowManager.LayoutParams params = dialogWindow.getAttributes();
        // lp.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.5
        lp.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.6
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.dialog_enter_from_bottom);
    }

    public static void initNormalDialog(Activity activity, Dialog dialog,float widthPercent,float heightPercent){
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
        if (heightPercent > 0) {
            lp.height = (int) (d.getHeight() * heightPercent); // 高度设置为屏幕的0.5
        }
        if (widthPercent > 0) {
            lp.width = (int) (d.getWidth() * widthPercent); // 宽度设置为屏幕的0.6
        }
        window.setAttributes(lp);
    }
}
