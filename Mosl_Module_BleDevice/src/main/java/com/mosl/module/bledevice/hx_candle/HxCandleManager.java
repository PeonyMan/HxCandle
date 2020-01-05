package com.mosl.module.bledevice.hx_candle;

import android.util.Log;

import com.mosl.module.bledevice.CRC8Util;
import com.mosl.module.bledevice.hx_candle.bean.HxCandle;

import java.io.BufferedReader;

public class HxCandleManager {
    private static String TAG = "HxCandleManager";

    //service uuid
    public static final String SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    //characteristic uuid
    public static final String CHARACTERISTIC_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    //indicate uuid
    public static final String INDICATE_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";

    private static HxCandle hxCandle;

    /**
     * function code
     * switch 0x01
     * lightness 0x02
     * coil 0x03
     * timer 0x04
     * coil level 0x05
     * RENAME 0X06
     */
    private static byte HEAD = (byte)0xAA;
    private static byte SWITCH = (byte)0x01;
    private static byte LIGHTNESS = (byte)0x02;
    private static byte COIL = (byte)0x03;
    private static byte TIMER = (byte)0x04;
    private static byte COIL_LEVEL = (byte)0x05;
    private static byte RENAME = (byte)0x06;

    //switch open
    public static byte[] getSwitchOpen(){
        return new byte[]{HEAD,SWITCH,(byte)0x01,(byte)0x01,(byte)0xAD};
    }

    //switch close
    public static byte[] getSwitchClose(){
        return new byte[]{HEAD,SWITCH,(byte)0x01,(byte)0x00,(byte)0xAC};
    }

    //coil open
    public static byte[] getCoilOpen(){
        return new byte[]{HEAD,COIL,(byte)0x01,(byte)0x01,(byte)0xAF};
    }

    //coil close
    public static byte[] getCoilClose(){
        return new byte[]{HEAD,COIL,(byte)0x01,(byte)0x00,(byte)0xAE};
    }

    //lightness
    public static byte[] getLightnessSetting(int lightness){

//        return new byte[]{HEAD,LIGHTNESS,(byte)0x01,(byte)lightness,getCrc(LIGHTNESS,lightness,1)};
        return getCrc(new byte[]{HEAD,LIGHTNESS,(byte)0x01,(byte)lightness,0});
    }

    //timer
    public static byte[] getTimerSetting(int timer){
//        return new byte[]{HEAD,TIMER,(byte)0x01,(byte)timer,getCrc(TIMER,timer,1)};
        return getCrc(new byte[]{HEAD,TIMER,(byte)0x01,(byte)timer,0});
    }

    //coil level
    public static byte[] getCoilLevelSetting(int level){
//        return new byte[]{HEAD,COIL_LEVEL,(byte)0x01,(byte)level,getCrc(COIL_LEVEL,level,1)};
        return getCrc(new byte[]{HEAD,COIL_LEVEL,(byte)0x01,(byte)level,0});
    }

    //rename
    public static byte[] getRenameSetting(String name){
        char[] nameChar = name.toCharArray();
        byte[] result = new byte[4+nameChar.length];
        result[0] = HEAD;
        result[1] = RENAME;
        result[2] = (byte)nameChar.length;

        int value = 0;
        for (int i=0;i<nameChar.length;i++){
            value = value + (int)nameChar[i];
            result[3+i] = (byte)(int)nameChar[i];
        }

//        result[result.length-1] = getCrc(RENAME,value,nameChar.length);
//        for (int i=0;i<result.length;i++){
//            System.out.println("result["+i+"] = "+result[i]);
//        }
        return getCrc(result);
    }

    private static byte getCrc(byte code,int value,int length){
        int crsres = ((0xFF&HEAD)+code+length+value)%0xFF;
        return (byte)crsres;
    }

    private static byte[] getCrc(byte[] data){
        int sum = 0;
        if (data != null) {
            for (int i = 0; i < data.length - 1; i++) {
                sum = sum + (0xFF & data[i]);
            }
        }
        data[data.length-1] = (byte) (sum%0xFF);
        return data;
    }

    public static HxCandle getHxCandleStatus(byte[] data){
        if (data.length > 3) {
            if (hxCandle == null) {
                hxCandle = new HxCandle();
            }
            int orl = 0xFF & data[data.length-1];
            int loc = 0xFF & getCrc(data)[data.length-1];
            System.out.println("");
            if (orl == loc){
                for (int i=2;i<data.length-1;i+=2){
                    if (data[i] == SWITCH){
                        hxCandle.setPowerSwitch(0xFF & data[i+1]);
                    } else if (data[i] == COIL){
                        hxCandle.setCoilSwitch(0xFF & data[i+1]);
                    } else if (data[i] == COIL_LEVEL){
                        hxCandle.setCoilLevel(0xFF & data[i+1]);
                    } else if (data[i] == LIGHTNESS){
                        hxCandle.setLightness(0xFF & data[i+1]);
                    } else if (data[i] == TIMER){
                        hxCandle.setTimer(0xFF & data[i+1]);
                    }
                }
                return hxCandle;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
}
