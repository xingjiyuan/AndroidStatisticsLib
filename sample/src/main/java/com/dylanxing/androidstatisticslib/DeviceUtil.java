package com.dylanxing.androidstatisticslib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class DeviceUtil {
    public static final String MAC = "mac";
    public static final String DEVICE_ID = "device_id";

    @SuppressLint("NewApi")
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                result = rest == PackageManager.PERMISSION_GRANTED;
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getDeviceInfo(Context context) {
        org.json.JSONObject json = new org.json.JSONObject();
        try {
            String mac = getMAC(context);
            json.put(MAC, mac);
            json.put(DEVICE_ID, getDeviceId(context, mac));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static String getDeviceId(Context context, String mac) {
        if (context == null) {
            return null;
        }
        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String device_id = null;

        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            device_id = tm.getDeviceId();
        }

        if (TextUtils.isEmpty(device_id)) {
            device_id = mac;
        }
        if (TextUtils.isEmpty(device_id)) {
            device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        }
        return device_id;
    }

    public static String getMAC(Context context) {
        if (context == null) {
            return null;
        }
        String mac = null;
        FileReader fstream = null;
        try {
            fstream = new FileReader("/sys/class/net/wlan0/address");
        } catch (FileNotFoundException e) {
            try {
                fstream = new FileReader("/sys/class/net/eth0/address");
            } catch (FileNotFoundException ignored) {
            }
        }
        BufferedReader in = null;
        if (fstream != null) {
            try {
                in = new BufferedReader(fstream, 1024);
                mac = in.readLine();
            } catch (IOException e) {
            } finally {
                if (fstream != null) {
                    try {
                        fstream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (TextUtils.isEmpty(mac)) {
            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mac = wifi.getConnectionInfo().getMacAddress();
        }
        return mac;
    }
}
