package com.maximilien0405.androidrelaunch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class KeepAliveService extends Service {

    private static final String TAG = "KeepAliveService";
    private static final String CHANNEL_ID = "AndroidRelaunchChannel";
    private Handler handler = new Handler();
    private Runnable heartbeatRunnable;
    private boolean isDestroyed = false;

    // Initialize service and start monitoring
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            createNotificationChannel();
            startHeartbeat();
        } catch (Exception e) {
            Log.e(TAG, "Failed to create service: " + e.getMessage(), e);
            stopSelf();
        }
    }

    // Handle service start command with sticky restart behavior
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // Immediately start foreground to avoid timeout
            startForegroundWithNotification();
        } catch (Exception e) {
            Log.e(TAG, "Failed to start foreground in onStartCommand: " + e.getMessage(), e);
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    // Clean up resources and handle service destruction
    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        
        if (handler != null && heartbeatRunnable != null) {
            handler.removeCallbacks(heartbeatRunnable);
        }

        if (AndroidRelaunchPlugin.isEnabled()) {
            // Relaunch the app after 3s if killed
            new Handler().postDelayed(() -> {
                if (!isDestroyed && AndroidRelaunchPlugin.isEnabled()) {
                    relaunchApp();
                }
            }, 3000);
        }
    }

    // Service binding not supported
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Start foreground service with persistent notification
    private void startForegroundWithNotification() {
        try {
            // Ensure notification channel exists before creating notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel();
            }
            
            // Create notification compatible with API 23+
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("App is active")
                    .setContentText("Monitoring app to keep it alive")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .setAutoCancel(false);

            // Set category for API 24+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setCategory(NotificationCompat.CATEGORY_SERVICE);
            }

            // For API 26+, set importance
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
            }

            Notification notification = builder.build();
            startForeground(1, notification);
            Log.d(TAG, "Foreground service started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start foreground: " + e.getMessage(), e);
            throw e; // Re-throw to handle in onStartCommand
        }
    }

    // Start heartbeat monitoring to check app process
    private void startHeartbeat() {
        try {
            // Heartbeat to check if app is alive
            heartbeatRunnable = this::checkAppAlive;
            handler.postDelayed(heartbeatRunnable, 30000);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start heartbeat: " + e.getMessage(), e);
        }
    }

    // Create notification channel for Android 8.0+
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager == null) {
                    Log.e(TAG, "NotificationManager is null");
                    return;
                }
                
                // Check if channel already exists
                if (manager.getNotificationChannel(CHANNEL_ID) != null) {
                    Log.d(TAG, "Notification channel already exists");
                    return;
                }
                
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID,
                        "Android Relaunch Service",
                        NotificationManager.IMPORTANCE_LOW
                );
                serviceChannel.setDescription("Keeps the app alive and monitors for crashes");
                
                // Set sound to null for API 26+
                serviceChannel.setSound(null, null);
                serviceChannel.setShowBadge(false);
                serviceChannel.enableLights(false);
                serviceChannel.enableVibration(false);
                
                manager.createNotificationChannel(serviceChannel);
                Log.d(TAG, "Notification channel created successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to create notification channel: " + e.getMessage(), e);
                throw e; // Re-throw to handle in calling method
            }
        }
    }

    // Check if app process is running and schedule next heartbeat
    private void checkAppAlive() {
        try {
            if (isDestroyed) return;
            
            if (!AndroidRelaunchPlugin.isEnabled()) {
                Log.d(TAG, "Plugin disabled, stopping heartbeat");
                return;
            }
            
            if (!AppProcessChecker.isAppRunning(getApplicationContext())) {
                relaunchApp();
            }
            
            if (!isDestroyed && handler != null && heartbeatRunnable != null && AndroidRelaunchPlugin.isEnabled()) {
                handler.postDelayed(heartbeatRunnable, 30000);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in heartbeat check: " + e.getMessage(), e);
        }
    }

    // Automatically relaunch the app if it was killed
    private void relaunchApp() {
        try {
            if (!AndroidRelaunchPlugin.isEnabled()) {
                Log.d(TAG, "Plugin disabled, skipping relaunch");
                return;
            }
            
            Intent restartIntent = getMainActivityIntent();
            if (restartIntent != null) {
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                }
                
                startActivity(restartIntent);
                
                notifyRelaunch();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to relaunch app: " + e.getMessage(), e);
        }
    }

    // Get intent to launch the main activity
    private Intent getMainActivityIntent() {
        try {
            String mainActivityClass = getMainActivityClassName();
            if (mainActivityClass != null) {
                return new Intent(this, Class.forName(mainActivityClass));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get main activity intent: " + e.getMessage(), e);
        }
        return null;
    }

    // Get the main activity class name from package manager
    private String getMainActivityClassName() {
        try {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(getPackageName());
            if (intent != null && intent.getComponent() != null) {
                return intent.getComponent().getClassName();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get main activity class name: " + e.getMessage(), e);
        }
        
        return getPackageName() + ".MainActivity";
    }

    // Notify the plugin about app relaunch events
    private void notifyRelaunch() {
        try {
            AndroidRelaunchPlugin plugin = AndroidRelaunchPlugin.getPluginInstance();
            if (plugin != null) {
                plugin.notifyRelaunch();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to notify relaunch: " + e.getMessage(), e);
        }
    }
}