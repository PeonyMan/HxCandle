package com.mosl.hxcandle.page;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.mosl.hxcandle.R;
import com.mosl.hxcandle.adapter.BleLightItemAdapter;
import com.mosl.hxcandle.bean.LocalBleDevice;
import com.mosl.hxcandle.util.SearchDeviceItemDecoration;
import com.mosl.module.util.activity.BaseActivity;
import com.mosl.module.util.comment.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanBleActivity extends BaseActivity {

    @BindView(R.id.ble_list)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private BleLightItemAdapter bleLightItemAdapter;

    @Override
    protected int initLayout() {
        return R.layout.activity_scan_ble;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        bleLightItemAdapter = new BleLightItemAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bleLightItemAdapter);
        recyclerView.addItemDecoration(new SearchDeviceItemDecoration(this));
        refreshLayout.setColorSchemeColors(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE);
    }

    @Override
    protected void iniTitleBar() {
        phoneToolbar.getCenterText().setText(getResources().getString(R.string.search_device));
        phoneToolbar.setBackgroundColor(getResources().getColor(R.color.color_white));
        phoneToolbar.setPadding(0,(Util.getStatusBarHeight(this)+Util.dp2px(this,10)),0,Util.dp2px(this,15));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        initBle();
        refreshLayout.setRefreshing(true);
        startScan();
    }

    @Override
    protected void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BleManager.getInstance().cancelScan();
                startScan();
            }
        });

        bleLightItemAdapter.setOnBleItemClickListener(new BleLightItemAdapter.OnBleItemClickListener() {
            @Override
            public void onClick(BleDevice device) {
                BleManager.getInstance().cancelScan();
//                Intent intent = new Intent(ScanBleActivity.this,CtrlDeviceActivity.class);
                Intent intent = new Intent(ScanBleActivity.this,CtrlDeviceSimpleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("BleDevice",device);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initBle(){
        BleManager.getInstance().enableBluetooth();
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "HX_C")         // 只扫描指定广播名的设备，可选
//                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(15000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

        if (!BleManager.getInstance().getBluetoothAdapter().enable()) {
            Toast.makeText(this,getResources().getString(R.string.please_open_ble),Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                Util.LOG( "start scan ble device");
                if (!isDestroyed()){
                    bleLightItemAdapter.clear();
                }
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Util.LOG( "find a ble device = "+ bleDevice.getName());
                if (!isDestroyed()){
//                    if (null != bleDevice.getName() && !"".equals(bleDevice.getName())) {
                        bleLightItemAdapter.addDevice(bleDevice);
                        Util.LOG( "add a ble device = "+ bleDevice.getName());
//                    }
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Util.LOG( "scan ble device finish");
                if (!isDestroyed()){
                    if (refreshLayout != null && refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BleManager.getInstance().cancelScan();
    }

}
