import { registerPlugin } from '@capacitor/core';

import type { AndroidRelaunchPlugin } from './definitions';

const AndroidRelaunch = registerPlugin<AndroidRelaunchPlugin>('AndroidRelaunch', {
  web: () => import('./web').then((m) => new m.AndroidRelaunchWeb()),
});

export * from './definitions';
export { AndroidRelaunch };
