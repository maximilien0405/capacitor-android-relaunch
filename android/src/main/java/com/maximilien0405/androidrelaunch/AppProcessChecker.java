package com.maximilien0405.androidrelaunch;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

public class AppProcessChecker {
    
    private static final String TAG = "AppProcessChecker";

    // Check if the app process is currently running
    public static boolean isAppRunning(Context context) {
        if (context == null) {
            Log.w(TAG, "Context is null");
            return false;
        }
        
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                Log.w(TAG, "ActivityManager service is null");
                return false;
            }
            
            // Use modern approach for Android 8.0+ (API 26+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return checkRunningAppProcessesModern(am, context);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0-7.1 can use getRunningAppProcesses
                return checkRunningProcesses(am, context);
            } else {
                // Fallback for older versions (though we target API 23+)
                return checkRunningTasks(am, context);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception checking app process: " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking app process: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Modern approach for Android 8.0+ using getRunningAppProcesses with proper permissions
    private static boolean checkRunningAppProcessesModern(ActivityManager am, Context context) {
        try {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses == null) {
                Log.w(TAG, "Running processes list is null - may need QUERY_ALL_PACKAGES permission");
                return isAppInForeground(context);
            }

            String packageName = context.getPackageName();
            if (packageName == null) {
                Log.w(TAG, "Package name is null");
                return false;
            }
            
            for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
                if (process != null && process.processName != null && 
                    process.processName.equals(packageName)) {
                    Log.d(TAG, "Found running process for package: " + packageName);
                    return true;
                }
            }
            
            Log.d(TAG, "No running process found for package: " + packageName);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking running processes (modern): " + e.getMessage(), e);
            return isAppInForeground(context);
        }
    }
    
    // Check running processes for Android 6.0-7.1 (API 23-25)
    private static boolean checkRunningProcesses(ActivityManager am, Context context) {
        try {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses == null) {
                Log.w(TAG, "Running processes list is null");
                return false;
            }

            String packageName = context.getPackageName();
            if (packageName == null) {
                Log.w(TAG, "Package name is null");
                return false;
            }
            
            for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
                if (process != null && process.processName != null && 
                    process.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking running processes: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Check running tasks as fallback for older API levels
    private static boolean checkRunningTasks(ActivityManager am, Context context) {
        try {
            // Fallback method for older API levels
            List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
            if (runningTasks == null || runningTasks.isEmpty()) {
                return false;
            }
            
            String packageName = context.getPackageName();
            if (packageName == null) {
                return false;
            }
            
            ActivityManager.RunningTaskInfo topTask = runningTasks.get(0);
            return topTask != null && topTask.topActivity != null && 
                   topTask.topActivity.getPackageName().equals(packageName);
        } catch (Exception e) {
            Log.e(TAG, "Error checking running tasks: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Alternative method to check if app is in foreground
    private static boolean isAppInForeground(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return false;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                if (tasks != null && !tasks.isEmpty()) {
                    String packageName = context.getPackageName();
                    return tasks.get(0).topActivity != null && 
                           tasks.get(0).topActivity.getPackageName().equals(packageName);
                }
            }
            
            // Check if app is in the list of running apps
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            return (appInfo.flags & ApplicationInfo.FLAG_STOPPED) == 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if app is in foreground: " + e.getMessage(), e);
            return false;
        }
    }
}