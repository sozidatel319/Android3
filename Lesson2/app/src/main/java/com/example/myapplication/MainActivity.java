package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 20;
    Button download;
    Button select;
    ImageView imageView;
    Disposable disposable;
    public static final int SELECT_VIEW = 10;
    Intent photoPickerIntent;
    Bitmap openedBitmap;
    Intent received;

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

            }

        });


    }

    public void start(Intent data) {
        received = data;
        disposable = Observable.fromCallable(this::readImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bitmap -> {
                            openedBitmap = bitmap;
                            storeThumbnail(getContentResolver(),openedBitmap,100,200,200,10);
                          //  savePng(openedBitmap);
                            imageView.setImageBitmap(bitmap);
                        },
                        throwable -> download.setText(throwable.toString())


                );
    }

    public Bitmap readImage() throws Exception {
        final Uri imageUri = received.getData();
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
            start(data);
        }
    }

    public void savePng(Bitmap bmp) {
        String filename;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        filename = sdf.format(date);

        try {
            String path = Environment.getExternalStorageDirectory() + "/Android/data/" +
                    getApplicationContext().getPackageName() + "/Files";
            ByteArrayOutputStream bOut = null;
            bmp.compress(Bitmap.CompressFormat.PNG, 85, bOut);
            byte[] arr = bOut.toByteArray();
            File file = new File(getGalleryPath(), filename + File.separator + ".png");
            FileOutputStream fOut = new FileOutputStream(file);

            fOut.flush();
            fOut.write(arr);
            fOut.close();

            MediaStore.Images.Media.insertImage(getContentResolver()
                    , file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getGalleryPath() {
        return Environment.getExternalStorageDirectory().toString() + "/";
    }
    private final void storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {
        // create the matrix to scale it
        if (!isHavePermission()){
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);
        }


        Matrix matrix = new Matrix();
        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND,kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.PNG, 100, thumbOut);
            thumbOut.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public boolean isHavePermission() {
        return ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}