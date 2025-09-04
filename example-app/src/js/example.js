import { AndroidRelaunch } from '@maximilien0405/capacitor-android-relaunch';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    AndroidRelaunch.echo({ value: inputValue })
}
