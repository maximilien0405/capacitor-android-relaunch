import { WebPlugin } from '@capacitor/core';

import type { AndroidRelaunchPlugin } from './definitions';

export class AndroidRelaunchWeb extends WebPlugin implements AndroidRelaunchPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
