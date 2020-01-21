package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.schedulers.IoScheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Button download;
    Button select;
    ImageView imageView;
    Disposable disposable;
    public static final int SELECT_VIEW = 10;
    Intent photoPickerIntent;
    Bitmap openedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        download = findViewById(R.id.download);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageView);
        select.setOnClickListener(v -> {
            photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_VIEW);

        });
        download.setOnClickListener(v -> {
            if (disposable == null || disposable.isDisposed()) {
                start();
            }

        });


    }

    public void start() {
        disposable = Observable.fromCallable(this::readImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bitmap -> {
                            openedBitmap = bitmap;
                            imageView.setImageBitmap(bitmap);
                        },
                        throwable -> download.setText(throwable.toString())


                );
    }

    public Bitmap readImage() throws Exception {
        final Uri imageUri = photoPickerIntent.getData();
        assert imageUri != null;
        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        if (selectedImage != null) return selectedImage;
        else throw new Exception("Ошибка загрузки");
    }

    public Bitmap convertImage(Bitmap bitmap) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] array = baos.toByteArray();
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_VIEW) {
            start();
        }
    }
}