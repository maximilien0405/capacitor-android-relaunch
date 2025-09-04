export interface AndroidRelaunchPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
