package com.applink.ford.hellosdlandroid;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.reactivex.functions.Consumer;

/**
 * Created by agurz on 4/27/18.
 */

public class Alfred {

    private static Alfred instance;

    // events
    public Consumer<Boolean> onCarMenuAvailable;
    public Consumer<Boolean> onCarServiceStarted;

    public Alfred() {
        SdlService.getInstance().onFirstNonHmi = new Consumer<Boolean>() {
            @Override
            public void accept(Boolean res) throws Exception {
                if (onCarServiceStarted != null) {
                    onCarServiceStarted.accept(res);
                }
            }
        };

        SdlService.getInstance().onFirstSubMenuCreated = new Consumer<Boolean>() {
            @Override
            public void accept(Boolean res) throws Exception {
                if (onCarMenuAvailable != null) {
                    onCarMenuAvailable.accept(res);
                }
            }
        };
    }

    public void executeCommand(String name, Object param) {
        SdlService.getInstance().executeCommand(name, param);
    }

    public void registerCommand(String name, Consumer<Object> consumer) {
        SdlService.getInstance().registerCommand(name, consumer);
    }

    public void sayHello() {
        SdlService.getInstance().playAudio("Hola Fernando, que bien te ves hoy.");
    }

    public void showImage(String url) {
        try {
            byte[] buffer = new byte[1024];
            int n = 0;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            InputStream stream = new URL(url).openStream();

            while ((n = stream.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }

            SdlService.getInstance().showImage(output.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void speak(String s) {
        SdlService.getInstance().playAudio(s);
    }

    public void wakeUp() {
        Log.i("Alfred", "Here I am");
    }

    public static Alfred getInstance() {
        if (instance == null) instance = new Alfred();
        return instance;
    }

}
