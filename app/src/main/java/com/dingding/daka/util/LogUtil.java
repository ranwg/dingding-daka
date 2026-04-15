package com.dingding.daka.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日志工具类 - 将日志同时输出到Logcat和文件
 */
public class LogUtil {

    private static final String TAG = "DingDingDaka";
    private static final String LOG_FILE_NAME = "dingding_log.txt";
    private static final int MAX_LOG_LINES = 500; // 最多保留500行

    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static String lastTag = "";

    public static void d(String message) {
        d(TAG, message);
    }

    public static void d(String tag, String message) {
        String log = formatLog(tag, message);
        Log.d(tag, message);
        writeToFile(log);
    }

    public static void e(String tag, String message) {
        String log = formatLog(tag, "❌ " + message);
        Log.e(tag, message);
        writeToFile(log);
    }

    public static void w(String tag, String message) {
        String log = formatLog(tag, "⚠️ " + message);
        Log.w(tag, message);
        writeToFile(log);
    }

    public static void i(String tag, String message) {
        String log = formatLog(tag, message);
        Log.i(tag, message);
        writeToFile(log);
    }

    private static String formatLog(String tag, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA);
        String time = sdf.format(new Date());
        return time + " [" + tag + "] " + message;
    }

    private static void writeToFile(String log) {
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor();
        }
        executor.execute(() -> {
            try {
                Context ctx = getStaticContext();
                if (ctx == null) return;

                File logFile = new File(ctx.getExternalFilesDir(null), LOG_FILE_NAME);
                if (logFile == null) return;

                // 读取现有内容
                StringBuilder content = new StringBuilder();
                if (logFile.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(logFile));
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null && lineCount < MAX_LOG_LINES - 1) {
                        content.append(line).append("\n");
                        lineCount++;
                    }
                    reader.close();
                }

                // 添加新日志
                content.append(log).append("\n");

                // 写回文件
                BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
                writer.write(content.toString());
                writer.close();

            } catch (IOException e) {
                Log.e(TAG, "写入日志失败: " + e.getMessage());
            }
        });
    }

    private static Context staticContext = null;

    public static void init(Context context) {
        staticContext = context.getApplicationContext();
        // 初始化时写入一条日志
        d(TAG, "========== 日志系统已初始化 ==========");
    }

    private static Context getStaticContext() {
        return staticContext;
    }

    /**
     * 获取日志文件内容
     */
    public static String getLogContent(Context context) {
        try {
            File logFile = new File(context.getExternalFilesDir(null), LOG_FILE_NAME);
            if (logFile != null && logFile.exists()) {
                StringBuilder content = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                return content.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "读取日志失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 清除日志
     */
    public static void clearLog(Context context) {
        try {
            File logFile = new File(context.getExternalFilesDir(null), LOG_FILE_NAME);
            if (logFile != null && logFile.exists()) {
                logFile.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "清除日志失败: " + e.getMessage());
        }
    }
}
