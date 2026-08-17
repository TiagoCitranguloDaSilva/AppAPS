package br.com.cyberchase.quickcall.network;

import android.os.StrictMode;

public final class NetworkPolicy {

    private NetworkPolicy() {
    }

    public static void enable() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);
    }
}
