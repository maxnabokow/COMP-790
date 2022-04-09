package com.example.a07_photodb_tflite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Bitmap> images = new ArrayList<Bitmap>();
    List<String> tags = new ArrayList<String>();
    List<Integer> sizes = new ArrayList<Integer>();
    int currentIndex = 0;
    Bitmap currentImage = null;

    TextView indexText = null;
    ImageView imageView = null;
    TextView tagEdit = null;
    TextView sizeEdit = null;

    SQLiteDatabase db = null;
    final static String DB_NAME = "MY_PHOTO_DB";
    final static String TABLE_NAME = "Photos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indexText = findViewById(R.id.indexText);
        imageView = findViewById(R.id.image);
        tagEdit = findViewById(R.id.tagEdit);
        sizeEdit = findViewById(R.id.sizeEdit);

        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        resetTable();
    }

    public void onCapture(View view) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // i.putExtra("crop", "true");
        startActivityForResult(i, 1);
    }

    public void onLoad(View view) {
        String query = "SELECT * from " + TABLE_NAME;

        final String tagFilterInput = tagEdit.getText().toString();
        final String sizeFilterInput = sizeEdit.getText().toString();
        if (tagFilterInput.equals("") && sizeFilterInput.equals("")) {
            Toast.makeText(this, "Searching Unfiltered...", Toast.LENGTH_SHORT).show();
        }

        query += generateFilterQuery(tagFilterInput, sizeFilterInput);
        query += ";";

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            List<Bitmap> images = new ArrayList<Bitmap>();
            List<String> tags = new ArrayList<String>();
            List<Integer> sizes = new ArrayList<Integer>();

            do {
                final String tag = c.getString(0);
                tags.add(tag);

                final int size = c.getInt(1);
                sizes.add(size);

                final byte[] ba = c.getBlob(2);
                final Bitmap bitmap = decodeBlob(ba);
                images.add(bitmap);
            } while(c.moveToNext());

            updateLists(images, tags, sizes);

            Toast.makeText(this, "✅ Loaded!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "❌ No Matches Found!", Toast.LENGTH_LONG).show();
        }
    }

    public void onSave(View view) {
        if (currentImage == null) {
            Toast.makeText(this, "❌ No Image Set!", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues cv = new ContentValues();
        final String tagInput = tagEdit.getText().toString();
        cv.put("Tag", tagInput);
        final int sizeInput = Integer.parseInt(sizeEdit.getText().toString());
        cv.put("Size", sizeInput);
        final byte[] ba = encodeBitmap(currentImage);
        cv.put("Data", ba);
        db.insert(TABLE_NAME, null, cv);

        Toast.makeText(this, "✅ Saved!", Toast.LENGTH_LONG).show();

        logFullTable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            List<Bitmap> images = new ArrayList<Bitmap>();
            images.add(bitmap);

            List<String> tags = new ArrayList<String>();


            classify(bitmap);

            tags.add("");

            List<Integer> sizes = new ArrayList<Integer>();
            sizes.add(getImageSize(bitmap));

            updateLists(images, tags, sizes);
            tagEdit.requestFocus();
        }
    }

    private void classify(Bitmap bitmap) {
        // Prepare Interpreter
        AssetFileDescriptor afd = null;
        try {
            afd = getAssets().openFd("mobilenet_v2_1.0_224.tflite");
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
        FileChannel fc = fis.getChannel();
        MappedByteBuffer mbb = null;
        try {
            mbb = fc.map(FileChannel.MapMode.READ_ONLY, afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Interpreter interpreter = new Interpreter(mbb);

        TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
        ImageProcessor processor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .build();
        TensorImage resized = processor.process(tensorImage);
        ByteBuffer input = resized.getBuffer();

        TensorBuffer result = TensorBuffer.createFixedSize(new int[]{1, 1001}, DataType.FLOAT32);
        FloatBuffer output = result.getBuffer().asFloatBuffer();

        Log.v("TAG", "" + input.capacity() + ", " + output.capacity());

        interpreter.run(input, output);
    }

    private void updateLists(List<Bitmap> images, List<String> tags, List<Integer> sizes) {
        if (images.isEmpty()) { return; }

        this.images = images;
        this.tags = tags;
        this.sizes = sizes;
        currentIndex = 0;

        updateUI();
    }

    private void updateUI() {
        currentImage = images.get(currentIndex);

        indexText.setText(currentIndex+1 + " / " + images.size());
        imageView.setImageBitmap(currentImage);
        tagEdit.setText(tags.get(currentIndex));
        sizeEdit.setText("" + sizes.get(currentIndex));
    }

    private String generateFilterQuery(String tagFilterInput, String sizeFilterInput) {
        String query = "";
        boolean hasTagFilter = false;
        if (!tagFilterInput.isEmpty()) {
            hasTagFilter = true;
            String[] tagFilters = tagFilterInput.split(";");

            Log.v("", "✅ Entered " + tagFilters.length + " tag(s).");

            query += (" WHERE Tag LIKE '%" + tagFilters[0] + "%'");

            for (int i = 1; i < tagFilters.length; i++) {
                query += (" OR Tag LIKE '%" + tagFilters[i] + "%'");
            }
        }

        if (!sizeFilterInput.isEmpty()) {
            int sizeFilter = Integer.parseInt(sizeFilterInput);
            int min = (int) (0.75 * (double) sizeFilter);
            int max = (int) (1.25 * (double) sizeFilter);

            Log.v("", "✅ Entered size: " + min + "-" + max);

            query += (hasTagFilter ? " AND " : " WHERE ");
            query += ("Size >= " + min);
            query += (" AND Size <= " + max);
        }

        return query;
    }

    private byte[] encodeBitmap(Bitmap b) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap decodeBlob(byte[] ba) {
        return BitmapFactory.decodeByteArray(ba,0, ba.length);
    }

    private void resetTable() {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        db.execSQL("CREATE TABLE Photos(Tag text, Size integer, Data blob);");
    }

    private int getImageSize(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        return imageInByte.length;
    }

    private void logFullTable() {
        final String query = "SELECT * from " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(query, null);

        String result = "";
        c.moveToFirst();
        int index = 0;

        do {
            final String tag = c.getString(0);
            final int size = c.getInt(1);
            result += (index + ":- " + "Tag: " + tag + " | Size: " + size + "\n");
            index++;
        } while(c.moveToNext());

        Log.v("FULL_TABLE", result);
    }

    public void onClearFilters(View view) {
        tagEdit.setText("");
        sizeEdit.setText("");
        tagEdit.requestFocus();
    }

    public void onPrev(View view) {
        if (currentIndex > 0) {
            currentIndex--;
            updateUI();
        }
    }

    public void onNext(View view) {
        if (currentIndex < images.size()-1) {
            currentIndex++;
            updateUI();
        }
    }
}