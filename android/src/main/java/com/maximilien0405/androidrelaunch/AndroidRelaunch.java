package com.maximilien0405.androidrelaunch;

import com.getcapacitor.Logger;

public class AndroidRelaunch {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
