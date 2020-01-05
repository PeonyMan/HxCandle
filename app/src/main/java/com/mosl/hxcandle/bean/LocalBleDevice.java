package com.mosl.hxcandle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LocalBleDevice implements Parcelable {
    @Id
    private String id;
    @Property
    private String name;
    @Generated(hash = 829076470)
    public LocalBleDevice(String id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 816135200)
    public LocalBleDevice() {
    }

    protected LocalBleDevice(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<LocalBleDevice> CREATOR = new Creator<LocalBleDevice>() {
        @Override
        public LocalBleDevice createFromParcel(Parcel in) {
            return new LocalBleDevice(in);
        }

        @Override
        public LocalBleDevice[] newArray(int size) {
            return new LocalBleDevice[size];
        }
    };

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }
}