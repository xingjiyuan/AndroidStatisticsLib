package com.dylanxing.statistics;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class StatisticsFileHelper {
    private static volatile StatisticsFileHelper instance;
    private String staticFileName = "statistics";

    private StatisticsFileHelper() {
    }

    public static StatisticsFileHelper getInstance() {
        if (instance == null) {
            synchronized (StatisticsFileHelper.class) {
                if (instance == null) {
                    instance = new StatisticsFileHelper();
                }
            }
        }
        return instance;
    }

    public synchronized String read(Context context) {
        String result = "";
        FileInputStream in = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int length;
        try {
            File file = new File(context.getFilesDir() + "/" + staticFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            in = context.openFileInput(staticFileName);
            while ((length = in.read(buf)) != -1) {
                bout.write(buf, 0, length);
            }
            byte[] content = bout.toByteArray();
            result = new String(content, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                bout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public synchronized void write(Context context, String content) {
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(staticFileName, Context.MODE_PRIVATE);
            out.write(content.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void clear(Context context, String newContent) {
        write(context, newContent);
    }
}
