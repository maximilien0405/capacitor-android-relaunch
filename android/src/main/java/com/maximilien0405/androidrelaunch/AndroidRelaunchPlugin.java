package com.maximilien0405.androidrelaunch;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "AndroidRelaunch")
public class AndroidRelaunchPlugin extends Plugin {
    private static boolean isEnabled = false;
    private static AndroidRelaunchPlugin pluginInstance;

    // Initialize plugin instance reference when plugin loads
    @Override
    public void load() {
        super.load();
        pluginInstance = this;
    }

    // Enable the relaunch mechanism by starting the foreground service
    @com.getcapacitor.PluginMethod
    public void enable(PluginCall call) {
        try {
            Context context = getContext();
            if (context == null) {
                call.reject("Context is null - cannot start service");
                return;
            }
            
            // Check if already enabled
            if (isEnabled) {
                call.resolve();
                return;
            }
            
            Intent serviceIntent = new Intent(context, KeepAliveService.class);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            
            isEnabled = true;
            call.resolve();
        } catch (SecurityException e) {
            call.reject("Permission denied: " + e.getMessage() + ". Make sure FOREGROUND_SERVICE permission is granted.", e);
        } catch (IllegalStateException e) {
            call.reject("Cannot start foreground service: " + e.getMessage() + ". App may be in background.", e);
        } catch (Exception e) {
            call.reject("Failed to enable relaunch service: " + e.getMessage(), e);
        }
    }

    // Disable the relaunch mechanism by stopping the foreground service
    @com.getcapacitor.PluginMethod
    public void disable(PluginCall call) {
        try {
            Context context = getContext();
            if (context == null) {
                call.reject("Context is null - cannot stop service");
                return;
            }
            
            Intent serviceIntent = new Intent(context, KeepAliveService.class);
            boolean stopped = context.stopService(serviceIntent);
            isEnabled = false;
                        
            if (stopped) {
                call.resolve();
            } else {
                call.reject("Service was not running or could not be stopped");
            }
        } catch (Exception e) {
            call.reject("Failed to disable relaunch service: " + e.getMessage(), e);
        }
    }

    // Check if the relaunch mechanism is currently enabled
    public static boolean isEnabled() {
        return isEnabled;
    }

    // Get the current plugin instance for service communication
    public static AndroidRelaunchPlugin getPluginInstance() {
        return pluginInstance;
    }

    // Notify JavaScript listeners about app relaunch events
    public void notifyRelaunch() {
        try {
            JSObject data = new JSObject();
            data.put("relaunch", true);
            notifyListeners("relaunch", data);
        } catch (Exception e) {
        }
    }
}