package com.example.buttoncamera;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // STATE
    private boolean firstShowingBg = true;
    private boolean secondShowingBg = true;

    // VIEWS
    private ImageButton firstButton = null;
    private ImageButton secondButton = null;
    private ImageView imageView = null;

    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstButton = findViewById(R.id.first);
        secondButton = findViewById(R.id.second);
        imageView = findViewById(R.id.imageView);
    }

    // Button Action Handlers

    public void firstButtonTapped(View view) {
        System.out.println("First tapped");

        int res = firstShowingBg ? 0 : R.drawable.camera;
        firstButton.setImageResource(res);

        firstShowingBg = !firstShowingBg;

        showImageStatusInToast();
    }

    public void secondButtonTapped(View view) {
        System.out.println("Second tapped");

        int res = secondShowingBg ? 0 : R.drawable.camera;
        secondButton.setImageResource(res);

        secondShowingBg = !secondShowingBg;

        showImageStatusInToast();
    }

    public void snapButtonTapped(View view) {
        System.out.println("Snap tapped");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, 1);
        } catch (ActivityNotFoundException e) {
            toast = Toast.makeText(getApplicationContext(), "Error taking picture", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    // Helpers

    private void showImageStatusInToast() {
        if (firstShowingBg && secondShowingBg) {
            toast = Toast.makeText(getApplicationContext(), "same image", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // ActivityResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }
}