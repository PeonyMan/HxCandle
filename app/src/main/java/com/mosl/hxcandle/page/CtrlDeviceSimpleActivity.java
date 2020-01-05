package com.mosl.hxcandle.page;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.mosl.hxcandle.R;
import com.mosl.hxcandle.bean.LocalBleDevice;
import com.mosl.hxcandle.database.LocalBleDeviceDao;
import com.mosl.hxcandle.database.manager.DBManager;
import com.mosl.hxcandle.popuwindow.TimingPopuWindow;
import com.mosl.module.bledevice.ZHexUtil;
import com.mosl.module.bledevice.hx_candle.HxCandleManager;
import com.mosl.module.util.activity.BaseActivity;
import com.mosl.module.util.comment.Util;
import com.mosl.module.widget.SquareImageView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CtrlDeviceSimpleActivity extends BaseActivity {

    @BindView(R.id.light_bg_image)
    SquareImageView lightBgImage;
    @BindView(R.id.power_turn_on)
    ImageView powerTurnOn;
    @BindView(R.id.power_turn_off)
    ImageView powerTurnOff;
    @BindView(R.id.power_layout)
    LinearLayout powerLayout;
    @BindView(R.id.timer)
    ImageView timer;
    @BindView(R.id.mode_turn_on)
    ImageView modeTurnOn;
    @BindView(R.id.mode_turn_off)
    ImageView modeTurnOff;
    @BindView(R.id.mode_layout)
    LinearLayout modeLayout;
    @BindView(R.id.lightness_value)
    TextView lightnessValue;
    @BindView(R.id.lightness_seekbar)
    SeekBar lightnessSeekbar;

    private LinearLayout powerTurnOnlayout;
    private LinearLayout powerTurnOfflayout;
    private LinearLayout timerlayout;
    private LinearLayout modeTurnOnlayout;
    private LinearLayout modeTurnOfflayout;

    private BleDevice currentDevice;
    private String remoteMacAddress;
    private TimingPopuWindow mTimingPopuWindow;

    @Override
    protected int initLayout() {
        return R.layout.activity_ctrl_device_simple;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        powerTurnOnlayout = findViewById(R.id.power_turn_on_layout);
        powerTurnOfflayout = findViewById(R.id.power_turn_off_layout);
        timerlayout = findViewById(R.id.timer_layout);
        modeTurnOnlayout = findViewById(R.id.mode_turn_on_layout);
        modeTurnOfflayout = findViewById(R.id.mode_turn_off_layout);

    }

    @Override
    protected void iniTitleBar() {
        remoteMacAddress = getIntent().getStringExtra("MacAddress");
        if (remoteMacAddress == null || "".equals(remoteMacAddress)) {
            currentDevice = getIntent().getParcelableExtra("BleDevice");
        } else {
            BluetoothDevice bluetoothDevice = BleManager.getInstance().getBluetoothAdapter().getRemoteDevice(remoteMacAddress);
            currentDevice = new BleDevice(bluetoothDevice);
        }

        if (currentDevice != null) {
            phoneToolbar.getCenterText().setText(currentDevice.getName());
        }
//        phoneToolbar.getRightText().setText(getResources().getString(R.string.rename));
        phoneToolbar.setBackgroundColor(getResources().getColor(R.color.color_white));
        phoneToolbar.getRightText().setVisibility(View.VISIBLE);
        phoneToolbar.setPadding(0, (Util.getStatusBarHeight(this) + Util.dp2px(this, 10)), 0, Util.dp2px(this, 15));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mTimingPopuWindow = new TimingPopuWindow(this);
        mTimingPopuWindow.getCancel().setText(getResources().getString(R.string.no));
        mTimingPopuWindow.getConfirm().setText(getResources().getString(R.string.yes));
        mTimingPopuWindow.getCenterTips().setText(getResources().getString(R.string.timer_open));
        mTimingPopuWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mTimingPopuWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        mTimingPopuWindow.setOutsideTouchable(true);
        mTimingPopuWindow.setAnimationStyle(R.style.dialog_enter_from_bottom);
        Glide.with(this).load(R.mipmap.light_open).into(lightBgImage);

        connectBle();
    }

    @Override
    protected void initListener() {
        //亮度调节
        lightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Util.LOG("seek bar progress is  = " + (progress + 1));
                lightnessValue.setText(getResources().getString(R.string.brightness_text)+" "+String.valueOf(progress + 1));
                write(HxCandleManager.getLightnessSetting(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //定时按钮
        timerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.6f;
                getWindow().setAttributes(lp);
                mTimingPopuWindow.showAtLocation(timer, Gravity.BOTTOM, 0, 0);
            }
        });

        //定时popuwindow
        mTimingPopuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        //定时选择监听
        mTimingPopuWindow.setOnTimeSelectListener(new TimingPopuWindow.OnTimeSelectListener() {
            @Override
            public void onConfirm(int hour) {
                if (hour != -1) {
                    write(HxCandleManager.getTimerSetting(hour));
                }
                mTimingPopuWindow.dismiss();
            }

            @Override
            public void onCancel() {
                mTimingPopuWindow.dismiss();
            }
        });

        //电源开
        powerTurnOnlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write(HxCandleManager.getSwitchOpen());
            }
        });

        //电源关
        powerTurnOfflayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write(HxCandleManager.getSwitchClose());
            }
        });

        //线圈摆动开
        modeTurnOnlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write(HxCandleManager.getCoilOpen());
            }
        });

        //线圈摆动关
        modeTurnOfflayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write(HxCandleManager.getCoilClose());
            }
        });
    }


    //连接蓝牙
    private void connectBle() {
        if (null == currentDevice) {
            return;
        }

        BleManager.getInstance().connect(currentDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Util.LOG("ble device is connecting");
                showLoadingDialog();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Util.LOG("ble device connect failure");
                //设备连接失败
                hideLoadingDialog();
                if (!isDestroyed()) {
                    Toast.makeText(CtrlDeviceSimpleActivity.this, getResources().getString(R.string.con_failure), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Util.LOG("ble device connect success");
                indicateBle();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Util.LOG("ble device disconnected");
//                if (!isDestroyed()) {
//                    Toast.makeText(CtrlDeviceSimpleActivity.this, getResources().getString(R.string.con_dis), Toast.LENGTH_SHORT).show();
//                    finish();
//                }
            }
        });
    }


    private void indicateBle() {
        BleManager.getInstance().indicate(currentDevice, HxCandleManager.SERVICE_UUID, HxCandleManager.INDICATE_UUID, new BleIndicateCallback() {
            @Override
            public void onIndicateSuccess() {
                Util.LOG("indicate device success");
                hideLoadingDialog();
                saveLocalBleDevice();
            }

            @Override
            public void onIndicateFailure(BleException exception) {
                Util.LOG("indicate device failure");
                hideLoadingDialog();
                if (!isDestroyed()) {
                    Toast.makeText(CtrlDeviceSimpleActivity.this, getResources().getString(R.string.con_failure), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
//                hxCandle = HxCandleManager.getHxCandleStatus(data);
//                refreshData();
            }
        });
    }

    //蓝牙写操作（发送命令）
    private void write(final byte[] data) {
        Util.LOG("write data is " + ZHexUtil.formatHexString(data, true));
        if (currentDevice == null) {
            return;
        }
        BleManager.getInstance().write(currentDevice, HxCandleManager.SERVICE_UUID, HxCandleManager.CHARACTERISTIC_UUID, data, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Util.LOG("write success" + Arrays.toString(data));
                if (data[1] == 0x06){//当前为修改名字功能
//                    updateLocalBleDevice();
                }
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Util.LOG("write failure" + Arrays.toString(data));
            }
        });
    }

    //加载本地项
    private void saveLocalBleDevice() {
        try {
            LocalBleDeviceDao deviceDao = DBManager.getInstance().getSession().getLocalBleDeviceDao();
            List<LocalBleDevice> devices = deviceDao.queryBuilder().where(LocalBleDeviceDao.Properties.Id.eq(currentDevice.getMac())).list();
            if (devices == null || devices.size() <= 0){
                LocalBleDevice device = new LocalBleDevice();
                device.setId(currentDevice.getMac());
                device.setName(currentDevice.getName());
                deviceDao.insert(device);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.LOG(e.toString());
        } finally {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (currentDevice != null) {
                BleManager.getInstance().disconnect(currentDevice);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
