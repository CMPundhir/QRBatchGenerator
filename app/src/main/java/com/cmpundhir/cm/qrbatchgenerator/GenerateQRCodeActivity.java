package com.cmpundhir.cm.qrbatchgenerator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateQRCodeActivity extends AppCompatActivity {

    private static final String TAG = "GenerateQRCodeActivity";
    ImageView imageView;
    Button button;
    TextView textView;
    Bitmap bitmap;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button2);
        textView = findViewById(R.id.textView);
        String data = getIntent().getStringExtra("data");
        createQRCode(data);
        String[] arr = data.split(",");
        StringBuilder builder = new StringBuilder();
        int pc = 0;
        for(String s : arr){
            pc++;
            builder.append(pc+".  "+s+"\n");
        }
        textView.setText(builder.toString());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage(bitmap);
            }
        });
    }

    private void createQRCode(String data){
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension

        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT,200);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
    }
    public void onShareButtonCLicked(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://drive.google.com/open?id=17_gC5WxEkX4h_fUW4rWiLM6VuQj2kVTU");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
    private void shareImage(Bitmap bm){
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_TEXT, "Demo message : This App is developed by @GenuineMark."
                + "\nInstall this app from here \n https://drive.google.com/file/d/1TMlN91gnFaaPgOEn1QIia3l_N9MffpvU/view?usp=sharing");
        String url= MediaStore.Images.Media.insertImage(this.getContentResolver(), bm, "title", "description");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share Image"));
    }
}
