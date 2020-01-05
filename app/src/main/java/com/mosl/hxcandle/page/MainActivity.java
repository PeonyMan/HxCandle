package com.mosl.hxcandle.page;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.mosl.hxcandle.R;
import com.mosl.hxcandle.adapter.LocalBleItemAdapter;
import com.mosl.hxcandle.bean.LocalBleDevice;
import com.mosl.hxcandle.database.LocalBleDeviceDao;
import com.mosl.hxcandle.database.manager.DBManager;
import com.mosl.hxcandle.util.LocalDeviceItemDecoration;
import com.mosl.module.util.activity.BaseActivity;
import com.mosl.module.util.comment.Util;
import com.mosl.module.widget.PhoneToolbar;
import com.mosl.module.widget.dialog.ConfirmDialog;
import com.mosl.module.widget.dialog.manager.DialogManager;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    @BindView(R.id.my_local_device_recyclerview)
    RecyclerView myLocalDeviceRecyclerview;
    @BindView(R.id.show_no_data_text)
    TextView showNoDataText;

    private LocalBleItemAdapter localBleItemAdapter;
    private List<LocalBleDevice> localBleDeviceList;
    private ConfirmDialog deleteDialog;
    private LocalBleDevice deleteBleDevice;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        myLocalDeviceRecyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        localBleItemAdapter = new LocalBleItemAdapter(this);
        myLocalDeviceRecyclerview.setAdapter(localBleItemAdapter);
        myLocalDeviceRecyclerview.addItemDecoration(new LocalDeviceItemDecoration(this));
        showNoDataText.setText(getResources().getString(R.string.no_device));

        deleteDialog = new ConfirmDialog(this);
        deleteDialog.getMessage().setText(getResources().getString(R.string.delete_or_not));
        deleteDialog.getTitle().setText(getResources().getString(R.string.dialog_tips));
        deleteDialog.getCancel().setText(getResources().getString(R.string.no));
        deleteDialog.getConfirm().setText(getResources().getString(R.string.yes));
        DialogManager.initBottomDialog(this,deleteDialog);
    }

    @Override
    protected void iniTitleBar() {
        phoneToolbar.getLeftImage().setVisibility(View.GONE);
        phoneToolbar.getCenterText().setText(getResources().getString(R.string.my_device));
        phoneToolbar.getRightImage().setVisibility(View.VISIBLE);
        phoneToolbar.getRightImage().setImageResource(R.drawable.phone_toobar_search_selector);
        phoneToolbar.setBackgroundColor(getResources().getColor(R.color.color_white));
        phoneToolbar.setPadding(0,(Util.getStatusBarHeight(this)+Util.dp2px(this,10)),0,Util.dp2px(this,15));
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
                toSearchDevice();
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        BleManager.getInstance().init(getApplication());
//        LocalBleDeviceDao deviceDao = DBManager.getInstance().getSession().getLocalBleDeviceDao();
//        for (int i=0;i<3;i++) {
//            LocalBleDevice device = new LocalBleDevice();
//            device.setId(System.currentTimeMillis()+"");
//            device.setName("123");
//            deviceDao.insert(device);
//        }
        initPermission();
    }

    @Override
    protected void initListener() {
        localBleItemAdapter.setOnBleItemClickListener(new LocalBleItemAdapter.OnBleItemClickListener() {
            @Override
            public void onClick(LocalBleDevice device) {
//                Intent intent = new Intent(MainActivity.this,CtrlDeviceActivity.class);
                Intent intent = new Intent(MainActivity.this,CtrlDeviceSimpleActivity.class);
                intent.putExtra("MacAddress",device.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(LocalBleDevice device) {
                deleteBleDevice = device;
                deleteDialog.show();
            }
        });

        showNoDataText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSearchDevice();
            }
        });

        deleteDialog.setOnClickListener(new ConfirmDialog.OnClickListener() {
            @Override
            public void cancel() {
                deleteDialog.dismiss();
            }

            @Override
            public void confirm() {
                deleteLocalBleDevice();
                deleteDialog.dismiss();
                getLocalBleDeviceList();
            }
        });
    }

    private void toSearchDevice(){
        Intent intent = new Intent(MainActivity.this,ScanBleActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocalBleDeviceList();
    }

    private void initPermission() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean){

                        }else {
                            Toast.makeText(MainActivity.this,"权限不足！",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //加载本地项
    private void getLocalBleDeviceList() {
        try {
            LocalBleDeviceDao deviceDao = DBManager.getInstance().getSession().getLocalBleDeviceDao();
            localBleDeviceList = deviceDao.loadAll();
        } catch (Exception e) {
            e.printStackTrace();
            Util.LOG(e.toString());
        } finally {
            if (localBleDeviceList != null && localBleDeviceList.size() > 0) {
                showNoDataText.setVisibility(View.GONE);
                myLocalDeviceRecyclerview.setVisibility(View.VISIBLE);
                localBleItemAdapter.setData(localBleDeviceList);
                localBleItemAdapter.notifyDataSetChanged();
            }else {
                showNoDataText.setVisibility(View.VISIBLE);
                myLocalDeviceRecyclerview.setVisibility(View.GONE);
            }
        }
    }

    //删除本地项
    private void deleteLocalBleDevice(){
        try {
            LocalBleDeviceDao deviceDao = DBManager.getInstance().getSession().getLocalBleDeviceDao();
            deviceDao.delete(deleteBleDevice);
        }catch (Exception e){
            e.printStackTrace();
            Util.LOG(e.toString());
        }
        Util.LOG("delete a device = "+deleteBleDevice.getName());
    }
}
