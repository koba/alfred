package com.applink.ford.hellosdlandroid;

import com.smartdevicelink.proxy.rpc.OnHMIStatus;

/**
 * Created by agurz on 4/27/18.
 */

public class Alfred {

    private static Alfred instance;

    public Alfred() {
    }

    public void sayHello() {
        SdlService.getInstance().playAudio("Hola Fernando, que bien te ves hoy.");
    }

    public void speak(String s) {
        SdlService.getInstance().playAudio(s);
    }

    public void wakeUp() {
        // nothing to do yet
    }

    public static Alfred getInstance() {
        if (instance == null) instance = new Alfred();
        return instance;
    }
}
