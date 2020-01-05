package com.mosl.module.bledevice.hx_candle.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HxCandle implements Parcelable {

    private int powerSwitch;
    private int coilSwitch;
    private int coilLevel;
    private int lightness;
    private int timer;

    public HxCandle(){}

    public int getPowerSwitch() {
        return powerSwitch;
    }

    public void setPowerSwitch(int powerSwitch) {
        this.powerSwitch = powerSwitch;
    }

    public int getCoilSwitch() {
        return coilSwitch;
    }

    public void setCoilSwitch(int coilSwitch) {
        this.coilSwitch = coilSwitch;
    }

    public int getCoilLevel() {
        return coilLevel;
    }

    public void setCoilLevel(int coilLevel) {
        this.coilLevel = coilLevel;
    }

    public int getLightness() {
        return lightness;
    }

    public void setLightness(int lightness) {
        this.lightness = lightness;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    protected HxCandle(Parcel in) {
        powerSwitch = in.readInt();
        coilSwitch = in.readInt();
        coilLevel = in.readInt();
        lightness = in.readInt();
        timer = in.readInt();
    }

    public static final Creator<HxCandle> CREATOR = new Creator<HxCandle>() {
        @Override
        public HxCandle createFromParcel(Parcel in) {
            return new HxCandle(in);
        }

        @Override
        public HxCandle[] newArray(int size) {
            return new HxCandle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(powerSwitch);
        dest.writeInt(coilSwitch);
        dest.writeInt(coilLevel);
        dest.writeInt(lightness);
        dest.writeInt(timer);
    }
}
