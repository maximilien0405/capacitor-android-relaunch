import { PluginListenerHandle, WebPlugin } from '@capacitor/core';
import type { AndroidRelaunchPlugin, RelaunchListener } from './definitions';

export class AndroidRelaunchWeb extends WebPlugin implements AndroidRelaunchPlugin {
  async enable(): Promise<void> {
    throw new Error('enable is not supported on the web.');
  }

  async disable(): Promise<void> {
    throw new Error('disable is not supported on the web.');
  }

  async addListener(eventName: 'relaunch', _listenerFunc: RelaunchListener): Promise<PluginListenerHandle> {
    return Promise.reject(new Error(eventName + 'listener is not supported on the web.'));
  }
}