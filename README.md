# Capacitor Android Relaunch Plugin

The Capacitor Android Relaunch Plugin allows you to keep your Android app alive and automatically relaunch it if it gets killed by the system.

When enabled, this plugin will run a foreground service to monitor your app's process and automatically restart it if the system terminates it.

## Installation

To install the plugin, run the following commands:

```bash
npm install @maximilien0405/capacitor-android-relaunch
npx cap sync
```

And import it just like this:
```ts
import { AndroidRelaunch } from '@maximilien0405/capacitor-android-relaunch';
```

## Usage

### Enable Relaunch Monitoring

This method enables the foreground service and starts monitoring your app's process. If the app gets killed, it will automatically be relaunched.

```ts
await AndroidRelaunch.enable();
```

### Disable Relaunch Monitoring

This method disables the foreground service and stops monitoring. The app will no longer be automatically relaunched if killed.

```ts
await AndroidRelaunch.disable();
```

### Listen for Relaunch Events

You can listen for when your app gets automatically relaunched:

```ts
AndroidRelaunch.addListener('relaunch', (relaunch: boolean) => {
  if (relaunch) {
    console.log('App was automatically relaunched');
    // Handle relaunch logic here 
  }
});
```

## Requirements

- **Android**: API level 23+ (Android 6.0 Marshmallow and later)
- **Capacitor**: Version 7.0.0 or later

## Support

For issues or feature requests, please open an issue on the [GitHub repository](https://github.com/maximilien0405/capacitor-android-relaunch).

## License

This project is licensed under the MIT License. See the LICENSE file for details.
