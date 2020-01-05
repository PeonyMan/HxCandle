package com.mosl.hxcandle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mosl.hxcandle.R;
import com.mosl.hxcandle.bean.LocalBleDevice;
import com.mosl.module.widget.SquareImageView;
import com.mosl.module.widget.SquareLinearLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalBleItemAdapter extends RecyclerView.Adapter<LocalBleItemAdapter.ViewHolder> {

    private Context context;
    private List<LocalBleDevice> localBleDeviceList;

    public LocalBleItemAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<LocalBleDevice> list) {
        localBleDeviceList = list;
    }

    public interface OnBleItemClickListener{
        void onClick(LocalBleDevice device);
        void onLongClick(LocalBleDevice device);
    }

    private OnBleItemClickListener bleItemClickListener;

    public void setOnBleItemClickListener(OnBleItemClickListener listener){
        bleItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_ble_item_layout, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.localBleName.setText(localBleDeviceList.get(position).getName());

        holder.localBleLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (bleItemClickListener != null) {
                    bleItemClickListener.onLongClick(localBleDeviceList.get(position));
                }
                return true;
            }
        });
        holder.localBleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bleItemClickListener != null){
                    bleItemClickListener.onClick(localBleDeviceList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return localBleDeviceList == null ? 0 : localBleDeviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.local_ble_layout)
        SquareLinearLayout localBleLayout;
        @BindView(R.id.local_ble_image)
        SquareImageView localBleImage;
        @BindView(R.id.local_ble_name)
        TextView localBleName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
