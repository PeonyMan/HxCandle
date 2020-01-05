package com.mosl.hxcandle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.data.BleDevice;
import com.mosl.hxcandle.R;

import java.util.ArrayList;
import java.util.List;

public class BleLightItemAdapter extends RecyclerView.Adapter<BleLightItemAdapter.ViewHolder> {

    private Context mContext;
    private List<BleDevice> mDeviceList = new ArrayList<>();

    public BleLightItemAdapter(Context context){
        mContext = context;
    }

    public void setData(List<BleDevice> list){
        mDeviceList = list;
    }

    public void addDevice(BleDevice device){
        mDeviceList.add(device);
        notifyDataSetChanged();
    }

    public void clear(){
        if (mDeviceList != null){
            mDeviceList.clear();
        }
        notifyDataSetChanged();
    }

    public interface OnBleItemClickListener{
        void onClick(BleDevice device);
    }

    private OnBleItemClickListener bleItemClickListener;

    public void setOnBleItemClickListener(OnBleItemClickListener listener){
        bleItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ble_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bleName.setText(mDeviceList.get(position).getName());
        holder.bleMac.setText(mDeviceList.get(position).getMac());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bleItemClickListener != null){
                    bleItemClickListener.onClick(mDeviceList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDeviceList==null?0:mDeviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private TextView bleName;
        private TextView bleMac;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.ble_item_layout);
            bleName = itemView.findViewById(R.id.device_name);
            bleMac = itemView.findViewById(R.id.device_mac);
        }
    }
}
