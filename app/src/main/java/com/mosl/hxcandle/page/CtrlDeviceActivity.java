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
import com.mosl.module.bledevice.hx_candle.bean.HxCandle;
import com.mosl.module.util.activity.BaseActivity;
import com.mosl.module.util.comment.Util;
import com.mosl.module.widget.SquareImageView;
import com.mosl.module.widget.PhoneToolbar;
import com.mosl.module.widget.VerticalSeekBar;
import com.mosl.module.widget.dialog.InputEditDialog;
import com.mosl.module.widget.dialog.manager.DialogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CtrlDeviceActivity extends BaseActivity {

    @BindView(R.id.lightness_value)
    TextView lightnessValue;
    @BindView(R.id.lightness_seekbar)
    VerticalSeekBar lightnessSeekbar;
    @BindView(R.id.reduce_btn)
    ImageView reduceBtn;
    @BindView(R.id.level_one)
    View levelOne;
    @BindView(R.id.level_two)
    View levelTwo;
    @BindView(R.id.level_three)
    View levelThree;
    @BindView(R.id.level_four)
    View levelFour;
    @BindView(R.id.level_five)
    View levelFive;
    @BindView(R.id.add_btn)
    ImageView addBtn;
    @BindView(R.id.timer)
    ImageView timer;
    @BindView(R.id.power)
    ImageView power;
    @BindView(R.id.coil_power)
    ImageView coilPower;
    @BindView(R.id.light_bg_image)
    SquareImageView lightBgImage;

    private BleDevice currentDevice;
    private String remoteMacAddress;
    private HxCandle hxCandle;

    private List<View> levelList = new ArrayList<>();
    private TimingPopuWindow mTimingPopuWindow;

    private InputEditDialog inputEditDialog;
    private String renameTxt;

    @Override
    protected int initLayout() {
        return R.layout.activity_ctrl_device;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
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
        phoneToolbar.getRightText().setText(getResources().getString(R.string.rename));
        phoneToolbar.setBackgroundColor(getResources().getColor(R.color.color_f3f3f3));
        phoneToolbar.getRightText().setVisibility(View.VISIBLE);
        phoneToolbar.setPadding(0, (Util.getStatusBarHeight(this) + Util.dp2px(this, 10)), 0, Util.dp2px(this, 15));
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
                inputEditDialog.show();
            }

            @Override
            public void rightImageClick() {

            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        levelList.add(levelOne);
        levelList.add(levelTwo);
        levelList.add(levelThree);
        levelList.add(levelFour);
        levelList.add(levelFive);

        mTimingPopuWindow = new TimingPopuWindow(this);
        mTimingPopuWindow.getCancel().setText(getResources().getString(R.string.no));
        mTimingPopuWindow.getConfirm().setText(getResources().getString(R.string.yes));
        mTimingPopuWindow.getCenterTips().setText(getResources().getString(R.string.timer_open));
        mTimingPopuWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mTimingPopuWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        mTimingPopuWindow.setOutsideTouchable(true);
        mTimingPopuWindow.setAnimationStyle(R.style.dialog_enter_from_bottom);

        inputEditDialog = new InputEditDialog(this);
        inputEditDialog.getCancel().setText(getResources().getString(R.string.no));
        inputEditDialog.getConfirm().setText(getResources().getString(R.string.yes));
        inputEditDialog.getTitle().setText(getResources().getString(R.string.rename));
        if (currentDevice != null) {
            inputEditDialog.getMessage().setText(currentDevice.getName());
            inputEditDialog.getMessage().setSelection(currentDevice.getName().length());
        }
        DialogManager.initBottomDialog(this, inputEditDialog);

        Glide.with(this).load(R.mipmap.light_close).into(lightBgImage);
//        connectBle();
    }

    @Override
    protected void initListener() {

        //亮度调节
        lightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Util.LOG("seek bar progress is  = " + (progress + 1));
                lightnessValue.setText(String.valueOf(progress + 1));
                write(HxCandleManager.getLightnessSetting(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        reduceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hxCandle != null) {
                    if (hxCandle.getCoilLevel() > 1) {
                        write(HxCandleManager.getCoilLevelSetting(hxCandle.getCoilLevel() - 1));
                    }
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hxCandle != null) {
                    if (hxCandle.getCoilLevel() < 5) {
                        write(HxCandleManager.getCoilLevelSetting(hxCandle.getCoilLevel() + 1));
                    }
                }
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hxCandle != null) {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 0.6f;
                    getWindow().setAttributes(lp);
                    mTimingPopuWindow.setInitPosition(hxCandle.getTimer());
                    mTimingPopuWindow.showAtLocation(timer, Gravity.BOTTOM, 0, 0);
                }
            }
        });

        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hxCandle != null) {
                    if (hxCandle.getPowerSwitch() == 0) {
                        write(HxCandleManager.getSwitchOpen());
                    } else {
                        write(HxCandleManager.getSwitchClose());
                    }
                }
            }
        });

        coilPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hxCandle != null) {
                    if (hxCandle.getCoilSwitch() == 0) {
                        write(HxCandleManager.getCoilOpen());
                    } else {
                        write(HxCandleManager.getCoilClose());
                    }
                }
            }
        });

        mTimingPopuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

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

        inputEditDialog.setOnClickListener(new InputEditDialog.OnClickListener() {
            @Override
            public void cancel() {
                inputEditDialog.dismiss();
            }

            @Override
            public void confirm(String txt) {
                renameTxt = txt;
                write(HxCandleManager.getRenameSetting(txt));
                inputEditDialog.dismiss();
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
                    Toast.makeText(CtrlDeviceActivity.this, getResources().getString(R.string.con_failure), Toast.LENGTH_SHORT).show();
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
                if (!isDestroyed()) {
                    Toast.makeText(CtrlDeviceActivity.this, getResources().getString(R.string.con_dis), Toast.LENGTH_SHORT).show();
                    finish();
                }
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
                    Toast.makeText(CtrlDeviceActivity.this, getResources().getString(R.string.con_failure), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                hxCandle = HxCandleManager.getHxCandleStatus(data);
                refreshData();
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
                    updateLocalBleDevice();
                }
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Util.LOG("write failure" + Arrays.toString(data));
            }
        });
    }

    private void refreshData() {
        if (isDestroyed()){
            return;
        }
        if (hxCandle != null) {
            if (hxCandle.getPowerSwitch() == 0) {
                power.setImageResource(R.mipmap.power_switch_normal);
                Glide.with(this).load(R.mipmap.light_open).into(lightBgImage);
            } else {
                power.setImageResource(R.mipmap.power_switch_normal);
                Glide.with(this).load(R.mipmap.light_close).into(lightBgImage);
            }

            if (hxCandle.getCoilSwitch() == 0) {
                coilPower.setImageResource(R.mipmap.coil_switch_normal);
            } else {
                coilPower.setImageResource(R.mipmap.coil_switch_press);
            }

            if (hxCandle.getTimer() > 0) {
                timer.setImageResource(R.mipmap.timer_press_icon);
            } else {
                timer.setImageResource(R.mipmap.timer_normal_icon);
            }

            lightnessValue.setText(String.valueOf(hxCandle.getLightness()));
            lightnessSeekbar.setProgress(hxCandle.getLightness());

            levelOne.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            levelTwo.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            levelThree.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            levelFour.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            levelFive.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            for (int i = 0; i < hxCandle.getCoilLevel(); i++) {
                levelList.get(i).setBackgroundColor(getResources().getColor(R.color.color_57dbe9));
            }
        }
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

    private void updateLocalBleDevice() {
        try {
            LocalBleDeviceDao deviceDao = DBManager.getInstance().getSession().getLocalBleDeviceDao();
            List<LocalBleDevice> devices = deviceDao.queryBuilder().where(LocalBleDeviceDao.Properties.Id.eq(currentDevice.getMac())).list();
            if (devices != null && devices.size() > 0){
                LocalBleDevice device = devices.get(0);
                device.setName(renameTxt);
                deviceDao.update(device);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.LOG(e.toString());
        } finally {
            phoneToolbar.getCenterText().setText(renameTxt);
        }
    }
}
