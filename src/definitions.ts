import { PluginListenerHandle } from "@capacitor/core";

export type RelaunchListener = (relaunch: boolean) => void;

export interface AndroidRelaunchPlugin {
  /**
   * Enable the relaunch mechanism.
   * Starts the foreground service + auto-relaunch + heartbeat monitor.
   */
  enable(): Promise<void>;

  /**
   * Disable the relaunch mechanism.
   * Stops the foreground service and disables auto-relaunch.
   */
  disable(): Promise<void>;

  /**
   * Listen to the "relaunch" event.
   * Triggered when the app was automatically relaunched after being killed.
   * 
   * @param eventName The event name.
   * @param listenerFunc Callback invoked with the relaunch value.
   */
  addListener(eventName: 'relaunch', listenerFunc: RelaunchListener): Promise<PluginListenerHandle>;
}