package com.mosl.hxcandle.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.mosl.module.util.comment.Util;

/**
 * Created by Administrator on 2018/8/3.
 */

public class LocalDeviceItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    public LocalDeviceItemDecoration(Context context){
        this.context = context;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.set(Util.dp2px(context,5),Util.dp2px(context,5),Util.dp2px(context,5),Util.dp2px(context,5));
    }
}
