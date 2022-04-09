package com.example.a05_simpleincrementservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    IncrementReceiver receiver = null;
    IntentFilter filter = null;

    TextView text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new IncrementReceiver();
        receiver.observer = this;
        filter = new IntentFilter("com.example.a05_simpleincrementservice");
        registerReceiver(receiver, filter);

        text = findViewById(R.id.label);
        text.setText(Integer.toString(receiver.currentValue));
    }

    public void onClick(View view) {
        Intent increment = new Intent(this, IncrementService.class);
        increment.putExtra("input", this.receiver.currentValue);
        startService(increment);
    }

    public void redraw() {
        text.setText(Integer.toString(receiver.currentValue));
        text.invalidate();
    }
}