# Capacitor Android Relaunch Plugin

A Capacitor plugin that keeps your Android app alive and automatically relaunches it if it gets killed by the system.

## Features

- **Foreground Service**: Runs a persistent service to keep the app alive
- **Auto-relaunch**: Automatically restarts the app if it gets killed
- **Heartbeat Monitoring**: Continuously checks if the app process is running
- **Event Notifications**: Notifies your app when it gets relaunched

## Installation

```bash
npm install capacitor-android-relaunch
```

## Requirements

- **Android**: API level 23+ (Android 6.0 Marshmallow and later)
- **Capacitor**: Version 7.0.0 or later

## Usage

### Basic Setup

```typescript
import { AndroidRelaunch } from 'capacitor-android-relaunch';

// Enable the relaunch mechanism
await AndroidRelaunch.enable();

// Listen for relaunch events
AndroidRelaunch.addListener('relaunch', (relaunch: boolean) => {
  console.log('App was relaunched:', relaunch);
  // Handle relaunch logic here
});

// Disable when no longer needed
await AndroidRelaunch.disable();
```

### Complete Example

```typescript
import { AndroidRelaunch } from 'capacitor-android-relaunch';

class AppRelaunchManager {
  private isEnabled = false;

  async startMonitoring() {
    try {
      await AndroidRelaunch.enable();
      this.isEnabled = true;
      
      // Listen for relaunch events
      AndroidRelaunch.addListener('relaunch', (relaunch: boolean) => {
        if (relaunch) {
          console.log('App was automatically relaunched');
          this.handleRelaunch();
        }
      });
      
      console.log('Relaunch monitoring started');
    } catch (error) {
      console.error('Failed to start relaunch monitoring:', error);
    }
  }

  async stopMonitoring() {
    try {
      await AndroidRelaunch.disable();
      this.isEnabled = false;
      console.log('Relaunch monitoring stopped');
    } catch (error) {
      console.error('Failed to stop relaunch monitoring:', error);
    }
  }

  private handleRelaunch() {
    // Implement your relaunch logic here
    // For example, restore app state, reconnect to services, etc.
  }
}

// Usage
const relaunchManager = new AppRelaunchManager();
await relaunchManager.startMonitoring();
```

## Permissions

The plugin automatically adds the following permissions to your Android manifest:

- `FOREGROUND_SERVICE`: Required to run the foreground service
- `SYSTEM_ALERT_WINDOW`: Required to relaunch the app
- `RECEIVE_BOOT_COMPLETED`: Allows auto-start after device reboot
- `WAKE_LOCK`: Keeps the device awake when needed

## How It Works

1. **Service Creation**: When enabled, creates a foreground service with a persistent notification
2. **Process Monitoring**: Continuously checks if your app's process is running
3. **Auto-relaunch**: If the process is killed, automatically restarts the main activity
4. **Event Notification**: Notifies your app through the 'relaunch' event listener

## Important Notes

- **Android Only**: This plugin only works on Android devices
- **API Level**: Requires Android 6.0 (API 23) or later
- **Foreground Service**: The plugin runs a foreground service that shows a persistent notification
- **Battery Optimization**: Users may need to disable battery optimization for your app
- **Auto-start Permissions**: Some Android devices require manual permission for auto-start

## Troubleshooting

### Service Not Starting
- Ensure all required permissions are granted
- Check if battery optimization is disabled for your app
- Verify the plugin is properly registered in your Capacitor configuration

### App Not Relaunching
- Check if the device has auto-start restrictions
- Verify the main activity is properly declared in your manifest
- Ensure the service is running (check notification)

### Permission Issues
- Request `SYSTEM_ALERT_WINDOW` permission at runtime if needed
- Guide users to disable battery optimization for your app

## API Reference

### Methods

#### `enable()`
Starts the foreground service and enables auto-relaunch functionality.

#### `disable()`
Stops the foreground service and disables auto-relaunch functionality.

#### `addListener(eventName, listenerFunc)`
Adds a listener for the 'relaunch' event.

### Events

#### `relaunch`
Triggered when the app is automatically relaunched after being killed.

## License

MIT
