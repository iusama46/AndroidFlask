package com.xpkian.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {
    String postUrl = "https://queststyle.ml/";

    ImageView imageView;

    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(),
                    null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView = findViewById(R.id.imageView);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(MainActivity2.this)
                        .crop()//Crop image(Optional), Check Customization for more option
                        .compress(512)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(100, 108)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                Uri imgUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                    Log.d("clima", "image");
                    //imageView.setImageBitmap(bitmap);
                    //backend.identify(bitmap);
                    //backend.identify2(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    // Read BitMap by file path
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    RequestBody postBodyImage = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", "androidFlsak.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                            .build();

                    postRequest(postUrl, postBodyImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, "ImagePicker.getError(data)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }


    void postRequest(final String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("clima", e.getMessage().toLowerCase());
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //responseText.setText("Failed to Connect to Server");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Log.d("clima 2", response.body().string());
                            /*InputStream input = new URL(response.body().string()).openStream();
                            Bitmap myBitmap = BitmapFactory.decodeStream(input);
                            imageView.setImageBitmap(myBitmap)*/
                            ;
                            Glide.with(MainActivity2.this).load(postUrl).into(imageView);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}