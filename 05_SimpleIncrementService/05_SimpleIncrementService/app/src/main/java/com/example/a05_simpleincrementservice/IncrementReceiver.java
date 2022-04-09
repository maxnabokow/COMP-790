package com.example.a05_simpleincrementservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IncrementReceiver extends BroadcastReceiver {

    MainActivity observer = null;
    int currentValue = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if(intent == null) { System.out.println("❌❌❌❌❌"); }

        final int received = intent.getIntExtra("result", -1);
        this.currentValue = received;
        observer.redraw();
    }
}