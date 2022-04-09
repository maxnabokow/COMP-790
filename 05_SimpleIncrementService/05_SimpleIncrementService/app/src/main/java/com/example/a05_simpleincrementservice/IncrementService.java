package com.example.a05_simpleincrementservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IncrementService extends Service {
    public IncrementService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final int input = intent.getIntExtra("input", -1);
        final int result = input + 1;
        final Intent incremented = new Intent();
        incremented.putExtra("result", result);
        incremented.setAction("com.example.a05_simpleincrementservice");
        sendBroadcast(incremented);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}