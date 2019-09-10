package com.cmpundhir.cm.qrbatchgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.cmpundhir.cm.qrbatchgenerator.util.CheckNetworkAvailability;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;
import info.androidhive.barcode.ScannerOverlay;

public class ScanQRcodeActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener{

    Toolbar toolbar;


    ImageView imageView;

    String lat, lon;

    ProgressBar bar;

    String userid;

    String productId;

    SharedPreferences pref;
    BarcodeReader barcodeReader ;
    ScannerOverlay scannerOverlay;
    MyGlobalListener myGlobalListener ;
    View viewFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        if(CheckNetworkAvailability.isNetworkAvailable(ScanQRcodeActivity.this)) {
            new CheckNetworkAvailability().isNetWorking(ScanQRcodeActivity.this);
        }else{
            showNetworkNotAvailableDialog(ScanQRcodeActivity.this);
        }
        viewFrag = findViewById(R.id.rel);
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);
        scannerOverlay = findViewById(R.id.scannerOverlay);
        barcodeReader.setUserVisibleHint(false);
        imageView = findViewById(R.id.image);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitleTextColor(Color.WHITE);

        toolbar.setNavigationIcon(R.drawable.blackarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        bar = (ProgressBar)findViewById(R.id.progress);
        bar.setVisibility(View.GONE);
        myGlobalListener = new MyGlobalListener();
        viewFrag.getViewTreeObserver().addOnGlobalLayoutListener(myGlobalListener);
    }

    @Override
    public void onScanned(final Barcode barcode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d("qrcode",barcode.rawValue);
                // fetchProductId(barcode.rawValue);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("qrcode",barcode.rawValue);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                //verifyQrcode(barcode.rawValue);
            }
        });

    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                barcodeReader.pauseScanning();
                scannerOverlay.setVisibility(View.GONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRcodeActivity.this);
                AlertDialog dialog = builder.setCancelable(true)
                        .setTitle("Error while Scanning QRCode")
                        .setMessage("This QRCode can't be scanned , Incomplete QRCode!!")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                barcodeReader.resumeScanning();
                                scannerOverlay.setVisibility(View.VISIBLE);
                            }
                        }).create();
                dialog.show();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        barcodeReader.resumeScanning();
                        scannerOverlay.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
    class MyGlobalListener implements ViewTreeObserver.OnGlobalLayoutListener{

        @Override
        public void onGlobalLayout() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            float x,y;
            y = viewFrag.getHeight();
            x = width*(y/height);
            viewFrag.setLayoutParams(new RelativeLayout.LayoutParams((int)width,(int)height-50));
            //Toast.makeText(CheckInScanActivity.this, "w = "+w+" h = "+h, Toast.LENGTH_SHORT).show();
            viewFrag.getViewTreeObserver().removeOnGlobalLayoutListener(myGlobalListener);
        }

    }
    private static void showNetworkNotAvailableDialog(final Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet !!").setMessage("Your Internet Connection is not working.");
        builder.setPositiveButton("Activate Internet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                context.startActivity(intent);
            }
        });

        builder.setCancelable(true);
        builder.show();
    }

    public void verifyQrcode(final String qrcode){
        bar.setVisibility(View.VISIBLE);
        barcodeReader.resumeScanning();
        scannerOverlay.setVisibility(View.VISIBLE);

    }
    private void showDialog4IncorrectQrcode(){
        barcodeReader.pauseScanning();
        scannerOverlay.setVisibility(View.GONE);
        new AlertDialog.Builder(ScanQRcodeActivity.this)
                .setCancelable(false)
                .setTitle("Incorrect Qrcode or already used")
                .setMessage("Please Scan QR code present on your loyalty card\nDo not scan if the hologram is scratched.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        barcodeReader.resumeScanning();
                        scannerOverlay.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("Report Incorrect QRcode", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create().show();
    }
}
